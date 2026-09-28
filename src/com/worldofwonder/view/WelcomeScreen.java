package com.worldofwonder.view;

import com.worldofwonder.model.*;
import com.worldofwonder.controller.*;
import com.worldofwonder.util.I18n;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

public class WelcomeScreen extends JPanel {

    private static final int TAB_LOGIN = 0;
    private static final int TAB_REGISTER = 1;

    private final MainUI app;

    private final UITheme.PillField loginUser;
    private final UITheme.PillPasswordField loginPass;
    private final JLabel loginStatus;
    private final JButton loginButton;

    private final UITheme.PillField regUser;
    private final UITheme.PillField regEmail;
    private final UITheme.PillPasswordField regPass;
    private final JLabel regStatus;
    private final JButton registerButton;

    private final UITheme.SegmentTabs tabs;
    private final UITheme.FadeCards forms;
    private int activeTab = TAB_LOGIN;

    // Localizable UI components
    private JLabel badge;
    private JLabel tagline;
    private JButton forgotButton;
    private JButton guestButton;
    private JLabel guestNote;
    private JLabel regHint;
    private JLabel dividerLabel;
    private JButton langToggleBtn;

    public WelcomeScreen(MainUI app) {
        super(new BorderLayout());
        this.app = app;
        setOpaque(false);

        // Real-life vector icons: User avatar for Username, Padlock for Password, Envelope for Email
        loginUser = UITheme.pillField(I18n.get("placeholder_user"), UITheme.FieldIcon.USER);
        loginPass = UITheme.pillPassword(I18n.get("placeholder_pass"), UITheme.FieldIcon.PASSWORD);
        loginUser.setFont(fieldFont());
        loginPass.setFont(fieldFont());
        loginStatus = statusLabel();
        loginButton = UITheme.glowButton(I18n.get("btn_login"), UITheme.TEAL, UITheme.VIOLET);
        loginButton.setFont(buttonFont());

        regUser = UITheme.pillField(I18n.get("placeholder_user"), UITheme.FieldIcon.USER);
        regEmail = UITheme.pillField(I18n.get("placeholder_email"), UITheme.FieldIcon.EMAIL);
        regPass = UITheme.pillPassword(I18n.get("placeholder_pass"), UITheme.FieldIcon.PASSWORD);
        regUser.setFont(fieldFont());
        regEmail.setFont(fieldFont());
        regPass.setFont(fieldFont());
        regStatus = statusLabel();
        registerButton = UITheme.glowButton(I18n.get("btn_register"), UITheme.VIOLET, UITheme.PINK);
        registerButton.setFont(buttonFont());

        // Attach auto-clearing error highlights when user types in fields
        attachClearErrorOnType(loginUser);
        attachClearErrorOnType(loginPass);
        attachClearErrorOnType(regUser);
        attachClearErrorOnType(regEmail);
        attachClearErrorOnType(regPass);

        tabs = (UITheme.SegmentTabs) UITheme.segmentTabs(
                new String[]{I18n.get("tab_login"), I18n.get("tab_register")},
                index -> showTab(index == TAB_LOGIN ? TAB_LOGIN : TAB_REGISTER));
        tabs.setPreferredSize(new Dimension(360, 52));
        tabs.setMinimumSize(new Dimension(320, 52));
        tabs.setMaximumSize(new Dimension(360, 52));

        forms = (UITheme.FadeCards) UITheme.fadeCards(
                new JComponent[]{buildLoginForm(), buildRegisterForm()});

        forms.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel card = UITheme.glowCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 36, 24, 36));
        card.setPreferredSize(new Dimension(590, 640));
        card.setMinimumSize(new Dimension(360, 480));
        card.add(buildContent(), BorderLayout.CENTER);

        JPanel root = UITheme.screenPage(card);
        add(root, BorderLayout.CENTER);

        // Register dynamic language listener
        I18n.addLanguageListener(this::updateLanguageTexts);
    }

    private void attachClearErrorOnType(UITheme.PillField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clear(); }
            @Override public void removeUpdate(DocumentEvent e) { clear(); }
            @Override public void changedUpdate(DocumentEvent e) { clear(); }
            private void clear() {
                field.setError(false);
                loginStatus.setText(" ");
                regStatus.setText(" ");
            }
        });
    }

    private void attachClearErrorOnType(UITheme.PillPasswordField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clear(); }
            @Override public void removeUpdate(DocumentEvent e) { clear(); }
            @Override public void changedUpdate(DocumentEvent e) { clear(); }
            private void clear() {
                field.setError(false);
                loginStatus.setText(" ");
                regStatus.setText(" ");
            }
        });
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Top bar with language switcher toggle
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        langToggleBtn = UITheme.iconPillButton(UITheme.VectorIcon.GLOBE, I18n.isKhmer() ? "ភាសាខ្មែរ" : "English", UITheme.GOLD);
        langToggleBtn.setPreferredSize(new Dimension(130, 32));
        langToggleBtn.setToolTipText(I18n.get("tip_language"));
        langToggleBtn.addActionListener(e -> {
            SoundUtil.playClick();
            I18n.toggleLanguage();
        });
        topBar.add(langToggleBtn);
        content.add(topBar);

        content.add(Box.createVerticalStrut(6));

        badge = UITheme.badge(I18n.get("badge_adventure"), UITheme.GOLD);
        badge.setFont(UITheme.fontFor(badge.getText(), Font.BOLD, UITheme.FONT_BADGE));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(badge);
        content.add(Box.createVerticalStrut(12));

        JComponent logo = UITheme.logoRow(50, 26);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createVerticalStrut(8));

        tagline = new UITheme.GradientTextLabel(
                I18n.get("tagline"),
                UITheme.FONT_BODY, new Color(0x9fe7ff), new Color(0xffc93c));
        tagline.setFont(UITheme.fontFor(tagline.getText(), Font.BOLD, UITheme.FONT_BODY));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tagline);
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        tabs.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tabs);
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        forms.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(forms);
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        dividerLabel = new JLabel(I18n.get("divider_or"), SwingConstants.CENTER);
        JPanel div = UITheme.divider(I18n.get("divider_or"), UITheme.FONT_SMALL);
        div.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(div);
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        guestButton = UITheme.ghostButton(I18n.get("btn_guest"), UITheme.VIOLET);
        guestButton.setFont(buttonFont());
        UIUtil.fullWidth(guestButton, UITheme.BTN_H);
        guestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestButton.addActionListener(e -> enterDashboard("Guest", true, 0, null, 0));
        content.add(guestButton);
        content.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        guestNote = new JLabel(I18n.get("guest_note"), SwingConstants.CENTER);
        guestNote.setFont(UITheme.fontFor(guestNote.getText(), Font.PLAIN, UITheme.FONT_SMALL));
        guestNote.setForeground(UITheme.TEXT_MUTED);
        guestNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(guestNote);

        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel buildLoginForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(Box.createVerticalGlue());

        form.add(field(loginUser));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(loginPass));
        form.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JPanel forgotRow = new JPanel(new BorderLayout(0, 0));
        forgotRow.setOpaque(false);
        forgotRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        forgotRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        forgotButton = UITheme.linkButton(I18n.get("btn_forgot"));
        forgotButton.setFont(UITheme.fontFor(forgotButton.getText(), Font.PLAIN, 12));
        forgotButton.addActionListener(e -> showPasswordHelp());
        forgotRow.add(forgotButton, BorderLayout.EAST);

        form.add(forgotRow);
        form.add(Box.createVerticalStrut(6));

        loginStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(loginStatus);
        form.add(Box.createVerticalStrut(14));

        UIUtil.fullWidth(loginButton, UITheme.BTN_H);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());
        form.add(loginButton);

        form.add(Box.createVerticalGlue());

        loginUser.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());
        return form;
    }

    private JPanel buildRegisterForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(Box.createVerticalGlue());

        form.add(field(regUser));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(regEmail));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(regPass));
        form.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        regHint = new JLabel(I18n.get("reg_hint"), SwingConstants.CENTER);
        regHint.setFont(UITheme.fontFor(regHint.getText(), Font.PLAIN, UITheme.FONT_SMALL));
        regHint.setForeground(UITheme.TEXT_MUTED);
        regHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(regHint);
        form.add(Box.createVerticalStrut(8));

        regStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(regStatus);
        form.add(Box.createVerticalStrut(12));

        UIUtil.fullWidth(registerButton, UITheme.BTN_H);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> doRegister());
        form.add(registerButton);

        form.add(Box.createVerticalGlue());

        regUser.addActionListener(e -> doRegister());
        regEmail.addActionListener(e -> doRegister());
        regPass.addActionListener(e -> doRegister());
        return form;
    }

    private void updateLanguageTexts() {
        if (langToggleBtn != null) {
            langToggleBtn.setText(I18n.isKhmer() ? "ភាសាខ្មែរ" : "English");
            langToggleBtn.setToolTipText(I18n.get("tip_language"));
        }
        if (badge != null) {
            badge.setText(I18n.get("badge_adventure"));
            badge.setFont(UITheme.fontFor(badge.getText(), Font.BOLD, UITheme.FONT_BADGE));
        }
        if (tagline != null) {
            tagline.setText(I18n.get("tagline"));
            tagline.setFont(UITheme.fontFor(tagline.getText(), Font.BOLD, UITheme.FONT_BODY));
        }
        if (tabs != null) {
            tabs.setLabels(new String[]{I18n.get("tab_login"), I18n.get("tab_register")});
        }
        loginUser.setPlaceholder(I18n.get("placeholder_user"));
        loginPass.setPlaceholder(I18n.get("placeholder_pass"));
        regUser.setPlaceholder(I18n.get("placeholder_user"));
        regEmail.setPlaceholder(I18n.get("placeholder_email"));
        regPass.setPlaceholder(I18n.get("placeholder_pass"));

        if (loginButton != null) {
            loginButton.setText(I18n.get("btn_login"));
            loginButton.setFont(buttonFont());
        }
        if (registerButton != null) {
            registerButton.setText(I18n.get("btn_register"));
            registerButton.setFont(buttonFont());
        }
        if (forgotButton != null) {
            forgotButton.setText(I18n.get("btn_forgot"));
            forgotButton.setFont(UITheme.fontFor(forgotButton.getText(), Font.PLAIN, 12));
        }
        if (guestButton != null) {
            guestButton.setText(I18n.get("btn_guest"));
            guestButton.setFont(buttonFont());
        }
        if (guestNote != null) {
            guestNote.setText(I18n.get("guest_note"));
            guestNote.setFont(UITheme.fontFor(guestNote.getText(), Font.PLAIN, UITheme.FONT_SMALL));
        }
        if (regHint != null) {
            regHint.setText(I18n.get("reg_hint"));
            regHint.setFont(UITheme.fontFor(regHint.getText(), Font.PLAIN, UITheme.FONT_SMALL));
        }

        loginStatus.setText(" ");
        regStatus.setText(" ");

        revalidate();
        repaint();
    }

    private void showTab(int tab) {
        if (activeTab == tab) {
            return;
        }
        activeTab = tab;
        tabs.select(tab);
        forms.show(tab);
        loginUser.setError(false);
        loginPass.setError(false);
        regUser.setError(false);
        regEmail.setError(false);
        regPass.setError(false);
        loginStatus.setText(" ");
        regStatus.setText(" ");
        if (tab == TAB_LOGIN) {
            loginUser.requestFocusInWindow();
        } else {
            regUser.requestFocusInWindow();
        }
    }

    private void showPasswordHelp() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                I18n.get("recovery_title"), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel bg = UITheme.gradientPanel(new GridBagLayout());
        JPanel card = UITheme.glowCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 34, 26, 34));
        UIUtil.fixedSize(card, 480, 330);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = UITheme.badge(I18n.get("recovery_badge"), UITheme.GOLD);
        icon.setFont(UITheme.fontFor(icon.getText(), Font.BOLD, UITheme.FONT_BADGE));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(icon);
        inner.add(Box.createVerticalStrut(14));

        JLabel title = UITheme.title(I18n.get("recovery_title"), UITheme.FONT_SECTION);
        title.setFont(UITheme.fontFor(title.getText(), Font.BOLD, UITheme.FONT_SECTION));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        JLabel body = new JLabel(I18n.get("recovery_body"), SwingConstants.CENTER);
        body.setFont(UITheme.fontFor(body.getText(), Font.PLAIN, UITheme.FONT_BODY));
        body.setForeground(UITheme.TEXT_MUTED);
        body.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(body);
        inner.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        JButton ok = UITheme.glowButton(I18n.get("btn_got_it"), UITheme.TEAL, UITheme.VIOLET);
        ok.setFont(buttonFont());
        UIUtil.fullWidth(ok, UITheme.BTN_H);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(e -> dialog.dispose());
        inner.add(ok);

        card.add(inner, BorderLayout.CENTER);
        bg.add(card);
        dialog.setContentPane(bg);
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private JComponent field(JTextField field) {
        field.setPreferredSize(new Dimension(470, 66));
        field.setMinimumSize(new Dimension(200, 66));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        return field;
    }

    private JLabel statusLabel() {
        JLabel label = new JLabel(" ", SwingConstants.CENTER);
        label.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_BODY));
        label.setForeground(UITheme.ERROR);
        return label;
    }

    private static Font fieldFont() {
        return UITheme.bodyFont(Font.PLAIN, 18);
    }

    private static Font buttonFont() {
        return UITheme.displayFont(Font.BOLD, UITheme.FONT_BUTTON);
    }

    private void doLogin() {
        String username = loginUser.getText().trim();
        String password = new String(loginPass.getPassword());

        boolean userEmpty = username.isEmpty();
        boolean passEmpty = password.isEmpty();

        // Prevent empty or whitespace-only submission with instant visual error feedback
        if (userEmpty || passEmpty) {
            loginUser.setError(userEmpty);
            loginPass.setError(passEmpty);
            loginStatus.setText(I18n.get("err_empty_login"));
            if (userEmpty) {
                loginUser.requestFocusInWindow();
            } else {
                loginPass.requestFocusInWindow();
            }
            JOptionPane.showMessageDialog(this,
                    I18n.get("err_empty_login"),
                    I18n.get("title_login"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthController.AuthResult result = app.getAuthController().login(username, password);
        if (result.isSuccess()) {
            User user = result.getUser();
            JOptionPane.showMessageDialog(WelcomeScreen.this,
                    I18n.get("msg_login_success", user.getUsername()),
                    I18n.get("title_login_success"),
                    JOptionPane.INFORMATION_MESSAGE);
            enterDashboard(user.getUsername(), false, user.getId(), "session-token", user.getTotalPoints());
        } else {
            loginStatus.setText(result.getMessage());
            loginButton.setEnabled(true);
            loginPass.setText("");
            loginPass.requestFocusInWindow();
            JOptionPane.showMessageDialog(WelcomeScreen.this,
                    result.getMessage(),
                    I18n.get("title_login_failed"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRegister() {
        String username = regUser.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPass.getPassword());

        boolean userEmpty = username.isEmpty();
        boolean emailEmpty = email.isEmpty();
        boolean passEmpty = password.isEmpty();

        // Prevent empty or whitespace-only submission with instant visual error feedback
        if (userEmpty || emailEmpty || passEmpty) {
            regUser.setError(userEmpty);
            regEmail.setError(emailEmpty);
            regPass.setError(passEmpty);
            regStatus.setText(I18n.get("err_empty_register"));
            if (userEmpty) {
                regUser.requestFocusInWindow();
            } else if (emailEmpty) {
                regEmail.requestFocusInWindow();
            } else {
                regPass.requestFocusInWindow();
            }
            JOptionPane.showMessageDialog(this,
                    I18n.get("err_empty_register"),
                    I18n.get("title_register"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthController.AuthResult result = app.getAuthController().register(username, email, password);
        if (result.isSuccess()) {
            User user = result.getUser();
            JOptionPane.showMessageDialog(WelcomeScreen.this,
                    I18n.get("msg_register_success", user.getUsername()),
                    I18n.get("title_register_success"),
                    JOptionPane.INFORMATION_MESSAGE);
            enterDashboard(user.getUsername(), false, user.getId(), "session-token", user.getTotalPoints());
        } else {
            regStatus.setText(result.getMessage());
            registerButton.setEnabled(true);
            regPass.setText("");
            regPass.requestFocusInWindow();
            JOptionPane.showMessageDialog(WelcomeScreen.this,
                    result.getMessage(),
                    I18n.get("title_register_failed"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enterDashboard(String username, boolean isGuest, int userId, String token, int totalPoints) {
        app.enterDashboard(username, isGuest, userId, token, totalPoints);
    }
}
