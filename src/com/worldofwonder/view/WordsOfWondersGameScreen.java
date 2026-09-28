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
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WordsOfWondersGameScreen extends JPanel {

    private static final String VIEW_DIFF = "diff";
    private static final String VIEW_GAME = "game";
    private static final String VIEW_DONE = "done";

    private static final int PTS_WORD = 20;
    private static final int BONUS_POINTS = 10;
    private static final int HINT_COST = 15;
    private static final int HAMMER_COST = 30;
    private static final int CELL = 34;
    private static final int CG = 4;

    private static final String[][] PUZZLES = {
            {"EARTH", "HEART", "HATER", "TEAR", "HEAR", "RATE", "HARE", "TARE", "HART"},
            {"GARDEN", "DANGER", "RANGED", "GRADE", "RANGE", "GRAND", "READ", "DEAR", "AGED"},
            {"LISTEN", "SILENT", "TINSEL", "INLET", "LENT", "SENT", "SITE", "NEST", "LINE"},
            {"CASTLE", "SCALE", "LACES", "LATE", "SALE", "SEAL", "LACE", "CAST", "ACES"},
            {"PLANET", "PLANT", "LEAPT", "PETAL", "PLEAT", "PLATE", "PLANE", "LANE", "LEAP"},
            {"GUITAR", "GRIT", "GAIT", "TRIG", "RUG", "GUT", "TAG", "RAG", "AIR"},
            {"WONDER", "DROWN", "ROWED", "OWNER", "WORN", "WORD", "NODE", "DREW", "RODE"},
            {"FROZEN", "FROZE", "ZONE", "ZERO", "FORE", "FERN", "FOE", "ONE", "ORE"},
    };

    private static final Set<String> BONUS_DICTIONARY = new HashSet<>(Arrays.asList(
            "ART", "ARE", "ATE", "EAR", "EAT", "ERA", "HAT", "HER", "HIT", "HOT",
            "NOT", "NET", "NOR", "NUT", "OUT", "OUR", "ONE", "ORE", "PET", "PIN",
            "PAN", "POT", "PUT", "RED", "RUN", "RAW", "ROW", "RAG", "RUG", "RAT",
            "SET", "SIT", "SUN", "SEA", "SEE", "SON", "TAR", "TEA", "TIE", "TOE",
            "TOP", "TEN", "TIN", "WAR", "WIN", "WET", "WON", "AIR", "AIM", "AGE",
            "ANT", "ACT", "BAG", "BED", "BIG", "BOX", "BOY", "BUS", "BUT", "CAN",
            "CAR", "CAT", "CUP", "CUT", "DAY", "DOG", "DRY", "END", "EYE", "FAR",
            "FLY", "FOR", "FOX", "FUN", "GAS", "GET", "GOD", "GUN", "GUY", "ICE",
            "ILL", "INK", "JAR", "JAW", "JOB", "JOY", "KEY", "KID", "LAW", "LEG",
            "LET", "LIP", "LOW", "MAN", "MAP", "MAY", "MUD", "NEW", "OIL", "OLD",
            "PAY", "PEN", "PIG", "POP", "RAY", "ROD", "SAD", "SAY", "SIX", "SKY",
            "SPY", "TAX", "TIP", "TOY", "TRY", "TWO", "VAN", "WAY", "WHO", "WHY",
            "YES", "YET", "ZOO", "CARE", "DARE", "DEAL", "DEAR", "FALL", "FAST",
            "FIRE", "FISH", "FOOD", "FOUR", "FREE", "GAME", "GIRL", "GIVE", "GLAD",
            "GOLD", "GOOD", "GROW", "HAIR", "HALF", "HAND", "HARD", "HEAD", "HEAR",
            "HELP", "HIGH", "HOLD", "HOME", "HOPE", "HOUR", "KIND", "KNOW", "LAND",
            "LAST", "LEAD", "LEFT", "LIFE", "LIKE", "LINE", "LIVE", "LONG", "LOOK",
            "LOVE", "MADE", "MAKE", "MANY", "MEET", "MIND", "MOON", "MORE", "MOST",
            "MOVE", "MUCH", "NAME", "NEAR", "NEXT", "NICE", "NIGHT", "NOTE", "OPEN",
            "PART", "PAST", "PLAY", "RAIN", "READ", "REST", "ROAD", "ROCK", "ROOM",
            "ROSE", "SAME", "SAVE", "SEAT", "SEEK", "SEEM", "SEEN", "SEND", "SHIP",
            "SHOW", "SIDE", "SIGN", "SNOW", "SOME", "SONG", "SOON", "STAY", "STEP",
            "SURE", "TAKE", "TALK", "TALL", "TELL", "THEN", "THEY", "TIME", "TRUE",
            "TURN", "WALK", "WANT", "WARM", "WASH", "WIND", "WISH", "WOOD", "WORK",
            "YEAR"
    ));

    private final Dashboard dashboard;
    private final Random rnd = new Random();
    private final Runnable langListener = this::refreshLanguage;

    private String[] data;
    private List<String> targetWords;
    private final Set<String> foundWords = new HashSet<>();
    private final Set<String> bonusWordsFound = new HashSet<>();
    private final Map<Integer, Set<Integer>> hintedMap = new HashMap<>();
    private List<Character> circleLetters;
    private final List<Integer> selection = new ArrayList<>();
    private int points;
    private boolean gameActive;

    private final CardLayout cards;
    private final JPanel content;
    private CrosswordGrid xGrid;
    private LetterCircle lCircle;
    private JLabel statusLbl, ptsLbl, feedLbl, curWordLbl, bonusWordsBadge;
    private UITheme.ProgressBar pBar;
    private JButton hintBtn, hammerBtn;
    private JPanel donePanel;
    private UITheme.GradientTextLabel doneTitle;
    private JLabel doneText;
    private UITheme.Confetti confetti;
    private javax.swing.Timer feedTimer;

    public WordsOfWondersGameScreen(Dashboard dashboard) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        setOpaque(false);

        this.cards = new CardLayout();
        this.content = new JPanel(cards);
        content.setOpaque(false);

        content.add(buildDiffPanel(), VIEW_DIFF);
        content.add(buildGamePanel(), VIEW_GAME);
        donePanel = buildDonePanel();
        content.add(donePanel, VIEW_DONE);
        confetti = new UITheme.Confetti(donePanel);

        this.viewCard = UITheme.card(new BorderLayout());
        viewCard.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.fixedSize(viewCard, 1020, 700);
        this.headerPanel = buildHeader();
        viewCard.add(headerPanel, BorderLayout.NORTH);
        viewCard.add(content, BorderLayout.CENTER);

        JPanel root = UITheme.screenPage(viewCard);
        UITheme.autoScale(root, 1100, 790, 0.85, 1.5);
        add(root, BorderLayout.CENTER);

        cards.show(content, VIEW_DIFF);
        I18n.addLanguageListener(langListener);
    }

    private final JPanel viewCard;
    private JPanel headerPanel;

    /** Called when language changes - refreshes labels in words of wonders screen. */
    private void refreshLanguage() {
        if (headerPanel != null && viewCard != null) {
            viewCard.remove(headerPanel);
            headerPanel = buildHeader();
            viewCard.add(headerPanel, BorderLayout.NORTH);
        }
        if (targetWords != null) {
            updateMeta();
        }
        revalidate();
        repaint();
    }


    private JPanel buildHeader() {
        JButton back = UITheme.backButton(I18n.get("back_to_dashboard"), UITheme.CORAL);
        UIUtil.fixedSize(back, 190, UITheme.BTN_H);
        back.addActionListener(e -> dashboard.showDashboard());
        return UITheme.screenHeader(back, I18n.get("game_words_title"), 30);
    }

    private JPanel buildDiffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(UITheme.sectionTitle(I18n.get("choose_puzzle"), 26));
        heading.add(Box.createVerticalStrut(4));
        JLabel sub = UITheme.subtitle(I18n.get("wow_diff_sub"));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.add(sub);
        panel.add(heading, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 20));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 34, 0, 34));

        buttons.add(diffTile(I18n.get("diff_easy"), 0, UITheme.GREEN));
        buttons.add(diffTile(I18n.get("diff_medium"), 1, UITheme.GOLD));
        buttons.add(diffTile(I18n.get("diff_hard"), 5, UITheme.CORAL));

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    /** The subtitle is derived from the puzzle itself, so the label can never
     *  disagree with the letters the player is actually given. */
    private JButton diffTile(String name, int puzzleIdx, Color accent) {
        String[] puzzle = PUZZLES[puzzleIdx];
        String desc = I18n.get("wow_words_to_find", puzzle[0].length(), puzzle.length);
        UITheme.TileButton tile = new UITheme.TileButton(name, desc, accent);
        tile.setDark(true);
        tile.setSubtitleColor(new Color(0xd0e8f5));
        UIUtil.fixedSize(tile, 580, 100);
        tile.addActionListener(e -> startGame(puzzleIdx));
        return tile;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        metaRow.setOpaque(false);
        statusLbl = UITheme.badge("0 / 9 words", UITheme.TEAL);
        ptsLbl = UITheme.badge("Points: 0", UITheme.GOLD);
        bonusWordsBadge = UITheme.badge("Bonus Jar: 0", UITheme.CORAL);
        metaRow.add(statusLbl);
        metaRow.add(ptsLbl);
        metaRow.add(bonusWordsBadge);
        metaRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(metaRow);

        pBar = new UITheme.ProgressBar();
        pBar.setPreferredSize(new Dimension(500, 16));
        pBar.setMaximumSize(new Dimension(500, 16));
        JPanel barWrap = UIUtil.centered(pBar);
        barWrap.setOpaque(false);
        barWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(barWrap);

        curWordLbl = new JLabel(" ", SwingConstants.CENTER);
        curWordLbl.setFont(UITheme.displayFont(Font.BOLD, 22));
        curWordLbl.setForeground(UITheme.TEAL);
        curWordLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel wordWrap = UIUtil.centered(curWordLbl);
        wordWrap.setOpaque(false);
        wordWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(wordWrap);

        panel.add(meta, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        xGrid = new CrosswordGrid(new ArrayList<>(), foundWords, hintedMap);
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        center.add(xGrid, gbc);

        lCircle = new LetterCircle(new ArrayList<>(), selection);
        gbc.gridx = 1;
        gbc.weightx = 0.58;
        center.add(lCircle, gbc);

        panel.add(center, BorderLayout.CENTER);

        feedLbl = new JLabel(" ", SwingConstants.CENTER);
        feedLbl.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_BODY));
        feedLbl.setForeground(UITheme.TEXT_MUTED);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel feedWrap = UIUtil.centered(feedLbl);
        feedWrap.setOpaque(false);
        feedWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(feedWrap);
        south.add(Box.createVerticalStrut(4));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setOpaque(false);

        hintBtn = UITheme.iconPillButton(UITheme.VectorIcon.LIGHTBULB, I18n.get("wow_hint"), UITheme.GOLD);
        UIUtil.fixedSize(hintBtn, 160, UITheme.BTN_H_SM);
        hintBtn.addActionListener(e -> useHint());
        btnRow.add(hintBtn);

        hammerBtn = UITheme.iconPillButton(UITheme.VectorIcon.SEARCH, I18n.get("wow_reveal"), UITheme.CORAL);
        UIUtil.fixedSize(hammerBtn, 175, UITheme.BTN_H_SM);
        hammerBtn.addActionListener(e -> useHammer());
        btnRow.add(hammerBtn);

        JButton shuffleBtn = UITheme.iconPillButton(UITheme.VectorIcon.SHUFFLE, I18n.get("wow_shuffle"), UITheme.ICE);
        UIUtil.fixedSize(shuffleBtn, 140, UITheme.BTN_H_SM);
        shuffleBtn.addActionListener(e -> shuffleLetters());
        btnRow.add(shuffleBtn);

        JButton clearBtn = UITheme.ghostButton(I18n.get("wow_clear"), UITheme.TEXT_MUTED);
        UIUtil.fixedSize(clearBtn, 110, UITheme.BTN_H_SM);
        clearBtn.addActionListener(e -> clearSelection());
        btnRow.add(clearBtn);

        JButton newBtn = UITheme.secondaryButton(I18n.get("wow_new_puzzle"));
        UIUtil.fixedSize(newBtn, 130, UITheme.BTN_H_SM);
        newBtn.addActionListener(e -> showDiff());
        btnRow.add(newBtn);

        JButton exitBtn = UITheme.iconPillButton(UITheme.VectorIcon.ARROW_LEFT, I18n.get("exit_to_games"), UITheme.CORAL);
        UIUtil.fixedSize(exitBtn, 150, UITheme.BTN_H_SM);
        exitBtn.setToolTipText("Exit back to the main game hub");
        exitBtn.addActionListener(e -> dashboard.showDashboard());
        btnRow.add(exitBtn);

        south.add(btnRow);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildDonePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (confetti != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.paintQuality(g2);
                    confetti.paint(g2);
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        doneTitle = new UITheme.GradientTextLabel(I18n.get("wow_solved_title"), 36, UITheme.GOLD, UITheme.TEAL);
        doneTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(doneTitle);
        center.add(Box.createVerticalStrut(8));

        JLabel sub = UITheme.subtitle(I18n.get("wow_solved_sub_desc"));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(sub);


        doneText = new JLabel(" ", SwingConstants.CENTER);
        doneText.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_CARD_TITLE));
        doneText.setForeground(UITheme.GOLD);
        doneText.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneText.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        center.add(doneText);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, UITheme.GAP_ELEMENT, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        JButton again = UITheme.primaryButton(I18n.get("wow_play_again"));
        UIUtil.fixedSize(again, 210, UITheme.BTN_H);
        again.addActionListener(e -> startGame(rnd.nextInt(PUZZLES.length)));
        buttons.add(again);
        JButton diffBtn = UITheme.secondaryButton(I18n.get("wow_change_diff"));
        UIUtil.fixedSize(diffBtn, 210, UITheme.BTN_H);
        diffBtn.addActionListener(e -> showDiff());
        buttons.add(diffBtn);
        JButton dashBtn = UITheme.iconPillButton(UITheme.VectorIcon.ARROW_LEFT, I18n.get("back_to_dashboard"), UITheme.TEXT_MUTED);
        UIUtil.fixedSize(dashBtn, 210, UITheme.BTN_H);
        dashBtn.addActionListener(e -> dashboard.showDashboard());
        buttons.add(dashBtn);
        center.add(buttons);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void startGame(int puzzleIdx) {
        data = PUZZLES[puzzleIdx];
        targetWords = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            targetWords.add(data[i]);
        }
        foundWords.clear();
        bonusWordsFound.clear();
        hintedMap.clear();
        selection.clear();
        points = 0;
        gameActive = true;
        if (feedTimer != null) {
            feedTimer.stop();
            feedTimer = null;
        }

        circleLetters = new ArrayList<>();
        for (char c : data[0].toCharArray()) {
            circleLetters.add(c);
        }
        Collections.shuffle(circleLetters, rnd);

        xGrid = new CrosswordGrid(targetWords, foundWords, hintedMap);
        lCircle = new LetterCircle(circleLetters, selection);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        center.add(xGrid, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.58;
        center.add(lCircle, gbc);

        JPanel gamePanel = (JPanel) content.getComponent(1);
        java.awt.Component oldCenter = gamePanel.getComponent(1);
        gamePanel.remove(oldCenter);
        gamePanel.add(center, BorderLayout.CENTER);

        updateMeta();
        feedLbl.setText(" ");
        curWordLbl.setText(" ");
        gamePanel.revalidate();
        gamePanel.repaint();

        cards.show(content, VIEW_GAME);
    }

    private void shuffleLetters() {
        if (!gameActive) return;
        Collections.shuffle(circleLetters, rnd);
        SoundUtil.playShuffle();
        clearSelection();
    }

    /** Awards {@code word} the first time the player forms it.
     *  @return true when the word was a new find. */
    private boolean tryAward(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        if (foundWords.contains(word)) {
            if (!canExtend(word)) {
                clearSelection();
            }
            return false;
        }
        for (String target : targetWords) {
            if (target.equals(word)) {
                awardWord(target);
                return true;
            }
        }
        return false;
    }

    /** True when carrying on from {@code word} could still complete a target that
     *  has not been found yet. Drives whether a banked word keeps its letters
     *  selected: "HEAR" must stay live so "HEART" remains reachable, but a word
     *  nothing longer depends on is cleared so the next attempt starts clean. */
    private boolean canExtend(String word) {
        for (String t : targetWords) {
            if (!foundWords.contains(t) && t.length() > word.length() && t.startsWith(word)) {
                return true;
            }
        }
        return false;
    }

    /** Drops every selected letter from {@code idx} onwards. */
    private void backtrackTo(int idx) {
        int at = selection.indexOf(idx);
        if (at < 0) {
            return;
        }
        while (selection.size() > at) {
            selection.remove(selection.size() - 1);
        }
    }

    private void onDragSubmit() {
        if (!gameActive || selection.isEmpty()) return;
        String word = buildWord();
        curWordLbl.setText(word);
        if (tryAward(word)) {
            clearSelection();
            return;
        }

        if (foundWords.contains(word)) {
            SoundUtil.playError();
            showFeed(I18n.get("wow_already_found", word), UITheme.GOLD);
        } else if (BONUS_DICTIONARY.contains(word)) {
            if (bonusWordsFound.contains(word)) {
                SoundUtil.playError();
                showFeed(I18n.get("wow_bonus_already", word), UITheme.GOLD);
            } else {
                bonusWordsFound.add(word);
                points += BONUS_POINTS;
                SoundUtil.playHint();
                updateMeta();
                showFeed(I18n.get("wow_bonus_word", word), UITheme.GOLD);
                dashboard.addGamePoints(BONUS_POINTS);
            }
        } else if (word.length() >= 3) {
            final String candidate = word;
            ApiService.fetchWikiSummary(candidate, summary -> {
                String extract = summary.get("extract");
                if (extract != null && !extract.isEmpty()) {
                    synchronized (BONUS_DICTIONARY) {
                        BONUS_DICTIONARY.add(candidate);
                    }
                    bonusWordsFound.add(candidate);
                    points += BONUS_POINTS;
                    SoundUtil.playHint();
                    updateMeta();
                    showFeed(I18n.get("wow_bonus_new", candidate), UITheme.GOLD);
                    dashboard.addGamePoints(BONUS_POINTS);
                } else {
                    SoundUtil.playError();
                    showFeed(I18n.get("wow_not_in_puzzle", candidate), UITheme.ERROR);
                }
            }, err -> {
                SoundUtil.playError();
                showFeed(I18n.get("wow_not_in_puzzle", candidate), UITheme.ERROR);
            });
        } else {
            SoundUtil.playError();
            showFeed(I18n.get("wow_not_in_puzzle", word), UITheme.ERROR);
        }
        clearSelection();
    }

    private void clearSelection() {
        selection.clear();
        curWordLbl.setText(" ");
        if (lCircle != null) {
            lCircle.setDragPoint(null);
            lCircle.repaint();
        }
    }

    /** Books a find. The selection is only dropped when nothing longer can be built
     *  from it, so a drag or a run of taps through "HEAR" carries on into "HEART". */
    private void awardWord(String word) {
        if (foundWords.contains(word)) {
            return;
        }
        foundWords.add(word);
        points += PTS_WORD;
        SoundUtil.playCorrect();
        updateMeta();
        xGrid.repaint();
        showFeed(I18n.get("wow_found_word", word, PTS_WORD), UITheme.GREEN);

        if (!canExtend(word)) {
            clearSelection();
        }

        if (foundWords.size() >= targetWords.size()) {
            gameActive = false;
            clearSelection();
            SoundUtil.playVictory();
            javax.swing.Timer t = new javax.swing.Timer(800, e -> showDone());
            t.setRepeats(false);
            t.start();
        }
    }

    private void useHint() {
        if (!gameActive || points < HINT_COST) {
            SoundUtil.playError();
            showFeed(I18n.get("wow_not_enough_points"), UITheme.ERROR);
            return;
        }
        List<String> unfound = new ArrayList<>();
        for (String w : targetWords) {
            if (!foundWords.contains(w)) unfound.add(w);
        }
        if (unfound.isEmpty()) return;

        String pick = unfound.get(rnd.nextInt(unfound.size()));
        int wIdx = targetWords.indexOf(pick);
        Set<Integer> hinted = hintedMap.computeIfAbsent(wIdx, k -> new HashSet<>());

        List<Integer> unrevealed = new ArrayList<>();
        for (int i = 0; i < pick.length(); i++) {
            if (!hinted.contains(i)) unrevealed.add(i);
        }
        if (unrevealed.isEmpty()) return;

        hinted.add(unrevealed.get(rnd.nextInt(unrevealed.size())));
        points -= HINT_COST;
        SoundUtil.playHint();
        updateMeta();
        xGrid.repaint();
        showFeed(I18n.get("wow_letter_revealed", (wIdx + 1), HINT_COST), UITheme.GOLD);
    }

    private void useHammer() {
        if (!gameActive || points < HAMMER_COST) {
            SoundUtil.playError();
            showFeed(I18n.get("wow_not_enough_points"), UITheme.ERROR);
            return;
        }
        List<String> unfound = new ArrayList<>();
        for (String w : targetWords) {
            if (!foundWords.contains(w)) unfound.add(w);
        }
        if (unfound.isEmpty()) return;

        String pick = unfound.get(rnd.nextInt(unfound.size()));
        foundWords.add(pick);
        points += PTS_WORD - HAMMER_COST;
        SoundUtil.playHint();
        updateMeta();
        xGrid.repaint();
        showFeed(I18n.get("wow_word_revealed", pick, (PTS_WORD - HAMMER_COST)), UITheme.CORAL);

        if (foundWords.size() >= targetWords.size()) {
            gameActive = false;
            SoundUtil.playVictory();
            javax.swing.Timer t = new javax.swing.Timer(800, e -> showDone());
            t.setRepeats(false);
            t.start();
        }
    }

    private void updateMeta() {
        if (targetWords == null || statusLbl == null) {
            return;
        }
        statusLbl.setText(I18n.get("wow_progress", foundWords.size(), targetWords.size()));
        ptsLbl.setText(I18n.get("wow_score", points));
        if (bonusWordsBadge != null) {
            bonusWordsBadge.setText(I18n.get("wow_bonus_jar", bonusWordsFound.size()));
        }
        pBar.setProgress((double) foundWords.size() / Math.max(1, targetWords.size()));
        hintBtn.setEnabled(gameActive && points >= HINT_COST);
        hammerBtn.setEnabled(gameActive && points >= HAMMER_COST);
    }


    private void showFeed(String text, Color color) {
        feedLbl.setText(text);
        feedLbl.setForeground(color);
        if (feedTimer != null) feedTimer.stop();
        feedTimer = new javax.swing.Timer(2000, e -> {
            feedLbl.setText(" ");
            feedTimer.stop();
        });
        feedTimer.setRepeats(false);
        feedTimer.start();
    }

    private void showDone() {
        int totalTargets = targetWords != null ? targetWords.size() : 0;
        int hintsUsed = hintedMap.values().stream().mapToInt(Set::size).sum();
        String rating = hintsUsed == 0 ? I18n.get("wow_rating_perfect")
                : hintsUsed <= 2 ? I18n.get("wow_rating_great")
                : I18n.get("wow_rating_solved");
        if (doneTitle != null) {
            doneTitle.setText(I18n.get("wow_done_title_prefix", rating));
        }
        doneText.setText("<html><center>" + I18n.get("wow_done_summary", totalTargets) + "<br>"
                + (bonusWordsFound.isEmpty() ? ""
                    : "<span style='color:#ffaa44;'>" + I18n.get("wow_done_bonus", bonusWordsFound.size()) + "</span><br>")
                + "<span style='color:#ffd700;font-size:16px;'>" + I18n.get("wow_done_earned", points) + "</span></center></html>");
        cards.show(content, VIEW_DONE);
        confetti.launch();
        syncToBackend();
    }

    private void showDiff() {
        cards.show(content, VIEW_DIFF);
    }

    private String buildWord() {
        StringBuilder sb = new StringBuilder();
        for (int i : selection) {
            sb.append(circleLetters.get(i));
        }
        return sb.toString();
    }

    private void syncToBackend() {
        dashboard.addGamePoints(points);
    }

    private int nodeAt(Point p, int cw, int ch) {
        Dimension natural = lCircle.getPreferredSize();
        double sx = natural.width > 0 ? cw / (double) natural.width : 1.0;
        double sy = natural.height > 0 ? ch / (double) natural.height : 1.0;
        int lx = sx > 0 ? (int) Math.round(p.x / sx) : p.x;
        int ly = sy > 0 ? (int) Math.round(p.y / sy) : p.y;

        int cx = natural.width / 2;
        int cy = natural.height / 2;
        float radius = Math.min(natural.width, natural.height) * 0.34f;
        int nr = Math.max(22, Math.round(radius * 0.22f));

        for (int i = 0; i < circleLetters.size(); i++) {
            float a = (float) (Math.PI * 2 * i / circleLetters.size() - Math.PI / 2);
            int nx = (int) (cx + radius * Math.cos(a));
            int ny = (int) (cy + radius * Math.sin(a));
            if (Math.hypot(lx - nx, ly - ny) <= nr + 12) {
                return i;
            }
        }
        return -1;
    }

    // ── Crossword Grid ───────────────────────────────────────────────

    private class CrosswordGrid extends JPanel {
        private final List<String> words;
        private final Set<String> found;
        private final Map<Integer, Set<Integer>> hinted;

        CrosswordGrid(List<String> words, Set<String> found, Map<Integer, Set<Integer>> hinted) {
            this.words = words;
            this.found = found;
            this.hinted = hinted;
            setOpaque(false);
            int maxLen = 0;
            for (String w : words) maxLen = Math.max(maxLen, w.length());
            int w = 36 + maxLen * (CELL + CG) + 16;
            int h = words.size() * (CELL + CG) + 16;
            setPreferredSize(new Dimension(Math.max(180, w), Math.max(200, h)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.paintQuality(g2);

            Dimension natural = getPreferredSize();
            double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
            double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
            if (sx > 0 && sy > 0 && (sx != 1.0 || sy != 1.0)) {
                g2.scale(sx, sy);
            }

            int startY = 8;
            Font numFont = UITheme.displayFont(Font.BOLD, 13);
            Font cellFont = UITheme.displayFont(Font.BOLD, 18);
            FontMetrics nfm = g2.getFontMetrics(numFont);
            FontMetrics cfm = g2.getFontMetrics(cellFont);

            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                boolean isFound = found.contains(word);
                Set<Integer> hintSet = hinted.getOrDefault(i, Collections.emptySet());
                int y = startY + i * (CELL + CG);

                g2.setFont(numFont);
                g2.setColor(isFound ? UITheme.GREEN : UITheme.TEXT_MUTED);
                String num = (i + 1) + ".";
                g2.drawString(num, 4, y + (CELL - nfm.getHeight()) / 2 + nfm.getAscent());

                for (int j = 0; j < word.length(); j++) {
                    int cx = 36 + j * (CELL + CG);

                    if (isFound) {
                        g2.setColor(new Color(32, 211, 194, 40));
                    } else if (hintSet.contains(j)) {
                        g2.setColor(new Color(255, 201, 60, 28));
                    } else {
                        g2.setColor(new Color(14, 26, 42, 160));
                    }
                    g2.fillRoundRect(cx, y, CELL, CELL, 8, 8);

                    if (isFound) {
                        g2.setColor(new Color(32, 211, 194, 100));
                    } else if (hintSet.contains(j)) {
                        g2.setColor(new Color(255, 201, 60, 80));
                    } else {
                        g2.setColor(new Color(255, 255, 255, 28));
                    }
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(cx, y, CELL, CELL, 8, 8);

                    if (isFound || hintSet.contains(j)) {
                        String ch = String.valueOf(word.charAt(j));
                        g2.setFont(cellFont);
                        g2.setColor(isFound ? Color.WHITE : new Color(0xffc93c));
                        g2.drawString(ch, cx + (CELL - cfm.stringWidth(ch)) / 2,
                                y + (CELL - cfm.getHeight()) / 2 + cfm.getAscent());
                    }
                }
            }
            g2.dispose();
        }
    }

    // ── Letter Circle ────────────────────────────────────────────────

    private class LetterCircle extends JPanel {
        private List<Character> letters;
        private final List<Integer> selection;
        private int nodeR = 26;
        private Point dragPoint;
        private boolean dragging;
        private boolean moved;

        LetterCircle(List<Character> letters, List<Integer> selection) {
            this.letters = letters;
            this.selection = selection;
            setOpaque(false);
            setPreferredSize(new Dimension(380, 380));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!gameActive) return;
                    int idx = nodeAt(e.getPoint(), getWidth(), getHeight());
                    if (idx >= 0) {
                        if (selection.contains(idx)) {
                            backtrackTo(idx);
                        } else {
                            selection.add(idx);
                        }
                        dragging = true;
                        moved = false;
                        dragPoint = e.getPoint();
                        SoundUtil.playLetterSelect(selection.size() - 1);
                        String word = buildWord();
                        curWordLbl.setText(word);
                        // Tapping letter by letter banks each word the moment it forms,
                        // so the whole game is playable without dragging.
                        tryAward(word);
                        repaint();
                    } else {
                        Dimension natural = getPreferredSize();
                        double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
                        double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
                        int lx = sx > 0 ? (int) Math.round(e.getPoint().x / sx) : e.getPoint().x;
                        int ly = sy > 0 ? (int) Math.round(e.getPoint().y / sy) : e.getPoint().y;
                        int cx = natural.width / 2;
                        int cy = natural.height / 2;
                        float radius = Math.min(natural.width, natural.height) * 0.34f;
                        int cR = Math.max(24, Math.round(radius * 0.20f));
                        if (Math.hypot(lx - cx, ly - cy) <= cR + 4) {
                            shuffleLetters();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!dragging) return;
                    dragging = false;
                    dragPoint = null;
                    if (moved) {
                        moved = false;
                        onDragSubmit();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!dragging || !gameActive) return;
                    moved = true;
                    dragPoint = e.getPoint();
                    int idx = nodeAt(dragPoint, getWidth(), getHeight());
                    if (idx >= 0) {
                        if (selection.size() >= 2 && selection.get(selection.size() - 2) == idx) {
                            // Backtracking
                            selection.remove(selection.size() - 1);
                            SoundUtil.playLetterSelect(selection.size() - 1);
                            curWordLbl.setText(buildWord());
                        } else if (!selection.contains(idx)) {
                            selection.add(idx);
                            SoundUtil.playLetterSelect(selection.size() - 1);
                            String word = buildWord();
                            curWordLbl.setText(word);
                            // Keep dragging: the letters already spell a find, but a
                            // longer target may still be hiding behind it.
                            tryAward(word);
                        }
                    }
                    repaint();
                }
            });
        }

        void setDragPoint(Point p) {
            this.dragPoint = p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.paintQuality(g2);

            Dimension natural = getPreferredSize();
            double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
            double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
            if (sx > 0 && sy > 0 && (sx != 1.0 || sy != 1.0)) {
                g2.scale(sx, sy);
            }

            int w = natural.width;
            int h = natural.height;
            int cx = w / 2;
            int cy = h / 2;
            float radius = Math.min(w, h) * 0.34f;
            nodeR = Math.max(22, Math.round(radius * 0.22f));

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, cy), radius + nodeR + 20,
                    new float[]{0f, 1f},
                    new Color[]{new Color(14, 26, 42, 80), new Color(14, 26, 42, 20)}));
            g2.fillOval(cx - (int) radius - nodeR - 20, cy - (int) radius - nodeR - 20,
                    (int) (radius + nodeR + 20) * 2, (int) (radius + nodeR + 20) * 2);

            g2.setColor(new Color(255, 255, 255, 18));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - (int) radius - nodeR - 10, cy - (int) radius - nodeR - 10,
                    (int) (radius + nodeR + 10) * 2, (int) (radius + nodeR + 10) * 2);

            if (selection.size() > 1) {
                int[] xs = new int[selection.size()];
                int[] ys = new int[selection.size()];
                for (int i = 0; i < selection.size(); i++) {
                    float a = (float) (Math.PI * 2 * selection.get(i) / letters.size() - Math.PI / 2);
                    xs[i] = (int) (cx + radius * Math.cos(a));
                    ys[i] = (int) (cy + radius * Math.sin(a));
                }

                // Broad outer neon glow
                g2.setColor(new Color(32, 211, 194, 55));
                g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(xs, ys, xs.length);

                // Mid vibrant neon
                g2.setColor(new Color(32, 211, 194, 180));
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(xs, ys, xs.length);

                // Bright core laser
                g2.setColor(new Color(240, 255, 255, 240));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(xs, ys, xs.length);
            }

            if (dragging && dragPoint != null && !selection.isEmpty()) {
                Dimension natural2 = getPreferredSize();
                double dsx = natural2.width > 0 ? getWidth() / (double) natural2.width : 1.0;
                double dsy = natural2.height > 0 ? getHeight() / (double) natural2.height : 1.0;
                int dlx = dsx > 0 ? (int) Math.round(dragPoint.x / dsx) : dragPoint.x;
                int dly = dsy > 0 ? (int) Math.round(dragPoint.y / dsy) : dragPoint.y;

                int lastIdx = selection.get(selection.size() - 1);
                float a = (float) (Math.PI * 2 * lastIdx / letters.size() - Math.PI / 2);
                int lnx = (int) (cx + radius * Math.cos(a));
                int lny = (int) (cy + radius * Math.sin(a));

                // Outer dragging halo
                g2.setColor(new Color(32, 211, 194, 60));
                g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(lnx, lny, dlx, dly);

                // Core laser line
                g2.setColor(new Color(240, 255, 255, 230));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(lnx, lny, dlx, dly);
            }

            Set<Integer> selSet = new HashSet<>(selection);
            for (int i = 0; i < letters.size(); i++) {
                float a = (float) (Math.PI * 2 * i / letters.size() - Math.PI / 2);
                int nx = (int) (cx + radius * Math.cos(a));
                int ny = (int) (cy + radius * Math.sin(a));
                boolean selected = selSet.contains(i);

                if (selected) {
                    g2.setColor(new Color(32, 211, 194, 60));
                    g2.fillOval(nx - nodeR - 4, ny - nodeR - 4, (nodeR + 4) * 2, (nodeR + 4) * 2);
                    g2.setColor(UITheme.TEAL);
                } else {
                    g2.setPaint(new LinearGradientPaint(nx, ny - nodeR, nx, ny + nodeR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(26, 50, 80, 220), new Color(14, 26, 42, 240)}));
                }
                g2.fillOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);

                g2.setColor(selected ? new Color(255, 255, 255, 120) : new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);

                g2.setPaint(new LinearGradientPaint(nx - nodeR, ny - nodeR, nx + nodeR, ny,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, selected ? 50 : 22), new Color(255, 255, 255, 0)}));
                g2.fillOval(nx - nodeR + 2, ny - nodeR + 2, nodeR * 2 - 4, nodeR - 2);

                int fontSize = Math.max(16, Math.round(radius * 0.19f));
                g2.setFont(UITheme.displayFont(Font.BOLD, fontSize));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(letters.get(i));
                g2.setColor(Color.WHITE);
                g2.drawString(s, nx - fm.stringWidth(s) / 2, ny + fm.getAscent() / 2 - 1);
            }

            int cR = Math.max(24, Math.round(radius * 0.20f));
            g2.setPaint(new LinearGradientPaint(cx, cy - cR, cx, cy + cR,
                    new float[]{0f, 1f},
                    new Color[]{new Color(30, 55, 85, 200), new Color(18, 34, 54, 220)}));
            g2.fillOval(cx - cR, cy - cR, cR * 2, cR * 2);
            g2.setColor(new Color(32, 211, 194, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - cR, cy - cR, cR * 2, cR * 2);

            int iconSize = Math.max(16, (int) (cR * 1.1f));
            UITheme.drawVectorIcon(g2, UITheme.VectorIcon.SHUFFLE, cx - iconSize / 2, cy - iconSize / 2, iconSize, UITheme.ICE);

            g2.dispose();
        }
    }
}
