package com.worldofwonder.view;

import com.worldofwonder.controller.GameController;
import com.worldofwonder.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Administrative User Management Modal (CRUD).
 * Enables administrators to view, search, create, edit, adjust points, and delete user accounts.
 */
public class AdminControlModal extends JDialog {

    private final GameController gameController;
    private final String currentAdminUsername;
    private final Dashboard dashboard;

    private DefaultTableModel tableModel;
    private JTable userTable;
    private JTextField searchField;
    private JLabel countLabel;
    private List<User> allUsers;

    public AdminControlModal(JFrame parent, Dashboard dashboard, GameController gameController, String currentAdminUsername) {
        super(parent, "Admin User Management", true);
        this.dashboard = dashboard;
        this.gameController = gameController;
        this.currentAdminUsername = currentAdminUsername;

        setResizable(true);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(new Color(13, 27, 42));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        content.add(buildHeader(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);
        content.add(buildActionsBar(), BorderLayout.SOUTH);

        setContentPane(content);
        refreshTable();

        // Clamp to the owner, then centre, so a small screen cannot push the
        // dialog off to one side.
        setSize(940, 640);
        UIUtil.centreOnOwner(this, parent, 720, 420);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(UITheme.vectorIcon(UITheme.VectorIcon.SHIELD, 26, UITheme.GOLD));
        String titleStr = com.worldofwonder.util.I18n.get("admin_modal_title");
        JLabel title = UITheme.title(titleStr, 24);
        title.setFont(UITheme.fontFor(titleStr, Font.BOLD, 24));
        title.setForeground(UITheme.GOLD);
        left.add(title);

        countLabel = new JLabel("(0 users)");
        countLabel.setFont(UITheme.bodyFont(Font.PLAIN, 13));
        countLabel.setForeground(UITheme.TEXT_MUTED);
        left.add(countLabel);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        right.add(UITheme.vectorIcon(UITheme.VectorIcon.SEARCH, 18, UITheme.ICE));

        searchField = new JTextField(16);
        searchField.setPreferredSize(new Dimension(190, 36));
        searchField.setBackground(new Color(24, 44, 70));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setFont(UITheme.bodyFont(Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        right.add(searchField);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {
                com.worldofwonder.util.I18n.get("admin_col_id"),
                com.worldofwonder.util.I18n.get("admin_col_user"),
                com.worldofwonder.util.I18n.get("admin_col_email"),
                com.worldofwonder.util.I18n.get("admin_col_points"),
                com.worldofwonder.util.I18n.get("admin_col_rank"),
                com.worldofwonder.util.I18n.get("admin_col_role")
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(36);
        userTable.setBackground(new Color(20, 39, 61));
        userTable.setForeground(Color.WHITE);
        userTable.setFont(UITheme.bodyFont(Font.PLAIN, 13));
        userTable.setSelectionBackground(new Color(32, 211, 194, 90));
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(255, 255, 255, 20));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = userTable.getTableHeader();
        th.setBackground(new Color(10, 20, 34));
        th.setForeground(UITheme.TEAL);
        th.setFont(UITheme.displayFont(Font.BOLD, 13));
        th.setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setOpaque(false);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        userTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(new Color(20, 39, 61));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bar.setOpaque(false);

        JButton addBtn = UITheme.iconPillButton(UITheme.VectorIcon.PLUS, com.worldofwonder.util.I18n.get("admin_create_user"), UITheme.GREEN);
        UIUtil.fixedSize(addBtn, 145, 42);
        addBtn.addActionListener(e -> showAddUserDialog());
        bar.add(addBtn);

        JButton editBtn = UITheme.iconPillButton(UITheme.VectorIcon.GEAR, com.worldofwonder.util.I18n.get("admin_edit_user"), UITheme.GOLD);
        UIUtil.fixedSize(editBtn, 125, 42);
        editBtn.addActionListener(e -> showEditUserDialog());
        bar.add(editBtn);

        JButton addPtsBtn = UITheme.ghostButton("+100 Pts", UITheme.TEAL);
        UIUtil.fixedSize(addPtsBtn, 110, 42);
        addPtsBtn.addActionListener(e -> awardBonusPoints(100));
        bar.add(addPtsBtn);

        JButton resetPtsBtn = UITheme.ghostButton(com.worldofwonder.util.I18n.get("admin_reset_pts"), UITheme.ICE);
        UIUtil.fixedSize(resetPtsBtn, 130, 42);
        resetPtsBtn.addActionListener(e -> resetPoints());
        bar.add(resetPtsBtn);

        JButton delBtn = UITheme.iconPillButton(UITheme.VectorIcon.NONE, com.worldofwonder.util.I18n.get("admin_delete_user"), UITheme.CORAL);
        UIUtil.fixedSize(delBtn, 125, 42);
        delBtn.addActionListener(e -> deleteSelectedUser());
        bar.add(delBtn);

        JButton refreshBtn = UITheme.iconPillButton(UITheme.VectorIcon.REFRESH, com.worldofwonder.util.I18n.get("admin_refresh"), UITheme.TEXT_MUTED);
        UIUtil.fixedSize(refreshBtn, 125, 42);
        refreshBtn.addActionListener(e -> refreshTable());
        bar.add(refreshBtn);

        JButton closeBtn = UITheme.ghostButton(com.worldofwonder.util.I18n.get("btn_close"), UITheme.TEXT_MUTED);
        UIUtil.fixedSize(closeBtn, 95, 42);
        closeBtn.addActionListener(e -> dispose());
        bar.add(closeBtn);

        return bar;
    }

    private void refreshTable() {
        allUsers = gameController.getAllUsers();
        filterTable();
    }

    private void filterTable() {
        String filter = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        tableModel.setRowCount(0);

        List<User> list = allUsers;
        if (!filter.isEmpty() && list != null) {
            list = list.stream().filter(u ->
                    (u.getUsername() != null && u.getUsername().toLowerCase().contains(filter)) ||
                    (u.getEmail() != null && u.getEmail().toLowerCase().contains(filter))
            ).collect(Collectors.toList());
        }

        if (list != null) {
            for (User u : list) {
                tableModel.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getEmail() != null ? u.getEmail() : "-",
                        u.getTotalPoints(),
                        u.getRankTitle(),
                        u.isAdmin() ? com.worldofwonder.util.I18n.get("admin_role_admin") : com.worldofwonder.util.I18n.get("admin_role_player")
                });
            }
            if (countLabel != null) {
                countLabel.setText("(" + list.size() + " users)");
            }
        }
    }


    private User getSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (allUsers != null) {
            for (User u : allUsers) {
                if (u.getId() == id) {
                    return u;
                }
            }
        }
        return null;
    }

    private void showAddUserDialog() {
        JDialog dlg = new JDialog(this, "Add New User", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 14));
        p.setBackground(new Color(20, 39, 61));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JTextField userField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField ptsField = new JTextField("0");
        JCheckBox adminCheck = new JCheckBox("Administrator role");
        adminCheck.setOpaque(false);
        adminCheck.setForeground(Color.WHITE);

        addFormField(p, "Username:", userField);
        addFormField(p, "Email:", emailField);
        addFormField(p, "Password:", passField);
        addFormField(p, "Initial Points:", ptsField);
        p.add(new JLabel("Admin Privileges:"));
        p.add(adminCheck);

        JButton saveBtn = UITheme.accentButton("Create Account", UITheme.GREEN);
        saveBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String mail = emailField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();
            if (u.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int pts = 0;
            try {
                pts = Integer.parseInt(ptsField.getText().trim());
            } catch (NumberFormatException ignored) {}

            User newUser = new User(u, mail, pwd);
            newUser.setTotalPoints(pts);
            newUser.setAdmin(adminCheck.isSelected());
            try {
                gameController.createUser(newUser);
                refreshTable();
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "User " + u + " created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Creation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(saveBtn);
        JButton cancelBtn = UITheme.ghostButton("Cancel", UITheme.TEXT_MUTED);
        cancelBtn.addActionListener(e -> dlg.dispose());
        p.add(cancelBtn);

        dlg.setContentPane(p);
        dlg.setVisible(true);
    }

    private void showEditUserDialog() {
        User user = getSelectedUser();
        if (user == null) return;

        JDialog dlg = new JDialog(this, "Edit User: " + user.getUsername(), true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 14));
        p.setBackground(new Color(20, 39, 61));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JTextField userField = new JTextField(user.getUsername());
        JTextField emailField = new JTextField(user.getEmail() != null ? user.getEmail() : "");
        JPasswordField passField = new JPasswordField(user.getPassword());
        JTextField ptsField = new JTextField(String.valueOf(user.getTotalPoints()));
        JCheckBox adminCheck = new JCheckBox("Administrator role", user.isAdmin());
        adminCheck.setOpaque(false);
        adminCheck.setForeground(Color.WHITE);

        addFormField(p, "Username:", userField);
        addFormField(p, "Email:", emailField);
        addFormField(p, "Password:", passField);
        addFormField(p, "Total Points:", ptsField);
        p.add(new JLabel("Admin Privileges:"));
        p.add(adminCheck);

        JButton saveBtn = UITheme.accentButton("Save Changes", UITheme.GOLD);
        saveBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String mail = emailField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();
            if (u.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int pts = user.getTotalPoints();
            try {
                pts = Integer.parseInt(ptsField.getText().trim());
            } catch (NumberFormatException ignored) {}

            user.setUsername(u);
            user.setEmail(mail);
            if (!pwd.isEmpty()) {
                user.setPassword(pwd);
            }
            user.setTotalPoints(pts);
            user.setAdmin(adminCheck.isSelected());

            try {
                gameController.updateUser(user);
                refreshTable();
                if (dashboard != null && user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
                    dashboard.updateScore(pts);
                }
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(saveBtn);
        JButton cancelBtn = UITheme.ghostButton("Cancel", UITheme.TEXT_MUTED);
        cancelBtn.addActionListener(e -> dlg.dispose());
        p.add(cancelBtn);

        dlg.setContentPane(p);
        dlg.setVisible(true);
    }

    private void awardBonusPoints(int pts) {
        User user = getSelectedUser();
        if (user == null) return;
        user.setTotalPoints(user.getTotalPoints() + pts);
        gameController.updateUser(user);
        refreshTable();
        if (dashboard != null && user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            dashboard.updateScore(user.getTotalPoints());
        }
        JOptionPane.showMessageDialog(this, "Awarded +" + pts + " points to " + user.getUsername() + "!", "Points Awarded", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetPoints() {
        User user = getSelectedUser();
        if (user == null) return;
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset points for " + user.getUsername() + " to 0?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            user.setTotalPoints(0);
            gameController.updateUser(user);
            refreshTable();
            if (dashboard != null && user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
                dashboard.updateScore(0);
            }
        }
    }

    private void deleteSelectedUser() {
        User user = getSelectedUser();
        if (user == null) return;

        if (user.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own active administrator account!", "Action Forbidden", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete user '" + user.getUsername() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            gameController.deleteUser(user.getId());
            refreshTable();
            JOptionPane.showMessageDialog(this, "User " + user.getUsername() + " deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addFormField(JPanel p, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setForeground(UITheme.ICE);
        l.setFont(UITheme.bodyFont(Font.BOLD, 13));
        p.add(l);
        p.add(field);
    }
}

