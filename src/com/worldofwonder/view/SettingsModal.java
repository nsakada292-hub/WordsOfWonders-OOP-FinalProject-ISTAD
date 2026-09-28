package com.worldofwonder.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Settings and customization modal dialog.
 * Enables switching themes (Midnight, Deep Ocean, Royal Amethyst, Daylight Crystal),
 * toggling Dark/Light mode, and managing game sound effects.
 */
public class SettingsModal extends JDialog {

    private final JFrame parent;
    private final JLabel titleLabel;
    private final JLabel subLabel;
    private final JLabel themeTitleLabel;
    private final JLabel langTitleLabel;
    private final JLabel audioTitleLabel;
    private final JButton langBtn;
    private final JButton sfxToggleBtn;
    private final JButton testSoundBtn;
    private final JButton closeBtn;
    private final JPanel themeCardsPanel;
    private final Runnable langListener;

    public SettingsModal(JFrame parent) {
        super(parent, com.worldofwonder.util.I18n.get("settings_title"), true);
        this.parent = parent;

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(new Color(13, 27, 42));
        content.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.add(UITheme.vectorIcon(UITheme.VectorIcon.GEAR, 26, UITheme.GOLD));

        titleLabel = UITheme.title(com.worldofwonder.util.I18n.get("settings_title"), 22);
        titleLabel.setForeground(UITheme.GOLD);
        titleRow.add(titleLabel);
        header.add(titleRow);
        header.add(Box.createVerticalStrut(4));

        subLabel = new JLabel(com.worldofwonder.util.I18n.get("settings_sub"));
        subLabel.setFont(UITheme.bodyFont(Font.PLAIN, 13));
        subLabel.setForeground(UITheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(subLabel);

        content.add(header, BorderLayout.NORTH);

        // Body with Theme Selection and Sound Options
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // Theme Mode Section
        themeTitleLabel = new JLabel(com.worldofwonder.util.I18n.get("settings_theme_title"));
        themeTitleLabel.setFont(UITheme.displayFont(Font.BOLD, 15));
        themeTitleLabel.setForeground(UITheme.ICE);
        themeTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(themeTitleLabel);
        body.add(Box.createVerticalStrut(10));

        themeCardsPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        themeCardsPanel.setOpaque(false);
        themeCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Cards may shrink on a small window, but the grid itself stretches to the
        // full body width so it never reads as a sheet pinned to the left.
        UIUtil.responsiveSize(themeCardsPanel, 404, 140, 300, 120, Integer.MAX_VALUE, 400);
        rebuildThemeButtons();
        body.add(themeCardsPanel);
        body.add(Box.createVerticalStrut(18));

        // Language Section
        langTitleLabel = new JLabel(com.worldofwonder.util.I18n.get("settings_lang_title"));
        langTitleLabel.setFont(UITheme.displayFont(Font.BOLD, 15));
        langTitleLabel.setForeground(UITheme.ICE);
        langTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(langTitleLabel);
        body.add(Box.createVerticalStrut(8));

        JPanel langRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        langRow.setOpaque(false);
        langRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String currentLangText = com.worldofwonder.util.I18n.isKhmer() ? "ភាសាខ្មែរ" : "English";
        langBtn = UITheme.iconPillButton(UITheme.VectorIcon.GLOBE, com.worldofwonder.util.I18n.get("lang_active", currentLangText), UITheme.GOLD);
        UIUtil.responsiveSize(langBtn, 200, UITheme.BTN_H_SM, 150, UITheme.BTN_H_SM, 200, UITheme.BTN_H_SM);
        langBtn.addActionListener(e -> {
            SoundUtil.playClick();
            com.worldofwonder.util.I18n.toggleLanguage();
            if (parent != null) {
                parent.repaint();
            }
        });
        langRow.add(langBtn);
        body.add(langRow);
        body.add(Box.createVerticalStrut(18));

        // Sound Settings Section
        audioTitleLabel = new JLabel(com.worldofwonder.util.I18n.get("settings_audio_title"));
        audioTitleLabel.setFont(UITheme.displayFont(Font.BOLD, 15));
        audioTitleLabel.setForeground(UITheme.ICE);
        audioTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(audioTitleLabel);
        body.add(Box.createVerticalStrut(10));

        JPanel audioRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        audioRow.setOpaque(false);
        audioRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        sfxToggleBtn = UITheme.primaryButton(com.worldofwonder.util.I18n.get(SoundUtil.isMuted() ? "sfx_muted" : "sfx_enabled"));
        UIUtil.responsiveSize(sfxToggleBtn, 160, UITheme.BTN_H_SM, 120, UITheme.BTN_H_SM, 160, UITheme.BTN_H_SM);
        sfxToggleBtn.addActionListener(e -> {
            SoundUtil.toggleMute();
            updateSfxButtonState();
            if (!SoundUtil.isMuted()) {
                SoundUtil.playClick();
            }
        });
        audioRow.add(sfxToggleBtn);

        testSoundBtn = UITheme.ghostButton(com.worldofwonder.util.I18n.get("test_sound"), UITheme.TEAL);
        UIUtil.responsiveSize(testSoundBtn, 150, UITheme.BTN_H_SM, 110, UITheme.BTN_H_SM, 150, UITheme.BTN_H_SM);
        testSoundBtn.addActionListener(e -> {
            if (!SoundUtil.isMuted()) {
                SoundUtil.playVictory();
            } else {
                JOptionPane.showMessageDialog(this,
                        com.worldofwonder.util.I18n.get("sound_muted_msg"),
                        com.worldofwonder.util.I18n.get("sound_muted_title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        audioRow.add(testSoundBtn);

        body.add(audioRow);
        // Scroll only if the panel is ever taller than the dialog: on a normal
        // screen no scrollbar appears and the layout is identical.
        content.add(UIUtil.verticalScroller(body), BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        closeBtn = UITheme.glowButton(com.worldofwonder.util.I18n.get("btn_save_close"), UITheme.TEAL, UITheme.VIOLET);
        UIUtil.responsiveSize(closeBtn, 150, UITheme.BTN_H_SM, 120, UITheme.BTN_H_SM, 150, UITheme.BTN_H_SM);
        closeBtn.addActionListener(e -> {
            SoundUtil.playClick();
            dispose();
        });
        footer.add(closeBtn);

        content.add(footer, BorderLayout.SOUTH);
        setContentPane(content);

        // Size to the content first, then centre. Sizing before the content exists
        // leaves spare width on the right, which is what made this read as a
        // right-shifted side sheet instead of a centred dialog.
        UIUtil.centreOnOwner(this, parent, 420, 380);
        setResizable(false);

        // Register language change listener
        this.langListener = this::refreshText;
        com.worldofwonder.util.I18n.addLanguageListener(langListener);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                com.worldofwonder.util.I18n.removeLanguageListener(langListener);
            }
        });
    }

    private void refreshText() {
        setTitle(com.worldofwonder.util.I18n.get("settings_title"));
        titleLabel.setText(com.worldofwonder.util.I18n.get("settings_title"));
        titleLabel.setFont(UITheme.fontFor(titleLabel.getText(), Font.BOLD, 22));

        subLabel.setText(com.worldofwonder.util.I18n.get("settings_sub"));
        subLabel.setFont(UITheme.fontFor(subLabel.getText(), Font.PLAIN, 13));

        themeTitleLabel.setText(com.worldofwonder.util.I18n.get("settings_theme_title"));
        themeTitleLabel.setFont(UITheme.fontFor(themeTitleLabel.getText(), Font.BOLD, 15));

        langTitleLabel.setText(com.worldofwonder.util.I18n.get("settings_lang_title"));
        langTitleLabel.setFont(UITheme.fontFor(langTitleLabel.getText(), Font.BOLD, 15));

        String currentLangText = com.worldofwonder.util.I18n.isKhmer() ? "ភាសាខ្មែរ" : "English";
        langBtn.setText(com.worldofwonder.util.I18n.get("lang_active", currentLangText));

        audioTitleLabel.setText(com.worldofwonder.util.I18n.get("settings_audio_title"));
        audioTitleLabel.setFont(UITheme.fontFor(audioTitleLabel.getText(), Font.BOLD, 15));

        updateSfxButtonState();
        testSoundBtn.setText(com.worldofwonder.util.I18n.get("test_sound"));
        closeBtn.setText(com.worldofwonder.util.I18n.get("btn_save_close"));

        revalidate();
        repaint();
    }

    private void updateSfxButtonState() {
        if (SoundUtil.isMuted()) {
            sfxToggleBtn.setText(com.worldofwonder.util.I18n.get("sfx_muted"));
            sfxToggleBtn.setBackground(UITheme.CORAL_DARK);
        } else {
            sfxToggleBtn.setText(com.worldofwonder.util.I18n.get("sfx_enabled"));
            sfxToggleBtn.setBackground(UITheme.TEAL_DARK);
        }
        sfxToggleBtn.repaint();
    }

    private void rebuildThemeButtons() {
        themeCardsPanel.removeAll();
        UITheme.ThemePalette current = UITheme.getCurrentPalette();

        for (UITheme.ThemePalette palette : UITheme.ThemePalette.values()) {
            boolean isActive = (palette == current);
            JButton btn = createThemeCardButton(palette, isActive);
            themeCardsPanel.add(btn);
        }
        themeCardsPanel.revalidate();
        themeCardsPanel.repaint();
    }

    private JButton createThemeCardButton(UITheme.ThemePalette palette, boolean isActive) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.quality(g2);
                int w = getWidth();
                int h = getHeight();

                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, palette.gradient[0], w, h, palette.gradient[palette.gradient.length - 1]);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 16, 16));

                // Border
                if (isActive) {
                    g2.setColor(UITheme.GOLD);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 16, 16));
                } else {
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 16, 16));
                }

                // Text
                String themeName = getThemeName(palette);
                g2.setFont(UITheme.fontFor(themeName, Font.BOLD, 13));
                g2.setColor(palette.isLight ? new Color(15, 30, 60) : Color.WHITE);
                g2.drawString(themeName, 14, 28);

                // Subtitle / Active indicator
                String sub = isActive ? com.worldofwonder.util.I18n.get("theme_active")
                        : (palette.isLight ? com.worldofwonder.util.I18n.get("theme_light_mode") : com.worldofwonder.util.I18n.get("theme_dark_mode"));
                g2.setFont(UITheme.fontFor(sub, Font.PLAIN, 11));
                if (isActive) {
                    g2.setColor(palette.isLight ? new Color(0, 110, 80) : UITheme.GOLD);
                } else {
                    g2.setColor(palette.isLight ? new Color(70, 90, 120) : UITheme.TEXT_MUTED);
                }
                g2.drawString(sub, 14, 48);

                g2.dispose();
            }
        };

        UIUtil.responsiveSize(btn, 196, 64, 140, 58, 196, 90);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            SoundUtil.playClick();
            UITheme.setPalette(palette);
            rebuildThemeButtons();
            if (parent != null) {
                parent.repaint();
            }
        });

        return btn;
    }

    private String getThemeName(UITheme.ThemePalette palette) {
        if (palette == null) return "";
        switch (palette) {
            case MIDNIGHT:
                return com.worldofwonder.util.I18n.get("theme_midnight");
            case OCEAN:
                return com.worldofwonder.util.I18n.get("theme_ocean");
            case AMETHYST:
                return com.worldofwonder.util.I18n.get("theme_amethyst");
            case DAYLIGHT_LIGHT:
                return com.worldofwonder.util.I18n.get("theme_daylight");
            default:
                return palette.displayName;
        }
    }
}
