package com.worldofwonder.model;

public class Level {

    private int id;
    private int worldId;
    private String name;
    private String difficulty;
    private int pointReward;

    public Level() {
    }

    public Level(String name, int worldId, String difficulty, int pointReward) {
        this.worldId = worldId;
        this.name = name;
        this.difficulty = difficulty;
        this.pointReward = pointReward;
    }

    public Level(int id, int worldId, String name, String difficulty, int pointReward) {
        this.id = id;
        this.worldId = worldId;
        this.name = name;
        this.difficulty = difficulty;
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

    public int getPointReward() {
        return pointReward;
    }

    public void setPointReward(int pointReward) {
        this.pointReward = pointReward;
    }
}
