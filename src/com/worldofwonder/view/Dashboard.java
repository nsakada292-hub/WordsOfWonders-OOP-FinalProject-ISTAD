package com.worldofwonder.view;

import com.worldofwonder.util.ApiService;
import com.worldofwonder.util.I18n;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public class Dashboard extends JPanel {

    private final MainUI app;

    private String username = "Guest";
    private boolean isGuest = true;
    private int userId = 0;
    private String token = null;
    private int totalPoints = 0;

    private final JPanel content;
    private JPanel topbar;
    private JPanel centerPanel;
    private JLabel scoreValue;
    private final Runnable langListener;

    public Dashboard(MainUI app) {
        super(new BorderLayout());
        this.app = app;
        setOpaque(false);

        this.content = new JPanel(new BorderLayout(0, UITheme.GAP_SECTION));
        content.setOpaque(false);
        this.topbar = buildTopbar();
        this.centerPanel = buildCenter();
        content.add(topbar, BorderLayout.NORTH);
        content.add(centerPanel, BorderLayout.CENTER);

        JPanel card = UITheme.card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        // Fills a large window, shrinks on a small one, and needs no scrolling.
        UIUtil.responsiveSize(card, 1160, 720, 460, 380, 1440, 1200);
        card.add(content, BorderLayout.CENTER);
        this.pageCard = card;

        // Plain GridBagLayout centring: deliberately not a JScrollPane, so the
        // dashboard is a single non-scrolling page.
        JPanel root = UIUtil.wrapCentered(card);
        add(root, BorderLayout.CENTER);

        // Track the viewport so the page card grows and shrinks with the window.
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                fitCardToViewport();
            }
        });

        this.langListener = this::onLanguageChanged;
        I18n.addLanguageListener(langListener);
    }

    private void onLanguageChanged() {
        content.removeAll();
        topbar = buildTopbar();
        centerPanel = buildCenter();
        content.add(topbar, BorderLayout.NORTH);
        content.add(centerPanel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    public void setUser(String username, boolean isGuest, int userId, String token, int totalPoints) {
        this.username = username;
        this.isGuest = isGuest;
        this.userId = userId;
        this.token = token;
        this.totalPoints = totalPoints;
        content.remove(topbar);
        topbar = buildTopbar();
        content.add(topbar, BorderLayout.NORTH);
        content.revalidate();
        content.repaint();
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void updateScore(int newTotal) {
        this.totalPoints = newTotal;
        if (scoreValue != null) {
            scoreValue.setText(String.valueOf(newTotal));
        }
    }

    public void addGamePoints(int pointsEarned) {
        if (userId > 0 && pointsEarned > 0) {
            int newTotal = app.getGameController().addPoints(userId, pointsEarned);
            updateScore(newTotal);
        }
    }

    public MainUI getApp() {
        return app;
    }

    /** Words of Wonders is the only game, so the whole centre stage is a single
     *  column of centred content with one large hero card. No grid, no scrolling. */
    private JPanel buildCenter() {
        // BorderLayout, not GridBagLayout: GridBagLayout drops a component to its
        // *minimum* size when the preferred size will not fit, which collapsed the
        // hero to 300px on narrow windows. The stack fills the page here and the
        // BoxLayout below centres each piece inside it.
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        wrapLabels.clear();

        // GradientTextLabel draws through drawString(), so it must be given plain
        // text - HTML markup here would be painted literally. It is centred by the
        // label itself, so it needs no wrapping rules.
        String titleText = I18n.get("dashboard_hero_title");
        this.heroTitleText = titleText;
        this.heroTitle = new UITheme.GradientTextLabel(titleText,
                UITheme.FONT_PAGE_TITLE + 10, new java.awt.Color(0xffe9a8), new java.awt.Color(0xffc04d));
        UITheme.GradientTextLabel title = this.heroTitle;
        title.setFont(UITheme.fontFor(titleText, Font.BOLD, UITheme.FONT_PAGE_TITLE + 10));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIUtil.responsiveSize(title, MAX_TEXT_WIDTH, 52, 180, 30, MAX_TEXT_WIDTH, 120);
        stack.add(title);
        stack.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        String subText = I18n.get("dashboard_hero_sub");
        JLabel subtitle = UITheme.subtitle(centredHtml(subText));
        subtitle.setFont(UITheme.fontFor(subText, Font.PLAIN, UITheme.FONT_BODY + 3));
        subtitle.setForeground(new java.awt.Color(0xb8c8e8));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackWrap(subtitle, subText);
        stack.add(subtitle);
        stack.add(Box.createVerticalStrut(UITheme.GAP_SECTION + 6));

        JButton hero = wordsHeroCard();
        hero.setAlignmentX(Component.CENTER_ALIGNMENT);
        stack.add(hero);
        stack.add(Box.createVerticalStrut(UITheme.GAP_SECTION + 6));

        // A single line of plain text keeps the page balanced without adding a
        // second card next to the hero.
        String defaultFact = I18n.isKhmer()
                ? "តើអ្នកដឹងទេ? ពីរ៉ាមីតហ្គីហ្សាគឺជាសំណង់ដែលខ្ពស់ជាងគេបំផុតនៅលើពិភពលោកអស់រយៈពេល ៣,៨០០ ឆ្នាំ!"
                : "Did you know? The Great Pyramid of Giza was the tallest man-made structure for over 3,800 years!";
        JLabel factText = new JLabel(centredHtml(defaultFact), SwingConstants.CENTER);
        factText.setFont(UITheme.fontFor(defaultFact, Font.PLAIN, 13));
        factText.setForeground(new java.awt.Color(0x93a9cc));
        factText.setAlignmentX(Component.CENTER_ALIGNMENT);
        trackWrap(factText, defaultFact);
        stack.add(factText);

        ApiService.fetchRandomFact(fact -> {
            if (fact != null && !fact.isEmpty()) {
                String prefix = I18n.isKhmer() ? "តើអ្នកដឹងទេ? " : "Did you know? ";
                setCentreText(factText, prefix + fact);
            }
        }, err -> {});

        center.add(stack, BorderLayout.CENTER);
        // Re-wrap the centred copy whenever the window resizes so nothing clips.
        center.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                rewrapCentreText();
            }
        });
        return center;
    }

    /** Widest the hero copy is allowed to get on a roomy screen. */
    private static final int MAX_TEXT_WIDTH = 820;

    private JPanel pageCard;
    private UITheme.GradientTextLabel heroTitle;
    private String heroTitleText = "";
    private final java.util.List<Object[]> wrapLabels = new java.util.ArrayList<>();

    /** Size the page card to the live viewport instead of a hard-coded box, so
     *  the centred hero stays prominent without ever needing a scrollbar. */
    private void fitCardToViewport() {
        if (pageCard == null) {
            return;
        }
        int availW = getWidth();
        int availH = getHeight();
        if (availW <= 0) {
            return;
        }
        int w = Math.max(460, Math.min(availW - 40, 1440));
        int h = Math.max(380, Math.min(availH - 40, 1200));
        Dimension want = new Dimension(w, h);
        if (!want.equals(pageCard.getPreferredSize())) {
            pageCard.setPreferredSize(want);
            rewrapCentreText();
            revalidate();
            repaint();
        }
        fitHeroTitle(w);
    }

    /**
     * The page title is drawn with drawString() and cannot wrap, so its point size is
     * reduced until the text fits the page with a margin. Without this the title
     * overflows a narrow window and is clipped on both sides.
     */
    private void fitHeroTitle(int pageWidth) {
        if (heroTitle == null) {
            return;
        }
        int room = Math.max(120, pageWidth - UITheme.PAD_CARD_X * 2 - 24);
        int base = UITheme.FONT_PAGE_TITLE + 10;
        int size = base;
        while (size > 18) {
            heroTitle.setFont(UITheme.fontFor(heroTitleText, Font.BOLD, size));
            if (heroTitle.getFontMetrics(heroTitle.getFont()).stringWidth(heroTitleText) <= room) {
                break;
            }
            size -= 2;
        }
        heroTitle.repaint();
    }

    /** HTML with an explicit measure so the text centre-aligns and re-wraps. */
    private static String centredHtml(String text) {
        return "<html><div style='text-align:center;width:" + MAX_TEXT_WIDTH + "px'>" + text + "</div></html>";
    }

    private void trackWrap(JLabel label, String plain) {
        wrapLabels.add(new Object[]{label, plain});
        UIUtil.responsiveSize(label, MAX_TEXT_WIDTH, 24, 200, 18, MAX_TEXT_WIDTH, 200);
    }

    private void setCentreText(JLabel label, String plain) {
        for (Object[] entry : wrapLabels) {
            if (entry[0] == label) {
                entry[1] = plain;
                break;
            }
        }
        label.setText(centredHtml(plain));
    }

    /** Re-measure every centred label against the real viewport width. */
    private void rewrapCentreText() {
        int available = centerPanel == null ? 0 : centerPanel.getWidth();
        if (available <= 0) {
            available = getWidth();
        }
        int w = Math.max(200, Math.min(MAX_TEXT_WIDTH, available - UITheme.PAD_CARD_X * 2));
        for (Object[] entry : wrapLabels) {
            JLabel label = (JLabel) entry[0];
            label.setText("<html><div style='text-align:center;width:" + w + "px'>" + entry[1] + "</div></html>");
        }
    }

    /** The single, central call-to-action: launch Words of Wonders. */
    private JButton wordsHeroCard() {
        String label = I18n.get("game_words_title");
        String subtitleText = I18n.get("game_words_sub");
        UITheme.GameModeCard card = new UITheme.GameModeCard(UITheme.GameIcon.WORDS, label, subtitleText, UITheme.GOLD);
        card.setBand(new java.awt.Color(0x2a1a00), new java.awt.Color(0xd4a020));
        // Big on a roomy screen, but allowed to shrink so it never clips.
        UIUtil.responsiveSize(card, 860, 320, 300, 200, 860, 320);
        card.setToolTipText(I18n.get("game_words_sub"));
        card.addActionListener(e -> {
            SoundUtil.playClick();
            app.showScreen(MainUI.SCREEN_WORDS);
        });
        return card;
    }

    private JPanel buildTopbar() {
        JPanel bar = UITheme.roundedBar();

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JPanel avatarBox = new JPanel(new BorderLayout());
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(44, 44));
        avatarBox.add(UITheme.avatar(isGuest ? "?" : username), BorderLayout.CENTER);
        left.add(avatarBox);
        if (!isGuest) {
            ApiService.fetchAvatar(username, 44, img -> {
                if (img != null) {
                    JLabel pic = new JLabel(new javax.swing.ImageIcon(img.getScaledInstance(44, 44, java.awt.Image.SCALE_SMOOTH)));
                    avatarBox.removeAll();
                    avatarBox.add(pic, BorderLayout.CENTER);
                    avatarBox.revalidate();
                    avatarBox.repaint();
                }
            }, err -> {});
        }

        JPanel userTextBox = new JPanel();
        userTextBox.setOpaque(false);
        userTextBox.setLayout(new BoxLayout(userTextBox, BoxLayout.Y_AXIS));

        String welcomeText = I18n.get("welcome_user", isGuest ? "Guest" : username);
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(UITheme.fontFor(welcomeText, Font.BOLD, UITheme.FONT_CARD_TITLE - 2));
        welcomeLabel.setForeground(UITheme.TEXT);
        userTextBox.add(welcomeLabel);

        String rankName;
        if (totalPoints >= 600) rankName = I18n.get("rank_legendary");
        else if (totalPoints >= 300) rankName = I18n.get("rank_gold");
        else if (totalPoints >= 150) rankName = I18n.get("rank_silver");
        else if (totalPoints >= 50) rankName = I18n.get("rank_bronze");
        else rankName = I18n.get("rank_novice");

        String rankText = I18n.get("rank_prefix", rankName);
        JLabel rankLabel = new JLabel(rankText);
        rankLabel.setFont(UITheme.fontFor(rankText, Font.PLAIN, 11));
        rankLabel.setForeground(new java.awt.Color(0xb0c4de));
        userTextBox.add(rankLabel);

        left.add(userTextBox);

        // Language toggle button cleanly grouped right beside user profile
        String langLabel = I18n.isKhmer() ? "ភាសាខ្មែរ" : "English";
        JButton langBtn = UITheme.iconPillButton(UITheme.VectorIcon.GLOBE, langLabel, UITheme.GOLD);
        UIUtil.fixedSize(langBtn, 105, UITheme.BTN_H);
        langBtn.setFont(UITheme.fontFor(langLabel, Font.BOLD, 12));
        langBtn.setToolTipText(I18n.get("tip_language"));
        langBtn.addActionListener(e -> {
            SoundUtil.playClick();
            I18n.toggleLanguage();
        });
        left.add(Box.createHorizontalStrut(6));
        left.add(langBtn);

        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        // Encyclopedia Codex Button
        JButton encBtn = UITheme.iconPillButton(UITheme.VectorIcon.SEARCH, I18n.get("encyclopedia_title"), UITheme.TEAL);
        encBtn.setFont(UITheme.fontFor(encBtn.getText(), Font.BOLD, 12));
        UIUtil.fixedSize(encBtn, 115, UITheme.BTN_H);
        encBtn.setToolTipText("Open World Encyclopedia");
        encBtn.addActionListener(e -> {
            SoundUtil.playClick();
            new EncyclopediaModal(app).setVisible(true);
        });
        right.add(encBtn);

        // Settings Button with Gear Icon
        JButton settingsBtn = UITheme.iconPillButton(UITheme.VectorIcon.GEAR, I18n.get("settings"), UITheme.TEAL);
        settingsBtn.setFont(UITheme.fontFor(settingsBtn.getText(), Font.BOLD, 12));
        UIUtil.fixedSize(settingsBtn, 95, UITheme.BTN_H);
        settingsBtn.setToolTipText("Change visual theme, dark/light mode, and sound");
        settingsBtn.addActionListener(e -> {
            SoundUtil.playClick();
            new SettingsModal(app).setVisible(true);
        });
        right.add(settingsBtn);

        // Hall of Fame / Leaderboard Button with Trophy Icon
        JButton leadBtn = UITheme.iconPillButton(UITheme.VectorIcon.TROPHY, I18n.get("hall_of_fame"), UITheme.VIOLET);
        leadBtn.setFont(UITheme.fontFor(leadBtn.getText(), Font.BOLD, 12));
        UIUtil.fixedSize(leadBtn, 110, UITheme.BTN_H);
        leadBtn.setToolTipText("View global top players and podium");
        leadBtn.addActionListener(e -> {
            SoundUtil.playClick();
            new LeaderboardModal(app, app.getGameController()).setVisible(true);
        });
        right.add(leadBtn);

        // Admin Control Panel Button with Shield Icon
        boolean isAdmin = !isGuest && (app.getGameController().isUserAdmin(userId) || app.getGameController().isUserAdmin(username));
        if (isAdmin) {
            JButton adminBtn = UITheme.iconPillButton(UITheme.VectorIcon.SHIELD, I18n.get("admin_panel"), UITheme.CORAL);
            adminBtn.setFont(UITheme.fontFor(adminBtn.getText(), Font.BOLD, 12));
            UIUtil.fixedSize(adminBtn, 110, UITheme.BTN_H);
            adminBtn.setToolTipText("Manage users, adjust points, and toggle roles");
            adminBtn.addActionListener(e -> {
                SoundUtil.playClick();
                new AdminControlModal(app, this, app.getGameController(), username).setVisible(true);
            });
            right.add(adminBtn);
        }

        // Score display box
        JPanel scoreBox = new JPanel();
        scoreBox.setOpaque(false);
        scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));

        String pointsLabelText = I18n.get("total_points");
        JLabel scoreLabel = new JLabel(pointsLabelText, SwingConstants.RIGHT);
        scoreLabel.setFont(UITheme.fontFor(pointsLabelText, Font.BOLD, UITheme.FONT_BADGE));
        scoreLabel.setForeground(UITheme.TEXT_MUTED);
        scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scoreBox.add(scoreLabel);

        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        scoreRow.setOpaque(false);
        scoreRow.add(UITheme.coin(22));

        JLabel scoreValueLocal = new JLabel(String.valueOf(totalPoints), SwingConstants.RIGHT);
        scoreValueLocal.setFont(UITheme.displayFont(Font.BOLD, UITheme.FONT_CARD_TITLE - 2));
        scoreValueLocal.setForeground(UITheme.GOLD);
        scoreRow.add(scoreValueLocal);
        scoreRow.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scoreBox.add(scoreRow);
        this.scoreValue = scoreValueLocal;

        right.add(scoreBox);

        // Logout button with Logout vector icon
        JButton logout = UITheme.iconPillButton(UITheme.VectorIcon.LOGOUT, I18n.get("logout"), UITheme.CORAL);
        logout.setFont(UITheme.fontFor(logout.getText(), Font.BOLD, 12));
        UIUtil.fixedSize(logout, 88, UITheme.BTN_H);
        logout.addActionListener(e -> logout());
        right.add(logout);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void logout() {
        app.showWelcome();
    }

    public void showDashboard() {
        app.showDashboard();
    }
}
