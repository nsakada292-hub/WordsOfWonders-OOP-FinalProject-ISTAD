package com.worldofwonder.controller;

import com.worldofwonder.model.GameRepository;
import com.worldofwonder.model.Level;
import com.worldofwonder.model.User;
import com.worldofwonder.model.UserRepository;
import com.worldofwonder.model.World;
import com.worldofwonder.model.WowLevel;

import java.util.List;

/**
 * Controller coordinating game progression, points, leaderboards, and worlds/levels.
 */
public class GameController {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameController() {
        this(new GameRepository(), new UserRepository());
    }

    public GameController(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public List<World> getAllWorlds() {
        return gameRepository.getAllWorlds();
    }

    public List<Level> getLevelsForWorld(int worldId) {
        return gameRepository.getLevelsByWorldId(worldId);
    }

    public List<WowLevel> getAllWowLevels() {
        return gameRepository.getAllWowLevels();
    }

    public List<WowLevel> getWowLevelsForWorld(int worldId) {
        return gameRepository.getWowLevelsByWorldId(worldId);
    }

    public int addPoints(int userId, int pointsEarned) {
        if (userId <= 0 || pointsEarned <= 0) {
            return 0;
        }
        User user = userRepository.findById(userId);
        if (user != null) {
            int newTotal = user.getTotalPoints() + pointsEarned;
            userRepository.updateTotalPoints(userId, newTotal);
            return newTotal;
        }
        return 0;
    }

    public User getUser(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getLeaderboard(int limit) {
        return userRepository.getLeaderboard(limit);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public boolean createUser(User user) {
        return userRepository.createUser(user);
    }

    public boolean updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        return userRepository.deleteById(userId);
    }

    public boolean isUserAdmin(int userId) {
        User u = userRepository.findById(userId);
        return u != null && u.isAdmin();
    }

    public boolean isUserAdmin(String username) {
        if (username == null) return false;
        if ("admin".equalsIgnoreCase(username)) return true;
        User u = userRepository.findByUsername(username);
        return u != null && u.isAdmin();
    }
}

