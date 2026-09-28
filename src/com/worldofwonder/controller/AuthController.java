package com.worldofwonder.controller;

import com.worldofwonder.model.User;
import com.worldofwonder.model.UserRepository;

/**
 * Controller handling user authentication, session state, and input validation.
 */
public class AuthController {

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    private final UserRepository userRepository;
    private User currentUser;

    public AuthController() {
        this(new UserRepository());
    }

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AuthResult login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_empty_login"), null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_empty_login"), null);
        }

        String trimmedUser = username.trim();

        User user = userRepository.findByUsername(trimmedUser);
        if (user == null) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_user_not_found"), null);
        }

        if (!userRepository.verifyPassword(user, password)) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_wrong_pass"), null);
        }

        this.currentUser = user;
        return new AuthResult(true, com.worldofwonder.util.I18n.get("msg_login_success", user.getUsername()), user);
    }

    public AuthResult register(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_empty_register"), null);
        }
        if (email == null || email.trim().isEmpty()) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_empty_register"), null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_empty_register"), null);
        }

        String cleanUsername = username.trim();
        String cleanEmail = email.trim();

        if (cleanUsername.length() < 3) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_user_short"), null);
        }
        if (!cleanUsername.matches("^[a-zA-Z0-9_.-]+$")) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_user_chars"), null);
        }
        if (!cleanEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_email_invalid"), null);
        }
        if (password.length() < 4) {
            return new AuthResult(false, com.worldofwonder.util.I18n.get("err_pass_short"), null);
        }

        try {
            User newUser = new User(cleanUsername, cleanEmail, password);
            newUser.setTotalPoints(0);
            newUser.setAdmin(false);
            userRepository.createUser(newUser);
            this.currentUser = newUser;
            return new AuthResult(true, com.worldofwonder.util.I18n.get("msg_register_success", newUser.getUsername()), newUser);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if ("Username already exists".equalsIgnoreCase(msg)) {
                msg = com.worldofwonder.util.I18n.get("err_user_exists");
            } else if ("Email is already registered".equalsIgnoreCase(msg)) {
                msg = com.worldofwonder.util.I18n.get("err_email_exists");
            }
            return new AuthResult(false, msg, null);
        } catch (Exception e) {
            return new AuthResult(false, "Failed to register: " + e.getMessage(), null);
        }
    }

    public User createGuestSession() {
        this.currentUser = new User(0, "Guest", "", "", 0);
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }
}

