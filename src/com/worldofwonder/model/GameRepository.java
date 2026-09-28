package com.worldofwonder.model;

import com.worldofwonder.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Model repository managing static game data (worlds, levels, crossword puzzles)
 * loaded into ArrayLists from clean JSON files.
 */
public class GameRepository {

    private final List<World> worlds = new CopyOnWriteArrayList<>();
    private final List<Level> levels = new CopyOnWriteArrayList<>();
    private final List<WowLevel> wowLevels = new CopyOnWriteArrayList<>();

    public GameRepository() {
        loadData();
    }

    public synchronized void reload() {
        loadData();
    }

    private void loadData() {
        File dataDir = resolveDataDirectory();

        // 1. Worlds
        File worldsFile = new File(dataDir, "worlds.json");
        worlds.clear();
        if (worldsFile.exists()) {
            try {
                String json = JsonUtil.readFile(worldsFile);
                List<Map<String, Object>> list = JsonUtil.parseList(json);
                for (Map<String, Object> map : list) {
                    World w = new World();
                    w.setId(JsonUtil.getAsInt(map, "id", 0));
                    w.setName(JsonUtil.getAsString(map, "name", ""));
                    w.setDescription(JsonUtil.getAsString(map, "description", ""));
                    worlds.add(w);
                }
            } catch (IOException e) {
                System.err.println("Failed to load worlds.json: " + e.getMessage());
            }
        }

        // 2. Levels
        File levelsFile = new File(dataDir, "levels.json");
        levels.clear();
        if (levelsFile.exists()) {
            try {
                String json = JsonUtil.readFile(levelsFile);
                List<Map<String, Object>> list = JsonUtil.parseList(json);
                for (Map<String, Object> map : list) {
                    Level l = new Level();
                    l.setId(JsonUtil.getAsInt(map, "id", 0));
                    l.setWorldId(JsonUtil.getAsInt(map, "worldId", 0));
                    l.setName(JsonUtil.getAsString(map, "name", ""));
                    l.setDifficulty(JsonUtil.getAsString(map, "difficulty", "easy"));
                    l.setPointReward(JsonUtil.getAsInt(map, "pointReward", 10));
                    levels.add(l);
                }
            } catch (IOException e) {
                System.err.println("Failed to load levels.json: " + e.getMessage());
            }
        }

        // 3. WOW Levels
        File wowFile = new File(dataDir, "wow_levels.json");
        wowLevels.clear();
        if (wowFile.exists()) {
            try {
                String json = JsonUtil.readFile(wowFile);
                List<Map<String, Object>> list = JsonUtil.parseList(json);
                for (Map<String, Object> map : list) {
                    WowLevel wl = new WowLevel();
                    wl.setId(JsonUtil.getAsInt(map, "id", 0));
                    wl.setWorldId(JsonUtil.getAsInt(map, "worldId", 0));
                    wl.setName(JsonUtil.getAsString(map, "name", ""));
                    wl.setDifficulty(JsonUtil.getAsString(map, "difficulty", "easy"));
                    wl.setTheme(JsonUtil.getAsString(map, "theme", ""));
                    wl.setWords(JsonUtil.getAsString(map, "words", "[]"));
                    wl.setPointReward(JsonUtil.getAsInt(map, "pointReward", 80));
                    wowLevels.add(wl);
                }
            } catch (IOException e) {
                System.err.println("Failed to load wow_levels.json: " + e.getMessage());
            }
        }
    }

    public static File resolveDataDirectory() {
        String[] candidates = {
            "data",
            "backend/data",
            "../data",
            System.getProperty("user.dir") + "/data",
            System.getProperty("user.dir") + "/backend/data"
        };
        for (String p : candidates) {
            File d = new File(p);
            if (d.exists() && d.isDirectory() && new File(d, "worlds.json").exists()) {
                return d;
            }
        }
        File defaultDir = new File("data");
        defaultDir.mkdirs();
        return defaultDir;
    }

    public List<World> getAllWorlds() {
        return new ArrayList<>(worlds);
    }

    public List<Level> getLevelsByWorldId(int worldId) {
        List<Level> result = new ArrayList<>();
        for (Level l : levels) {
            if (l.getWorldId() == worldId) {
                result.add(l);
            }
        }
        return result;
    }

    public Level findLevelById(int levelId) {
        for (Level l : levels) {
            if (l.getId() == levelId) {
                return l;
            }
        }
        return null;
    }

    public List<WowLevel> getAllWowLevels() {
        return new ArrayList<>(wowLevels);
    }

    public List<WowLevel> getWowLevelsByWorldId(int worldId) {
        List<WowLevel> result = new ArrayList<>();
        for (WowLevel wl : wowLevels) {
            if (wl.getWorldId() == worldId) {
                result.add(wl);
            }
        }
        return result;
    }

    public WowLevel findWowLevelById(int wowLevelId) {
        for (WowLevel wl : wowLevels) {
            if (wl.getId() == wowLevelId) {
                return wl;
            }
        }
        return null;
    }
}

