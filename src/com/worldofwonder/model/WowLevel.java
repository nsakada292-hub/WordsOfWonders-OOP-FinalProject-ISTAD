package com.worldofwonder.model;

public class WowLevel {

    private int id;
    private int worldId;
    private String name;
    private String difficulty;
    private String theme;
    private String words;
    private int pointReward;

    public WowLevel() {
    }

    public WowLevel(int id, int worldId, String name, String difficulty, String theme, String words, int pointReward) {
        this.id = id;
        this.worldId = worldId;
        this.name = name;
        this.difficulty = difficulty;
        this.theme = theme;
        this.words = words;
        this.pointReward = pointReward;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public int getPointReward() {
        return pointReward;
    }

    public void setPointReward(int pointReward) {
        this.pointReward = pointReward;
    }
}
