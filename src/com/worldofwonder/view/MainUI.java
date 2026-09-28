package com.worldofwonder.view;

import com.worldofwonder.model.*;
import com.worldofwonder.controller.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainUI extends JFrame {

    public static final String SCREEN_WELCOME = "welcome";
    public static final String SCREEN_DASHBOARD = "dashboard";
    public static final String SCREEN_WORDS = "words";

    private final CardLayout cards;
    private final JPanel cardsPanel;
    private final Dashboard dashboard;
    private final UITheme.FloatRoot background;
    private String currentScreen;

    private final AuthController authController;
    private final GameController gameController;

    public MainUI() {
        super("World of Wonder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(768, 580));

        this.authController = new AuthController();
        this.gameController = new GameController();

        this.cards = new CardLayout();
        this.cardsPanel = new JPanel(cards);
        this.cardsPanel.setOpaque(false);
        this.dashboard = new Dashboard(this);

        cardsPanel.add(new WelcomeScreen(this), SCREEN_WELCOME);
        cardsPanel.add(dashboard, SCREEN_DASHBOARD);
        cardsPanel.add(new WordsOfWondersGameScreen(dashboard), SCREEN_WORDS);

        this.background = UITheme.animatedRoot(new BorderLayout());
        background.add(cardsPanel, BorderLayout.CENTER);
        setContentPane(background);
        setSize(1920, 1080);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        showScreen(SCREEN_WELCOME);
        setVisible(true);
    }

    public void showScreen(String name) {
        this.currentScreen = name;
        cards.show(cardsPanel, name);
        if (background != null) {
            background.setTheme(name);
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /** The route currently on screen: welcome, dashboard or words. */
    public String getCurrentScreen() {
        return currentScreen;
    }

    public void showDashboard() {
        showScreen(SCREEN_DASHBOARD);
    }

    public void showWelcome() {
        showScreen(SCREEN_WELCOME);
    }

    public void enterDashboard(String username, boolean isGuest, int userId, String token, int totalPoints) {
        dashboard.setUser(username, isGuest, userId, token, totalPoints);
        showScreen(SCREEN_DASHBOARD);
    }

    public AuthController getAuthController() {
        return authController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
