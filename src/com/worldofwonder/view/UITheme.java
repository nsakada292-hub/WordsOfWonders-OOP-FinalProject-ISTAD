package com.worldofwonder.view;

import com.worldofwonder.model.*;
import com.worldofwonder.controller.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.CardLayout;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.IntConsumer;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BG_TOP = new Color(0x1b3a5c);
    public static final Color BG_BOTTOM = new Color(0x0d1b2a);
    public static final Color PANEL_BG = new Color(255, 255, 255, 20);
    public static final Color PANEL_BG_HOVER = new Color(255, 255, 255, 34);
    public static final Color PANEL_BORDER = new Color(255, 255, 255, 41);
    public static final Color CARD_BG = new Color(20, 39, 61, 175);
    public static final Color TEXT = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(0xc8d4e6);
    public static final Color ICE = new Color(0xeaf6ff);
    public static final Color TEAL = new Color(0x20d3c2);
    public static final Color TEAL_DARK = new Color(0x0fa896);
    public static final Color GOLD = new Color(0xffc93c);
    public static final Color GOLD_DARK = new Color(0xf5a623);
    public static final Color CORAL = new Color(0xff6b6b);
    public static final Color CORAL_DARK = new Color(0xe64545);
    public static final Color VIOLET = new Color(0x9b5de5);
    public static final Color PINK = new Color(0xf15bb5);
    public static final Color GREEN = new Color(0x3ddc97);
    public static final Color ERROR = new Color(0xff5d5d);
    public static final Color DISABLED_FILL = new Color(255, 255, 255, 22);
    public static final Color FIELD_BG = new Color(255, 255, 255, 23);
    public static final Color FIELD_BORDER = new Color(255, 255, 255, 41);

    /** Radius for the large outer cards (36-40px). */
    public static final int CARD_RADIUS = 40;
    /** Radius for fully-rounded capsule shapes (fields, buttons, pills). */
    public static final int CAPSULE = Integer.MAX_VALUE;

    // ── Typography hierarchy ──────────────────────────────────────────
    public static final int FONT_HERO      = 36;
    public static final int FONT_PAGE_TITLE  = 30;
    public static final int FONT_SECTION     = 22;
    public static final int FONT_CARD_TITLE  = 18;
    public static final int FONT_BODY        = 15;
    public static final int FONT_SMALL       = 13;
    public static final int FONT_BADGE       = 11;
    public static final int FONT_BUTTON      = 15;

    // ── Spacing system ────────────────────────────────────────────────
    public static final int PAD_SCREEN_X = 32;
    public static final int PAD_SCREEN_Y = 28;
    public static final int PAD_CARD_X   = 30;
    public static final int PAD_CARD_Y   = 26;
    public static final int GAP_XL       = 32;
    public static final int GAP_LG       = 24;
    public static final int GAP_SECTION  = 22;
    public static final int GAP_ELEMENT  = 14;
    public static final int GAP_TIGHT    = 8;
    public static final int BTN_H        = 52;
    public static final int BTN_H_SM     = 42;

    private static final String DISPLAY_FAMILY;
    private static final String BODY_FAMILY;
    private static final String EMOJI_FAMILY;
    public static final String KHMER_FAMILY;

    static {
        List<String> available = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        DISPLAY_FAMILY = firstAvailable(available, new String[]{
                "Segoe UI Variable Display", "Segoe UI Semibold", "Segoe UI",
                "Century Gothic", "Trebuchet MS", "Verdana",
                "Baloo 2", "Baloo", "Fredoka", "Quicksand", "Nunito", "Montserrat", "Poppins", "SansSerif"});
        BODY_FAMILY = firstAvailable(available, new String[]{
                "Segoe UI", "Segoe UI Variable Text", "Calibri", "Verdana",
                "Nunito", "Ubuntu", "Noto Sans", "SansSerif"});
        EMOJI_FAMILY = resolveEmojiFamily(available);
        KHMER_FAMILY = firstAvailable(available, new String[]{
                "Noto Sans Khmer UI", "Noto Sans Khmer", "Khmer UI", "Siemreap", "Suwannaphum", "Nokora", "Leelawadee UI", "SansSerif"});
    }

    private static String firstAvailable(List<String> available, String[] candidates) {
        for (String candidate : candidates) {
            for (String family : available) {
                if (family.equalsIgnoreCase(candidate)) {
                    return family;
                }
            }
        }
        return "SansSerif";
    }

    private static String resolveEmojiFamily(List<String> available) {
        for (String family : available) {
            if (family.equalsIgnoreCase("Noto Color Emoji")
                    || family.equalsIgnoreCase("Noto Emoji")
                    || family.equalsIgnoreCase("Apple Color Emoji")
                    || family.equalsIgnoreCase("Segoe UI Emoji")
                    || family.equalsIgnoreCase("EmojiOne Color")
                    || family.equalsIgnoreCase("Symbola")) {
                return family;
            }
        }
        return null;
    }

    public static Font emojiFont(int size) {
        return EMOJI_FAMILY != null ? new Font(EMOJI_FAMILY, Font.PLAIN, size) : displayFont(Font.BOLD, size);
    }

    public static boolean hasKhmer(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= '\u1780' && c <= '\u17FF') || (c >= '\u19E0' && c <= '\u19FF')) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLatin(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                return true;
            }
        }
        return false;
    }

    public static Font displayFont(int style, int size) {
        if (com.worldofwonder.util.I18n.isKhmer()) {
            return new Font(Font.SANS_SERIF, style, size);
        }
        return new Font(DISPLAY_FAMILY, style, size);
    }

    public static Font bodyFont(int style, int size) {
        if (com.worldofwonder.util.I18n.isKhmer()) {
            return new Font(Font.SANS_SERIF, style, size);
        }
        return new Font(BODY_FAMILY, style, size);
    }

    public static Font fontFor(String text, int style, int size) {
        if (text == null || text.isEmpty()) {
            return com.worldofwonder.util.I18n.isKhmer() ? new Font(Font.SANS_SERIF, style, size) : new Font(BODY_FAMILY, style, size);
        }
        boolean khmer = hasKhmer(text);
        boolean latin = hasLatin(text);
        if (khmer && !latin) {
            return new Font(KHMER_FAMILY, style, size);
        }
        if (khmer && latin) {
            return new Font(Font.SANS_SERIF, style, size);
        }
        return com.worldofwonder.util.I18n.isKhmer() ? new Font(Font.SANS_SERIF, style, size) : new Font(DISPLAY_FAMILY, style, size);
    }

    private static final List<float[]> STARS = buildStars();

    private static List<float[]> buildStars() {
        Random rnd = new Random(4242);
        List<float[]> stars = new ArrayList<>();
        for (int i = 0; i < 110; i++) {
            stars.add(new float[]{
                    rnd.nextFloat(),
                    rnd.nextFloat() * 0.85f,
                    0.6f + rnd.nextFloat() * 1.3f,
                    0.25f + rnd.nextFloat() * 0.55f});
        }
        return stars;
    }

    public enum ThemePalette {
        MIDNIGHT("Midnight Nebula (Dark)", false,
                new Color[]{new Color(0x08101d), new Color(0x0c172a), new Color(0x111e38), new Color(0x0a1220)},
                new Color(20, 160, 210, 65), new Color(125, 75, 230, 55), new Color(32, 211, 194, 45)),
        OCEAN("Deep Ocean (Dark)", false,
                new Color[]{new Color(0x041322), new Color(0x08233c), new Color(0x0d3559), new Color(0x051a30)},
                new Color(0, 180, 216, 70), new Color(0, 119, 182, 60), new Color(72, 202, 228, 50)),
        AMETHYST("Royal Amethyst (Dark)", false,
                new Color[]{new Color(0x13091f), new Color(0x210f36), new Color(0x2f164d), new Color(0x170b26)},
                new Color(170, 75, 230, 65), new Color(241, 91, 181, 55), new Color(130, 50, 200, 45)),
        DAYLIGHT_LIGHT("Daylight Crystal (Light)", true,
                new Color[]{new Color(0xeaf2fb), new Color(0xd9e7f7), new Color(0xc9ddf2), new Color(0xdde8f7)},
                new Color(56, 130, 214, 45), new Color(110, 80, 200, 35), new Color(40, 180, 160, 35));

        public final String displayName;
        public final boolean isLight;
        public final Color[] gradient;
        public final Color glow1;
        public final Color glow2;
        public final Color glow3;

        ThemePalette(String displayName, boolean isLight, Color[] gradient, Color glow1, Color glow2, Color glow3) {
            this.displayName = displayName;
            this.isLight = isLight;
            this.gradient = gradient;
            this.glow1 = glow1;
            this.glow2 = glow2;
            this.glow3 = glow3;
        }
    }

    private static ThemePalette currentPalette = ThemePalette.MIDNIGHT;
    private static final List<Runnable> themeChangeListeners = new ArrayList<>();

    public static ThemePalette getCurrentPalette() {
        return currentPalette;
    }

    public static void setPalette(ThemePalette palette) {
        if (palette != null && palette != currentPalette) {
            currentPalette = palette;
            for (Runnable listener : new ArrayList<>(themeChangeListeners)) {
                try {
                    listener.run();
                } catch (Exception ignored) {}
            }
        }
    }

    public static void addThemeListener(Runnable r) {
        if (r != null && !themeChangeListeners.contains(r)) {
            themeChangeListeners.add(r);
        }
    }

    public static void removeThemeListener(Runnable r) {
        themeChangeListeners.remove(r);
    }

    public static void scenic(Graphics2D g2, int w, int h) {
        ThemePalette p = currentPalette;
        g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                new float[]{0f, 0.35f, 0.70f, 1f},
                p.gradient));
        g2.fillRect(0, 0, w, h);

        // Celestial nebula glows
        glow(g2, w, h, 0.15f, 0.12f, 0.62f, p.glow1);
        glow(g2, w, h, 0.85f, 0.15f, 0.55f, p.glow2);
        glow(g2, w, h, 0.50f, 0.40f, 0.45f, p.glow3);
        glow(g2, w, h, 0.25f, 0.85f, 0.52f, new Color(245, 166, 35, p.isLight ? 20 : 30));
        glow(g2, w, h, 0.80f, 0.85f, 0.48f, new Color(155, 93, 229, p.isLight ? 20 : 35));

        g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(255, 255, 255, p.isLight ? 40 : 15),
                        new Color(255, 255, 255, 0),
                        new Color(0, 0, 0, 0)}));
        g2.fillRect(0, 0, w, h);

        if (!p.isLight) {
            for (float[] s : STARS) {
                g2.setColor(new Color(255, 255, 255, (int) (s[3] * 230)));
                g2.fillOval((int) (s[0] * w), (int) (s[1] * h),
                        Math.max(1, Math.round(s[2])), Math.max(1, Math.round(s[2])));
            }
        } else {
            for (float[] s : STARS) {
                g2.setColor(new Color(100, 140, 190, (int) (s[3] * 90)));
                g2.fillOval((int) (s[0] * w), (int) (s[1] * h),
                        Math.max(1, Math.round(s[2])), Math.max(1, Math.round(s[2])));
            }
        }

        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.55f), Math.max(w, h) * 0.9f,
                new float[]{0f, 1f},
                new Color[]{new Color(0, 0, 0, 0), p.isLight ? new Color(120, 150, 190, 40) : new Color(0, 0, 0, 100)}));
        g2.fillRect(0, 0, w, h);
    }

    private static void glow(Graphics2D g2, int w, int h, float cx, float cy, float r, Color color) {
        float radius = r * Math.max(w, h);
        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(cx * w, cy * h), radius,
                new float[]{0f, 1f},
                new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}));
        g2.fillRect(0, 0, w, h);
    }

    private static final Color[] SHADOW_PASS = {
            new Color(0, 0, 0, 8),
            new Color(0, 0, 0, 12),
            new Color(0, 0, 0, 16),
            new Color(0, 0, 0, 20),
            new Color(0, 0, 0, 24),
    };

    public static void softShadow(Graphics2D g2, int x, int y, int w, int h, int radius) {
        for (int i = 0; i < 5; i++) {
            g2.setColor(SHADOW_PASS[i]);
            g2.drawRoundRect(x + i + 1, y + i + 3, w - 2 * (i + 1) - 1, h - 2 * (i + 1) - 1, radius, radius);
        }
    }

    public static JPanel gradientPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            private BufferedImage bg;
            private ThemePalette cachedPalette;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                if (bg == null || bg.getWidth() != w || bg.getHeight() != h || cachedPalette != currentPalette) {
                    cachedPalette = currentPalette;
                    bg = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_RGB);
                    Graphics2D img = bg.createGraphics();
                    quality(img);
                    scenic(img, w, h);
                    img.dispose();
                }
                g.drawImage(bg, 0, 0, null);
            }
        };
        addThemeListener(panel::repaint);
        return panel;
    }

    public static Graphics2D quality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g2;
    }

    /** Lighter quality preset for high-frequency paints (typing, hover, pulse).
     *  Skips expensive render-quality/fractional-metric hints that cause
     *  perceived lag during input and interaction. */
    public static Graphics2D paintQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        return g2;
    }

    /** Fully-rounded capsule radius for the given component height. */
    static int capsule(int height) {
        return Math.max(12, height / 2);
    }

    static Color lerp(Color a, Color b, float t) {
        if (t <= 0) {
            return a;
        }
        if (t >= 1) {
            return b;
        }
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t),
                Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    static final class SmoothHover {
        private final JComponent component;
        private final javax.swing.Timer timer;
        private final Runnable onUpdate;
        private float value;
        private boolean target;

        SmoothHover(JComponent component, Runnable onUpdate) {
            this.component = component;
            this.onUpdate = onUpdate;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        void enter() {
            target = true;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        void exit() {
            target = false;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        float value() {
            return value;
        }

        private void tick() {
            float goal = target ? 1f : 0f;
            value += (goal - value) * 0.24f;
            if (Math.abs(goal - value) < 0.02f) {
                value = goal;
                timer.stop();
            }
            if (component.isShowing()) {
                onUpdate.run();
            }
        }
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            private BufferedImage chrome;

            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                if (chrome == null || chrome.getWidth() != w || chrome.getHeight() != h) {
                    chrome = new BufferedImage(Math.max(1, w), Math.max(1, h),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = chrome.createGraphics();
                    quality(g2);
                    softShadow(g2, 0, 0, w, h, 24);
                    g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                            new float[]{0f, 0.45f, 1f},
                            new Color[]{new Color(255, 255, 255, 38),
                                    new Color(20, 39, 61, 150),
                                    new Color(13, 27, 42, 160)}));
                    g2.fillRoundRect(0, 0, w, h, CARD_RADIUS, CARD_RADIUS);

                    Shape body = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);
                    g2.setClip(body);
                    g2.setPaint(new LinearGradientPaint(w * 0.15f, 0, w * 0.85f, h,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(32, 211, 194, 22),
                                    new Color(255, 255, 255, 0),
                                    new Color(155, 93, 229, 20)}));
                    g2.fillRect(0, 0, w, h);

                    int glowR = Math.round(Math.min(w, h) * 0.9f);
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(-glowR * 0.35f, -glowR * 0.35f), glowR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(32, 211, 194, 70), new Color(32, 211, 194, 0)}));
                    g2.fillRect(0, 0, w, h);
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(w + glowR * 0.35f, h + glowR * 0.35f), glowR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(155, 93, 229, 85), new Color(155, 93, 229, 0)}));
                    g2.fillRect(0, 0, w, h);
                    g2.setClip(null);

                    g2.setColor(new Color(255, 255, 255, 34));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(32, 211, 194, 150),
                                    new Color(255, 255, 255, 60),
                                    new Color(155, 93, 229, 160)}));
                    g2.draw(new RoundRectangle2D.Float(1.4f, 1.4f, w - 3.8f, h - 3.8f, CARD_RADIUS, CARD_RADIUS));
                    g2.setClip(0, 0, w, 4);
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.draw(new RoundRectangle2D.Float(1.2f, 1.2f, w - 2.4f, h - 2.4f, CARD_RADIUS, CARD_RADIUS));
                    g2.setClip(null);
                    g2.dispose();
                }
                g.drawImage(chrome, 0, 0, null);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /** Fully opaque, high-contrast modal dialog card that blocks background text completely. */
    public static JPanel modalCard(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            private BufferedImage chrome;
            private int chromeW = -1;
            private int chromeH = -1;

            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;
                if (chrome == null || chromeW != w || chromeH != h) {
                    chromeW = w;
                    chromeH = h;
                    chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = chrome.createGraphics();
                    quality(g2);
                    softShadow(g2, 0, 0, w, h, 24);
                    // Solid dark slate gradient - 100% opaque, zero bleed-through
                    g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(15, 23, 42), new Color(13, 22, 38), new Color(10, 16, 29)}));
                    g2.fillRoundRect(0, 0, w, h, CARD_RADIUS, CARD_RADIUS);

                    // Crisp high-contrast border
                    g2.setColor(new Color(56, 77, 117));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);

                    // Subtle top edge highlight
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.setClip(0, 0, w, 3);
                    g2.drawRoundRect(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);
                    g2.setClip(null);
                    g2.dispose();
                }
                g.drawImage(chrome, 0, 0, null);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel centerInCard(JComponent child, int width, int height) {
        JPanel holder = new JPanel(new java.awt.GridBagLayout());
        holder.setOpaque(false);
        JPanel card = card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(PAD_SCREEN_Y, PAD_SCREEN_X, PAD_SCREEN_Y, PAD_SCREEN_X));
        UIUtil.flexSize(card, width, height, Math.min(320, width), Integer.MAX_VALUE);
        card.add(child, BorderLayout.CENTER);
        holder.add(card);
        return holder;
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(displayFont(Font.BOLD, size > 0 ? size : FONT_PAGE_TITLE));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(bodyFont(Font.PLAIN, FONT_BODY));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        return new RoundedButton(text, TEAL, TEAL_DARK, Color.WHITE, true);
    }

    public static JButton secondaryButton(String text) {
        return new RoundedButton(text, GOLD, GOLD_DARK, Color.WHITE, true);
    }

    public static JButton dangerButton(String text) {
        return new RoundedButton(text, CORAL, CORAL_DARK, Color.WHITE, true);
    }

    public static JButton accentButton(String text, Color color) {
        return new RoundedButton(text, color, color, Color.WHITE, false);
    }

    public static JButton outlineButton(String text, Color color) {
        RoundedButton button = (RoundedButton) accentButton(text, color);
        button.outline = true;
        return button;
    }

    public static JButton ghostButton(String text, Color color) {
        RoundedButton button = (RoundedButton) accentButton(text, color);
        button.glass = true;
        return button;
    }

    public static <T extends JTextField> T styleField(T field) {
        field.setOpaque(false);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(TEAL);
        field.setSelectedTextColor(Color.WHITE);
        field.setFont(bodyFont(Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(FIELD_BORDER, CAPSULE, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        return field;
    }

    public static JTextField placeholderField(String placeholder) {
        return styleField(new PlaceholderField(placeholder));
    }

    public static JPasswordField placeholderPassword(String placeholder) {
        return styleField(new PlaceholderPasswordField(placeholder));
    }

    public static class CenteringPanel extends JPanel implements javax.swing.Scrollable {
        public CenteringPanel(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 22;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 88;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            Container parent = getParent();
            if (parent instanceof javax.swing.JViewport) {
                return parent.getWidth() > getPreferredSize().width;
            }
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            Container parent = getParent();
            if (parent instanceof javax.swing.JViewport) {
                return parent.getHeight() > getPreferredSize().height;
            }
            return false;
        }
    }

    public static javax.swing.JScrollPane createScrollPane(JComponent content) {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(content);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(22);
        sp.getHorizontalScrollBar().setUnitIncrement(22);
        styleScrollBar(sp.getVerticalScrollBar());
        styleScrollBar(sp.getHorizontalScrollBar());
        return sp;
    }

    public static void styleScrollBar(javax.swing.JScrollBar bar) {
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 10));
        bar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setVisible(false);
                return b;
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Keep track transparent so game art and gradient show through cleanly
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                g2.setColor(isThumbRollover() ? new Color(32, 211, 194, 175) : new Color(255, 255, 255, 75));
                int arc = Math.min(thumbBounds.width, thumbBounds.height);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                        thumbBounds.width - 4, thumbBounds.height - 4, arc, arc);
                g2.dispose();
            }
        });
    }

    /** Transparent scrollable page container that centers the given card while letting a
     *  single shared background layer (owned by MainUI) show through.
     *  Automatically enables smooth vertical & horizontal scrolling when the window or display
     *  resolution is smaller than the card! */
    public static JPanel screenPage(JComponent card) {
        CenteringPanel center = new CenteringPanel(new GridBagLayout());
        center.add(card);

        javax.swing.JScrollPane scroll = createScrollPane(center);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    public static void autoScale(JComponent root, int baseW, int baseH, double minFactor, double maxFactor) {
        for (Component child : root.getComponents()) {
            recordBase(child);
        }
        double[] lastScale = {1.0};
        root.putClientProperty("wow.lastScale", lastScale);
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = root.getWidth();
                int h = root.getHeight();
                if (w <= 0 || h <= 0) {
                    return;
                }
                double scale = Math.min(w / (double) baseW, h / (double) baseH);
                scale = Math.max(minFactor, Math.min(maxFactor, scale));
                if (Math.abs(scale - lastScale[0]) < 0.01) {
                    return;
                }
                lastScale[0] = scale;
                for (Component child : root.getComponents()) {
                    applyScale(child, scale);
                }
                root.revalidate();
                root.repaint();
            }
        });
    }

    public static void rescale(JComponent root) {
        Object o = root.getClientProperty("wow.lastScale");
        if (!(o instanceof double[])) {
            return;
        }
        double scale = ((double[]) o)[0];
        for (Component child : root.getComponents()) {
            applyScale(child, scale);
        }
        root.revalidate();
        root.repaint();
    }

    public static void recordBaseTree(JComponent c) {
        recordBase(c);
    }

    private static void recordBase(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            boolean[] set = new boolean[3];
            set[0] = jc.isPreferredSizeSet();
            set[1] = jc.isMinimumSizeSet();
            set[2] = jc.isMaximumSizeSet();
            Dimension[] dims = new Dimension[3];
            dims[0] = set[0] ? jc.getPreferredSize() : null;
            dims[1] = set[1] ? jc.getMinimumSize() : null;
            dims[2] = set[2] ? jc.getMaximumSize() : null;
            jc.putClientProperty("wow.baseDims", dims);
            jc.putClientProperty("wow.dimsSet", set);
            Font f = jc.getFont();
            if (f != null) {
                jc.putClientProperty("wow.baseFont", f);
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                recordBase(child);
            }
        }
    }

    private static void applyScale(Component c, double scale) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Dimension[] dims = (Dimension[]) jc.getClientProperty("wow.baseDims");
            boolean[] set = (boolean[]) jc.getClientProperty("wow.dimsSet");
            if (dims != null) {
                if (set[0] && dims[0] != null) {
                    jc.setPreferredSize(scaleDim(dims[0], scale));
                }
                if (set[1] && dims[1] != null) {
                    jc.setMinimumSize(scaleDim(dims[1], scale));
                }
                if (set[2] && dims[2] != null) {
                    jc.setMaximumSize(scaleDim(dims[2], scale));
                }
            }
            Font f = (Font) jc.getClientProperty("wow.baseFont");
            if (f != null) {
                jc.setFont(f.deriveFont((float) (f.getSize() * scale)));
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                applyScale(child, scale);
            }
        }
    }

    private static Dimension scaleDim(Dimension d, double scale) {
        if (d == null) {
            return null;
        }
        int w = d.width == Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(d.width * scale);
        int h = d.height == Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(d.height * scale);
        return new Dimension(w, h);
    }

    public static class BackgroundManager {
        private static final Map<String, BufferedImage> CACHE = new java.util.concurrent.ConcurrentHashMap<>();

        /** Game artwork is scoped to active game screens only. Neutral screens
         *  (welcome / dashboard) return null so FloatRoot paints the clean
         *  dark scenic gradient instead of any game art bleeding through. */
        public static BufferedImage getImageForScreen(String screenName) {
            if (screenName == null) {
                return null;
            }
            switch (screenName.toLowerCase().trim()) {
                case "words":
                    return getOrLoadImage("Game4.jpeg");
                default:
                    return null;
            }
        }

        public static BufferedImage getOrLoadImage(String filename) {
            return CACHE.computeIfAbsent(filename, BackgroundManager::loadImage);
        }

        private static BufferedImage loadImage(String filename) {
            String[] candidatePaths = {
                "images/" + filename,
                "assets/images/" + filename,
                "../assets/images/" + filename,
                System.getProperty("user.dir") + "/assets/images/" + filename,
                "backend/images/" + filename,
                "../images/" + filename,
                "../../images/" + filename,
                System.getProperty("user.dir") + "/images/" + filename,
                System.getProperty("user.dir") + "/backend/images/" + filename
            };
            for (String path : candidatePaths) {
                java.io.File file = new java.io.File(path);
                if (file.exists() && file.isFile()) {
                    try {
                        BufferedImage img = javax.imageio.ImageIO.read(file);
                        if (img != null) {
                            return img;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            try {
                java.io.InputStream is = UITheme.class.getResourceAsStream("/images/" + filename);
                if (is == null) is = UITheme.class.getResourceAsStream("/" + filename);
                if (is == null) is = UITheme.class.getClassLoader().getResourceAsStream("images/" + filename);
                if (is != null) {
                    BufferedImage img = javax.imageio.ImageIO.read(is);
                    if (img != null) {
                        return img;
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    public static JPanel animatedRoot(JComponent card) {
        FloatRoot root = new FloatRoot(new GridBagLayout());
        root.add(card);
        return root;
    }

    /** Root panel with the floating-letter animated background, using the
     *  given layout manager. Use for every full-screen page so the letters
     *  stay visible behind the dashboard and all game views. */
    public static FloatRoot animatedRoot(LayoutManager layout) {
        return new FloatRoot(layout);
    }

    public static class FloatRoot extends JPanel {

        private final List<Particle> particles;
        private final javax.swing.Timer timer;
        private final Rectangle oldBounds = new Rectangle();
        private final Rectangle newBounds = new Rectangle();
        private final Rectangle dirty = new Rectangle();
        private BufferedImage background;
        private boolean scattered;
        private long lastTickNanos = System.nanoTime();
        private String currentTheme = "welcome";

        public FloatRoot(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            particles = new ArrayList<>();
            Random rnd = new Random();
            for (int i = 0; i < 42; i++) {
                particles.add(new Particle(rnd));
            }
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    background = null;
                }
            });
            UITheme.addThemeListener(() -> {
                background = null;
                repaint();
            });
            timer = new javax.swing.Timer(16, e -> {
                if (!isShowing()) {
                    return;
                }
                long now = System.nanoTime();
                float dt = Math.min(0.05f, (now - lastTickNanos) / 1_000_000_000f);
                lastTickNanos = now;
                int w = getWidth();
                int h = getHeight();
                if (!scattered && w > 0 && h > 0) {
                    for (Particle p : particles) {
                        p.reset(w, h);
                    }
                    scattered = true;
                }
                boolean first = true;
                for (Particle p : particles) {
                    p.bounds(oldBounds);
                    p.advance(dt, w, h);
                    p.bounds(newBounds);
                    int minX = Math.min(oldBounds.x, newBounds.x);
                    int minY = Math.min(oldBounds.y, newBounds.y);
                    int maxX = Math.max(oldBounds.x + oldBounds.width, newBounds.x + newBounds.width);
                    int maxY = Math.max(oldBounds.y + oldBounds.height, newBounds.y + newBounds.height);
                    if (first) {
                        dirty.setBounds(minX, minY, maxX - minX, maxY - minY);
                        first = false;
                    } else {
                        int dMinX = Math.min(dirty.x, minX);
                        int dMinY = Math.min(dirty.y, minY);
                        int dMaxX = Math.max(dirty.x + dirty.width, maxX);
                        int dMaxY = Math.max(dirty.y + dirty.height, maxY);
                        dirty.setBounds(dMinX, dMinY, dMaxX - dMinX, dMaxY - dMinY);
                    }
                }
                if (!first) {
                    repaint(dirty.x, dirty.y, dirty.width, dirty.height);
                }
            });
            timer.setRepeats(true);
        }

        public void setTheme(String theme) {
            if (theme == null) theme = "dashboard";
            if (theme.equalsIgnoreCase(this.currentTheme)) {
                return;
            }
            this.currentTheme = theme;
            this.background = null;
            repaint();
        }

        public String getTheme() {
            return currentTheme;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        private ThemePalette cachedPalette;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            if (background == null || background.getWidth() != w || background.getHeight() != h || cachedPalette != currentPalette) {
                cachedPalette = currentPalette;
                background = new BufferedImage(Math.max(1, w), Math.max(1, h),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D bg = background.createGraphics();
                quality(bg);
                renderCompositeBackground(bg, w, h, currentTheme);
                bg.dispose();
            }
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            g2.drawImage(background, 0, 0, null);
            for (Particle p : particles) {
                g2.drawImage(p.image, (int) Math.floor(p.x + p.ox), (int) Math.floor(p.y + p.oy), null);
            }
            g2.dispose();
        }

        private void renderCompositeBackground(Graphics2D bg, int w, int h, String theme) {
            ThemePalette tp = currentPalette;
            BufferedImage img = BackgroundManager.getImageForScreen(theme);
            if (img != null) {
                int imgW = img.getWidth();
                int imgH = img.getHeight();
                double scale = Math.max((double) w / imgW, (double) h / imgH);
                int dw = (int) Math.round(imgW * scale);
                int dh = (int) Math.round(imgH * scale);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                bg.drawImage(img, dx, dy, dw, dh, null);

                // Deep contrast overlay adapted to current palette
                Color c1 = tp.isLight ? new Color(220, 235, 250, 160) : new Color(tp.gradient[0].getRed(), tp.gradient[0].getGreen(), tp.gradient[0].getBlue(), 180);
                Color c2 = tp.isLight ? new Color(200, 220, 245, 185) : new Color(tp.gradient[1].getRed(), tp.gradient[1].getGreen(), tp.gradient[1].getBlue(), 200);
                Color c3 = tp.isLight ? new Color(180, 205, 235, 215) : new Color(tp.gradient[3].getRed(), tp.gradient[3].getGreen(), tp.gradient[3].getBlue(), 235);

                bg.setPaint(new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.45f, 1f},
                        new Color[]{c1, c2, c3}));
                bg.fillRect(0, 0, w, h);

                // Subtle radial vignette
                bg.setPaint(new RadialGradientPaint(
                        new Point2D.Float(w * 0.5f, h * 0.5f), Math.max(w, h) * 0.8f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 0, 0, 0), tp.isLight ? new Color(120, 150, 190, 50) : new Color(0, 0, 0, 130)}));
                bg.fillRect(0, 0, w, h);
            } else {
                scenic(bg, w, h);
            }
        }

        static class Particle {
            private final Random rnd;
            private BufferedImage image;
            private int ox;
            private int oy;
            private float baseX;
            private float baseY;
            private float x;
            private float y;
            private float vx;
            private float vy;
            private float perpX;
            private float perpY;
            private float t;
            private float wobblePhase;
            private float wobbleFreq;
            private float wobbleAmp;
            private int size;
            private Color color;

            Particle(Random rnd) {
                this.rnd = rnd;
                reset(1600, 2200);
            }

            private float marginX() {
                return size + (int) wobbleAmp + 16;
            }

            private float marginY() {
                return size + (int) wobbleAmp + 16;
            }

            void advance(float dt, int width, int height) {
                t += dt;
                baseX += vx * dt;
                baseY += vy * dt;
                float marginX = marginX();
                float marginY = marginY();
                float spanX = width + 2f * marginX;
                float spanY = height + 2f * marginY;
                if (baseX < -marginX) {
                    baseX += spanX;
                } else if (baseX > width + marginX) {
                    baseX -= spanX;
                }
                if (baseY < -marginY) {
                    baseY += spanY;
                } else if (baseY > height + marginY) {
                    baseY -= spanY;
                }
                float wobble = (float) (Math.sin(t * wobbleFreq + wobblePhase) * wobbleAmp);
                x = baseX + perpX * wobble;
                y = baseY + perpY * wobble;
            }

            void bounds(Rectangle out) {
                int r = size * 2 + 12;
                out.setBounds((int) Math.floor(x - r / 2f), (int) Math.floor(y - r / 2f), r, r);
            }

            void reset(int width, int height) {
                size = 2 + rnd.nextInt(4);
                wobbleAmp = 4f + rnd.nextFloat() * 10f;
                float mx = marginX();
                baseX = -mx + rnd.nextFloat() * (width + 2f * mx);
                baseY = -mx + rnd.nextFloat() * (height + 2f * mx);
                float angle = rnd.nextFloat() * 6.2831855f;
                float speed = 12f + rnd.nextFloat() * 24f; // gentle ambient celestial drift
                vx = (float) Math.cos(angle) * speed;
                vy = (float) Math.sin(angle) * speed;
                float len = (float) Math.hypot(vx, vy);
                perpX = -vy / len;
                perpY = vx / len;
                t = 0f;
                wobblePhase = rnd.nextFloat() * 6.2831855f;
                wobbleFreq = 0.35f + rnd.nextFloat() * 0.7f;
                Color[] palette = {TEAL, GOLD, ICE, VIOLET, PINK};
                Color base = palette[rnd.nextInt(palette.length)];
                int alpha = 45 + rnd.nextInt(65);
                color = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
                buildImage();
            }

            private void buildImage() {
                int pad = 10;
                int dim = size * 2 + pad * 2;
                image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                quality(g2);
                float cx = dim / 2f;
                float cy = dim / 2f;
                // Soft celestial outer glow
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(cx, cy), size + 6f,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{
                                new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()),
                                new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 2),
                                new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                        }));
                g2.fillOval((int) (cx - size - 6), (int) (cy - size - 6), (size + 6) * 2, (size + 6) * 2);
                // Bright core
                g2.setColor(new Color(255, 255, 255, Math.min(255, color.getAlpha() + 90)));
                g2.fillOval((int) (cx - size / 2f), (int) (cy - size / 2f), Math.max(2, size), Math.max(2, size));
                g2.dispose();
                ox = -dim / 2;
                oy = -dim / 2;
            }
        }
    }
    public static JLabel sectionTitle(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(displayFont(Font.BOLD, size));
        label.setForeground(TEXT);
        return label;
    }

    public static JPanel screenHeader(JButton backButton, String title, int titleSize) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));
        if (backButton != null) {
            header.add(backButton, BorderLayout.WEST);
        }
        JLabel label = title(title, titleSize);
        header.add(label, BorderLayout.CENTER);

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        east.setOpaque(false);

        JButton soundBtn = ghostButton(SoundUtil.isMuted() ? "SFX: OFF" : "SFX: ON", TEAL);
        UIUtil.fixedSize(soundBtn, 95, BTN_H_SM);
        soundBtn.setToolTipText("Toggle Sound Effects");
        soundBtn.addActionListener(e -> {
            SoundUtil.toggleMute();
            soundBtn.setText(SoundUtil.isMuted() ? "SFX: OFF" : "SFX: ON");
            if (!SoundUtil.isMuted()) {
                SoundUtil.playClick();
            }
        });
        east.add(soundBtn);

        header.add(east, BorderLayout.EAST);
        return header;
    }

    public static JPanel roundedBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                int w = getWidth();
                int h = getHeight();
                int r = capsule(h);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(new Color(255, 255, 255, 34));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return bar;
    }

    public static JLabel badge(String text, Color accent) {
        return new Badge(text, accent);
    }

    public static JComponent letterTilesRow(String letters, int size) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        Color[] colors = {TEAL, GOLD, CORAL, VIOLET, PINK, GOLD, TEAL, CORAL};
        for (int i = 0; i < letters.length(); i++) {
            row.add(new LetterTile(letters.charAt(i), colors[i % colors.length], size));
            if (i < letters.length() - 1) {
                row.add(Box.createHorizontalStrut(5));
            }
        }
        return row;
    }

    public static class RoundBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int r = radius == CAPSULE ? capsule(h) : radius;
            g2.drawRoundRect(x, y, w - thickness, h - thickness, r, r);
            g2.dispose();
        }
    }

    public static class StyleField extends JTextField {
        private boolean focused;

        public StyleField() {
            super();
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = capsule(h);
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            if (focused) {
                g2.setColor(new Color(32, 211, 194, 52));
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class StylePasswordField extends JPasswordField {
        private boolean focused;

        public StylePasswordField() {
            super();
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = capsule(h);
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            if (focused) {
                g2.setColor(new Color(32, 211, 194, 52));
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class PlaceholderField extends StyleField {
        private final String placeholder;

        public PlaceholderField(String placeholder) {
            super();
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(184, 199, 221, 130));
                g2.drawString(placeholder, 14, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static class PlaceholderPasswordField extends StylePasswordField {
        private final String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            super();
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (placeholder != null && getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(184, 199, 221, 130));
                g2.drawString(placeholder, 14, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static class Badge extends JLabel {
        private final Color accent;

        public Badge(String text, Color accent) {
            super(text == null ? null : text.toUpperCase());
            this.accent = accent;
            setOpaque(false);
            setFont(displayFont(Font.BOLD, FONT_BADGE));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 13, 5, 13));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = h / 2;
            g2.setColor(alpha(accent, 42));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(alpha(accent, 120));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color alpha(Color color, int a) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
        }
    }

    public static JPanel accentBar() {
        JPanel bar = new JPanel();
        bar.setOpaque(true);
        bar.setBackground(TEAL);
        bar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        return bar;
    }

    public static JLabel chipLabel(String text) {
        JLabel chip = new JLabel(text);
        chip.setFont(displayFont(Font.BOLD, FONT_CARD_TITLE));
        chip.setForeground(ICE);
        chip.setBorder(BorderFactory.createCompoundBorder(
                new PillBorder(PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        return chip;
    }

    public static class PillBorder implements javax.swing.border.Border {
        private final Color color;
        private final int thickness;

        public PillBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - thickness, h - thickness, h, h);
            g2.dispose();
        }
    }

    /* ==================== Neon components ==================== */

    public static final class Anim {
        private final javax.swing.Timer timer;
        private final Runnable onUpdate;
        private float value;
        private float target;
        private float ease = 0.22f;

        public Anim(Runnable onUpdate) {
            this.onUpdate = onUpdate;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        public void setTarget(float target) {
            if (Math.abs(this.target - target) < 0.0001f && !timer.isRunning()) {
                return;
            }
            this.target = target;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        public void setTarget(float target, float ease) {
            this.ease = ease;
            setTarget(target);
        }

        public float value() {
            return value;
        }

        public boolean running() {
            return timer.isRunning();
        }

        private void tick() {
            value += (target - value) * ease;
            if (Math.abs(target - value) < 0.001f) {
                value = target;
                timer.stop();
            }
            onUpdate.run();
        }
    }

    public static JPanel glowCard(LayoutManager layout) {
        return new GlowCard(layout);
    }

    public static class GlowCard extends JPanel {

        private static final Color[] RING = {TEAL, VIOLET, PINK, GOLD, TEAL};
        private final javax.swing.Timer timer;
        private BufferedImage chrome;
        private float phase;

        public GlowCard(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            timer = new javax.swing.Timer(16, e -> {
                phase += 0.045f;
                repaint();
            });
            timer.setRepeats(true);
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        private void buildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            quality(g2);
            int radius = CARD_RADIUS;
            softShadow(g2, 0, 0, w, h, 26);

            g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(26, 50, 80, 218),
                            new Color(16, 32, 52, 228),
                            new Color(9, 20, 34, 240)}));
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            Shape body = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, radius, radius);
            g2.setClip(body);
            g2.setPaint(new LinearGradientPaint(w * 0.12f, 0, w * 0.9f, h,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{new Color(32, 211, 194, 34),
                            new Color(255, 255, 255, 6),
                            new Color(155, 93, 229, 34)}));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRoundRect(3, 3, w - 6, h - 6, radius - 3, radius - 3);

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(radius, radius), radius * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(32, 211, 194, 70), new Color(32, 211, 194, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w - radius, h - radius), radius * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(155, 93, 229, 80), new Color(155, 93, 229, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);

            g2.setClip(0, 0, w, 6);
            g2.setStroke(new BasicStroke(1.8f));
            g2.setColor(new Color(255, 255, 255, 84));
            g2.draw(new RoundRectangle2D.Float(1.1f, 1.1f, w - 2.2f, h - 2.2f, radius, radius));
            g2.setClip(null);
            g2.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }
            if (chrome == null || chrome.getWidth() != w || chrome.getHeight() != h) {
                buildChrome(w, h);
            }
            g2.drawImage(chrome, 0, 0, null);

            int radius = CARD_RADIUS;
            float cx = w / 2f;
            float cy = h / 2f;
            float dx = (float) Math.cos(phase);
            float dy = (float) Math.sin(phase);
            float rad = (float) Math.hypot(w, h) / 2f;
            Point2D p1 = new Point2D.Float(cx - dx * rad, cy - dy * rad);
            Point2D p2 = new Point2D.Float(cx + dx * rad, cy + dy * rad);
            float pulse = 0.5f + 0.5f * (float) Math.sin(phase * 1.6f);

            Shape ring = new RoundRectangle2D.Float(1.2f, 1.2f, w - 2.4f, h - 2.4f, radius - 1, radius - 1);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.28f + 0.15f * pulse));
            g2.setPaint(new LinearGradientPaint(p1, p2, new float[]{0f, 0.25f, 0.5f, 0.75f, 1f}, RING));
            g2.setStroke(new BasicStroke(7f));
            g2.draw(ring);
            g2.setComposite(AlphaComposite.SrcOver.derive(1f));
            g2.setStroke(new BasicStroke(2.4f));
            g2.draw(ring);

            g2.setClip(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, radius, radius));
            float sweep = ((phase * 0.35f * (w + h)) % (w + 220f)) - 110f;
            g2.setPaint(new LinearGradientPaint(sweep - 130f, 0, sweep + 130f, 0,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 0),
                            new Color(255, 255, 255, 26),
                            new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);
            g2.dispose();
        }
    }

    public static JPanel segmentTabs(String[] labels, IntConsumer onSelect) {
        return new SegmentTabs(labels, onSelect);
    }

    public static class SegmentTabs extends JPanel {

        private final JButton[] buttons;
        private final Anim slide = new Anim(() -> repaint());
        private final IntConsumer onSelect;
        private int selected;
        private int hovered = -1;
        private Rectangle from;
        private Rectangle to;
        private boolean pillReady;

        public SegmentTabs(String[] labels, IntConsumer onSelect) {
            this.onSelect = onSelect;
            setOpaque(false);
            setLayout(new GridLayout(1, labels.length, 8, 0));
            buttons = new JButton[labels.length];
            for (int i = 0; i < labels.length; i++) {
                final int index = i;
                JButton b = new JButton(labels[i]) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        paintQuality(g2);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        String text = getText();
                        int tx = (getWidth() - fm.stringWidth(text)) / 2;
                        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                        if (index == SegmentTabs.this.selected) {
                            g2.setColor(Color.WHITE);
                        } else if (index == hovered) {
                            g2.setColor(new Color(0xe8fbff));
                        } else {
                            g2.setColor(TEXT_MUTED);
                        }
                        g2.drawString(text, tx, ty);
                        g2.dispose();
                    }
                };
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
                b.setFocusPainted(false);
                b.setOpaque(false);
                b.setFont(displayFont(Font.BOLD, 16));
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = index;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hovered == index) {
                            hovered = -1;
                            repaint();
                        }
                    }
                });
                b.addActionListener(e -> {
                    SoundUtil.playClick();
                    select(index);
                    onSelect.accept(index);
                });
                buttons[i] = b;
                add(b);
            }
            selected = 0;
        }

        public void setLabels(String[] newLabels) {
            if (newLabels != null) {
                for (int i = 0; i < Math.min(buttons.length, newLabels.length); i++) {
                    buttons[i].setText(newLabels[i]);
                    buttons[i].setFont(fontFor(newLabels[i], Font.BOLD, 16));
                }
                repaint();
            }
        }

        public void select(int index) {
            if (index < 0 || index >= buttons.length) {
                return;
            }
            if (pillReady) {
                from = pillRect();
            }
            selected = index;
            to = buttons[index].getBounds();
            if (!pillReady) {
                if (to.width > 0) {
                    pillReady = true;
                    from = to;
                }
                repaint();
                return;
            }
            slide.setTarget(0f);
            slide.setTarget(1f, 0.2f);
        }

        public int selectedIndex() {
            return selected;
        }

        private Rectangle pillRect() {
            float v = slide.value();
            if (from == null || to == null) {
                return new Rectangle();
            }
            int x = Math.round(from.x + (to.x - from.x) * v);
            int y = Math.round(from.y + (to.y - from.y) * v);
            int w = Math.round(from.width + (to.width - from.width) * v);
            int h = Math.round(from.height + (to.height - from.height) * v);
            return new Rectangle(x, y, w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!pillReady && buttons.length > 0) {
                Rectangle r = buttons[selected].getBounds();
                if (r.width > 0) {
                    from = to = r;
                    pillReady = true;
                }
            }
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }
            g2.setColor(new Color(255, 255, 255, 26));
            g2.fillRoundRect(0, 0, w, h, capsule(h), capsule(h));
            g2.setColor(new Color(255, 255, 255, 38));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, capsule(h), capsule(h));

            Rectangle r = pillRect();
            if (r.width > 0 && r.height > 0) {
                g2.setPaint(new LinearGradientPaint(r.x, r.y, r.x + r.width, r.y + r.height,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{TEAL, VIOLET, PINK}));
                g2.setComposite(AlphaComposite.SrcOver.derive(0.34f));
                g2.fillRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, capsule(r.height), capsule(r.height));
                g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                g2.fillRoundRect(r.x, r.y, r.width, r.height, capsule(r.height), capsule(r.height));
                g2.setPaint(new GradientPaint(0, r.y, new Color(255, 255, 255, 66),
                        0, r.y + r.height / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, Math.max(1, r.height / 2 - 1), capsule(r.height), capsule(r.height));
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawRoundRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3, capsule(r.height), capsule(r.height));
            }
            g2.dispose();
        }
    }

    public static JButton glowButton(String text, Color top, Color bottom) {
        return new GlowButton(text, top, bottom);
    }

    public static class GlowButton extends JButton {

        private final Color top;
        private final Color bottom;
        private final SmoothHover hover;
        private final javax.swing.Timer pulse;
        private float phase;
        private boolean pressed;

        public GlowButton(String text, Color top, Color bottom) {
            super(text);
            this.top = top;
            this.bottom = bottom;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setFont(displayFont(Font.BOLD, 17));
            setBorder(BorderFactory.createEmptyBorder(12, 26, 12, 26));
            pulse = new javax.swing.Timer(16, e -> {
                phase += 0.08f;
                repaint();
            });
            pulse.setRepeats(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            pulse.start();
        }

        @Override
        public void removeNotify() {
            pulse.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int radius = capsule(h);
            float hv = hover.value();

            if (pressed) {
                double s = 0.965;
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(s, s);
                g2.translate(-w / 2.0, -h / 2.0);
            }

            if (!isEnabled()) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            } else {
                float drift = 0.5f + 0.5f * (float) Math.sin(phase);
                Color cTop = lerp(top, lerp(top, bottom, 0.5f), 0.25f * drift);
                Color cBottom = lerp(bottom, lerp(bottom, top, 0.5f), 0.25f * (1f - drift));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.35f + 0.25f * hv));
                g2.setPaint(new LinearGradientPaint(0, 0, 0, h, new float[]{0f, 1f},
                        new Color[]{cTop, cBottom}));
                g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                g2.setComposite(AlphaComposite.SrcOver.derive(1f));

                Color gTop = lerp(cTop, cTop.brighter(), hv);
                Color gBottom = pressed ? cBottom.darker() : cBottom;
                g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{gTop, lerp(gTop, gBottom, 0.5f), gBottom}));
                g2.fillRoundRect(0, 0, w, h, radius, radius);

                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
                g2.setClip(clip);
                g2.setColor(new Color(255, 255, 255, 34 + Math.round(26 * hv)));
                g2.fillRoundRect(2, 2, w - 4, (int) (h * 0.45f), radius, radius);
                if (hv > 0.02f) {
                    float sweep = ((phase * 60f) % (w + 240f)) - 120f;
                    g2.setPaint(new LinearGradientPaint(sweep - 80f, 0, sweep + 80f, 0,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(255, 255, 255, 0),
                                    new Color(255, 255, 255, 120),
                                    new Color(255, 255, 255, 0)}));
                    g2.fillRoundRect(0, 0, w, h, radius, radius);
                }
                g2.setClip(null);

                g2.setPaint(new LinearGradientPaint(0, 0, w, h, new float[]{0f, 1f},
                        new Color[]{cTop.brighter(), cBottom}));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String label = getText();
            int tx = (w - fm.stringWidth(label)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(isEnabled() ? Color.WHITE : new Color(0x8fa2bd));
            g2.drawString(label, tx, ty);
            g2.dispose();
        }
    }

    public static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setFont(displayFont(Font.BOLD, 14));
        button.setForeground(TEAL);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(0x9af2e8));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(TEAL);
            }
        });
        return button;
    }

    public enum VectorIcon {
        NONE,
        USER,
        EMAIL,
        PASSWORD,
        EYE,
        EYE_OFF,
        GLOBE,
        GEAR,
        TROPHY,
        SHIELD,
        SEARCH,
        LIGHTBULB,
        RADAR,
        SHUFFLE,
        CHECK,
        SPEAKER_ON,
        SPEAKER_OFF,
        ARROW_LEFT,
        ARROW_RIGHT,
        PLUS,
        REFRESH,
        LOGOUT
    }

    public static void drawVectorIcon(Graphics2D g2, VectorIcon icon, int x, int y, int size, Color color) {
        if (icon == null || icon == VectorIcon.NONE) return;
        Graphics2D g = (Graphics2D) g2.create();
        paintQuality(g);
        g.setColor(color);
        switch (icon) {
            case USER: {
                int headR = size * 44 / 100;
                int headX = x + (size - headR) / 2;
                int headY = y + size * 4 / 100;
                g.fillOval(headX, headY, headR, headR);
                int shW = size * 82 / 100;
                int shH = size * 54 / 100;
                int shX = x + (size - shW) / 2;
                int shY = y + size * 48 / 100;
                g.fillArc(shX, shY, shW, shH, 0, 180);
                break;
            }
            case EMAIL: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ew = size * 82 / 100;
                int eh = size * 58 / 100;
                int ex = x + (size - ew) / 2;
                int ey = y + (size - eh) / 2;
                g.drawRoundRect(ex, ey, ew, eh, 4, 4);
                int[] vx = {ex + 1, ex + ew / 2, ex + ew - 1};
                int[] vy = {ey + 2, ey + eh * 54 / 100, ey + 2};
                g.drawPolyline(vx, vy, 3);
                break;
            }
            case PASSWORD: {
                int pw = size * 70 / 100;
                int ph = size * 50 / 100;
                int px = x + (size - pw) / 2;
                int py = y + size * 44 / 100;
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int sw = size * 42 / 100;
                int sh = size * 42 / 100;
                int sx = x + (size - sw) / 2;
                int sy = y + size * 8 / 100;
                g.drawArc(sx, sy, sw, sh, 0, 180);
                g.drawLine(sx, sy + sh / 2, sx, py + 2);
                g.drawLine(sx + sw, sy + sh / 2, sx + sw, py + 2);
                g.fillRoundRect(px, py, pw, ph, 6, 6);
                g.setColor(new Color(15, 23, 42));
                int khR = size * 14 / 100;
                g.fillOval(x + (size - khR) / 2, py + ph * 22 / 100, khR, khR);
                g.fillRect(x + size * 47 / 100, py + ph * 34 / 100, Math.max(2, size * 6 / 100), ph * 36 / 100);
                break;
            }
            case EYE: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ew = size * 86 / 100;
                int eh = size * 46 / 100;
                int ex = x + (size - ew) / 2;
                int ey = y + (size - eh) / 2;
                g.drawArc(ex, ey - eh / 2, ew, eh * 2, 215, 110);
                g.drawArc(ex, ey - eh / 2, ew, eh * 2, 35, 110);
                int pR = size * 22 / 100;
                g.fillOval(x + (size - pR) / 2, y + (size - pR) / 2, pR, pR);
                break;
            }
            case EYE_OFF: {
                drawVectorIcon(g, VectorIcon.EYE, x, y, size, color);
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(x + size * 16 / 100, y + size * 84 / 100, x + size * 84 / 100, y + size * 16 / 100);
                break;
            }
            case GLOBE: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = Math.max(2, size * 8 / 100);
                int d = size - 2 * pad;
                g.drawOval(x + pad, y + pad, d, d);
                g.drawLine(x + pad, y + size / 2, x + pad + d, y + size / 2);
                int mw = Math.max(4, d * 48 / 100);
                g.drawOval(x + (size - mw) / 2, y + pad, mw, d);
                g.drawLine(x + size / 2, y + pad, x + size / 2, y + pad + d);
                break;
            }
            case GEAR: {
                int cx = x + size / 2;
                int cy = y + size / 2;
                int rOut = size * 42 / 100;
                int rIn = size * 30 / 100;
                int rHole = size * 14 / 100;
                g.setStroke(new BasicStroke(Math.max(2f, size * 0.16f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(cx - rIn, cy - rIn, rIn * 2, rIn * 2);
                for (int i = 0; i < 6; i++) {
                    double angle = i * Math.PI / 3;
                    int x1 = cx + (int) Math.round((rIn - 1) * Math.cos(angle));
                    int y1 = cy + (int) Math.round((rIn - 1) * Math.sin(angle));
                    int x2 = cx + (int) Math.round(rOut * Math.cos(angle));
                    int y2 = cy + (int) Math.round(rOut * Math.sin(angle));
                    g.drawLine(x1, y1, x2, y2);
                }
                g.fillOval(cx - rHole, cy - rHole, rHole * 2, rHole * 2);
                break;
            }
            case TROPHY: {
                int cx = x + size / 2;
                int cupW = size * 54 / 100;
                int cupH = size * 44 / 100;
                int cupX = cx - cupW / 2;
                int cupY = y + size * 14 / 100;
                // Cup bowl
                g.fillRoundRect(cupX, cupY, cupW, cupH * 70 / 100, 6, 6);
                int[] bx = {cupX, cx, cupX + cupW};
                int[] by = {cupY + cupH * 60 / 100, cupY + cupH, cupY + cupH * 60 / 100};
                g.fillPolygon(bx, by, 3);
                // Handles
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int hW = size * 18 / 100;
                g.drawArc(cupX - hW + 2, cupY + 2, hW * 2, cupH * 55 / 100, 90, 180);
                g.drawArc(cupX + cupW - hW - 2, cupY + 2, hW * 2, cupH * 55 / 100, 270, 180);
                // Stem
                int stemW = Math.max(3, size * 12 / 100);
                int stemH = size * 16 / 100;
                int stemY = cupY + cupH - 2;
                g.fillRect(cx - stemW / 2, stemY, stemW, stemH);
                // Base
                int baseW = size * 62 / 100;
                int baseH = size * 14 / 100;
                g.fillRoundRect(cx - baseW / 2, stemY + stemH - 2, baseW, baseH, 4, 4);
                break;
            }
            case SHIELD: {
                int sw = size * 74 / 100;
                int sh = size * 82 / 100;
                int sx = x + (size - sw) / 2;
                int sy = y + size * 10 / 100;
                java.awt.geom.Path2D.Float path = new java.awt.geom.Path2D.Float();
                path.moveTo(sx, sy);
                path.lineTo(sx + sw, sy);
                path.curveTo(sx + sw, sy + sh * 55 / 100, sx + sw * 60 / 100, sy + sh * 85 / 100, sx + sw / 2, sy + sh);
                path.curveTo(sx + sw * 40 / 100, sy + sh * 85 / 100, sx, sy + sh * 55 / 100, sx, sy);
                path.closePath();
                g.fill(path);
                // Inner highlight star or cross
                g.setColor(new Color(15, 23, 42, 180));
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = sx + sw / 2;
                g.drawLine(cx, sy + sh * 25 / 100, cx, sy + sh * 68 / 100);
                g.drawLine(sx + sw * 28 / 100, sy + sh * 45 / 100, sx + sw * 72 / 100, sy + sh * 45 / 100);
                break;
            }
            case SEARCH: {
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int r = size * 50 / 100;
                int lx = x + size * 12 / 100;
                int ly = y + size * 12 / 100;
                g.drawOval(lx, ly, r, r);
                int hx1 = lx + r * 82 / 100;
                int hy1 = ly + r * 82 / 100;
                int hx2 = x + size * 86 / 100;
                int hy2 = y + size * 86 / 100;
                g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(hx1, hy1, hx2, hy2);
                break;
            }
            case LIGHTBULB: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + size / 2;
                int br = size * 26 / 100;
                int by = y + size * 30 / 100;
                g.drawOval(cx - br, by - br, br * 2, br * 2);
                // base neck
                int nw = size * 28 / 100;
                int ny = by + br - 2;
                int nh = size * 26 / 100;
                g.fillRoundRect(cx - nw / 2, ny, nw, nh, 3, 3);
                // rays
                g.drawLine(cx, y + size * 4 / 100, cx, y + size * 12 / 100);
                g.drawLine(cx - br - 4, by, cx - br, by);
                g.drawLine(cx + br, by, cx + br + 4, by);
                break;
            }
            case RADAR: {
                g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = Math.max(2, size * 8 / 100);
                int d = size - 2 * pad;
                int cx = x + size / 2;
                int cy = y + size / 2;
                g.drawOval(x + pad, y + pad, d, d);
                int d2 = d * 55 / 100;
                g.drawOval(cx - d2 / 2, cy - d2 / 2, d2, d2);
                // Crosshairs
                g.drawLine(cx, y + pad, cx, y + pad + d);
                g.drawLine(x + pad, cy, x + pad + d, cy);
                // Sweep blip line
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int bx = cx + (int) Math.round(d / 2.3 * 0.707);
                int by_ = cy - (int) Math.round(d / 2.3 * 0.707);
                g.drawLine(cx, cy, bx, by_);
                g.fillOval(bx - 2, by_ - 2, 4, 4);
                break;
            }
            case SHUFFLE: {
                // Two smooth curved crossing arrows
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int x1 = x + size * 16 / 100;
                int x2 = x + size * 84 / 100;
                int yTop = y + size * 30 / 100;
                int yBot = y + size * 70 / 100;
                int midX = x + size / 2;

                java.awt.geom.Path2D.Float p1 = new java.awt.geom.Path2D.Float();
                p1.moveTo(x1, yTop);
                p1.curveTo(midX, yTop, midX, yBot, x2, yBot);
                g.draw(p1);
                g.drawLine(x2, yBot, x2 - size * 16 / 100, yBot - size * 12 / 100);
                g.drawLine(x2, yBot, x2 - size * 16 / 100, yBot + size * 12 / 100);

                java.awt.geom.Path2D.Float p2 = new java.awt.geom.Path2D.Float();
                p2.moveTo(x1, yBot);
                p2.curveTo(midX, yBot, midX, yTop, x2, yTop);
                g.draw(p2);
                g.drawLine(x2, yTop, x2 - size * 16 / 100, yTop - size * 12 / 100);
                g.drawLine(x2, yTop, x2 - size * 16 / 100, yTop + size * 12 / 100);
                break;
            }
            case CHECK: {
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] cx_ = {x + size * 20 / 100, x + size * 44 / 100, x + size * 82 / 100};
                int[] cy_ = {y + size * 52 / 100, y + size * 76 / 100, y + size * 26 / 100};
                g.drawPolyline(cx_, cy_, 3);
                break;
            }
            case SPEAKER_ON: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int sx = x + size * 14 / 100;
                int sy = y + size * 34 / 100;
                int sw = size * 18 / 100;
                int sh = size * 32 / 100;
                g.fillRect(sx, sy, sw, sh);
                int[] fx = {sx + sw, x + size * 52 / 100, x + size * 52 / 100, sx + sw};
                int[] fy = {sy, y + size * 18 / 100, y + size * 82 / 100, sy + sh};
                g.fillPolygon(fx, fy, 4);
                g.drawArc(x + size * 36 / 100, y + size * 32 / 100, size * 36 / 100, size * 36 / 100, -50, 100);
                g.drawArc(x + size * 36 / 100, y + size * 20 / 100, size * 54 / 100, size * 60 / 100, -50, 100);
                break;
            }
            case SPEAKER_OFF: {
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int sx = x + size * 14 / 100;
                int sy = y + size * 34 / 100;
                int sw = size * 18 / 100;
                int sh = size * 32 / 100;
                g.fillRect(sx, sy, sw, sh);
                int[] fx = {sx + sw, x + size * 52 / 100, x + size * 52 / 100, sx + sw};
                int[] fy = {sy, y + size * 18 / 100, y + size * 82 / 100, sy + sh};
                g.fillPolygon(fx, fy, 4);
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(x + size * 64 / 100, y + size * 30 / 100, x + size * 86 / 100, y + size * 70 / 100);
                g.drawLine(x + size * 86 / 100, y + size * 30 / 100, x + size * 64 / 100, y + size * 70 / 100);
                break;
            }
            case ARROW_LEFT: {
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int midY = y + size / 2;
                int leftX = x + size * 20 / 100;
                int rightX = x + size * 80 / 100;
                g.drawLine(leftX, midY, rightX, midY);
                g.drawLine(leftX, midY, leftX + size * 26 / 100, midY - size * 26 / 100);
                g.drawLine(leftX, midY, leftX + size * 26 / 100, midY + size * 26 / 100);
                break;
            }
            case ARROW_RIGHT: {
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int midY = y + size / 2;
                int leftX = x + size * 20 / 100;
                int rightX = x + size * 80 / 100;
                g.drawLine(leftX, midY, rightX, midY);
                g.drawLine(rightX, midY, rightX - size * 26 / 100, midY - size * 26 / 100);
                g.drawLine(rightX, midY, rightX - size * 26 / 100, midY + size * 26 / 100);
                break;
            }
            case PLUS: {
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + size / 2;
                int cy = y + size / 2;
                int arm = size * 32 / 100;
                g.drawLine(cx - arm, cy, cx + arm, cy);
                g.drawLine(cx, cy - arm, cx, cy + arm);
                break;
            }
            case REFRESH: {
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int pad = Math.max(3, size * 16 / 100);
                int d = size - 2 * pad;
                g.drawArc(x + pad, y + pad, d, d, 45, 270);
                int ax = x + pad + d * 85 / 100;
                int ay = y + pad + d * 15 / 100;
                g.drawLine(ax, ay, ax + size * 16 / 100, ay);
                g.drawLine(ax, ay, ax, ay - size * 16 / 100);
                break;
            }
            case LOGOUT: {
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int dx = x + size * 16 / 100;
                int dy = y + size * 16 / 100;
                int dw = size * 42 / 100;
                int dh = size * 68 / 100;
                // Door frame
                g.drawLine(dx + dw, dy, dx, dy);
                g.drawLine(dx, dy, dx, dy + dh);
                g.drawLine(dx, dy + dh, dx + dw, dy + dh);
                // Exit arrow
                int ax1 = dx + dw * 45 / 100;
                int ax2 = x + size * 86 / 100;
                int ay = y + size / 2;
                g.drawLine(ax1, ay, ax2, ay);
                g.drawLine(ax2, ay, ax2 - size * 16 / 100, ay - size * 16 / 100);
                g.drawLine(ax2, ay, ax2 - size * 16 / 100, ay + size * 16 / 100);
                break;
            }
        }
        g.dispose();
    }

    public enum FieldIcon {
        NONE,
        USER,
        EMAIL,
        PASSWORD
    }

    public static void drawFieldIcon(Graphics2D g2, FieldIcon icon, int x, int y, int size, Color color) {
        if (icon == null || icon == FieldIcon.NONE) return;
        drawVectorIcon(g2, VectorIcon.valueOf(icon.name()), x, y, size, color);
    }

    public static JComponent vectorIcon(VectorIcon icon, int size, Color color) {
        return new JComponent() {
            {
                setPreferredSize(new Dimension(size, size));
                setMinimumSize(new Dimension(size, size));
                setMaximumSize(new Dimension(size, size));
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                drawVectorIcon(g2, icon, 0, 0, size, color);
                g2.dispose();
            }
        };
    }

    public static JButton iconPillButton(VectorIcon icon, String text, Color accent) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                int w = getWidth();
                int h = getHeight();
                boolean hovered = getModel().isRollover();
                boolean pressed = getModel().isPressed();

                Color bg = pressed ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70)
                        : (hovered ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40)
                                   : new Color(255, 255, 255, 14));
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, h, h);

                g2.setColor(hovered ? accent : new Color(255, 255, 255, 45));
                g2.setStroke(new BasicStroke(hovered ? 1.6f : 1.0f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

                Font font = fontFor(getText(), Font.BOLD, 13);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int iconSize = Math.max(16, h * 38 / 100);
                int textW = fm.stringWidth(getText());
                int gap = (icon != null && icon != VectorIcon.NONE && !getText().isEmpty()) ? 8 : 0;
                int contentW = (icon != null && icon != VectorIcon.NONE ? iconSize : 0) + gap + textW;
                int startX = (w - contentW) / 2;

                if (icon != null && icon != VectorIcon.NONE) {
                    drawVectorIcon(g2, icon, startX, (h - iconSize) / 2, iconSize, hovered ? accent : TEXT);
                    startX += iconSize + gap;
                }

                g2.setColor(hovered ? Color.WHITE : TEXT_MUTED);
                g2.drawString(getText(), startX, (h - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton backButton(String text, Color accent) {
        return iconPillButton(VectorIcon.ARROW_LEFT, text, accent);
    }

    public static PillField pillField(String placeholder, FieldIcon icon) {
        return new PillField(placeholder, icon);
    }

    public static PillField pillField(String placeholder, String icon) {
        return new PillField(placeholder, icon);
    }

    public static PillPasswordField pillPassword(String placeholder, FieldIcon icon) {
        return new PillPasswordField(placeholder, icon);
    }

    public static PillPasswordField pillPassword(String placeholder, String icon) {
        return new PillPasswordField(placeholder, icon);
    }

    /** Static capsule field: fully-rounded chrome, real-life vector icon and
     *  dynamic placeholder. The chrome is cached to an off-screen image for smooth rendering. */
    public static class PillField extends JTextField {
        private static final Color BG = new Color(255, 255, 255, 16);
        private static final Color FILL = new Color(255, 255, 255, 10);
        private static final Color BORDER = new Color(255, 255, 255, 60);
        private static final Color MUTED = new Color(255, 255, 255, 120);

        private String placeholder;
        private final FieldIcon fieldIcon;
        private final String icon;
        private final int pad;
        private boolean focused;
        private boolean hasError = false;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public PillField(String placeholder, FieldIcon fieldIcon) {
            this.placeholder = placeholder;
            this.fieldIcon = fieldIcon;
            this.icon = null;
            this.pad = (fieldIcon != null && fieldIcon != FieldIcon.NONE) ? 48 : 16;
            init();
        }

        public PillField(String placeholder, String icon) {
            this.placeholder = placeholder;
            this.fieldIcon = FieldIcon.NONE;
            this.icon = icon;
            this.pad = icon != null ? 48 : 16;
            init();
        }

        private void init() {
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setSelectionColor(TEAL);
            setSelectedTextColor(Color.WHITE);
            setFont(bodyFont(Font.PLAIN, 18));
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 16));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }

        public void setError(boolean error) {
            if (this.hasError != error) {
                this.hasError = error;
                repaint();
            }
        }

        public boolean hasError() {
            return hasError;
        }

        private void rebuildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            paintQuality(g2);
            int r = capsule(h);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(FILL);
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));

            if (fieldIcon != null && fieldIcon != FieldIcon.NONE) {
                drawFieldIcon(g2, fieldIcon, 14, (h - 22) / 2, 22, MUTED);
            } else if (icon != null) {
                g2.setFont(bodyFont(Font.PLAIN, 18));
                FontMetrics im = g2.getFontMetrics();
                g2.setColor(MUTED);
                g2.drawString(icon, 15, (h - im.getHeight()) / 2 + im.getAscent());
            }

            g2.setClip(2, 2, w - 4, Math.max(1, (h - 4) / 2));
            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            g2.setClip(null);
            g2.dispose();
            chromeW = w;
            chromeH = h;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (w > 0 && h > 0) {
                if (chrome == null || chromeW != w || chromeH != h) {
                    rebuildChrome(w, h);
                }
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                g2.drawImage(chrome, 0, 0, null);
                int r = capsule(h);
                if (placeholder != null && !placeholder.isEmpty() && getText().isEmpty() && !focused) {
                    g2.setFont(fontFor(placeholder, Font.PLAIN, getFont().getSize()));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(hasError ? new Color(252, 165, 165, 180) : MUTED);
                    g2.drawString(placeholder, pad, (h - fm.getHeight()) / 2 + fm.getAscent());
                }

                if (hasError) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(239, 68, 68, 55));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.setColor(new Color(239, 68, 68));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                } else if (focused) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(32, 211, 194, 42));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.setColor(TEAL);
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                } else {
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(BORDER);
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Password variant of PillField with interactive Secret Show/Hide eye toggle. */
    public static class PillPasswordField extends JPasswordField {
        private static final Color BG = new Color(255, 255, 255, 16);
        private static final Color FILL = new Color(255, 255, 255, 10);
        private static final Color BORDER = new Color(255, 255, 255, 60);
        private static final Color MUTED = new Color(255, 255, 255, 120);

        private String placeholder;
        private final FieldIcon fieldIcon;
        private final String icon;
        private final int pad;
        private boolean focused;
        private boolean hasError = false;
        private boolean passwordRevealed = false;
        private boolean eyeHover = false;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public PillPasswordField(String placeholder, FieldIcon fieldIcon) {
            this.placeholder = placeholder;
            this.fieldIcon = fieldIcon;
            this.icon = null;
            this.pad = (fieldIcon != null && fieldIcon != FieldIcon.NONE) ? 48 : 16;
            init();
        }

        public PillPasswordField(String placeholder, String icon) {
            this.placeholder = placeholder;
            this.fieldIcon = FieldIcon.NONE;
            this.icon = icon;
            this.pad = icon != null ? 48 : 16;
            init();
        }

        private void init() {
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setSelectionColor(TEAL);
            setSelectedTextColor(Color.WHITE);
            setFont(bodyFont(Font.PLAIN, 18));
            setEchoChar('\u2022');
            // Right padding 44px reserves space for the interactive eye button
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 44));

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    boolean over = isOverEye(e.getX(), e.getY());
                    if (over != eyeHover) {
                        eyeHover = over;
                        setCursor(over ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                        setToolTipText(over ? com.worldofwonder.util.I18n.get(passwordRevealed ? "tip_hide_password" : "tip_show_password") : null);
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if (eyeHover) {
                        eyeHover = false;
                        setCursor(Cursor.getDefaultCursor());
                        setToolTipText(null);
                        repaint();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isOverEye(e.getX(), e.getY())) {
                        setPasswordRevealed(!passwordRevealed);
                        setToolTipText(com.worldofwonder.util.I18n.get(passwordRevealed ? "tip_hide_password" : "tip_show_password"));
                    }
                }
            });
        }

        private boolean isOverEye(int mx, int my) {
            int ex = getWidth() - 38;
            int ey = (getHeight() - 24) / 2;
            return mx >= ex - 6 && mx <= ex + 28 && my >= ey - 4 && my <= ey + 28;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }

        public void setError(boolean error) {
            if (this.hasError != error) {
                this.hasError = error;
                repaint();
            }
        }

        public boolean hasError() {
            return hasError;
        }

        public boolean isPasswordRevealed() {
            return passwordRevealed;
        }

        public void setPasswordRevealed(boolean revealed) {
            this.passwordRevealed = revealed;
            setEchoChar(revealed ? (char) 0 : '\u2022');
            repaint();
        }

        private void rebuildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            paintQuality(g2);
            int r = capsule(h);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(FILL);
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));

            if (fieldIcon != null && fieldIcon != FieldIcon.NONE) {
                drawFieldIcon(g2, fieldIcon, 14, (h - 22) / 2, 22, MUTED);
            } else if (icon != null) {
                g2.setFont(bodyFont(Font.PLAIN, 18));
                FontMetrics im = g2.getFontMetrics();
                g2.setColor(MUTED);
                g2.drawString(icon, 15, (h - im.getHeight()) / 2 + im.getAscent());
            }

            g2.setClip(2, 2, w - 4, Math.max(1, (h - 4) / 2));
            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            g2.setClip(null);
            g2.dispose();
            chromeW = w;
            chromeH = h;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (w > 0 && h > 0) {
                if (chrome == null || chromeW != w || chromeH != h) {
                    rebuildChrome(w, h);
                }
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                g2.drawImage(chrome, 0, 0, null);
                int r = capsule(h);
                if (placeholder != null && !placeholder.isEmpty() && getPassword().length == 0 && !focused) {
                    g2.setFont(fontFor(placeholder, Font.PLAIN, getFont().getSize()));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(hasError ? new Color(252, 165, 165, 180) : MUTED);
                    g2.drawString(placeholder, pad, (h - fm.getHeight()) / 2 + fm.getAscent());
                }

                // Render interactive Show/Hide secret password eye icon on the right
                int eyeX = w - 36;
                int eyeY = (h - 16) / 2;
                Color eyeColor = eyeHover ? TEAL : (focused ? ICE : MUTED);
                g2.setColor(eyeColor);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                java.awt.geom.Path2D eyePath = new java.awt.geom.Path2D.Float();
                eyePath.moveTo(eyeX, eyeY + 8);
                eyePath.quadTo(eyeX + 10, eyeY - 2, eyeX + 20, eyeY + 8);
                eyePath.quadTo(eyeX + 10, eyeY + 18, eyeX, eyeY + 8);
                g2.draw(eyePath);

                // Pupil
                g2.fillOval(eyeX + 7, eyeY + 5, 6, 6);

                // If secret/masked, draw sleek diagonal slash indicating hidden password
                if (!passwordRevealed) {
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(eyeHover ? CORAL : new Color(248, 113, 113, 210));
                    g2.drawLine(eyeX, eyeY + 16, eyeX + 20, eyeY);
                }

                if (hasError) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(239, 68, 68, 55));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.setColor(new Color(239, 68, 68));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                } else if (focused) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(32, 211, 194, 42));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.setColor(TEAL);
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                } else {
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(BORDER);
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Container that slides and fades its children in. */
    public static class SlidePanel extends JPanel {
        private final Anim slide = new Anim(() -> repaint());

        public SlidePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        public void play() {
            slide.setTarget(0f);
            slide.setTarget(1f, 0.2f);
        }

        @Override
        protected void paintChildren(Graphics g) {
            if (slide.running()) {
                float v = slide.value();
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, v))));
                g2.translate(0, (1f - v) * 26f);
                super.paintChildren(g2);
                g2.dispose();
            } else {
                super.paintChildren(g);
            }
        }
    }

    public static JPanel fadeCards(JComponent[] cards) {
        return new FadeCards(cards);
    }

    public static class FadeCards extends JPanel {

        private static final Color COVER_TOP = new Color(22, 43, 70, 212);
        private static final Color COVER_MID = new Color(15, 30, 48, 224);
        private static final Color COVER_BOTTOM = new Color(9, 20, 34, 238);

        private final CardLayout cards;
        private final JPanel deck;
        private final Anim fade = new Anim(() -> repaint());
        private int next;
        private int phase;
        private boolean active;

        public FadeCards(JComponent[] cards) {
            setOpaque(false);
            setLayout(new BorderLayout());
            this.cards = new CardLayout();
            this.deck = new JPanel(this.cards);
            deck.setOpaque(false);
            for (int i = 0; i < cards.length; i++) {
                deck.add(cards[i], "c" + i);
            }
            add(deck, BorderLayout.CENTER);
        }

        public void show(int index) {
            if (active || index < 0 || index >= deck.getComponentCount()) {
                return;
            }
            next = index;
            phase = 0;
            active = true;
            fade.setTarget(0f);
            fade.setTarget(1f, 0.2f);
        }

        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (!active) {
                return;
            }
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            float p = fade.value();
            float alpha = p < 0.5f ? p / 0.5f : (1f - p) / 0.5f;
            if (alpha > 0.002f) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alpha))));
                g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.55f, 1f},
                        new Color[]{COVER_TOP, COVER_MID, COVER_BOTTOM}));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
            if (phase == 0 && p >= 0.5f) {
                cards.show(deck, "c" + next);
                phase = 1;
            }
            if (p >= 0.999f) {
                active = false;
            }
        }
    }

    /* ==================== Decorative components ==================== */

    public static JComponent logoRow() {
        return logoRow(42, 24);
    }

    public static JComponent logoRow(int tileSize, int textSize) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        char[] letters = {'W', 'o', 'r', 'l', 'd'};
        Color[] colors = {TEAL, CORAL, GOLD, VIOLET, PINK};
        for (int i = 0; i < letters.length; i++) {
            row.add(new LetterTile(letters[i], colors[i % colors.length], tileSize));
            if (i < letters.length - 1) {
                row.add(Box.createHorizontalStrut(6));
            }
        }
        row.add(Box.createHorizontalStrut(16));

        JComponent divider = dividerLine();
        divider.setPreferredSize(new Dimension(2, tileSize));
        divider.setMaximumSize(new Dimension(2, tileSize));
        row.add(divider);
        row.add(Box.createHorizontalStrut(12));

        JLabel sub = new JLabel("of Wonder");
        sub.setFont(displayFont(Font.BOLD, textSize));
        sub.setForeground(ICE);
        row.add(sub);

        return row;
    }

    public static JPanel tabBar() {
        JPanel bar = new JPanel(new GridLayout(1, 2, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                int r = capsule(getHeight());
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
                g2.setColor(new Color(255, 255, 255, 38));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, r, r);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        return bar;
    }

    public static JPanel divider(String text) {
        return divider(text, 11);
    }

    public static JPanel divider(String text, int fontSize) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(dividerLine());
        row.add(Box.createHorizontalStrut(10));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        label.setForeground(TEXT_MUTED);
        row.add(label);
        row.add(Box.createHorizontalStrut(10));
        row.add(dividerLine());
        return row;
    }

    private static JComponent dividerLine() {
        return new JComponent() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(10, 1));
                setMinimumSize(new Dimension(8, 1));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 36));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
    }

    public static JComponent avatar(String initial) {
        JComponent avatar = new JComponent() {
            {
                setPreferredSize(new Dimension(44, 44));
                setMaximumSize(new Dimension(44, 44));
                setMinimumSize(new Dimension(44, 44));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setPaint(new GradientPaint(0, 0, CORAL, getWidth(), getHeight(), PINK));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String s = initial == null || initial.isEmpty() ? "?" : initial.substring(0, 1).toUpperCase();
                int x = (getWidth() - fm.stringWidth(s)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        return avatar;
    }

    public static class RoundedButton extends JButton {

        private Color base;
        private Color fg;
        private Color top;
        private Color bottom;
        private boolean outline;
        private boolean glass;
        private boolean gradient;
        private boolean pressed;
        private final SmoothHover hover;

        RoundedButton(String text, Color base, Color fg) {
            this(text, base, base, fg, false);
        }

        RoundedButton(String text, Color top, Color bottom, Color fg, boolean gradient) {
            super(text);
            this.top = top;
            this.bottom = bottom;
            this.base = top;
            this.fg = fg;
            this.gradient = gradient;
            this.hover = new SmoothHover(this, this::repaint);
            setFont(displayFont(Font.BOLD, 15));
            setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setPalette(Color base, Color fg, boolean outline) {
            this.base = base;
            this.fg = fg;
            this.outline = outline;
            this.glass = false;
            this.gradient = false;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();
            int radius = capsule(h);

            if (pressed) {
                double s = 0.965;
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(s, s);
                g2.translate(-w / 2.0, -h / 2.0);
            }

            if (!isEnabled()) {
                g2.setColor(DISABLED_FILL);
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(new Color(0x8fa2bd));
            } else if (gradient) {
                if (hv > 0.02f) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.30f + 0.35f * hv));
                    g2.setPaint(new GradientPaint(0, 0, lerp(top, top.brighter(), hv),
                            h, h, pressed ? bottom.darker() : bottom));
                    g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                    g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                }
                g2.setPaint(new GradientPaint(0, 0, lerp(top, top.brighter(), hv),
                        pressed ? 0 : h, h, pressed ? bottom.darker() : bottom));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 60), 0, h / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(0, 0, w, h / 2, radius, radius);
                g2.setColor(fg);
            } else if (glass) {
                g2.setColor(pressed
                        ? new Color(255, 255, 255, 38)
                        : new Color(255, 255, 255, 22 + Math.round(10 * hv)));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(lerp(new Color(255, 255, 255, 72), base, hv));
                g2.setStroke(new BasicStroke(1.4f + 0.6f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(lerp(fg, base, hv));
            } else if (outline) {
                g2.setColor(lerp(new Color(0, 0, 0, 0), alpha(base, 40), hv));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(lerp(base, base.brighter(), hv));
                g2.setStroke(new BasicStroke(2f + 0.5f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(fg);
            } else {
                if (hv > 0.02f) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.35f + 0.35f * hv));
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(w / 2f, h / 2f), w * 0.7f,
                            new float[]{0f, 1f},
                            new Color[]{base, base.darker()}));
                    g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                    g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                }
                g2.setColor(pressed ? base.darker() : lerp(base, base.brighter(), hv));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 50), 0, h / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(0, 0, w, h / 2, radius, radius);
                g2.setColor(fg);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        private static Color alpha(Color color, int a) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
        }
    }

    public static class TileButton extends JButton {

        private final String title;
        private final String subtitle;
        private final String icon;
        private final Color accent;
        private Color subtitleColor = TEXT_MUTED;
        private boolean dark;
        private final SmoothHover hover;
        private boolean pressed;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;
        private int chromeRadius = -1;
        private boolean chromeDark;

        public TileButton(String title, String subtitle, Color accent) {
            this(title, subtitle, accent, null);
        }

        public TileButton(String title, String subtitle, Color accent, String icon) {
            super();
            this.title = title;
            this.subtitle = subtitle;
            this.accent = accent;
            this.icon = icon;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setSubtitleColor(Color color) {
            this.subtitleColor = color;
            repaint();
        }

        public void setDark(boolean dark) {
            this.dark = dark;
            chromeW = -1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();

            int radius = capsule(h);
            if (chrome == null || chromeW != w || chromeH != h || chromeRadius != radius || chromeDark != dark) {
                chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                Graphics2D cg = chrome.createGraphics();
                quality(cg);
                softShadow(cg, 0, 2, w, h - 2, Math.min(24, radius));
                if (dark) {
                    cg.setColor(new Color(14, 26, 42, 160));
                } else {
                    cg.setColor(PANEL_BG);
                }
                cg.fillRoundRect(0, 0, w, h, radius, radius);
                if (dark) {
                    cg.setColor(new Color(255, 255, 255, 28));
                } else {
                    cg.setColor(PANEL_BORDER);
                }
                cg.setStroke(new BasicStroke(1.4f));
                cg.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                cg.dispose();
                chromeW = w;
                chromeH = h;
                chromeRadius = radius;
                chromeDark = dark;
            }
            g2.drawImage(chrome, 0, 0, null);

            if (!pressed && hv > 0.02f) {
                if (dark) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                            Math.round(38 * hv)));
                } else {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                            Math.round(26 * hv)));
                }
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.round(170 * hv)));
                g2.setStroke(new BasicStroke(1.4f + 0.8f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.round(55 * hv)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(3, 3, w - 7, h - 7, Math.max(1, radius - 2), Math.max(1, radius - 2));
            } else if (pressed) {
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            int lift = Math.round(3f * hv);
            int hasIcon = icon != null && !icon.isEmpty() ? 1 : 0;
            int tf = Math.max(17, (int) Math.round(h * 0.17));
            int sf = Math.max(13, (int) Math.round(h * 0.13));
            int iconSize = Math.max(26, (int) Math.round(h * 0.32));
            Font titleFont = fontFor(title, Font.BOLD, tf);
            Font subFont = fontFor(subtitle, Font.PLAIN, sf);
            FontMetrics tm = g2.getFontMetrics(titleFont);
            FontMetrics sm = g2.getFontMetrics(subFont);
            FontMetrics im = g2.getFontMetrics(displayFont(Font.BOLD, iconSize));

            int iconH = hasIcon == 1 ? iconSize + 4 : 0;
            int availSubW = w - 32;
            boolean twoLineSub = h >= 125 && subtitle != null && !subtitle.isEmpty() && sm.stringWidth(subtitle) > availSubW;
            int subHeight = subtitle == null || subtitle.isEmpty() ? 0 : (twoLineSub ? sm.getHeight() * 2 + 5 : sm.getHeight() + 6);
            int total = (hasIcon == 1 ? im.getHeight() + 4 : 0) + tm.getHeight() + subHeight;
            int startY = (h - total) / 2 - lift;

            if (hasIcon == 1) {
                int iy = startY + im.getAscent();
                g2.setFont(displayFont(Font.BOLD, iconSize));
                int ix = (w - im.stringWidth(icon)) / 2;
                g2.drawString(icon, ix, iy);
                startY += im.getHeight() + 4;
            }

            int titleY = startY + tm.getAscent();
            g2.setFont(titleFont);
            g2.setColor(isEnabled() ? TEXT : new Color(0x8fa2bd));
            g2.drawString(title, (w - tm.stringWidth(title)) / 2, titleY);

            if (subHeight > 0) {
                g2.setFont(subFont);
                g2.setColor(isEnabled() ? subtitleColor : new Color(0x8fa2bd));
                if (twoLineSub) {
                    java.util.List<String> lines = new java.util.ArrayList<>();
                    String[] words = subtitle.contains(" ") ? subtitle.split(" ") : null;
                    if (words != null && words.length > 1) {
                        StringBuilder line1 = new StringBuilder();
                        int idx = 0;
                        while (idx < words.length && sm.stringWidth(line1 + (line1.length() == 0 ? "" : " ") + words[idx]) <= availSubW) {
                            if (line1.length() > 0) line1.append(" ");
                            line1.append(words[idx++]);
                        }
                        if (line1.length() == 0 && idx < words.length) {
                            line1.append(words[idx++]);
                        }
                        lines.add(line1.toString());
                        StringBuilder line2 = new StringBuilder();
                        while (idx < words.length) {
                            if (line2.length() > 0) line2.append(" ");
                            line2.append(words[idx++]);
                        }
                        String l2 = line2.toString();
                        if (sm.stringWidth(l2) > availSubW) {
                            while (l2.length() > 1 && sm.stringWidth(l2 + "...") > availSubW) {
                                l2 = l2.substring(0, l2.length() - 1);
                            }
                            l2 += "...";
                        }
                        if (!l2.isEmpty()) lines.add(l2);
                    } else {
                        int cut = subtitle.length();
                        while (cut > 1 && sm.stringWidth(subtitle.substring(0, cut)) > availSubW) {
                            cut--;
                        }
                        lines.add(subtitle.substring(0, cut));
                        String rem = subtitle.substring(cut);
                        if (sm.stringWidth(rem) > availSubW) {
                            while (rem.length() > 1 && sm.stringWidth(rem + "...") > availSubW) {
                                rem = rem.substring(0, rem.length() - 1);
                            }
                            rem += "...";
                        }
                        if (!rem.isEmpty()) lines.add(rem);
                    }
                    int curY = titleY + tm.getDescent() + 5 + sm.getAscent();
                    for (String l : lines) {
                        g2.drawString(l, (w - sm.stringWidth(l)) / 2, curY);
                        curY += sm.getHeight();
                    }
                } else {
                    int subY = titleY + tm.getDescent() + 6 + sm.getAscent();
                    String subClipped = subtitle;
                    if (sm.stringWidth(subClipped) > availSubW) {
                        while (subClipped.length() > 1 && sm.stringWidth(subClipped + "...") > availSubW) {
                            subClipped = subClipped.substring(0, subClipped.length() - 1);
                        }
                        subClipped = subClipped + "...";
                    }
                    g2.drawString(subClipped, (w - sm.stringWidth(subClipped)) / 2, subY);
                }
            }
            g2.dispose();
        }
    }

    public static class OptionButton extends JButton {

        private final String letter;
        private final String label;
        private State state = State.NEUTRAL;
        private final SmoothHover hover;
        private final Anim enter = new Anim(() -> repaint());
        private final Anim pop = new Anim(() -> repaint());
        private final Anim shake = new Anim(() -> repaint());
        private boolean pressed;

        public OptionButton(String letter, String label) {
            super();
            this.letter = letter;
            this.label = label;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void reset() {
            state = State.NEUTRAL;
            repaint();
        }

        public void setSelected() {
            state = State.SELECTED;
            repaint();
        }

        public void setCorrect() {
            state = State.CORRECT;
            pop.setTarget(0f);
            pop.setTarget(1f, 0.16f);
            repaint();
        }

        public void setWrong() {
            state = State.WRONG;
            shake.setTarget(0f);
            shake.setTarget(1f, 0.22f);
            repaint();
        }

        public void playEnter(int delay) {
            javax.swing.Timer t = new javax.swing.Timer(delay, e -> {
                enter.setTarget(0f);
                enter.setTarget(1f, 0.18f);
            });
            t.setRepeats(false);
            t.start();
        }

        public String getLetter() {
            return letter;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();

            float ev = enter.value();
            if (enter.running()) {
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, ev))));
                g2.translate(0, (1f - ev) * 22f);
            }
            float pv = pop.value();
            if (pop.running()) {
                double bump = 1.0 + 0.07 * Math.sin(pv * Math.PI);
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(bump, bump);
                g2.translate(-w / 2.0, -h / 2.0);
            }
            float shv = shake.value();
            if (shake.running()) {
                double off = Math.sin(shv * 30.0) * 5.0 * (1.0 - shv);
                g2.translate(off, 0);
            }

            float hv = hover.value();
            int radius = capsule(h);

            Color fill;
            Color border;
            Color bubble;
            switch (state) {
                case SELECTED:
                    fill = new Color(32, 211, 194, 46);
                    border = TEAL;
                    bubble = TEAL;
                    break;
                case CORRECT:
                    fill = new Color(61, 220, 151, 46);
                    border = GREEN;
                    bubble = GREEN;
                    break;
                case WRONG:
                    fill = new Color(255, 93, 93, 40);
                    border = UITheme.ERROR;
                    bubble = UITheme.ERROR;
                    break;
                default:
                    fill = pressed ? PANEL_BG : lerp(PANEL_BG, new Color(32, 211, 194, 44), hv);
                    border = lerp(PANEL_BORDER, TEAL, hv);
                    bubble = GOLD;
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.4f + 0.8f * hv));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            if (hv > 0.02f) {
                g2.setColor(new Color(32, 211, 194, Math.round(60 * hv)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(3, 3, w - 7, h - 7, radius - 2, radius - 2);
            }

            int lift = Math.round(2f * hv);
            int slide = Math.round(5f * hv);
            int bubbleSize = Math.max(30, (int) Math.round(h * 0.55));
            int bx = 14 + slide;
            int by = (h - bubbleSize) / 2 - lift;
            int bubbleRadius = capsule(bubbleSize);
            g2.setColor(bubble);
            g2.fillRoundRect(bx, by, bubbleSize, bubbleSize, bubbleRadius, bubbleRadius);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx, by, bubbleSize, bubbleSize, bubbleRadius, bubbleRadius);

            int letterFont = Math.max(15, (int) Math.round(bubbleSize * 0.5));
            g2.setFont(fontFor(letter, Font.BOLD, letterFont));
            FontMetrics bm = g2.getFontMetrics();
            int lx = bx + (bubbleSize - bm.stringWidth(letter)) / 2;
            int ly = by + (bubbleSize - bm.getHeight()) / 2 + bm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(letter, lx, ly);

            int labelFont = Math.max(15, (int) Math.round(h * 0.27));
            g2.setFont(fontFor(label, Font.BOLD, labelFont));
            FontMetrics tm = g2.getFontMetrics();
            int tx = bx + bubbleSize + 14;
            int ty = (h - tm.getHeight()) / 2 + tm.getAscent() - lift;
            g2.setColor(isEnabled() ? TEXT : new Color(0x8fa2bd));
            String clipped = clip(tm, label, w - tx - 12);
            g2.drawString(clipped, tx, ty);
            g2.dispose();
        }

        private String clip(FontMetrics fm, String text, int maxWidth) {
            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int ellipsisWidth = fm.stringWidth(ellipsis);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (fm.stringWidth(sb.toString() + ch) + ellipsisWidth > maxWidth) {
                    break;
                }
                sb.append(ch);
            }
            return sb.toString() + ellipsis;
        }

        private enum State {
            NEUTRAL, SELECTED, CORRECT, WRONG
        }
    }

    public enum GameIcon {
        WORDS
    }

    public static class GameModeCard extends JButton {

        private final GameIcon icon;
        private final String title;
        private final String subtitle;
        private final Color accent;
        private Color bandTop;
        private Color bandBottom;
        private boolean pressed;
        private final SmoothHover hover;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public GameModeCard(GameIcon icon, String title, String subtitle, Color accent) {
            super();
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.accent = accent;
            this.bandTop = accent;
            this.bandBottom = accent.darker();
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setBand(Color top, Color bottom) {
            this.bandTop = top;
            this.bandBottom = bottom;
            chrome = null;
            chromeW = -1;
            chromeH = -1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();
            int lift = Math.round(4f * hv);
            int radius = Math.min(CARD_RADIUS, capsule(h));
            int bandH = Math.max(84, (int) (h * 0.32f));

            if (chrome == null || chromeW != w || chromeH != h) {
                chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                buildChrome(chrome, w, h, radius, bandH);
                chromeW = w;
                chromeH = h;
            }

            if (hv > 0.02f) {
                int glowR = Math.round(Math.max(w, h) * 0.95f);
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(w / 2f, h * 0.38f), glowR,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(55 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(25 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }
            g2.drawImage(chrome, 0, 0, null);

            if (pressed) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            if (hv > 0.02f) {
                g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (150 * hv + 40)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (100 * hv + 30)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (60 * hv + 20))}));
                g2.setStroke(new BasicStroke(1.6f + 1.0f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            }

            int yOff = -lift;

            int pillW = Math.max(130, Math.round(w * 0.52f));
            int pillH = Math.max(42, Math.round(h * 0.11f));
            int pillY = h - pillH - 18 + yOff;

            int titleSize = Math.max(22, Math.round(h * 0.060f));
            g2.setFont(fontFor(title, Font.BOLD, titleSize));
            FontMetrics tm = g2.getFontMetrics();
            String titleClipped = clip(tm, title, w - 20);
            int titleY = bandH + 20 + tm.getAscent() + yOff;
            g2.setColor(isEnabled() ? new Color(0xf0f6ff) : new Color(0x8fa2bd));
            g2.drawString(titleClipped, (w - tm.stringWidth(titleClipped)) / 2, titleY);

            int subSize = Math.max(14, Math.round(h * 0.040f));
            g2.setFont(fontFor(subtitle, Font.PLAIN, subSize));
            FontMetrics sm = g2.getFontMetrics();
            int subBaseline = titleY + tm.getDescent() + 6 + sm.getAscent();
            g2.setColor(isEnabled() ? new Color(0xb8c8e8) : new Color(0x8fa2bd));
            drawSubtitle(g2, sm, subtitle, w - 24, subBaseline, sm.getHeight() + 3, pillY - 14);

            int px = (w - pillW) / 2;

            if (hv > 0.02f) {
                int glowR = Math.round(pillH * 2.2f);
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(px + pillW / 2f, pillY + pillH / 2f), glowR,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(80 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(30 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
                g2.fillOval(px - pillH, pillY - pillH, pillW + pillH * 2, pillH * 3);
            }

            g2.setPaint(new GradientPaint(0, 0, lerp(accent, accent.brighter(), hv),
                    0, pillH, isEnabled() ? accent.darker() : new Color(0x8fa2bd)));
            g2.fillRoundRect(px, pillY, pillW, pillH, pillH / 2, pillH / 2);
            g2.setPaint(new GradientPaint(0, pillY, new Color(255, 255, 255, 60),
                    0, pillY + pillH / 2f, new Color(255, 255, 255, 0)));
            g2.fillRoundRect(px, pillY, pillW, pillH / 2, pillH / 2, pillH / 2);

            int pillSize = Math.max(15, Math.round(h * 0.042f));
            String play = com.worldofwonder.util.I18n.get("play_now");
            g2.setFont(fontFor(play, Font.BOLD, pillSize));
            FontMetrics pf = g2.getFontMetrics();
            int playW = pf.stringWidth(play);
            int playX = px + (pillW - playW) / 2;
            int playY = pillY + (pillH + pf.getAscent() - pf.getDescent()) / 2;
            g2.setColor(Color.WHITE);
            g2.drawString(play, playX, playY);
            g2.dispose();
        }

        private void buildChrome(BufferedImage img, int w, int h, int radius, int bandH) {
            Graphics2D g2 = img.createGraphics();
            quality(g2);
            softShadow(g2, 0, 2, w, h - 2, radius);
            g2.setColor(PANEL_BG);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            Shape cardBody = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
            g2.setClip(cardBody);
            g2.setPaint(new LinearGradientPaint(w * 0.2f, 0, w * 0.8f, h,
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26),
                            new Color(255, 255, 255, 0),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30)}));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.5f, h), Math.max(w, h) * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 90),
                            new Color(255, 255, 255, 26),
                            new Color(255, 255, 255, 60)}));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);

            Shape clip = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
            g2.setClip(clip);
            g2.setPaint(new GradientPaint(0, 0, bandTop, 0, bandH, bandBottom));
            g2.fillRect(0, 0, w, bandH);

            int bandGlowR = Math.round(Math.max(w, bandH) * 0.95f);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w / 2f, bandH * 0.1f), bandGlowR,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 70), new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, bandH);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.88f, bandH * 0.8f), bandGlowR * 0.8f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, bandH);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.12f, bandH * 0.7f), bandGlowR * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
            g2.fillRect(0, 0, w, bandH);

            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRect(0, bandH - 2, w, 2);
            g2.setPaint(new LinearGradientPaint(0, bandH - 1, w, bandH - 1,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60),
                            new Color(255, 255, 255, 40),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70)}));
            g2.fillRect(0, bandH - 1, w, 1);
            g2.setClip(null);

            int iconPad = Math.max(14, Math.round(bandH * 0.13f));
            int iconSize = Math.min(w - 2 * iconPad, bandH - 2 * iconPad);
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, bandH, radius, radius));
            int iconCx = w / 2;
            int iconCy = bandH / 2 + Math.round(iconSize * 0.02f);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(iconCx, iconCy), iconSize * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 40), new Color(255, 255, 255, 0)}));
            g2.fillOval(iconCx - iconSize / 2, iconCy - iconSize / 2, iconSize, iconSize);
            paintIcon(g2, icon, iconCx, iconCy, iconSize);
            g2.setClip(null);
            g2.dispose();
        }

        private void paintIcon(Graphics2D g2, GameIcon icon, int cx, int cy, int size) {
            switch (icon) {
                case WORDS:
                    paintWordsIcon(g2, cx, cy, size);
                    break;
                default:
                    break;
            }
        }

        private void paintWordsIcon(Graphics2D g2, int cx, int cy, int size) {
            float r = size * 0.34f;
            int nodeR = Math.max(5, Math.round(size * 0.09f));

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, cy), r * 1.6f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(32, 211, 194, 50), new Color(32, 211, 194, 0)}));
            g2.fillOval((int) (cx - r * 1.6f), (int) (cy - r * 1.6f),
                    (int) (r * 3.2f), (int) (r * 3.2f));

            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(Math.max(1.5f, size * 0.025f)));
            g2.drawOval((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2));

            String letters = "WORD";
            Color[] nc = {TEAL, GOLD, CORAL, VIOLET};
            int n = letters.length();
            for (int i = 0; i < n; i++) {
                float a = (float) (Math.PI * 2 * i / n - Math.PI / 2);
                int nx = (int) (cx + r * Math.cos(a));
                int ny = (int) (cy + r * Math.sin(a));
                float na = (float) (Math.PI * 2 * ((i + 1) % n) / n - Math.PI / 2);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(Math.max(1f, size * 0.015f)));
                g2.drawLine(nx, ny, (int) (cx + r * Math.cos(na)), (int) (cy + r * Math.sin(na)));
                g2.setColor(nc[i % nc.length]);
                g2.fillOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);
                int fs = Math.max(8, Math.round(size * 0.11f));
                g2.setFont(displayFont(Font.BOLD, fs));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(letters.charAt(i));
                g2.setColor(Color.WHITE);
                g2.drawString(s, nx - fm.stringWidth(s) / 2, ny + fm.getAscent() / 2 - 1);
            }
        }

        private void drawTestTube(Graphics2D g2, int x, int y, int w, int h,
                                  float liquidFrac, Color liquidTop, Color liquidBottom) {
            int r = Math.max(6, w / 2);
            g2.setColor(new Color(255, 255, 255, 46));
            g2.fillRoundRect(x, y, w, h, r, r);

            int liqH = Math.round(h * liquidFrac);
            Shape clip = new RoundRectangle2D.Float(x + 2, y + 2, w - 4, h - 4, Math.max(4, r - 2), Math.max(4, r - 2));
            g2.setClip(clip);
            g2.setPaint(new GradientPaint(0, y + h - liqH, liquidTop, 0, y + h, liquidBottom));
            g2.fillRoundRect(x + 2, y + h - liqH, w - 4, liqH, Math.max(4, r - 2), Math.max(4, r - 2));
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRect(x + 3, y + 3, 3, h - 6);
            g2.setClip(null);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.setStroke(new BasicStroke(Math.max(1.5f, w * 0.10f)));
            g2.drawRoundRect(x, y, w, h, r, r);
            g2.drawLine(x, y, x + w, y);
        }

        private List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
            List<String> result = new ArrayList<>();
            if (text == null || text.trim().isEmpty()) {
                return result;
            }
            if (fm.stringWidth(text) <= maxWidth) {
                result.add(text);
                return result;
            }
            StringBuilder cur = new StringBuilder();
            if (text.contains(" ")) {
                String[] words = text.split(" ");
                for (String word : words) {
                    if (word.isEmpty()) continue;
                    String next = cur.length() == 0 ? word : cur + " " + word;
                    if (fm.stringWidth(next) <= maxWidth) {
                        cur = new StringBuilder(next);
                    } else {
                        if (cur.length() > 0) {
                            result.add(cur.toString());
                            cur = new StringBuilder();
                        }
                        if (fm.stringWidth(word) > maxWidth) {
                            for (int c = 0; c < word.length(); c++) {
                                char ch = word.charAt(c);
                                if (fm.stringWidth(cur.toString() + ch) <= maxWidth) {
                                    cur.append(ch);
                                } else {
                                    if (cur.length() > 0) {
                                        result.add(cur.toString());
                                        cur = new StringBuilder();
                                    }
                                    cur.append(ch);
                                }
                            }
                        } else {
                            cur.append(word);
                        }
                    }
                }
                if (cur.length() > 0) {
                    result.add(cur.toString());
                }
            } else {
                for (int c = 0; c < text.length(); c++) {
                    char ch = text.charAt(c);
                    if (fm.stringWidth(cur.toString() + ch) <= maxWidth) {
                        cur.append(ch);
                    } else {
                        if (cur.length() > 0) {
                            result.add(cur.toString());
                            cur = new StringBuilder();
                        }
                        cur.append(ch);
                    }
                }
                if (cur.length() > 0) {
                    result.add(cur.toString());
                }
            }
            return result;
        }

        private void drawSubtitle(Graphics2D g2, FontMetrics fm, String text, int maxWidth,
                                  int baseline, int lineHeight) {
            drawSubtitle(g2, fm, text, maxWidth, baseline, lineHeight, Integer.MAX_VALUE);
        }

        private void drawSubtitle(Graphics2D g2, FontMetrics fm, String text, int maxWidth,
                                  int baseline, int lineHeight, int maxBaseline) {
            List<String> lines = wrapText(fm, text, maxWidth);
            int maxLines = Math.min(lines.size(), 2);
            for (int i = 0; i < maxLines; i++) {
                int y = baseline + i * lineHeight;
                if (y + fm.getDescent() > maxBaseline && i > 0) {
                    break;
                }
                String line = lines.get(i);
                if (i == maxLines - 1 && lines.size() > maxLines) {
                    line = clip(fm, line, maxWidth);
                }
                g2.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, y);
            }
        }

        private String clip(FontMetrics fm, String text, int maxWidth) {
            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int ellipsisWidth = fm.stringWidth(ellipsis);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (fm.stringWidth(sb.toString() + ch) + ellipsisWidth > maxWidth) {
                    break;
                }
                sb.append(ch);
            }
            return sb.toString() + ellipsis;
        }
    }

    /* ==================== Web-style components ==================== */

    public static class ProgressBar extends JComponent {

        private final Anim anim = new Anim(() -> repaint());
        private double target;

        public ProgressBar() {
            setOpaque(false);
        }

        public void setProgress(double p) {
            target = Math.max(0.0, Math.min(1.0, p));
            anim.setTarget((float) target, 0.14f);
        }

        public double getProgress() {
            return target;
        }

        @Override
        protected void paintComponent(Graphics g) {
            double progress = anim.value();
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int trackH = Math.max(10, h - 4);
            int ty = (h - trackH) / 2;

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, ty, w, trackH, trackH, trackH);
            g2.setColor(PANEL_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, ty, w, trackH, trackH, trackH);

            if (progress > 0.01) {
                int fw = Math.max(trackH, (int) Math.round(w * progress));
                g2.setClip(0, ty, fw, trackH);
                g2.setPaint(new GradientPaint(0, 0, TEAL, w, 0, GOLD));
                g2.fillRoundRect(0, ty, w, trackH, trackH, trackH);
                g2.setClip(null);
                g2.setColor(new Color(32, 211, 194, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, ty, fw, trackH, trackH, trackH);
            }

            int sx = (int) Math.round(w * progress);
            int star = trackH + 6;
            g2.setColor(new Color(255, 201, 60, 70));
            g2.fillOval(sx - star / 2 - 3, ty - 3, star + 6, star + 6);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(sx, ty + trackH / 2f), star / 2f + 1,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0xffffff), new Color(0xf5a623)}));
            g2.fillOval(sx - star / 2, ty, star, star);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(sx - star / 2, ty, star, star);
            g2.dispose();
        }
    }

    public static class HintBox extends JLabel {

        public HintBox(String text) {
            super(text);
            setOpaque(false);
            setFont(bodyFont(Font.PLAIN, 15));
            setForeground(ICE);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = Math.max(14, Math.min(20, h / 2));
            g2.setColor(new Color(255, 201, 60, 26));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(new Color(255, 201, 60, 120));
            g2.setStroke(new BasicStroke(4f));
            g2.drawRoundRect(0, 0, 4, h, 2, 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class GradientTextLabel extends JLabel {

        private final Color from;
        private final Color to;

        public GradientTextLabel(String text, int size, Color from, Color to) {
            super(text, SwingConstants.CENTER);
            this.from = from;
            this.to = to;
            setOpaque(false);
            setFont(displayFont(Font.BOLD, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            String text = plainText(getText());
            if (!text.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics(getFont());
                int w = fm.stringWidth(text);
                int x = (getWidth() - w) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 110));
                g2.drawString(text, x + 1, y + 2);
                g2.setPaint(new LinearGradientPaint(
                        new Point2D.Float(x, 0), new Point2D.Float(x + w, 0),
                        new float[]{0f, 1f}, new Color[]{from, to}));
                g2.drawString(text, x, y);
            }
            g2.dispose();
        }

        /** This label draws through {@code drawString}, which has no HTML renderer.
         *  Any markup handed to it would otherwise be painted literally, so strip
         *  tags and resolve the handful of entities Swing's HTML would have. */
        static String plainText(String raw) {
            if (raw == null) {
                return "";
            }
            String s = raw;
            if (s.length() > 4 && s.substring(0, 5).equalsIgnoreCase("<html")) {
                s = s.replaceAll("(?i)<br\\s*/?>", " ");
                s = s.replaceAll("(?i)</?div[^>]*>", " ");
                s = s.replaceAll("(?i)<[^>]+>", "");
                s = s.replace("&nbsp;", " ").replace("&amp;", "&")
                        .replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
            }
            return s.replaceAll("\\s+", " ").trim();
        }
    }

    public static JComponent coin(int size) {
        JComponent coin = new JComponent() {
            {
                setPreferredSize(new Dimension(size, size));
                setMaximumSize(new Dimension(size, size));
                setMinimumSize(new Dimension(size, size));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                int s = getWidth();
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(s * 0.35f, s * 0.30f), s * 0.8f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0xfff7d6), new Color(0xf5a623)}));
                g2.fillOval(0, 0, s, s);
                g2.setColor(new Color(0, 0, 0, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, s, s);
                // Draw crisp vector 5-pointed star instead of emoji glyph
                java.awt.geom.Path2D.Double star = new java.awt.geom.Path2D.Double();
                double cx = s / 2.0;
                double cy = s / 2.0;
                double outerR = s * 0.32;
                double innerR = outerR * 0.42;
                for (int i = 0; i < 10; i++) {
                    double angle = -Math.PI / 2.0 + i * Math.PI / 5.0;
                    double r = (i % 2 == 0) ? outerR : innerR;
                    double x = cx + r * Math.cos(angle);
                    double y = cy + r * Math.sin(angle);
                    if (i == 0) star.moveTo(x, y);
                    else star.lineTo(x, y);
                }
                star.closePath();
                g2.setColor(Color.WHITE);
                g2.fill(star);
                g2.setColor(new Color(0xd4, 0x88, 0x00, 110));
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(star);
                g2.dispose();
            }
        };
        return coin;
    }

    public static final class Confetti {

        private static final Color[] COLORS = {
                new Color(0xffc93c), new Color(0x20d3c2), new Color(0xff6b6b),
                new Color(0x9b5de5), new Color(0xf15bb5), new Color(0x3ddc97),
        };

        private final List<Particle> particles = new ArrayList<>();
        private final javax.swing.Timer timer;
        private final JComponent host;

        public Confetti(JComponent host) {
            this.host = host;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        public void launch() {
            Random rnd = new Random();
            int w = Math.max(host.getWidth(), 600);
            int h = Math.max(host.getHeight(), 400);
            particles.clear();
            for (int i = 0; i < 170; i++) {
                particles.add(new Particle(rnd, w, h));
            }
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        public void paint(Graphics2D g2) {
            int h = Math.max(host.getHeight(), 400);
            for (Particle p : particles) {
                if (p.y > h + 40) {
                    continue;
                }
                g2.rotate(p.rot, p.x, p.y);
                g2.setColor(p.color);
                if (p.circle) {
                    g2.fillOval((int) (p.x - p.w / 2f), (int) (p.y - p.w / 2f), p.w, p.w);
                } else {
                    g2.fillRect((int) (p.x - p.w / 2f), (int) (p.y - p.h / 2f), p.w, p.h);
                }
                g2.rotate(-p.rot, p.x, p.y);
            }
        }

        private void tick() {
            int h = Math.max(host.getHeight(), 400);
            particles.removeIf(p -> p.y > h + 40);
            boolean any = false;
            for (Particle p : particles) {
                p.y += p.vy;
                p.x += p.vx;
                p.vy += 0.03f;
                p.rot += p.vrot;
                any = true;
            }
            if (any) {
                host.repaint();
            }
            if (particles.isEmpty()) {
                timer.stop();
            }
        }

        private static class Particle {
            final float vx;
            float x;
            float y;
            float vy;
            float rot;
            final float vrot;
            final int w;
            final int h;
            final Color color;
            final boolean circle;

            Particle(Random rnd, int width, int height) {
                x = rnd.nextFloat() * width;
                y = -24 - rnd.nextFloat() * height * 0.4f;
                w = 6 + rnd.nextInt(6);
                h = 8 + rnd.nextInt(8);
                color = COLORS[rnd.nextInt(COLORS.length)];
                vx = (rnd.nextFloat() - 0.5f) * 2.6f;
                vy = 2.2f + rnd.nextFloat() * 3.4f;
                rot = rnd.nextFloat() * (float) Math.PI;
                vrot = (rnd.nextFloat() - 0.5f) * 0.22f;
                circle = rnd.nextBoolean();
            }
        }
    }

    public static class LetterTile extends JComponent {

        private final char letter;
        private final Color color;

        LetterTile(char letter, Color color, int size) {
            this.letter = letter;
            this.color = color;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();

            int r = capsule(h);
            g2.setColor(color.darker());
            g2.fillRoundRect(0, 3, w, h - 3, r, r);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, w, h - 3, r, r);

            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawRoundRect(0, 3, w - 1, h - 3, r, r);

            g2.setColor(Color.WHITE);
            g2.setFont(displayFont(Font.BOLD, Math.max(16, (int) (h * 0.5f))));
            FontMetrics fm = g2.getFontMetrics();
            String s = String.valueOf(letter);
            int x = (w - fm.stringWidth(s)) / 2;
            int y = ((h - 3) - fm.getHeight()) / 2 + fm.getAscent() + 1;
            g2.drawString(s, x, y);
            g2.dispose();
        }
    }
}
