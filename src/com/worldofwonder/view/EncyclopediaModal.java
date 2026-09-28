package com.worldofwonder.view;

import com.worldofwonder.util.ApiService;
import com.worldofwonder.util.I18n;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Modern modal dialog presenting an illustrated World Encyclopedia Codex.
 * Features articles for all 6 worlds with async Wikipedia summaries and
 * bilingual English & Khmer offline fallbacks.
 */
public class EncyclopediaModal extends JDialog {

    private static final String[][] TOPICS = {
            {"Great Pyramid of Giza", "world_1_name", "Pyramid of Giza is the oldest of the Seven Wonders of the Ancient World.", "ពីរ៉ាមីតហ្គីហ្សាគឺជាសំណង់ចំណាស់បំផុតក្នុងចំណោមអច្ឆរិយៈទាំង ៧ នៃពិភពលោកបុរាណ។"},
            {"Solar System", "world_2_name", "The gravitationally bound system of the Sun and the objects that orbit it.", "ប្រព័ន្ធព្រះអាទិត្យគឺជាប្រព័ន្ធដែលមានព្រះអាទិត្យ និងភពនានាវិលជុំវិញ។"},
            {"Coral reef", "world_3_name", "Underwater ecosystems characterized by reef-building corals and rich marine life.", "ថ្មប៉ប្រះទឹកផ្កាថ្មគឺជាប្រព័ន្ធអេកូឡូស៊ីក្រោមទឹកដែលសម្បូរដោយផ្កាថ្ម និងជីវិតសមុទ្រ។"},
            {"Tyrannosaurus", "world_4_name", "One of the largest land carnivores that roamed the Earth during the Late Cretaceous.", "ទីរ៉ាណូស័រគឺជាសត្វស៊ីសាច់លើគោកដ៏ធំបំផុតមួយក្នុងយុគសម័យបុរេប្រវត្តិ។"},
            {"Castle", "world_5_name", "A type of fortified structure built during the Middle Ages by nobility.", "ប្រាសាទគឺជាសំណង់ការពាររឹងមាំដែលត្រូវបានសាងសង់ក្នុងយុគសម័យកណ្តាល។"},
            {"Amazon rainforest", "world_6_name", "The world's largest tropical rainforest covering most of the Amazon basin.", "ព្រៃអាម៉ាហ្សូនគឺជាព្រៃទឹកភ្លៀងត្រូពិចដ៏ធំបំផុតនៅលើពិភពលោក។"}
    };

    private final JPanel cardsContainer;
    private final Map<String, JLabel> thumbMap = new HashMap<>();

    public EncyclopediaModal(JFrame parent) {
        super(parent, "World Codex - Encyclopedia", true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout(0, UITheme.GAP_SECTION));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header Title
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        header.add(UITheme.vectorIcon(UITheme.VectorIcon.SEARCH, 36, UITheme.TEAL));
        header.add(Box.createVerticalStrut(6));

        String titleStr = I18n.get("encyclopedia_title");
        JLabel title = new UITheme.GradientTextLabel(titleStr,
                UITheme.FONT_PAGE_TITLE + 2, new Color(0x20d0c0), new Color(0x60a0ff));
        title.setFont(UITheme.fontFor(titleStr, Font.BOLD, UITheme.FONT_PAGE_TITLE + 2));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));

        String subStr = I18n.get("encyclopedia_sub");
        JLabel sub = UITheme.subtitle(subStr);
        sub.setFont(UITheme.fontFor(subStr, Font.PLAIN, UITheme.FONT_BODY));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(sub);
        content.add(header, BorderLayout.NORTH);

        // Center scroll area with world articles
        cardsContainer = new JPanel(new GridLayout(3, 2, 14, 14));
        cardsContainer.setOpaque(false);

        for (String[] topic : TOPICS) {
            cardsContainer.add(buildTopicCard(topic[0], topic[1], topic[2], topic[3]));
        }

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(680, 420));
        content.add(scroll, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton closeBtn = UITheme.iconPillButton(UITheme.VectorIcon.CHECK, I18n.get("btn_got_it"), UITheme.TEAL);
        UIUtil.fixedSize(closeBtn, 200, UITheme.BTN_H);
        closeBtn.addActionListener(e -> {
            SoundUtil.playClick();
            dispose();
        });
        footer.add(closeBtn);
        content.add(footer, BorderLayout.SOUTH);

        // Card Container
        JPanel card = UITheme.modalCard(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(0x2080a0), 2, true));
        card.setPreferredSize(new Dimension(740, 600));
        card.add(content, BorderLayout.CENTER);

        setContentPane(card);
        pack();
        setLocationRelativeTo(parent);

        // Asynchronously fetch Wikipedia article summaries and thumbnails
        loadWikiData();
    }

    private JPanel buildTopicCard(String wikiTitle, String worldNameKey, String fallbackEn, String fallbackKm) {
        JPanel card = UITheme.roundedBar();
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setPreferredSize(new Dimension(320, 110));

        // Thumbnail placeholder
        JPanel thumbBox = new JPanel(new BorderLayout());
        thumbBox.setPreferredSize(new Dimension(70, 70));
        thumbBox.setOpaque(true);
        thumbBox.setBackground(new Color(0x182438));
        JLabel thumb = new JLabel();
        thumb.setHorizontalAlignment(SwingConstants.CENTER);
        thumb.setLayout(new BorderLayout());
        thumb.add(UITheme.vectorIcon(UITheme.VectorIcon.LIGHTBULB, 24, UITheme.GOLD), BorderLayout.CENTER);
        thumbBox.add(thumb, BorderLayout.CENTER);
        thumbMap.put(wikiTitle, thumb);
        card.add(thumbBox, BorderLayout.WEST);

        // Info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        String worldTag = I18n.get(worldNameKey);
        JLabel tagLabel = new JLabel(worldTag.toUpperCase());
        tagLabel.setFont(UITheme.fontFor(worldTag, Font.BOLD, 10));
        tagLabel.setForeground(UITheme.TEAL);
        info.add(tagLabel);
        info.add(Box.createVerticalStrut(2));

        JLabel titleLabel = new JLabel(wikiTitle);
        titleLabel.setFont(UITheme.fontFor(wikiTitle, Font.BOLD, 14));
        titleLabel.setForeground(UITheme.TEXT);
        info.add(titleLabel);
        info.add(Box.createVerticalStrut(3));

        String summaryText = I18n.isKhmer() ? fallbackKm : fallbackEn;
        JLabel descLabel = new JLabel("<html><body style='width: 190px;'>" + summaryText + "</body></html>");
        descLabel.setFont(UITheme.fontFor(summaryText, Font.PLAIN, 11));
        descLabel.setForeground(UITheme.TEXT_MUTED);
        info.add(descLabel);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private void loadWikiData() {
        for (String[] topic : TOPICS) {
            String title = topic[0];
            ApiService.fetchWikiSummary(title, summary -> {
                String thumbUrl = summary.get("thumbnail");
                if (thumbUrl != null && !thumbUrl.isEmpty()) {
                    new Thread(() -> {
                        try {
                            BufferedImage img = ImageIO.read(java.net.URI.create(thumbUrl).toURL());
                            if (img != null) {
                                Image scaled = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                                SwingUtilities.invokeLater(() -> {
                                    JLabel lbl = thumbMap.get(title);
                                    if (lbl != null) {
                                        lbl.removeAll();
                                        lbl.setIcon(new javax.swing.ImageIcon(scaled));
                                        lbl.revalidate();
                                        lbl.repaint();
                                    }
                                });
                            }
                        } catch (Exception ignored) {
                        }
                    }).start();
                }
            }, err -> {
                // Keep default offline illustration
            });
        }
    }
}
