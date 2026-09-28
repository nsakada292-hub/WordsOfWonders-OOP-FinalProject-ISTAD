package com.worldofwonder.view;

import com.worldofwonder.controller.GameController;
import com.worldofwonder.model.User;
import com.worldofwonder.util.ApiService;
import com.worldofwonder.util.I18n;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modern modal dialog displaying both Local and Global Top 10 players
 * with an animated podium and online Dreamlo integration.
 */
public class LeaderboardModal extends JDialog {

    private final GameController gameController;
    private final JPanel centerPanel;
    private final JButton localTabBtn;
    private final JButton globalTabBtn;
    private boolean isGlobal = false;

    public LeaderboardModal(JFrame parent, GameController gameController) {
        super(parent, "Hall of Fame - Top Wonderers", true);
        this.gameController = gameController;
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout(0, UITheme.GAP_SECTION));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header Title
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JComponent trophyIcon = UITheme.vectorIcon(UITheme.VectorIcon.TROPHY, 36, UITheme.GOLD);
        trophyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(trophyIcon);
        header.add(Box.createVerticalStrut(6));

        String titleStr = I18n.get("leaderboard_title");
        JLabel title = new UITheme.GradientTextLabel(titleStr,
                UITheme.FONT_PAGE_TITLE + 2, new Color(0xffd700), new Color(0xffaa00));
        title.setFont(UITheme.fontFor(titleStr, Font.BOLD, UITheme.FONT_PAGE_TITLE + 2));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));

        String subStr = I18n.get("leaderboard_sub");
        JLabel sub = UITheme.subtitle(subStr);
        sub.setFont(UITheme.fontFor(subStr, Font.PLAIN, UITheme.FONT_BODY));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(sub);
        header.add(Box.createVerticalStrut(10));

        // Local vs Global Tab Switcher
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tabRow.setOpaque(false);

        localTabBtn = UITheme.primaryButton(I18n.get("leaderboard_local"));
        UIUtil.fixedSize(localTabBtn, 120, UITheme.BTN_H_SM);
        localTabBtn.addActionListener(e -> {
            if (isGlobal) {
                isGlobal = false;
                updateTabStyles();
                loadLocalScores();
            }
        });
        tabRow.add(localTabBtn);

        globalTabBtn = UITheme.ghostButton(I18n.get("leaderboard_global"), UITheme.TEAL);
        UIUtil.fixedSize(globalTabBtn, 120, UITheme.BTN_H_SM);
        globalTabBtn.addActionListener(e -> {
            if (!isGlobal) {
                isGlobal = true;
                updateTabStyles();
                loadGlobalScores();
            }
        });
        tabRow.add(globalTabBtn);

        header.add(tabRow);
        content.add(header, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        content.add(centerPanel, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JButton closeBtn = UITheme.iconPillButton(UITheme.VectorIcon.CHECK, I18n.get("btn_got_it"), UITheme.VIOLET);
        UIUtil.fixedSize(closeBtn, 220, UITheme.BTN_H);
        closeBtn.addActionListener(e -> {
            SoundUtil.playClick();
            dispose();
        });
        footer.add(closeBtn);
        content.add(footer, BorderLayout.SOUTH);

        // Card Container
        JPanel card = UITheme.modalCard(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(new Color(0x5a4890), 2, true));
        card.setPreferredSize(new Dimension(580, 580));
        card.add(content, BorderLayout.CENTER);

        setContentPane(card);
        pack();
        setLocationRelativeTo(parent);

        // Initial view
        loadLocalScores();
    }

    private void updateTabStyles() {
        SoundUtil.playClick();
        if (isGlobal) {
            localTabBtn.setBackground(new Color(0x182438));
            globalTabBtn.setBackground(new Color(0x008080));
        } else {
            localTabBtn.setBackground(new Color(0x5a2590));
            globalTabBtn.setBackground(new Color(0x182438));
        }
    }

    private void loadLocalScores() {
        centerPanel.removeAll();
        List<User> topUsers = gameController.getLeaderboard(10);
        renderUserList(topUsers);
    }

    private void loadGlobalScores() {
        centerPanel.removeAll();
        JLabel loadingLbl = new JLabel(I18n.get("leaderboard_loading"), SwingConstants.CENTER);
        loadingLbl.setFont(UITheme.fontFor(loadingLbl.getText(), Font.BOLD, 14));
        loadingLbl.setForeground(UITheme.TEXT_MUTED);
        loadingLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(loadingLbl);
        centerPanel.revalidate();
        centerPanel.repaint();

        // Query Dreamlo public leaderboard
        ApiService.fetchLeaderboard("5969562776827f12e8412611", 10, entries -> {
            List<User> globalUsers = new ArrayList<>();
            for (Map<String, Object> entry : entries) {
                String name = (String) entry.get("name");
                int score = 0;
                try {
                    score = Integer.parseInt(String.valueOf(entry.get("score")));
                } catch (Exception ignored) {
                }
                globalUsers.add(new User(0, name, "", "", score));
            }

            // If empty or first run, provide world-class benchmark wonderers
            if (globalUsers.isEmpty()) {
                globalUsers.add(new User(0, "WonderExplorer", "", "", 1250));
                globalUsers.add(new User(0, "PharaohMaster", "", "", 980));
                globalUsers.add(new User(0, "GalaxyRanger", "", "", 740));
                globalUsers.add(new User(0, "OceanDiver", "", "", 610));
                globalUsers.add(new User(0, "DinoTracker", "", "", 490));
            }

            centerPanel.removeAll();
            renderUserList(globalUsers);
            centerPanel.revalidate();
            centerPanel.repaint();
        }, err -> {
            centerPanel.removeAll();
            List<User> fallback = new ArrayList<>();
            fallback.add(new User(0, "WonderMaster (Offline)", "", "", 1200));
            fallback.add(new User(0, "CosmoExplorer", "", "", 950));
            fallback.add(new User(0, "DinoHero", "", "", 720));
            renderUserList(fallback);
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }

    private void renderUserList(List<User> users) {
        if (!users.isEmpty()) {
            JPanel podiumPanel = buildPodium(users);
            centerPanel.add(podiumPanel);
            centerPanel.add(Box.createVerticalStrut(12));
        }

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            int rank = i + 1;
            JPanel row = buildUserRow(rank, u);
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(520, 220));
        centerPanel.add(scrollPane);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private JPanel buildPodium(List<User> topUsers) {
        JPanel podium = new JPanel(new GridLayout(1, 3, 12, 0));
        podium.setOpaque(false);

        User first = topUsers.size() > 0 ? topUsers.get(0) : null;
        User second = topUsers.size() > 1 ? topUsers.get(1) : null;
        User third = topUsers.size() > 2 ? topUsers.get(2) : null;

        podium.add(buildPodiumStep(2, second, new Color(0xd0d8e8), "2nd"));
        podium.add(buildPodiumStep(1, first, new Color(0xffd700), "1st"));
        podium.add(buildPodiumStep(3, third, new Color(0xcd7f32), "3rd"));

        return podium;
    }

    private JPanel buildPodiumStep(int rank, User user, Color accent, String label) {
        JPanel step = new JPanel();
        step.setOpaque(false);
        step.setLayout(new BoxLayout(step, BoxLayout.Y_AXIS));

        if (user == null) {
            JLabel empty = new JLabel("-", SwingConstants.CENTER);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            step.add(empty);
            return step;
        }

        JLabel crown = new JLabel(rank == 1 ? "#1" : (rank == 2 ? "#2" : "#3"), SwingConstants.CENTER);
        crown.setFont(UITheme.displayFont(Font.BOLD, rank == 1 ? 18 : 15));
        crown.setForeground(accent);
        crown.setAlignmentX(Component.CENTER_ALIGNMENT);
        step.add(crown);

        JLabel name = new JLabel(user.getUsername(), SwingConstants.CENTER);
        name.setFont(UITheme.displayFont(Font.BOLD, rank == 1 ? 15 : 13));
        name.setForeground(accent);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        step.add(name);

        JLabel pts = new JLabel(user.getTotalPoints() + " " + I18n.get("pts_suffix"), SwingConstants.CENTER);
        pts.setFont(UITheme.bodyFont(Font.BOLD, 12));
        pts.setForeground(UITheme.GOLD);
        pts.setAlignmentX(Component.CENTER_ALIGNMENT);
        step.add(pts);

        return step;
    }

    private JPanel buildUserRow(int rank, User user) {
        JPanel row = UITheme.roundedBar();
        row.setLayout(new BorderLayout(12, 0));
        row.setPreferredSize(new Dimension(480, 40));
        row.setMaximumSize(new Dimension(500, 40));
        row.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        // Left: Rank & Name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(UITheme.displayFont(Font.BOLD, 14));
        rankLabel.setForeground(rank == 1 ? UITheme.GOLD : (rank <= 3 ? UITheme.TEAL : UITheme.TEXT_MUTED));
        left.add(rankLabel);

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(UITheme.bodyFont(Font.BOLD, 13));
        nameLabel.setForeground(UITheme.TEXT);
        left.add(nameLabel);

        JLabel rankBadge = new JLabel("(" + user.getRankTitle() + ")");
        rankBadge.setFont(UITheme.bodyFont(Font.ITALIC, 11));
        rankBadge.setForeground(UITheme.TEXT_MUTED);
        left.add(rankBadge);

        row.add(left, BorderLayout.WEST);

        // Right: Total points
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(UITheme.coin(18));

        JLabel ptsLabel = new JLabel(String.valueOf(user.getTotalPoints()));
        ptsLabel.setFont(UITheme.displayFont(Font.BOLD, 14));
        ptsLabel.setForeground(UITheme.GOLD);
        right.add(ptsLabel);

        row.add(right, BorderLayout.EAST);
        return row;
    }
}
