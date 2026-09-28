package com.worldofwonder.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internationalization (I18n) manager supporting English and Khmer (ភាសាខ្មែរ).
 * Provides dynamic language switching with event notification.
 */
public class I18n {

    public enum Language {
        EN("English", "English"),
        KM("ភាសាខ្មែរ", "Khmer");

        private final String nativeName;
        private final String englishName;

        Language(String nativeName, String englishName) {
            this.nativeName = nativeName;
            this.englishName = englishName;
        }

        public String getNativeName() {
            return nativeName;
        }

        public String getEnglishName() {
            return englishName;
        }
    }

    private static Language currentLanguage = Language.EN;
    private static final List<Runnable> listeners = new ArrayList<>();
    private static final Map<String, Map<Language, String>> translations = new HashMap<>();

    static {
        // --- Header & Badges ---
        put("badge_adventure", "GLOBAL ADVENTURE", "ដំណើរផ្សងព្រេងពិភពលោក");
        put("tagline", "Travel the world. Answer the questions. Earn the stars.", "ធ្វើដំណើរជុំវិញពិភពលោក ឆ្លើយសំណួរ និងទទួលបានផ្កាយ។");

        // --- Tabs ---
        put("tab_login", "Login", "ចូលប្រើ");
        put("tab_register", "Register", "ចុះឈ្មោះ");

        // --- Fields & Placeholders ---
        put("placeholder_user", "Username", "ឈ្មោះអ្នកប្រើ");
        put("placeholder_pass", "Password", "ពាក្យសម្ងាត់");
        put("placeholder_email", "Email", "អ៊ីមែល");

        // --- Buttons ---
        put("btn_login", "Start Exploring", "ចាប់ផ្តើមរុករក");
        put("btn_register", "Create Account", "បង្កើតគណនី");
        put("btn_guest", "Play as Guest", "លេងជាភ្ញៀវ");
        put("btn_forgot", "Forgot password?", "ភ្លេចពាក្យសម្ងាត់?");
        put("btn_got_it", "Got it", "យល់ព្រម");
        put("btn_save_close", "Save & Close", "រក្សាទុក & បិទ");
        put("btn_close", "Close", "បិទ");
        put("btn_later", "Later", "ពេលក្រោយ");
        put("pts_suffix", "pts", "ពិន្ទុ");

        // --- Hints & Subtitles ---
        put("guest_note", "No account needed. Progress and points won't be saved.", "មិនចាំបាច់មានគណនីទេ។ ពិន្ទុនឹងមិនត្រូវបានរក្សាទុកឡើយ។");
        put("reg_hint", "Create a profile to save your progress.", "បង្កើតប្រវត្តិរូបដើម្បីរក្សាទុកការរីកចម្រើនរបស់អ្នក។");
        put("divider_or", "or", "ឬ");

        // --- Password Reset Dialog ---
        put("recovery_badge", "ACCOUNT RECOVERY", "ការសង្គ្រោះគណនី");
        put("recovery_title", "Forgot your password?", "តើអ្នកភ្លេចពាក្យសម្ងាត់មែនទេ?");
        put("recovery_body", "<html><div style='text-align:center'>Password resets are handled by your teacher or "
                + "administrator.<br>Ask them to reset your account and you can sign in "
                + "with a fresh password right away.</div></html>",
                "<html><div style='text-align:center'>ការកំណត់ពាក្យសម្ងាត់ឡើងវិញត្រូវបានគ្រប់គ្រងដោយគ្រូ ឬអ្នកគ្រប់គ្រង (Admin)។<br>"
                + "សូមស្នើសុំឱ្យពួកគាត់កំណត់គណនីឡើងវិញ ហើយអ្នកអាចចូលប្រើ<br>ជាមួយពាក្យសម្ងាត់ថ្មីបានភ្លាមៗ។</div></html>");

        // --- Validation & Error Messages ---
        put("err_empty_login", "Please enter both username and password.", "សូមបញ្ចូលទាំងឈ្មោះអ្នកប្រើ និងពាក្យសម្ងាត់។");
        put("err_empty_register", "All fields (username, email, and password) are required.", "សូមបំពេញគ្រប់ប្រអប់ (ឈ្មោះអ្នកប្រើ អ៊ីមែល និងពាក្យសម្ងាត់)។");
        put("err_user_short", "Username must be at least 3 characters", "ឈ្មោះអ្នកប្រើត្រូវមានយ៉ាងតិច ៣ តួអក្សរ");
        put("err_user_chars", "Username can only contain letters, numbers, and underscores", "ឈ្មោះអ្នកប្រើអាចមានតែអក្សរ លេខ និងសញ្ញា _ ប៉ុណ្ណោះ");
        put("err_email_invalid", "Please enter a valid email address (e.g. user@example.com)", "សូមបញ្ចូលអាសយដ្ឋានអ៊ីមែលដែលត្រឹមត្រូវ (ឧទាហរណ៍ user@example.com)");
        put("err_pass_short", "Password must be at least 4 characters", "ពាក្យសម្ងាត់ត្រូវមានយ៉ាងតិច ៤ តួអក្សរ");
        put("err_user_not_found", "User not found. Please check spelling or register.", "រកមិនឃើញអ្នកប្រើប្រាស់ទេ។ សូមពិនិត្យ ឬចុះឈ្មោះ។");
        put("err_wrong_pass", "Incorrect password. Please try again.", "ពាក្យសម្ងាត់មិនត្រឹមត្រូវទេ។ សូមព្យាយាមម្តងទៀត។");
        put("err_user_exists", "Username already exists", "ឈ្មោះអ្នកប្រើប្រាស់នេះមានរួចហើយ");
        put("err_email_exists", "Email is already registered", "អ៊ីមែលនេះត្រូវបានចុះឈ្មោះរួចហើយ");

        // --- Success Messages ---
        put("msg_login_success", "Login successful! Welcome back, {0}!", "ការចូលប្រើបានជោគជ័យ! សូមស្វាគមន៍, {0}!");
        put("msg_register_success", "Account created successfully! Welcome, {0}!", "បង្កើតគណនីបានជោគជ័យ! សូមស្វាគមន៍, {0}!");

        // --- Dialog Titles ---
        put("title_login", "Login", "ចូលប្រើ");
        put("title_login_success", "Login Successful", "ចូលប្រើបានជោគជ័យ");
        put("title_login_failed", "Login Failed", "ការចូលប្រើបានបរាជ័យ");
        put("title_register", "Registration", "ការចុះឈ្មោះ");
        put("title_register_success", "Registration Successful", "ចុះឈ្មោះបានជោគជ័យ");
        put("title_register_failed", "Registration Failed", "ការចុះឈ្មោះបានបរាជ័យ");

        // --- Navigation & Common Actions ---
        put("exit_to_games", "Exit to Games", "ចាកចេញទៅហ្គេម");
        put("back_to_dashboard", "Back to Dashboard", "ត្រឡប់ទៅផ្ទាំងដើម");
        put("settings", "Settings", "ការកំណត់");
        put("hall_of_fame", "Hall of Fame", "តារាងកិត្តិយស");
        put("admin_panel", "Admin Panel", "ផ្ទាំងគ្រប់គ្រង");
        put("logout", "Logout", "ចាកចេញ");
        put("total_points", "Total Points", "ពិន្ទុសរុប");
        put("welcome_user", "Welcome, {0}", "សូមស្វាគមន៍, {0}");
        put("rank_prefix", "Rank: {0}", "ចំណាត់ថ្នាក់: {0}");
        put("rank_novice", "Novice Explorer", "អ្នករុករកដំបូង");
        put("rank_bronze", "Bronze Adventurer", "អ្នកផ្សងព្រេងសំរិទ្ធ");
        put("rank_silver", "Silver Scholar", "អ្នកប្រាជ្ញប្រាក់");
        put("rank_gold", "Gold Master", "កំពូលអ្នកលេងមាស");
        put("rank_legendary", "Legendary Wonderer", "អ្នកអច្ឆរិយៈរឿងព្រេងនិទាន");
        put("play_now", "Play Now", "លេងឥឡូវនេះ");

        // --- Game Titles & Descriptions ---
        put("game_words_title", "Words of Wonders", "ពាក្យនៃភាពអស្ចារ្យ");
        put("game_words_sub", "Connect letters, find hidden words, and complete crossword puzzles.", "ភ្ជាប់តួអក្សរ ស្វែងរកពាក្យលាក់ និងបំពេញល្បែងផ្គុំពាក្យឆ្លាស់។");

        put("dashboard_hero_title", "Words of Wonders", "ពាក្យនៃភាពអស្ចារ្យ");
        put("dashboard_hero_sub", "Your only game - spin the letters and clear the board", "ហ្គេមតែមួយរបស់អ្នក - បង្វិលតួអក្សរ និងសម្អាតក្តារី");

        // --- Settings Modal ---
        put("settings_title", "Game Settings & Appearance", "ការកំណត់ហ្គេម & រូបរាង");
        put("settings_sub", "Customize visual themes, switch dark/light mode, and toggle audio.", "កំណត់រូបរាង ផ្លាស់ប្តូរពន្លឺ និងបើក/បិទសម្លេង។");
        put("settings_theme_title", "Visual Theme & Color Palette", "រចនាប័ទ្មពណ៌ និងរូបរាង");
        put("settings_lang_title", "Language / ភាសា", "ភាសា / Language");
        put("settings_audio_title", "Audio & Sound Effects", "សម្លេង និងបែបផែនសម្លេង");
        put("sfx_muted", "SFX: Muted (Off)", "សម្លេង: បិទ");
        put("sfx_enabled", "SFX: Enabled (On)", "សម្លេង: បើក");
        put("test_sound", "Play Test Chime", "សាកល្បងសម្លេង");
        put("sound_muted_title", "Sound Muted", "សម្លេងត្រូវបានបិទ");
        put("sound_muted_msg", "Turn sound on first to test the audio.", "សូមបើកសម្លេងជាមុនសិន ដើម្បីសាកល្បងសំឡេង។");
        put("lang_active", "{0} (Active)", "{0} (កំពុងប្រើ)");
        put("theme_midnight", "Midnight Nebula (Dark)", "មីដណាយ ណេប៊ុយឡា (ងងឹត)");
        put("theme_ocean", "Deep Ocean (Dark)", "មហាសមុទ្រជ្រៅ (ងងឹត)");
        put("theme_amethyst", "Royal Amethyst (Dark)", "រ៉ូយ៉ាល់ អាមេធីស (ងងឹត)");
        put("theme_daylight", "Daylight Crystal (Light)", "គ្រីស្តាល់ពន្លឺថ្ងៃ (ភ្លឺ)");
        put("theme_active", "[Active Theme]", "[រូបរាងកំពុងប្រើ]");
        put("theme_light_mode", "Light Mode", "ម៉ូដភ្លឺ");
        put("theme_dark_mode", "Dark Mode", "ម៉ូដងងឹត");

        // --- Difficulty ---
        put("diff_easy", "Easy", "ងាយស្រួល");
        put("diff_medium", "Medium", "មធ្យម");
        put("diff_hard", "Hard", "ពិបាក");

        // --- Worlds (Encyclopedia & Wonder Wheel) ---
        put("world_1_name", "Ancient Egypt", "អេហ្ស៊ីបបុរាណ");
        put("world_2_name", "Outer Space", "លំហអាកាស");
        put("world_3_name", "The Deep Ocean", "មហាសមុទ្រជ្រៅ");
        put("world_4_name", "Dinosaur World", "ពិភពដាយណូស័រ");
        put("world_5_name", "Medieval Kingdoms", "រាជាណាចក្រមជ្ឈិមសម័យ");
        put("world_6_name", "Rainforest Adventure", "ដំណើរផ្សងព្រេងព្រៃទឹកភ្លក់");

        // --- Words of Wonders Game Screen ---
        put("choose_puzzle", "Choose your puzzle", "ជ្រើសរើសល្បែងផ្គុំរបស់អ្នក");
        put("wow_diff_sub", "Match the letters, then cross out every hidden word.", "ផ្គូរតួអក្សរ រួចចោលពាក្យលាក់ទាំងអស់។");
        put("wow_bonus_jar", "Bonus Jar: {0}", "កន្ត្រកបន្ថែម: {0}");
        put("wow_shuffle", "Shuffle", "ច្របល់");
        put("wow_hint", "Hint (-15)", "ជំនួយ (-១៥)");
        put("wow_reveal", "Reveal Word (-30)", "បើកពាក្យ (-៣០)");
        put("wow_score", "Points: {0}", "ពិន្ទុ: {0}");
        put("wow_solved_title", "WONDER CONQUERED!", "ដោះស្រាយភាពអស្ចារ្យបានសម្រេច!");
        put("wow_solved_sub", "Crossword completed!", "បានបឆ្សេងពាក្យឆ្លាស់រួចរាល់!");
        put("wow_words_to_find", "{0} letters • {1} words to find", "{0} តួអក្សរ • {1} ពាក្យត្រូវស្វែងរក");
        put("wow_clear", "Clear", "សម្អាត");
        put("wow_play_again", "Play Again", "លេងម្តងទៀត");
        put("wow_change_diff", "Change Difficulty", "ប្តូរកម្រិតលឆ្នាំ");
        put("wow_new_puzzle", "New Puzzle", "ល្បគឺថ្មី");
        put("wow_solved_sub_desc", "Amazing word-finding skills!", "ជំនាញស្វែងរកពាក្យដ៏អស្ចារ្យ!");
        put("wow_found_word", "Found: {0}! +{1} pts", "រកឃើញ: {0}! +{1} ពិន្ទុ");
        put("wow_letter_revealed", "Letter revealed in #{0}! -{1} pts", "បានបើកអក្សរក្នុងពាក្យទី {0}! -{1} ពិន្ទុ");
        put("wow_word_revealed", "Revealed: {0}! net {1} pts", "បានបើកពាក្យ: {0}! ទទួលបាន {1} ពិន្ទុ");
        put("wow_progress", "{0} / {1} words", "{0} / {1} ពាក្យ");
        put("wow_not_enough_points", "Not enough points - find a word first!", "ពិន្ទុមិនគ្រប់គ្រាន់ - ស្វែងរកពាក្យមួយជាមុនឡើយ!");

        // --- Gameplay Feedback ---
        put("wow_not_in_puzzle", "Not in puzzle: {0}", "មិននៅក្នុងល្បគឺ: {0}");
        put("wow_already_found", "Already found: {0}", "បានរកឃើញរួចហើយ: {0}");
        put("wow_bonus_word", "Bonus word! '{0}' +10 pts", "ពាក្យបន្ថែម! '{0}' +១០ ពិន្ទុ");
        put("wow_bonus_already", "Already in Bonus Jar: {0}", "មានក្នុងកន្ត្របន្ថែមរួចហើយ: {0}");
        put("wow_bonus_new", "Bonus dictionary word! '{0}' +10 pts", "ពាក្យពីរយៈពាក្យធម្យម! '{0}' +១០ ពិន្ទុ");
        put("wow_done_summary", "You found all {0} words!", "អ្នកបានរកឃើញពាក្យទាំង {0} ពាក្យ!");
        put("wow_done_bonus", "Bonus Jar: {0} extra words collected!", "កន្ត្របន្ថែម: {0} ពាក្យបន្ថែម!");
        put("wow_done_earned", "Total Earned: +{0} points", "ទទួលបានសរុប: +{0} ពិន្ទុ");
        put("wow_rating_perfect", "PERFECT (3/3)", "ល្អបំផុត (៣/៣)");
        put("wow_rating_great", "GREAT JOB (2/3)", "ល្អណាស់ (២/៣)");
        put("wow_rating_solved", "SOLVED (1/3)", "បានដោះស្រាយ (១/៣)");
        put("wow_done_title_prefix", "Puzzle Complete! - {0}", "បានបញ្ចប់ល្បគឺ! - {0}");

        // --- Leaderboard & Daily Reward Modals ---
        put("leaderboard_title", "Hall of Fame - Top Adventurers", "តារាងកិត្តិយស - កំពូលអ្នកផ្សងព្រេង");
        put("leaderboard_sub", "Live global rankings across all games & challenges", "ចំណាត់ថ្នាក់សកលផ្ទាល់នៅគ្រប់ហ្គេម & ការប្រកួតប្រជែង");

        // --- Admin Control Modal ---
        put("admin_modal_title", "Admin Control Center", "មជ្ឈមណ្ឌលគ្រប់គ្រង Admin");
        put("admin_create_user", "Create User", "បង្កើតអ្នកប្រើ");
        put("admin_edit_user", "Edit", "កែប្រែ");
        put("admin_delete_user", "Delete", "លុប");
        put("admin_reset_pts", "Reset Pts", "កំណត់ពិន្ទុឡើងវិញ");
        put("admin_refresh", "Refresh", "ផ្ទុកឡើងវិញ");
        put("admin_col_id", "ID", "ល.រ");
        put("admin_col_user", "Username", "ឈ្មោះអ្នកប្រើ");
        put("admin_col_email", "Email", "អ៊ីមែល");
        put("admin_col_points", "Points", "ពិន្ទុ");
        put("admin_col_rank", "Rank", "ចំណាត់ថ្នាក់");
        put("admin_col_role", "Role", "តួនាទី");
        put("admin_role_admin", "ADMIN", "អ្នកគ្រប់គ្រង");
        put("admin_role_player", "Player", "អ្នកលេង");

        // --- Tooltips ---
        put("tip_show_password", "Show secret password", "បង្ហាញពាក្យសម្ងាត់");
        put("tip_hide_password", "Hide secret password", "លាក់ពាក្យសម្ងាត់");
        put("tip_language", "Switch language / ប្តូរភាសា", "Switch language / ប្តូរភាសា");

        // --- Encyclopedia / World Codex ---
        put("encyclopedia_title", "World Encyclopedia", "សព្វវចនាធិប្បាយពិភពលោក");
        put("encyclopedia_sub", "Discover fascinating facts about each world", "ស្វែងយល់ពីការពិតគួរឱ្យចាប់អារម្មណ៍អំពីពិភពនីមួយៗ");

        // --- Leaderboard Online ---
        put("leaderboard_local", "Local", "មូលដ្ឋាន");
        put("leaderboard_global", "Global", "ពិភពលោក");
        put("leaderboard_loading", "Loading global scores...", "កំពុងផ្ទុកពិន្ទុពិភពលោក...");

    }

    private static void put(String key, String en, String km) {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.EN, en);
        map.put(Language.KM, km);
        translations.put(key, map);
    }

    public static synchronized Language getLanguage() {
        return currentLanguage;
    }

    public static synchronized void setLanguage(Language lang) {
        if (lang != null && lang != currentLanguage) {
            currentLanguage = lang;
            for (Runnable listener : new ArrayList<>(listeners)) {
                try {
                    listener.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized void toggleLanguage() {
        setLanguage(currentLanguage == Language.EN ? Language.KM : Language.EN);
    }

    public static synchronized void addLanguageListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static synchronized void removeLanguageListener(Runnable listener) {
        listeners.remove(listener);
    }

    public static String get(String key) {
        Map<Language, String> map = translations.get(key);
        if (map != null) {
            String val = map.get(currentLanguage);
            if (val != null) return val;
            String enVal = map.get(Language.EN);
            if (enVal != null) return enVal;
        }
        return key;
    }

    public static String get(String key, Object... args) {
        String template = get(key);
        if (args == null || args.length == 0) return template;
        for (int i = 0; i < args.length; i++) {
            template = template.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return template;
    }

    public static boolean isKhmer() {
        return currentLanguage == Language.KM;
    }
}
