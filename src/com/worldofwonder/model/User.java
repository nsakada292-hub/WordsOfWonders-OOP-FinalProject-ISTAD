package com.worldofwonder.model;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private int totalPoints;
    private boolean isAdmin;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(int id, String username, String email, String password, int totalPoints) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.totalPoints = totalPoints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getRankTitle() {
        if (totalPoints >= 600) return com.worldofwonder.util.I18n.get("rank_legendary");
        if (totalPoints >= 300) return com.worldofwonder.util.I18n.get("rank_gold");
        if (totalPoints >= 150) return com.worldofwonder.util.I18n.get("rank_silver");
        if (totalPoints >= 50) return com.worldofwonder.util.I18n.get("rank_bronze");
        return com.worldofwonder.util.I18n.get("rank_novice");
    }

    private int hearts = 5;           // Max 5 hearts, lose 1 per wrong answer
    private int hintCoins = 0;         // Currency for hints/power-ups
    private String avatarUrl;          // Cached avatar URL or local path

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public int getHintCoins() {
        return hintCoins;
    }

    public void setHintCoins(int hintCoins) {
        this.hintCoins = hintCoins;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // Lose a heart (minimum 0)
    public void loseHeart() {
        if (hearts > 0) hearts--;
    }

    // Restore all hearts to 5
    public void restoreHearts() {
        hearts = 5;
    }

    // Check if player has hearts remaining
    public boolean hasHearts() {
        return hearts > 0;
    }

    // Add hint coins
    public void addHintCoins(int amount) {
        if (amount > 0) hintCoins += amount;
    }

    // Spend hint coins (returns true if successful)
    public boolean spendHintCoins(int cost) {
        if (cost > 0 && hintCoins >= cost) {
            hintCoins -= cost;
            return true;
        }
        return false;
    }

}
