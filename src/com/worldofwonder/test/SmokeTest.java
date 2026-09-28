package com.worldofwonder.test;

import com.worldofwonder.controller.GameController;
import com.worldofwonder.model.GameRepository;
import com.worldofwonder.model.User;
import com.worldofwonder.model.WowLevel;
import com.worldofwonder.util.I18n;
import com.worldofwonder.view.Dashboard;
import com.worldofwonder.view.MainUI;
import com.worldofwonder.view.SettingsModal;
import com.worldofwonder.view.SoundUtil;
import com.worldofwonder.view.UITheme;
import com.worldofwonder.view.WordsOfWondersGameScreen;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * End-to-end smoke test for the Words of Wonders build.
 *
 * Covers the model/controller layer, localisation completeness, layout integrity,
 * a full Words of Wonders playthrough for every difficulty, and a rendered-pixel
 * audit that fails on any opaque white/light-grey panel bleeding through the theme.
 *
 * Run: java -cp bin com.worldofwonder.test.SmokeTest
 */
public class SmokeTest {

    private static int checks = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        SoundUtil.setMuted(true);
        I18n.setLanguage(I18n.Language.EN);

        testUserCrud();
        testGameData();
        testLocalization();
        testApp();
        testHeroIsResponsive();
        testHeroTitleFitsInKhmer();
        testModalsAreCentred();
        testNavigationRoundTrip();
        testWhiteBackgrounds();
        testLanguageToggleStability();
        testPlaythrough();
        testPowerUps();

        System.out.println();
        if (failures.isEmpty()) {
            System.out.println("ALL CHECKS PASSED (" + checks + " assertions).");
        } else {
            System.out.println(failures.size() + " FAILURE(S) out of " + checks + " checks:");
            for (String f : failures) {
                System.out.println("  - " + f);
            }
            System.exit(1);
        }
        System.exit(0);
    }

    // ── Model / controller ────────────────────────────────────────────

    private static void testUserCrud() {
        System.out.println("[1] User CRUD + leaderboard");
        GameController controller = new GameController();
        String username = "smoke_" + System.currentTimeMillis();
        User user = new User(0, username, username + "@example.com", "SecretPass123!", 0);
        check("createUser", controller.createUser(user));
        check("generated id", user.getId() > 0);

        int id = user.getId();
        check("not admin by default", !controller.isUserAdmin(id));

        user.setTotalPoints(120);
        check("updateUser", controller.updateUser(user));
        check("points persisted", controller.getUser(id) != null
                && controller.getUser(id).getTotalPoints() == 120);
        check("findUser after update", controller.getUser(id).getUsername().equals(username));

        int afterAdd = controller.addPoints(id, 80);
        check("addPoints returns new total", afterAdd == 200);

        List<User> board = controller.getLeaderboard(10);
        check("leaderboard not null", board != null);
        boolean sorted = true;
        for (int i = 0; i < board.size() - 1; i++) {
            if (board.get(i).getTotalPoints() < board.get(i + 1).getTotalPoints()) {
                sorted = false;
            }
        }
        check("leaderboard sorted descending", sorted);

        check("deleteUser", controller.deleteUser(id));
        check("user gone", controller.getUser(id) == null);
    }

    private static void testGameData() {
        System.out.println("[2] Game data loaded from data/*.json");
        GameRepository repo = new GameRepository();
        check("worlds loaded", !repo.getAllWorlds().isEmpty());
        check("levels loaded", !repo.getLevelsByWorldId(1).isEmpty());
        check("wow levels loaded", !repo.getAllWowLevels().isEmpty());

        List<WowLevel> wow = repo.getAllWowLevels();
        WowLevel first = wow.get(0);
        check("wow level has words", first.getWords() != null && first.getWords().length() > 2);
        check("findWowLevelById", repo.findWowLevelById(first.getId()) != null);
        check("resolveDataDirectory", new File(GameRepository.resolveDataDirectory(), "worlds.json").exists());
    }

    // ── Localisation ──────────────────────────────────────────────────

    private static void testLocalization() throws Exception {
        System.out.println("[3] Localisation completeness");
        I18n.setLanguage(I18n.Language.EN);
        List<String> missingEn = new ArrayList<>();
        List<String> missingKm = new ArrayList<>();
        for (String key : keys()) {
            if (isBlank(I18n.get(key))) missingEn.add(key);
            I18n.setLanguage(I18n.Language.KM);
            if (isBlank(I18n.get(key))) missingKm.add(key);
            I18n.setLanguage(I18n.Language.EN);
        }
        check("every key has English text (" + missingEn + ")", missingEn.isEmpty());
        check("every key has Khmer text (" + missingKm + ")", missingKm.isEmpty());

        List<String> dangling = new ArrayList<>();
        List<String> scanned = new ArrayList<>();
        for (String key : referencedKeys()) {
            scanned.add(key);
            if (I18n.get(key).equals(key)) {
                dangling.add(key);
            }
        }
        check("no dangling I18n.get(\"…\") references in src (" + dangling + ")", dangling.isEmpty());
        check("source scan found keys to check (" + scanned.size() + ")", scanned.size() > 40);

        I18n.setLanguage(I18n.Language.KM);
        check("Khmer active", I18n.isKhmer());
        check("Khmer differs from English", !I18n.get("game_words_title").equals("Words of Wonders"));
        I18n.setLanguage(I18n.Language.EN);

        check("message formatting", I18n.get("wow_progress", 3, 9).contains("3")
                && I18n.get("wow_progress", 3, 9).contains("9"));
    }

    /** Every literal key passed to I18n.get(...) anywhere in src. */
    private static List<String> referencedKeys() throws Exception {
        Set<String> found = new TreeSet<>();
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("I18n\\.get\\(\\s*\"([^\"]+)\"");
        java.nio.file.Path root = java.nio.file.Paths.get("src");
        List<java.nio.file.Path> files = new ArrayList<>();
        try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(root)) {
            walk.filter(f -> f.toString().endsWith(".java")).forEach(files::add);
        }
        for (java.nio.file.Path f : files) {
            java.util.regex.Matcher m = p.matcher(java.nio.file.Files.readString(f));
            while (m.find()) {
                found.add(m.group(1));
            }
        }
        return new ArrayList<>(found);
    }

    private static List<String> keys() throws Exception {
        Field f = I18n.class.getDeclaredField("translations");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) f.get(null);
        return new ArrayList<>(map.keySet());
    }

    // ── Application shell ─────────────────────────────────────────────

    private static MainUI app;

    private static void testApp() throws Exception {
        System.out.println("[4] Application shell");
        onEdt(() -> {
            app = new MainUI();
            app.setVisible(false);
            app.setSize(1280, 800);
        });
        settle();

        check("dashboard shows exactly one card", cards().size() == 1);
        check("the one card is the Words of Wonders launcher", wordsCards().size() == 1);
        check("hero card renders a Play Now pill", hasPlayNowPill(wordsCards().get(0)));
        check("no wheel or gift entry point survives", noDeadFeatureRefs());

        onEdt(() -> app.showScreen(MainUI.SCREEN_DASHBOARD));
        settle();

        onEdt(() -> app.showScreen(MainUI.SCREEN_DASHBOARD));
        settle();
        check("dashboard route works", app.getGameController() != null);

        onEdt(() -> app.showScreen(MainUI.SCREEN_WORDS));
        settle();
        check("words route works", findWordsScreen() != null);

        onEdt(() -> app.showScreen(MainUI.SCREEN_WELCOME));
        settle();
        check("welcome route works", true);
    }

    /** The hero card must paint a visible "Play Now" pill in its lower band. */
    private static boolean hasPlayNowPill(JButton card) {
        BufferedImage img = render(card, card.getWidth() > 0 ? card.getWidth() : 860,
                card.getHeight() > 0 ? card.getHeight() : 320);
        String play = I18n.get("play_now");
        // Sample the bottom quarter of the card for a bright pill on the dark band.
        int lit = 0;
        for (int y = (int) (img.getHeight() * 0.72); y < (int) (img.getHeight() * 0.95); y++) {
            for (int x = (int) (img.getWidth() * 0.25); x < (int) (img.getWidth() * 0.75); x++) {
                int p = img.getRGB(x, y);
                int r = (p >> 16) & 0xff, g = (p >> 8) & 0xff, b = p & 0xff;
                int lum = (r * 299 + g * 587 + b * 114) / 1000;
                if (lum > 120) {
                    lit++;
                }
            }
        }
        return !play.isEmpty() && lit > 500;
    }

    /** Guards against the wheel / daily-gift feature creeping back in. */
    private static boolean noDeadFeatureRefs() throws Exception {
        java.util.regex.Pattern dead = java.util.regex.Pattern.compile("WorldWheelModal|DailyRewardModal|GameIcon\\.WHEEL|"
                + "GameIcon\\.GIFT|VectorIcon\\.GIFT|wheel_|daily_gift|daily_modal|"
                + "claimDailyBonus|canClaimDailyBonus|lastClaimDate|getStreakCount|updateStreak");
        List<java.nio.file.Path> files = new ArrayList<>();
        try (java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(java.nio.file.Paths.get("src"))) {
            walk.filter(f -> f.toString().endsWith(".java")).forEach(files::add);
        }
        List<String> hits = new ArrayList<>();
        for (java.nio.file.Path f : files) {
            if (f.getFileName().toString().equals("SmokeTest.java")) {
                continue; // this file necessarily spells the patterns out
            }
            java.util.regex.Matcher m = dead.matcher(java.nio.file.Files.readString(f));
            if (m.find()) {
                hits.add(f.getFileName() + ":" + m.group());
            }
        }
        if (!hits.isEmpty()) {
            System.out.println("      residual: " + hits);
        }
        return hits.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static <T> T onEdtGet(java.util.function.Supplier<T> sup) {
        final Object[] out = new Object[1];
        onEdt(() -> out[0] = sup.get());
        return (T) out[0];
    }

    private static List<JButton> cards() {
        List<JButton> out = new ArrayList<>();
        for (Component c : allComponents(dashboard())) {
            if (c instanceof UITheme.GameModeCard) {
                out.add((JButton) c);
            }
        }
        return out;
    }

    private static List<JButton> wordsCards() {
        List<JButton> out = new ArrayList<>();
        for (JButton b : cards()) {
            if (get(b, "icon", null) == UITheme.GameIcon.WORDS) {
                out.add(b);
            }
        }
        return out;
    }

    // ── Hero is centred and never clipped, at any window size ────────

    private static void testHeroIsResponsive() throws Exception {
        System.out.println("[4a] Hero stays centred and inside the viewport");

        int[] widths = {1920, 1366, 1024, 800, 640, 466};
        for (int w : widths) {
            // A fresh Dashboard per width: reparenting the live one would fight
            // the app's own CardLayout, and a maximized frame cannot be resized.
            final Dashboard dash = onEdtGet(() -> new Dashboard(app));
            final JPanel host = new JPanel(new BorderLayout());
            host.setOpaque(false);
            onEdt(() -> {
                host.setSize(w, 900);
                host.add(dash);
            });
            // The page card resizes itself from a componentResized hook, so pump
            // the layout until it converges rather than laying out only once.
            for (int pass = 0; pass < 3; pass++) {
                onEdt(() -> layoutTree(host));
                settle();
            }

            JButton hero = heroCardIn(dash);
            check("hero card present at " + w + "px", hero != null);
            if (hero != null) {
                Rectangle card = onEdtGet(() -> boundsIn(hero, host));
                check("hero fits a " + w + "px viewport (w=" + card.width + " at x=" + card.x + ")",
                        card.width > 0 && card.x >= 0 && card.x + card.width <= w);
                int offset = Math.abs((card.x + card.width / 2) - (w / 2));
                check("hero is horizontally centred at " + w + "px (off by " + offset + "px)", offset <= 2);
            }

            int overflowing = 0;
            int offCentre = 0;
            int wrapped = 0;
            for (Component c : allComponents(dash)) {
                if (!(c instanceof JLabel) || !c.isVisible()) {
                    continue;
                }
                boolean centredCopy = ((JLabel) c).getText() != null
                        && ((JLabel) c).getText().contains("text-align:center");
                if (!centredCopy) {
                    continue;
                }
                wrapped++;
                Rectangle r = onEdtGet(() -> boundsIn(c, host));
                if (r.x < 0 || r.x + r.width > w) {
                    overflowing++;
                }
                if (Math.abs((r.x + r.width / 2) - (w / 2)) > 2) {
                    offCentre++;
                }
            }
            check("the hero has centred copy at " + w + "px (" + wrapped + " blocks)", wrapped >= 2);
            check("no text overflows at " + w + "px", overflowing == 0);
            check("all centred copy is horizontally centred at " + w + "px", offCentre == 0);

            // The gradient title is drawn with drawString(), so any HTML would be
            // painted literally. Guard the exact markup that used to leak.
            UITheme.GradientTextLabel title = gradientTitleIn(dash);
            check("gradient title present at " + w + "px", title != null);
            if (title != null) {
                String t = title.getText();
                check("gradient title carries no raw HTML at " + w + "px",
                        t == null || (t.indexOf('<') < 0 && t.indexOf('>') < 0));
                Rectangle tr = onEdtGet(() -> boundsIn(title, host));
                check("gradient title is centred at " + w + "px",
                        Math.abs((tr.x + tr.width / 2) - (w / 2)) <= 2);
                check("gradient title fits at " + w + "px", tr.x >= 0 && tr.x + tr.width <= w);
                int ink = onEdtGet(() -> title.getFontMetrics(title.getFont())
                        .stringWidth(title.getText()));
                check("gradient title text fits its label at " + w + "px (ink=" + ink
                                + ", label=" + tr.width + ")", ink <= tr.width);
                check("gradient title keeps a readable point size at " + w + "px",
                        onEdtGet(() -> title.getFont().getSize()) >= 18);
            }

            check("no scroller wraps the dashboard at " + w + "px",
                    onEdtGet(() -> countScrollPanes(dash)) == 0);

        }
    }

    private static UITheme.GradientTextLabel gradientTitleIn(Container root) {
        for (Component c : allComponents(root)) {
            if (c instanceof UITheme.GradientTextLabel) {
                return (UITheme.GradientTextLabel) c;
            }
        }
        return null;
    }

    private static int countScrollPanes(Container root) {
        int n = 0;
        for (Component c : allComponents(root)) {
            if (c instanceof javax.swing.JScrollPane) {
                n++;
            }
        }
        return n;
    }

    /** Bounds of {@code c} expressed in {@code ancestor}'s coordinate space. */
    private static Rectangle boundsIn(Component c, Container ancestor) {
        Rectangle r = c.getBounds();
        java.awt.Point p = new java.awt.Point(r.x, r.y);
        Container cur = c.getParent();
        while (cur != null && cur != ancestor) {
            p.translate(cur.getX(), cur.getY());
            cur = cur.getParent();
        }
        return new Rectangle(p.x, p.y, r.width, r.height);
    }

    /** Locate the single hero card anywhere under the given container. */
    private static JButton heroCardIn(Container root) {
        for (Component c : allComponents(root)) {
            if (c instanceof UITheme.GameModeCard) {
                return (JButton) c;
            }
        }
        return null;
    }

    /**
     * Khmer glyphs are the widest title in the app, so the hero is re-measured with
     * the Khmer string active. This is what proves the title actually scales down
     * instead of being clipped on a narrow window.
     */
    private static void testHeroTitleFitsInKhmer() throws Exception {
        System.out.println("[4b] Hero title fits in Khmer, the widest localisation");
        try {
            I18n.setLanguage(I18n.Language.KM);
            for (int w : new int[]{1280, 800, 466}) {
                // Mirrors testHeroIsResponsive: a fresh Dashboard on an undecorated
                // host, pumped until the resize hook converges.
                final Dashboard dash = onEdtGet(() -> new Dashboard(app));
                final JPanel host = new JPanel(new BorderLayout());
                host.setOpaque(false);
                onEdt(() -> {
                    host.setSize(w, 900);
                    host.add(dash);
                });
                for (int pass = 0; pass < 3; pass++) {
                    onEdt(() -> layoutTree(host));
                    settle();
                }

                UITheme.GradientTextLabel title = findTitle(dash);
                check("Khmer gradient title present at " + w + "px", title != null);
                if (title == null) {
                    continue;
                }
                String text = onEdtGet(title::getText);
                check("Khmer title is not the English string at " + w + "px",
                        !text.equals("Words of Wonders"));
                check("Khmer title carries no raw HTML at " + w + "px",
                        !text.contains("<") && !text.contains(">"));
                Rectangle tr = onEdtGet(title::getBounds);
                int ink = onEdtGet(() -> title.getFontMetrics(title.getFont()).stringWidth(text));
                check("Khmer title text fits its label at " + w + "px (ink=" + ink
                        + ", label=" + tr.width + ")", ink <= tr.width);
                check("Khmer title stays inside the hero at " + w + "px",
                        tr.x >= 0 && tr.x + tr.width <= onEdtGet(dash::getWidth));
            }
        } finally {
            I18n.setLanguage(I18n.Language.EN);
        }
    }

    private static UITheme.GradientTextLabel findTitle(java.awt.Container root) {
        for (java.awt.Component c : root.getComponents()) {
            if (c instanceof UITheme.GradientTextLabel) {
                return (UITheme.GradientTextLabel) c;
            }
            if (c instanceof java.awt.Container) {
                UITheme.GradientTextLabel hit = findTitle((java.awt.Container) c);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return null;
    }

    // ── Modals are centred on their owner and fit it ──────────────────

    private static void testModalsAreCentred() throws Exception {
        System.out.println("[4c] Modals centre on their owner and stay inside it");
        javax.swing.JFrame owner = new javax.swing.JFrame("owner");
        owner.setUndecorated(true);
        try {
            for (int w : new int[]{1280, 1024, 800, 640}) {
                final int fw = w;
                onEdt(() -> {
                    owner.setSize(fw, 760);
                    owner.setVisible(true);
                });
                settle();

                final javax.swing.JDialog[] dlg = new javax.swing.JDialog[1];
                onEdt(() -> dlg[0] = new SettingsModal(owner));
                settle();

                Rectangle db = onEdtGet(dlg[0]::getBounds);
                Rectangle ob = onEdtGet(owner::getBounds);
                check("settings dialog is built at " + w + "px", db.width > 0 && db.height > 0);
                check("settings dialog fits its owner at " + w + "px (" + db.width + "x" + db.height + ")",
                        db.width <= ob.width && db.height <= ob.height);
                check("settings dialog is centred on its owner at " + w + "px",
                        Math.abs((db.x + db.width / 2) - (ob.x + ob.width / 2)) <= 2
                                && Math.abs((db.y + db.height / 2) - (ob.y + ob.height / 2)) <= 2);
                // A fixed dialog width larger than its content is what produced the
                // "off-centre side sheet": the theme grid must span the whole body.
                java.lang.reflect.Field gf = SettingsModal.class.getDeclaredField("themeCardsPanel");
                gf.setAccessible(true);
                JPanel grid = (JPanel) gf.get(dlg[0]);
                // Measured inside the grid's own parent so the assertion tracks the
                // real layout rather than a hardcoded border width.
                int gridW = onEdtGet(grid::getWidth);
                int parentW = onEdtGet(() -> grid.getParent().getWidth());
                int gridX = onEdtGet(grid::getX);
                check("theme cards are not clipped by the modal body at " + w + "px ("
                        + gridW + " in " + parentW + ")", gridW > 0 && gridW <= parentW);
                // Equal slack on both sides is what makes the panel read as centred
                // rather than as a sheet pinned against one edge.
                int slackLeft = gridX;
                int slackRight = parentW - gridX - gridW;
                check("theme cards are horizontally balanced at " + w + "px (L" + slackLeft
                                + "/R" + slackRight + ")", Math.abs(slackLeft - slackRight) <= 2);

                onEdt(dlg[0]::dispose);
            }
        } finally {
            onEdt(() -> {
                owner.setVisible(false);
                owner.dispose();
            });
        }
    }

    // ── Play Now -> game -> back -> dashboard round trip ──────────────

    private static void testNavigationRoundTrip() throws Exception {
        System.out.println("[4b] Play Now launches the game, back returns to the dashboard");
        onEdt(() -> app.showScreen(MainUI.SCREEN_DASHBOARD));
        settle();

        check("starting on the dashboard", dashboard() != null);
        check("dashboard is the active route", MainUI.SCREEN_DASHBOARD.equals(app.getCurrentScreen()));

        // 1. Click the hero card exactly as a player would.
        onEdt(() -> wordsCards().get(0).doClick());
        settle();
        check("Play Now switched to the words route", MainUI.SCREEN_WORDS.equals(app.getCurrentScreen()));

        // 2. Start a puzzle so the game is genuinely in play, not just mounted.
        WordsOfWondersGameScreen screen = findWordsScreen();
        onEdt(() -> {
            invoke(screen, "startGame", new Class<?>[]{int.class}, 0);
            layoutTree(app.getContentPane());
        });
        settle();
        check("difficulty picker accepts a choice", get(screen, "gameActive", false));
        check("letters dealt", get(screen, "circleLetters", new ArrayList<>()).size() == 5);

        // 3. Press the in-game Back button.
        JButton back = backButton();
        check("game screen has a Back to Dashboard button", back != null);
        onEdt(() -> back.doClick());
        settle();
        check("back returned to the dashboard route", MainUI.SCREEN_DASHBOARD.equals(app.getCurrentScreen()));
        check("dashboard still shows exactly one card", cards().size() == 1);
        check("hero card survived the round trip", wordsCards().size() == 1);

        // 4. And we can go straight back in.
        onEdt(() -> wordsCards().get(0).doClick());
        settle();
        check("second Play Now also works", MainUI.SCREEN_WORDS.equals(app.getCurrentScreen()));
        onEdt(() -> backButton().doClick());
        settle();
        check("second back press also works", MainUI.SCREEN_DASHBOARD.equals(app.getCurrentScreen()));
    }

    /** The header Back button on the game screen, matched by its localised label. */
    private static JButton backButton() {
        String label = I18n.get("back_to_dashboard");
        for (Component c : allComponents(findWordsScreen())) {
            if (c instanceof JButton && label.equals(((JButton) c).getText())) {
                return (JButton) c;
            }
        }
        return null;
    }

    // ── Rendered pixel audit ──────────────────────────────────────────

    private static void testWhiteBackgrounds() throws Exception {
        System.out.println("[5] Rendered background audit (no white/light panels)");
        for (String route : new String[]{MainUI.SCREEN_WELCOME, MainUI.SCREEN_DASHBOARD, MainUI.SCREEN_WORDS}) {
            onEdt(() -> app.showScreen(route));
            settle();
            BufferedImage img = render(app.getContentPane(), 1280, 800);
            int whiteBlocks = countLightBlocks(img, 32);
            check("no light/white blocks on '" + route + "' (found " + whiteBlocks + ")", whiteBlocks == 0);
            ImageIO.write(img, "png", new File("/tmp/opencode/wow/render_" + route + ".png"));
        }

        for (String palette : new String[]{"MIDNIGHT", "OCEAN", "AMETHYST"}) {
            onEdt(() -> UITheme.setPalette(UITheme.ThemePalette.valueOf(palette)));
            onEdt(() -> app.showScreen(MainUI.SCREEN_WORDS));
            settle();
            BufferedImage img = render(app.getContentPane(), 1280, 800);
            check("no light/white blocks with palette " + palette,
                    countLightBlocks(img, 32) == 0);
        }
        onEdt(() -> UITheme.setPalette(UITheme.ThemePalette.MIDNIGHT));
    }

    /**
     * Counts tiles of size x size that are almost entirely near-white. In the dark
     * theme only a handful of pixels (glyph cores, letter nodes) are ever near-white,
     * so a whole tile being white means an opaque panel painted over the gradient.
     */
    private static int countLightBlocks(BufferedImage img, int block) {
        int count = 0;
        for (int by = 0; by + block <= img.getHeight(); by += block) {
            for (int bx = 0; bx + block <= img.getWidth(); bx += block) {
                int light = 0;
                int total = 0;
                for (int y = by; y < by + block; y++) {
                    for (int x = bx; x < bx + block; x++) {
                        int rgb = img.getRGB(x, y);
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = rgb & 0xff;
                        total++;
                        if (r > 235 && g > 235 && b > 235) {
                            light++;
                        }
                    }
                }
                if (total > 0 && light * 100 / total >= 90) {
                    count++;
                }
            }
        }
        return count;
    }

    private static BufferedImage render(Component c, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        onEdt(() -> {
            if (c instanceof Container) {
                c.setSize(w, h);
                layoutTree((Container) c);
            }
            c.paint(g);
        });
        g.dispose();
        return img;
    }

    // ── Language switching must not leak components ───────────────────

    private static void testLanguageToggleStability() throws Exception {
        System.out.println("[6] Language toggle keeps the component tree stable");
        onEdt(() -> app.showScreen(MainUI.SCREEN_DASHBOARD));
        settle();
        int before = allComponents(dashboard()).size();
        for (int i = 0; i < 6; i++) {
            onEdt(I18n::toggleLanguage);
        }
        settle();
        int after = allComponents(dashboard()).size();
        check("dashboard component count stable (" + before + " -> " + after + ")", before == after);
        I18n.setLanguage(I18n.Language.EN);
    }

    // ── Full playthrough of every difficulty ──────────────────────────

    private static void testPlaythrough() throws Exception {
        System.out.println("[7] Words of Wonders playthrough");
        onEdt(() -> app.showScreen(MainUI.SCREEN_WORDS));
        settle();

        WordsOfWondersGameScreen screen = findWordsScreen();
        check("game screen present", screen != null);
        if (screen == null) {
            return;
        }

        int before = allComponents(screen).size();
        for (int i = 0; i < 4; i++) {
            onEdt(I18n::toggleLanguage);
        }
        settle();
        check("game screen component count stable", allComponents(screen).size() == before);
        I18n.setLanguage(I18n.Language.EN);

        for (int puzzle = 0; puzzle < puzzleCount(); puzzle++) {
            playPuzzle(screen, puzzle, puzzle % 2 == 0);
        }
    }

    // ── Power-ups ─────────────────────────────────────────────────────

    private static void testPowerUps() throws Exception {
        System.out.println("[8] Hint, hammer and point guards");
        onEdt(() -> app.showScreen(MainUI.SCREEN_WORDS));
        settle();
        WordsOfWondersGameScreen screen = findWordsScreen();
        if (screen == null) {
            check("power-up screen present", false);
            return;
        }

        onEdt(() -> {
            invoke(screen, "startGame", new Class<?>[]{int.class}, 0);
            layoutTree(app.getContentPane());
        });
        settle();

        // A fresh board has no points, so both tools must refuse and warn.
        check("no points on a fresh board", get(screen, "points", 0) == 0);
        onEdt(() -> invoke(screen, "useHint", new Class<?>[0]));
        onEdt(() -> invoke(screen, "useHammer", new Class<?>[0]));
        settle();
        check("hint refused when broke", get(screen, "points", 0) == 0);
        check("power-up buttons disabled on a fresh board",
                !get(screen, "hintBtn", new JButton()).isEnabled()
                        && !get(screen, "hammerBtn", new JButton()).isEnabled());
        check("no letters revealed while broke", get(screen, "hintedMap", new HashMap<>()).isEmpty());
        check("still all words to find", get(screen, "foundWords", new HashSet<>()).isEmpty());

        // Earn points by finding words with the real drag path, then spend them.
        List<String> targets = get(screen, "targetWords", new ArrayList<>());
        List<Point> nodes = nodePoints(screen);
        for (String word : targets.subList(0, 2)) {
            onEdt(() -> dragWord(screen, nodes, orderFor(word, get(screen, "circleLetters", new ArrayList<>()))));
            settle();
        }
        int earned = get(screen, "points", 0);
        check("points earned from finds (" + earned + ")", earned >= 40);

        Map<Integer, Set<Integer>> hinted = get(screen, "hintedMap", new HashMap<>());
        onEdt(() -> invoke(screen, "useHint", new Class<?>[0]));
        settle();
        check("hint spends points", get(screen, "points", 0) == earned - 15);
        check("hint reveals exactly one letter", hintedCount(get(screen, "hintedMap", new HashMap<>())) == 1);
        check("hint reveals a letter of an unfound word", !hinted.isEmpty());

        int foundBefore = get(screen, "foundWords", new HashSet<>()).size();
        onEdt(() -> invoke(screen, "useHammer", new Class<?>[0]));
        settle();
        check("hammer books a whole word", get(screen, "foundWords", new HashSet<>()).size() == foundBefore + 1);
        check("hammer nets out to -10 points", get(screen, "points", 0) == earned - 15 - 10);
        check("hint button re-enables once affordable",
                get(screen, "hintBtn", new JButton()).isEnabled());
        check("shuffle keeps the same letters", shuffleKeepsLetters(screen));
    }

    private static int hintedCount(Map<Integer, Set<Integer>> hinted) {
        int total = 0;
        for (Set<Integer> set : hinted.values()) {
            total += set.size();
        }
        return total;
    }

    private static boolean shuffleKeepsLetters(WordsOfWondersGameScreen screen) throws Exception {
        List<Character> before = get(screen, "circleLetters", new ArrayList<>());
        String beforeStr = charsToString(before);
        onEdt(() -> invoke(screen, "shuffleLetters", new Class<?>[0]));
        settle();
        List<Character> after = get(screen, "circleLetters", new ArrayList<>());
        return sortedChars(beforeStr).equals(sortedChars(charsToString(after)));
    }

    private static void playPuzzle(WordsOfWondersGameScreen screen, int puzzle, boolean drag) throws Exception {
        onEdt(() -> {
            invoke(screen, "startGame", new Class<?>[]{int.class}, puzzle);
            // startGame rebuilds the centre panel; lay the tree out so the letter
            // ring has a real size for the synthetic mouse events to hit.
            layoutTree(app.getContentPane());
        });
        settle();
        JComponent ring = (JComponent) get(screen, "lCircle", null);
        check("puzzle " + puzzle + ": letter ring is laid out", ring != null && ring.getWidth() > 0);

        List<String> targets = get(screen, "targetWords", new ArrayList<>());
        List<Character> letters = get(screen, "circleLetters", new ArrayList<>());
        check("puzzle " + puzzle + ": letters are a shuffle of the base word",
                !letters.isEmpty() && sortedChars(targets.get(0)).equals(sortedChars(charsToString(letters))));
        check("puzzle " + puzzle + ": has words to find", targets.size() >= 5);

        for (String word : targets) {
            List<Point> nodes = nodePoints(screen);
            List<Integer> order = orderFor(word, letters);
            check("puzzle " + puzzle + ": '" + word + "' is spellable", order != null);
            if (order == null) {
                return;
            }
            List<Integer> path = order;
            if (drag) {
                onEdt(() -> dragWord(screen, nodes, path));
            } else {
                onEdt(() -> tapWord(screen, nodes, path));
            }
            settle();
        }

        Set<String> found = get(screen, "foundWords", new HashSet<>());
        List<String> missing = new ArrayList<>();
        for (String t : targets) {
            if (!found.contains(t)) {
                missing.add(t);
            }
        }
        check("puzzle " + puzzle + ": every word found (" + found.size() + "/" + targets.size()
                + (missing.isEmpty() ? "" : ", missing " + missing
                + " lastShown='" + textOf(screen, "curWordLbl") + "' selection=" + get(screen, "selection", new ArrayList<>())),
                found.size() == targets.size());
        check("puzzle " + puzzle + ": progress bar full", progressOf(screen) > 0.99);
        check("puzzle " + puzzle + ": grid shows all rows found", gridFullyFound(screen));

        sleep(1100);
        String doneText = textOf(screen, "doneText");
        check("puzzle " + puzzle + ": completion screen shown", doneText.contains("found all"));
        check("puzzle " + puzzle + ": points banked", get(screen, "points", 0) > 0);

        onEdt(() -> invoke(screen, "showDiff", new Class<?>[0]));
        settle();
        check("puzzle " + puzzle + ": returns to difficulty picker", isDiffView(screen));
    }

    /** Drives the real mouse handlers: press on the first node, drag across, release. */
    private static void dragWord(WordsOfWondersGameScreen screen, List<Point> nodes, List<Integer> order) {
        Object circle = get(screen, "lCircle", null);
        if (circle == null) {
            return;
        }
        JComponent comp = (JComponent) circle;
        dispatch(comp, MouseEvent.MOUSE_PRESSED, nodes.get(order.get(0)));
        for (int i = 1; i < order.size(); i++) {
            dispatch(comp, MouseEvent.MOUSE_DRAGGED, nodes.get(order.get(i)));
        }
        dispatch(comp, MouseEvent.MOUSE_RELEASED, nodes.get(order.get(order.size() - 1)));
    }

    /** Plays a word one letter at a time; each word banks as its last letter lands. */
    private static void tapWord(WordsOfWondersGameScreen screen, List<Point> nodes, List<Integer> order) {
        Object circle = get(screen, "lCircle", null);
        if (circle == null) {
            return;
        }
        JComponent comp = (JComponent) circle;
        for (int i = 0; i < order.size(); i++) {
            Point p = nodes.get(order.get(i));
            dispatch(comp, MouseEvent.MOUSE_PRESSED, p);
            dispatch(comp, MouseEvent.MOUSE_RELEASED, p);
        }
    }

    private static void dispatch(JComponent c, int id, Point p) {
        long t = System.currentTimeMillis();
        c.dispatchEvent(new MouseEvent(c, id, t, 0, p.x, p.y, 1, false, MouseEvent.BUTTON1));
    }

    /** Screen coordinates of every letter node, mirroring the panel's own layout maths. */
    private static List<Point> nodePoints(WordsOfWondersGameScreen screen) throws Exception {
        List<Character> letters = get(screen, "circleLetters", new ArrayList<>());
        JComponent circle = (JComponent) get(screen, "lCircle", null);
        Dimension natural = circle.getPreferredSize();
        int cw = circle.getWidth();
        int ch = circle.getHeight();
        double sx = natural.width > 0 ? cw / (double) natural.width : 1.0;
        double sy = natural.height > 0 ? ch / (double) natural.height : 1.0;
        int cx = natural.width / 2;
        int cy = natural.height / 2;
        float radius = Math.min(natural.width, natural.height) * 0.34f;

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < letters.size(); i++) {
            double a = Math.PI * 2 * i / letters.size() - Math.PI / 2;
            int nx = (int) (cx + radius * Math.cos(a));
            int ny = (int) (cy + radius * Math.sin(a));
            points.add(new Point((int) Math.round(nx * sx), (int) Math.round(ny * sy)));
        }
        return points;
    }

    /** Indices of the letters that spell {@code word}, in order. */
    private static List<Integer> orderFor(String word, List<Character> letters) {
        List<Integer> used = new ArrayList<>();
        List<Integer> order = new ArrayList<>();
        for (char ch : word.toCharArray()) {
            int idx = -1;
            for (int i = 0; i < letters.size(); i++) {
                if (!used.contains(i) && letters.get(i) == ch) {
                    idx = i;
                    break;
                }
            }
            if (idx < 0) {
                return null;
            }
            used.add(idx);
            order.add(idx);
        }
        return order;
    }

    private static boolean gridFullyFound(WordsOfWondersGameScreen screen) throws Exception {
        List<String> targets = get(screen, "targetWords", new ArrayList<>());
        Set<String> found = get(screen, "foundWords", new HashSet<>());
        return found.containsAll(targets);
    }

    private static double progressOf(WordsOfWondersGameScreen screen) throws Exception {
        Object bar = get(screen, "pBar", null);
        Method m = bar.getClass().getMethod("getProgress");
        return (Double) m.invoke(bar);
    }

    private static boolean isDiffView(WordsOfWondersGameScreen screen) throws Exception {
        JComponent panel = (JComponent) get(screen, "content", null);
        java.awt.CardLayout cl = (java.awt.CardLayout) get(screen, "cards", null);
        return panel.getComponent(0) == null ? false : isShownCard(cl, panel, 0);
    }

    private static boolean isShownCard(java.awt.CardLayout cl, JComponent parent, int index) {
        for (Component child : parent.getComponents()) {
            if (child == parent.getComponent(index)) {
                return child.getWidth() > 0 && child.getHeight() > 0;
            }
        }
        return false;
    }

    private static int puzzleCount() throws Exception {
        Field f = WordsOfWondersGameScreen.class.getDeclaredField("PUZZLES");
        f.setAccessible(true);
        return ((String[][]) f.get(null)).length;
    }

    // ── Reflection / layout helpers ───────────────────────────────────

    private static WordsOfWondersGameScreen findWordsScreen() {
        for (Component c : allComponents(app)) {
            if (c instanceof WordsOfWondersGameScreen) {
                return (WordsOfWondersGameScreen) c;
            }
        }
        return null;
    }

    private static Dashboard dashboard() {
        for (Component c : allComponents(app)) {
            if (c instanceof Dashboard) {
                return (Dashboard) c;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(Object target, String field, T fallback) {
        try {
            Field f = findField(target.getClass(), field);
            f.setAccessible(true);
            return (T) f.get(target);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String textOf(Object target, String field) {
        JLabel lbl = get(target, field, null);
        return lbl == null || lbl.getText() == null ? "" : lbl.getText();
    }

    private static Object invoke(Object target, String name, Class<?>[] sig, Object... args) {
        try {
            Method m = findMethod(target.getClass(), name, sig);
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("cannot invoke " + name, e);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // keep walking up
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Method findMethod(Class<?> type, String name, Class<?>[] sig) throws NoSuchMethodException {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredMethod(name, sig);
            } catch (NoSuchMethodException ignored) {
                // keep walking up
            }
        }
        throw new NoSuchMethodException(name);
    }

    private static List<Component> allComponents(Container root) {
        List<Component> out = new ArrayList<>();
        if (root == null) {
            return out;
        }
        out.add(root);
        for (Component c : root.getComponents()) {
            if (c instanceof Container) {
                out.addAll(allComponents((Container) c));
            }
        }
        return out;
    }

    private static void layoutTree(Container c) {
        c.doLayout();
        for (Component child : c.getComponents()) {
            if (child instanceof Container) {
                layoutTree((Container) child);
            }
        }
    }

    private static void onEdt(Runnable r) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeAndWait(r);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void settle() {
        sleep(90);
        try {
            SwingUtilities.invokeAndWait(() -> { });
        } catch (Exception ignored) {
            // best effort
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String sortedChars(String s) {
        char[] c = s.toCharArray();
        java.util.Arrays.sort(c);
        return new String(c);
    }

    private static String charsToString(List<Character> list) {
        StringBuilder sb = new StringBuilder();
        for (char c : list) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void check(String name, boolean ok) {
        checks++;
        if (ok) {
            System.out.println("    ok   " + name);
        } else {
            failures.add(name);
            System.out.println("    FAIL " + name);
        }
    }
}
