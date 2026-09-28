# Words of Wonders

A word-finding puzzle game built in **Java Swing** as a single self-contained
desktop application.

You are given a small circle of letters. You trace words out of those letters,
letter by letter, and try to find every hidden word. Find them all to complete
the puzzle.

The whole project is **24 Java files, about 11,000 lines**, and it has
**zero external libraries**. Everything — the JSON reader, the sound effects,
every icon, every animation — is written by hand using only the Java Standard
Library. That is why it compiles with a single `javac` command and needs no
Maven, Gradle, or internet connection to run.

---

## Table of Contents

1. [How to Run It](#1-how-to-run-it)
2. [The Five-Minute Tour](#2-the-five-minute-tour)
3. [Overall Architecture](#3-overall-architecture)
4. [The Four Layers in Detail](#4-the-four-layers-in-detail)
5. [Login and Registration](#5-login-and-registration)
6. [The Dashboard](#6-the-dashboard)
7. [How to Play: Words of Wonders](#7-how-to-play-words-of-wonders)
8. [Score, Hints and Finishing a Puzzle](#8-score-hints-and-finishing-a-puzzle)
9. [Settings](#9-settings)
10. [Leaderboard](#10-leaderboard)
11. [Encyclopedia](#11-encyclopedia)
12. [Admin Panel](#12-admin-panel)
13. [Languages](#13-languages)
14. [Sound Effects](#14-sound-effects)
15. [The Visual Design System](#15-the-visual-design-system)
16. [Data Files](#16-data-files)
17. [Testing](#17-testing)
18. [Project Layout](#18-project-layout)
19. [Honest Notes and Known Limits](#19-honest-notes-and-known-limits)
20. [Demo Script](#20-demo-script-for-a-presentation)

---

## 1. How to Run It

You need a **JDK 11 or newer** installed. Check with `java -version`.

### macOS / Linux

```bash
./run.sh
```

### Windows

```bash
cd to your-repository name

chmod +x run.sh

./run.sh
```

### What the scripts actually do

`run.sh` is only 22 lines. It does three things:

1. `cd` to its own folder, so the data files are always found.
2. Compile every `.java` file under `src/` into `bin/`.
3. Launch the app (or the test suite).

**The `-encoding UTF-8` flag is not optional.** The app contains Khmer text, and
without that flag the compiler will read those characters as garbage and either
fail or produce broken text. This is the single most important thing to know
about building this project.

`run.bat` does the same thing for Windows, but it **cannot run the tests** — it
only launches the app. So run the tests from macOS or Linux.

You can also do it by hand:

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin com.worldofwonder.Main
```

---

## 2. The Five-Minute Tour

Use these logins if you want them (all three are created automatically the
first time the app starts):

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Administrator |
| `bob` | `password456` | Player |
| `rithphea` | `password123` | Player |

Or just press **Continue as Guest** and skip the login entirely.

**The path through the app:**

```
Welcome Screen  →  Dashboard  →  Words of Wonders  →  Completion Screen
      ↑                                              │
      └──────────────────────────────────────────────┘
                   (Back to Dashboard)
```

From the Dashboard you can also open four pop-up windows: **Settings**,
**Encyclopedia**, **Leaderboard**, and **Admin Panel** (admin only).

---

## 3. Overall Architecture

### It uses the MVC pattern

**MVC** stands for **Model–View–Controller**. It is a way of splitting a program
into three jobs so each file has one clear responsibility.

| Layer | The job | Think of it as |
|---|---|---|
| **Model** | Holds the data and the rules about that data | The filing cabinet |
| **View** | Everything the user sees and touches | The shop front |
| **Controller** | Receives what the user did, then tells the Model what to do | The manager |

**Why bother?** Because it means you can change the look of the app without
touching the game logic, and change the game logic without touching the layout.
The two never get tangled together.

### The full flow, with a real example

When the player finds the word `HEART`, this is what happens, in order:

```
1. WordsOfWondersGameScreen   (View)
   The player traces H→E→A→R→T. The code notices the word is complete.
   It checks the target list and finds HEART is missing.
   It adds HEART to the found list and adds 20 points.

2. Dashboard                  (View)
   The points label is redrawn so the new score appears.

3. GameController             (Controller)
   Reads the user's new total and asks the Model to save it.

4. UserRepository             (Model)
   Rewrites data/users.json with the updated score.
```

The important rule: **the View never writes to a file directly.** It always asks
the Controller, which asks the Model. That is the whole point of MVC.

### The "zero external dependencies" claim, explained

When people say "no dependencies", they mean: open a fresh Java install, copy
this folder in, and it runs. Nothing needs downloading.

Here is what that meant in practice:

- **No JSON library.** Most Java projects download `Gson` or `Jackson` to read
  JSON files. This project instead contains its own JSON reader and writer
  (`JsonUtil.java`, 386 lines) that reads and writes files directly.
- **No sound files.** Instead of shipping `.wav` or `.mp3` files, every sound
  effect is *computed* as a waveform using sine waves and maths, then played
  through Java's built-in audio support (`SoundUtil.java`).
- **No icon files.** There are no `.png` icon assets. All 22 icons are drawn
  with Java's 2D drawing commands (`fillOval`, `drawRoundRect`, `fillArc`, and
  so on) in `UITheme.java`.
- **No build tool.** No `pom.xml`, no `build.gradle`. A plain `javac` command
  builds everything.
- **No image assets** (except one background photo, `Game4.jpeg`).

The only thing outside the Standard Library is Java's built-in
`java.net.http` client, used for optional online features.

### The design system in one paragraph

`UITheme.java` (4,335 lines) is the largest file in the project. It is the app's
entire visual language in one place: all colours, all font sizes, all spacing
values, all button styles, the card painting, the icons, the progress bar, the
confetti animation, and the animated background. Nothing else in the project
hard-codes a colour or a font size. If a button looks a certain way, the code
that did it is in `UITheme.java`, and every other button in the app matches
automatically.

---

## 4. The Four Layers in Detail

### 4.1 Model — the data

| File | Lines | What it does |
|---|---|---|
| `User.java` | 142 | One player: username, email, hashed password, points, admin flag |
| `UserRepository.java` | 249 | Loads and saves `users.json`; hashes and checks passwords |
| `World.java` | 46 | A theme world (Ancient Egypt, Outer Space, …) |
| `Level.java` | 68 | A level belonging to a world |
| `WowLevel.java` | 81 | A Words of Wonders puzzle definition from JSON |
| `GameRepository.java` | 162 | Loads the three level data files |

### 4.2 Controller — the rules

| File | Lines | What it does |
|---|---|---|
| `AuthController.java` | 125 | Login, registration, input validation, error messages |
| `GameController.java` | 94 | Adding points, leaderboard, user management for the admin panel |

Controllers never draw anything. They validate input, call the model, and
return a result object the view can read.

### 4.3 View — what you see

| File | Lines | What it does |
|---|---|---|
| `MainUI.java` | 93 | The main window; switches between the three screens |
| `WelcomeScreen.java` | 537 | Login and registration forms |
| `Dashboard.java` | 457 | The home screen after logging in |
| `WordsOfWondersGameScreen.java` | 1,037 | The actual game — the biggest feature file |
| `SettingsModal.java` | 312 | Theme, language, sound settings |
| `LeaderboardModal.java` | 322 | Top players list |
| `EncyclopediaModal.java` | 200 | Wonder articles |
| `AdminControlModal.java` | 439 | User management for admins |
| `UITheme.java` | 4,335 | The whole design system |
| `UIUtil.java` | 142 | Small layout helpers |
| `SoundUtil.java` | 257 | Generates and plays the sound effects |

### 4.4 Util — shared tools

| File | Lines | What it does |
|---|---|---|
| `JsonUtil.java` | 386 | Hand-written JSON reader and writer |
| `ApiService.java` | 193 | Optional online lookups (see note below) |
| `I18n.java` | 292 | All 142 English and Khmer text strings |

---

## 5. Login and Registration

This is the first screen the user sees. It has two tabs plus a guest option.

### What is on the screen

- A **segmented tab control** at the top switching between "Login" and
  "Register".
- **Login tab:** username field, password field (with an eye icon to reveal it),
  a "Forgot password?" link, and the Login button.
- **Register tab:** username, email, password, and Register button.
- **"Continue as Guest"** button below a divider, with a note explaining that
  guest progress is not saved.
- A **language toggle** (globe icon) in the corner.
- Enter works in either field — press Enter in the password box and it logs in.

### The exact rules for registering

The `register` method checks these seven things, in this order. The first one
that fails stops the process and shows an error:

| # | Rule | Error shown if broken |
|---|---|---|
| 1 | Username is not empty | "Please fill in all fields" |
| 2 | Email is not empty | "Please fill in all fields" |
| 3 | Password is not empty | "Please fill in all fields" |
| 4 | Username is at least 3 characters | "Username must be at least 3 characters" |
| 5 | Username uses only letters, numbers, `_`, `.`, `-` | "Username can only contain letters, numbers, _ . and -" |
| 6 | Email looks like a real email address | "Please enter a valid email address" |
| 7 | Password is at least 4 characters | "Password must be at least 4 characters" |

The email check is a regular expression:

```
^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
```

The `[A-Za-z]{2,}` at the end means the part after the final dot must be at
least two letters, so `a@b.c` is rejected but `a@b.co` is accepted.

**"Forgot password?"** does not send an email — there is no mail server. It
opens a small dialog that tells the user to ask their teacher, which is the
honest behaviour for an offline app.

### The exact rules for logging in

Only three checks, in order:

1. Username and password are not empty.
2. The username exists in the database (case does not matter — `Admin` and
   `admin` are the same account).
3. The password matches.

### How passwords are stored

**Passwords are never saved as plain text.** When a password is stored, the app
takes its SHA-256 fingerprint — a 64-character hexadecimal string — and saves
*that* instead. So `admin123` is not in `users.json`; this is:

```
240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
```

When you log in, the app hashes what you typed and compares the two fingerprints.
This means nobody reading the JSON file — including someone who opens the file in
Notepad — can read anyone's password.

**How to verify this yourself:** the SHA-256 of `admin123` really is that value
above. Type `admin123` into any online SHA-256 tool and you will get the same
result.

There is also an **automatic upgrade path**: if an old `users.json` from a
previous version still contains a plain-text password, the app accepts it once
and then immediately rewrites it as a hash. You never notice, but the file
self-heals.

### Where accounts are saved

Everything is in one file, `data/users.json`. Here is a real entry:

```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "password": "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9",
  "totalPoints": 780,
  "isAdmin": true
}
```

| Field | Meaning |
|---|---|
| `id` | The account number. New accounts get the highest existing number + 1. |
| `username` | The login name. Compared without case sensitivity. |
| `email` | Checked for uniqueness, but never used to log in or to send mail. |
| `password` | The SHA-256 hash described above. |
| `totalPoints` | Lifetime points. Drives the rank and the leaderboard. |
| `isAdmin` | `true` shows the Admin Panel button. |

**The app is fully multi-user.** If you register `sara` on the welcome screen and
then open the Admin Panel, `sara` is there — the admin panel reads the same file
the registration form just wrote.

**How saving works:** the app keeps all users in memory, and rewrites the whole
file every time something changes. It does this safely: it writes to a temporary
file first, then swaps it into place. If the app crashes halfway through, the
original file is still intact.

### Accounts created automatically

If `users.json` is missing or empty, the app creates three accounts so there is
always something to log in with. This is why the demo logins above always work.

---

## 6. The Dashboard

The Dashboard is what you see after logging in.

### The layout

```
┌──────────────────────────────────────────────────────┐
│ (avatar)  sna          EN/KM  Encyclopedia Settings   │  ← top bar
│          Nova · Gold             Trophy  Shield  Exit │
├──────────────────────────────────────────────────────┤
│                                                      │
│                  WORDS OF WONDERS                    │
│              Your only game - spin the letters       │
│                    and clear the board                │
│              ┌────────────────────────────┐          │
│              │         ▶ PLAY NOW          │          │  ← the hero card
│              └────────────────────────────┘          │
│                                                      │
│        Did you know? A curious fact appears here.    │  ← online fact
└──────────────────────────────────────────────────────┘
```

### The top bar

- **Avatar** — a generated picture, or your first letter as a fallback.
- **Your name and rank** — the rank comes from your total points.
- **Language toggle** — switches between English and Khmer instantly.
- **Encyclopedia** (magnifier icon)
- **Settings** (gear icon)
- **Hall of Fame** (trophy icon)
- **Admin Panel** (shield icon) — **only** for admin accounts.
- **Points** — a gold coin and your score, in the top right.
- **Log out** (arrow icon)

### The hero card

There is **exactly one** game, so the Dashboard shows **exactly one large
card**: Words of Wonders. Clicking it (or the Play Now button on it) opens the
game.

The card is deliberately responsive. It is 860 px wide on a big monitor and
shrinks smoothly on a small window, so it never gets cut off and the Dashboard
never needs a scrollbar.

### The rank system

Your rank is calculated from your total points:

| Points | Rank |
|---|---|
| 600+ | Legendary |
| 300+ | Gold |
| 150+ | Silver |
| 50+ | Bronze |
| Below 50 | Novice |

The same thresholds appear in the Admin Panel's Rank column.

### The "Did you know?" line

A curious fact is fetched from a free online API and shown under the card. If
there is no internet connection, the line is simply left blank — nothing breaks.

---

## 7. How to Play: Words of Wonders

### The idea

You get a small circle of **5 or 6 letters**. Some words are "hidden" — they can
be spelled using only those letters, in any order, reusing a letter only if it
appears twice.

Your job is to find **all 9 hidden words**.

**Example — the easiest puzzle.** The letters are `E A R T H`.

The hidden words are: `EARTH, HEART, HATER, TEAR, HEAR, RATE, HARE, TARE, HART`

Notice how they all use the same five letters, just rearranged. `HEART`,
`HATER`, and `HART` all come from the same circle. That is the whole trick.

### Choosing a difficulty

When you press Play Now, three tiles appear. Each one tells you how many letters
it has and how many words to find — the number is read from the puzzle data
itself, so the label can never be wrong:

| Difficulty | Base word | Letters | Words |
|---|---|---|---|
| Easy | EARTH | 5 | 9 |
| Medium | GARDEN | 6 | 9 |
| Hard | GUITAR | 6 | 9 |

**A full list of the 8 puzzles** (the completion screen's "Play Again" button
picks a random one from all eight):

| # | Base word | Letters | The 9 words to find |
|---|---|---|---|
| 1 | EARTH | 5 | EARTH, HEART, HATER, TEAR, HEAR, RATE, HARE, TARE, HART |
| 2 | GARDEN | 6 | GARDEN, DANGER, RANGED, GRADE, RANGE, GRAND, READ, DEAR, AGED |
| 3 | LISTEN | 6 | LISTEN, SILENT, TINSEL, INLET, LENT, SENT, SITE, NEST, LINE |
| 4 | CASTLE | 6 | CASTLE, SCALE, LACES, LATE, SALE, SEAL, LACE, CAST, ACES |
| 5 | PLANET | 6 | PLANET, PLANT, LEAPT, PETAL, PLEAT, PLATE, PLANE, LANE, LEAP |
| 6 | GUITAR | 6 | GUITAR, GRIT, GAIT, TRIG, RUG, GUT, TAG, RAG, AIR |
| 7 | WONDER | 6 | WONDER, DROWN, ROWED, OWNER, WORN, WORD, NODE, DREW, RODE |
| 8 | FROZEN | 6 | FROZEN, FROZE, ZONE, ZERO, FORE, FERN, FOE, ONE, ORE |

Every word in a puzzle is made only from that puzzle's letters. That is verified
automatically by the test suite.

### The letter circle

The letters sit in a circle, like a wheel you can trace around. In the middle
there is a shuffle button.

**You can play two different ways, and both work equally well:**

- **Drag** — hold the mouse down on a letter and drag from letter to letter
  around the circle. A glowing line follows your finger. Let go when you reach
  the last letter.
- **Tap** — click each letter one at a time. The word appears above the circle
  as you go. A word is accepted the moment you tap its final letter.

**Undoing a mistake:**

- **While dragging:** drag back onto the letter you just came from to peel off
  the last letter.
- **While tapping:** tap any letter you already selected, and everything from
  that letter onward is removed.
- **Or** press the **Clear** button to wipe the whole selection.

**Re-arranging the letters:** click the shuffle button in the middle of the
circle, or the **Shuffle** button. This is **free and unlimited** — no points
are charged, and it does not change the puzzle, it only changes where the
letters sit.

### The key mechanic: words that build on each other

This is the cleverest part of the design and it is worth explaining carefully.

Suppose you find `HEAR`. There is still a bigger word, `HEART`, waiting. If the
app cleared your selection after `HEAR`, you would have to start from scratch.

**Instead, `HEAR` stays selected.** The letters `H→E→A→R` are still highlighted
and still connected. So you can simply **tap one more letter, `T`**, and you
instantly have `HEART`.

That is what `canExtend()` checks: if any unfound word *starts with* the word you
just found, the selection stays alive. The moment no longer word depends on your
current chain, the selection clears and you start fresh.

This is why the game feels fast. Good players chain several words together
without ever lifting the mouse.

### Bonus words: the open-ended part

The 9 hidden words are only the *minimum*. You can spell **any** other real
word from the same letters and you get points for it too:

- **+10 points** for a bonus word.
- They are counted separately in a **"Bonus Jar"** badge.
- They do **not** help you finish the puzzle. Only the 9 hidden words do that.

The app ships with a **229-word built-in dictionary** (mostly 3- and 4-letter
words). If you type a word that is not in it, the app looks it up on Wikipedia.
If Wikipedia confirms it is a real word, you get the points — and the word is
added to the dictionary so it is remembered for the rest of the session.

**This means the game is never "finished."** You can always keep hunting for
extra words.

### The word grid

On the left is a numbered grid — one row per hidden word, with a blank box for
each letter. It is not a traditional interlocking crossword; it is a numbered
word list where you fill in each word as you find it.

Three visual states:

| State | What it looks like |
|---|---|
| **Not found yet** | Dark empty boxes, the letters are hidden |
| **Hinted** | One box glows amber with that letter visible |
| **Found** | The whole row glows teal with the word written in white |

The row number turns green when the word is found.

### The feedback strip

A message line under the circle tells you what just happened, then fades after
two seconds:

- `Found: HEART! +20 pts` — in green
- `Bonus: ART! +10 pts` — in coral
- `Letter revealed in #3! -15 pts` — in gold
- `Not enough points - find a word first!` — in red

---

## 8. Score, Hints and Finishing a Puzzle

### Every way to gain or lose points

| Action | Points |
|---|---|
| Find a hidden word | **+20** |
| Find a bonus word | **+10** |
| Use a Hint | **−15** |
| Use "Reveal Word" | **net −10** (see below) |
| Shuffle | 0 |
| Clear selection | 0 |

The maximum for a single puzzle is **180** points (9 words × 20), plus 10 for
every bonus word you find.

Points can go negative if you keep using power-ups.

### The two power-ups

**Hint (lightbulb icon) — costs 15 points**

Reveals **one random letter** of a **random unfound word**. You still have to
find the word yourself. If you hint every letter of a 5-letter word, that cost
you 75 points.

**Reveal Word (magnifier icon) — labelled "−30"**

Instantly completes a random unfound word. The word counts as found, so the
whole row lights up and you move closer to finishing.

**A small maths detail worth knowing:** the button says −30, and finding a word
normally gives +20. So using it actually changes your score by **20 − 30 = −10**,
not −30. The confirmation message says this honestly: *"Revealed: GARDEN! net
−10 pts"*.

**The catch:** a fresh puzzle always starts at 0 points. So you **cannot** use
either power-up until you have found at least one word by hand. The buttons are
greyed out until then, and if you somehow trigger one anyway, the app refuses
with *"Not enough points"* and plays an error sound.

### Finishing a puzzle

The puzzle completes the moment all 9 words are found — whether you found them
yourself or used the power-ups. Then:

1. The game locks immediately, so you cannot change anything.
2. A victory fanfare plays.
3. There is a short 800 ms pause so you can see the last word light up.
4. **Confetti** falls across the screen — 170 coloured particles, animated in
   real time.
5. Your points are saved to your account.
6. The completion screen shows your title and score.

### The rating

Your title depends on how many hint letters you used:

| Hint letters used | Title |
|---|---|
| 0 | **PERFECT (3/3)** |
| 1–2 | **GREAT JOB (2/3)** |
| 3+ | **SOLVED (1/3)** |

**What to say if a teacher asks about this:** the rating counts *hint letters
used* only. Finishing with the "Reveal Word" power-up is not counted, so a
player who reveals every word still gets "PERFECT". That is a known gap in the
rating, listed in the notes at the end.

### After you finish

Three buttons:

- **Play Again** — starts a **random** puzzle from all eight.
- **Change Difficulty** — back to the difficulty picker.
- **Back to Dashboard** — returns home and saves your points.

---

## 9. Settings

Opened from the gear icon. Four settings, in a dialog that centres itself on the
main window and always fits inside it, even on a small screen.

| Setting | What it does |
|---|---|
| **Theme** | Pick from four colour palettes, applied instantly |
| **Language** | Switch between English and Khmer |
| **Sound** | Turn sound effects on or off |
| **Test sound** | Play the chime so you can check the volume |

### The four themes

| Palette | Style |
|---|---|
| **Midnight Nebula** | Deep navy blue (default) |
| **Deep Ocean** | Blue-teal |
| **Royal Amethyst** | Purple |
| **Daylight Crystal** | **Light** theme — the only pale one |

Switching a theme repaints the whole app instantly, because every colour in the
project is read from the active palette at paint time rather than stored.

The test suite checks every screen in all four themes to make sure no pale panel
leaks through a dark background.

---

## 10. Leaderboard

Opened from the trophy icon, titled "Hall of Fame". Two tabs:

**Local tab** — the top 10 players **from this computer**, read straight from
`users.json`. The app reads the accounts, sorts them by points (highest first),
leaves out admin accounts, and shows the top 10 with their rank. Ties are broken
alphabetically by name, so the order is always stable.

**Global tab** — the top 10 from a shared online scoreboard, so you can compare
your score with other people's. If there is no connection, the app shows a
clearly-labelled offline list instead of an error.

---

## 11. Encyclopedia

Opened from the magnifier icon. Six wonder articles, one per world theme:

| World | Article |
|---|---|
| Ancient Egypt | The Great Pyramid of Giza |
| Outer Space | The Solar System |
| The Deep Ocean | Coral reefs |
| Dinosaur World | Tyrannosaurus |
| Medieval Kingdoms | Castles |
| Rainforest Adventure | The Amazon rainforest |

**Each article is written twice** — once in English and once in Khmer — so the
whole encyclopedia works offline in both languages. Opening a topic also tries
to fetch a live Wikipedia summary and thumbnail, which appears underneath if the
connection works.

---

## 12. Admin Panel

Opened from the shield icon, which **only appears for admin accounts**. A table
of every user with these columns: ID, Username, Email, Points, Rank, Role.

Five actions:

| Button | What it does |
|---|---|
| **Create User** | Adds a new account with a name, email, password, starting points, and admin checkbox |
| **Edit User** | Changes any of those details for an existing account |
| **Reset Points** | Sets that user's score to 0 |
| **Add Points** | Increases a user's score by a chosen amount |
| **Delete User** | Removes the account |

There is a **Refresh** button and a **Close** button.

Everything is validated — the admin cannot create a duplicate username or email,
and cannot save a blank username.

**Which accounts are admin?** The `isAdmin` flag in `users.json`, or the username
`admin`. The username `admin` is always treated as an admin, which is why the
`admin` account works out of the box.

---

## 13. Languages

The entire app is available in **English** and **Khmer (ភាសាខ្មែរ)**.

### How it works

All 142 text strings are held in one place, `I18n.java`. Each string is
registered with both languages side by side:

```java
put("btn_login", "Log In", "ចូល");
```

The first argument is English, the second is Khmer. Screen code then asks for
the string by its name:

```java
I18n.get("btn_login")
```

and gets whichever language is currently selected. **Screen code never contains
a hard-coded word** — that is why the whole app switches language instantly with
one click.

### Switching language

The toggle (globe icon) is in three places: the Welcome Screen, the Dashboard,
and Settings. Clicking it swaps every label, button, and message in the app at
once. Screens register a listener, so they are told to redraw themselves.

**Placeholders** like `{0}` and `{1}` get filled in with live numbers:

```
"wow_progress" = "You found {0} / {1} words"
```

so it renders as "You found 3 / 9 words", and in Khmer as well.

### Fallback rule

If a key is missing in the selected language, the app falls back to English. If
the key does not exist at all, the app shows the key's own name — which is
deliberately easy to spot, and the test suite uses exactly that to prove no
screen is asking for a string that does not exist.

**One thing to know:** the language choice is not saved. It resets to English
each time the app starts. Same for the theme and the sound setting.

---

## 14. Sound Effects

There are **no sound files in this project**. Every sound is generated with
maths at the moment it plays, then sent to the speakers. `SoundUtil.java` builds
each sound as a list of numbers (a "waveform"), converts the numbers into bytes,
and plays them through Java's built-in audio support.

**Format:** 44,100 samples per second, 16-bit, single channel.

### The seven sounds

| Sound | When it plays | How it is made |
|---|---|---|
| **Click** | Any button press | A short 750 Hz blip that fades fast |
| **Letter** | Selecting each letter | A bell. The **pitch rises as the word gets longer** — C, D, E, G, A, C, D, E — so a long word plays a rising tune |
| **Correct** | Finding a hidden word | Four bright notes played in quick succession (C, E, G, C) |
| **Error** | A mistake, or not enough points | Two short descending sweeps |
| **Hint** | Using a power-up, or finding a bonus word | A rising sparkle |
| **Shuffle** | Re-arranging the letters | A whoosh that speeds up and slows down |
| **Victory** | Finishing a puzzle | A five-note fanfare |

The rising letter pitch is the detail worth pointing out in a demo — it means
the game *sounds* like you are building a word.

### How it stays smooth

- Sounds are generated and played on **four background threads**, never on the
  screen-drawing thread. The UI never freezes or stutters.
- Each sound is generated **once** and reused, so replaying it is instant.
- If there is **no sound card** — a school laptop in silent mode, for example —
  the failure is caught and ignored silently. The game never crashes over audio.

---

## 15. The Visual Design System

`UITheme.java` is the largest file in the project, and it is worth explaining
because it is what makes the app look consistent and professional.

### The colour palette

Every colour has a name and a purpose. For example `TEAL` is the "good /
success" colour used for found words, links and highlights; `GOLD` is for
points; `CORAL` is for warnings; `ERROR` red is for failures.

Four themes each redefine the background gradients and glow colours, while the
accent colours (teal, gold, coral, violet) stay the same across all four — so a
button looks familiar even when the background changes completely.

### The spacing scale

Sizes are not random numbers. There is a scale:

| Constant | Value | Used for |
|---|---|---|
| `PAD_SCREEN_X` / `PAD_SCREEN_Y` | 32 / 28 | Space around the whole screen |
| `PAD_CARD_X` / `PAD_CARD_Y` | 30 / 26 | Space inside a card |
| `GAP_XL` / `GAP_LG` | 32 / 24 | Between major sections |
| `GAP_SECTION` | 22 | Between groups |
| `GAP_ELEMENT` | 14 | Between related items |
| `GAP_TIGHT` | 8 | Small tweaks |
| `BTN_H` / `BTN_H_SM` | 52 / 42 | Button heights |

The font sizes follow the same idea: 36 for the biggest headings, then 30, 22,
18, 15, 13, and 11 for small labels.

### The reusable components

Rather than writing button code again and again, `UITheme` provides ready-made
components: `primaryButton`, `secondaryButton`, `dangerButton`, `ghostButton`,
`glowButton`, `card`, `glowCard`, `badge`, `chipLabel`, `title`, `subtitle`,
`pillField`, `pillPassword`, `segmentTabs`, `divider`, `letterTilesRow`,
`avatar`, `coin`, `progressBar`, and more.

That is why a table in the Admin Panel and a card on the Dashboard look like they
belong to the same app — because they are built from the same code.

### The 22 hand-drawn icons

`VectorIcon` contains 22 icons, all drawn with Java 2D shapes: `USER`, `EMAIL`,
`PASSWORD`, `EYE`, `EYE_OFF`, `GLOBE`, `GEAR`, `TROPHY`, `SHIELD`, `SEARCH`,
`LIGHTBULB`, `RADAR`, `SHUFFLE`, `CHECK`, `SPEAKER_ON`, `SPEAKER_OFF`,
`ARROW_LEFT`, `ARROW_RIGHT`, `PLUS`, `REFRESH`, `LOGOUT`, and `NONE`.

Every one is a `switch` statement of `fillOval` / `fillArc` / `drawRoundRect`
calls. No image files, and they stay sharp at any size and on any theme.

### The custom-drawn pieces

Four things are painted by hand rather than assembled from Swing components:

**The letter circle.** Every node, the glowing selection line, the drag line, the
hub, and the radial background are painted directly onto the panel. Because
painting is done in a fixed 380×380 space and then scaled to fit, the circle
stays correct at any window size.

**The word grid.** Same idea — drawn cell by cell, scaled to fit. Cells are 34 px
with a 4 px gap.

**The progress bar.** A teal-to-gold gradient fill with a glowing knob at the
leading edge, animated smoothly toward its new value.

**The animated background.** Soft coloured blobs drift slowly across the
background. Each screen has its own colour set, so the Dashboard and the game
feel like different places.

### Making it fit any screen

The app handles window resizing in two ways:

1. **Automatic global scaling** — the app records its original sizes and fonts,
   then scales everything proportionally when the window changes size, between
   0.85× and 1.5×. Small hysteresis stops it flickering when you drag a border.
2. **Per-component responsive sizing** — `UIUtil.responsiveSize()` gives each
   component a preferred, minimum, and maximum size. Components shrink on small
   windows and stretch on large ones.

The long gradient title is handled specially: because it is drawn letter by
letter, it cannot wrap onto a second line, so the app measures it and **reduces
its font size** until it fits. That is tested in both English and Khmer.

---

## 16. Data Files

There are four JSON files in `data/`. All of them are plain text you can open and
read in any text editor.

### `users.json` — the live one

The real player accounts. Written and read constantly. See
[section 5](#5-login-and-registration) for the format.

### `worlds.json` — 6 worlds

The six wonder themes: Ancient Egypt, Outer Space, The Deep Ocean, Dinosaur
World, Medieval Kingdoms, and Rainforest Adventure. Each has an `id`, a `name`,
and a one-line `description`.

### `levels.json` — 18 levels

Three levels for each of the six worlds. Each has an `id`, a `worldId`, a `name`,
a `difficulty` (easy, medium, or hard), and a `pointReward` (10, 15, or 20).

| # | World | Levels |
|---|---|---|
| 1 | Ancient Egypt | Pyramids of Giza, Secrets of the Nile, Valley of the Kings |
| 2 | Outer Space | Planets, Stellar Nurseries, Black Holes |
| 3 | The Deep Ocean | Coral Reefs, Ocean Giants, The Deep Trench |
| 4 | Dinosaur World | Early Discoveries, Mesozoic Era, Mass Extinction |
| 5 | Medieval Kingdoms | Castles, Knights, Medieval Life |
| 6 | Rainforest Adventure | Canopy Explorers, Jungle Creatures, Survival Skills |

### `wow_levels.json` — 18 puzzle definitions

The same six worlds, but with word lists for Words of Wonders puzzles, and bigger
rewards (80, 150, or 250).

For example, the first one:

```json
{
  "id": 1,
  "worldId": 1,
  "name": "Pyramid Puzzle",
  "difficulty": "easy",
  "theme": "Ancient Egypt",
  "words": "[\"PYRAMID\",\"SPHINX\",\"MUMMY\",\"TOMB\",\"NILE\"]",
  "pointReward": 80
}
```

### An important honesty note about these files

**The three level files are a working demonstration of the data layer, but the
game does not actually play from them.**

The eight puzzles you play (EARTH, GARDEN, LISTEN, CASTLE, PLANET, GUITAR,
WONDER, FROZEN) are written directly in the game file as a simple list. They are
**not** loaded from `wow_levels.json`, and they are unrelated to the world themes
in that file.

The files are still genuinely useful: they are read, parsed and validated by the
test suite, and `users.json` — the account file — is very much alive. But if you
edit `wow_levels.json`, the puzzles in the game will not change. That is
explained fully in [section 19](#19-honest-notes-and-known-limits).

### `data/cache/avatars/`

A small cache of downloaded avatar pictures, so the same avatar is not downloaded
twice. The folder is listed in `.gitignore` because it is generated at runtime.

---

## 17. Testing

There is one test file, `SmokeTest.java` (1,104 lines), and it runs **321
assertions**. Every one prints `ok` or `FAIL`, and the process exits with a
non-zero code if anything fails — so it works as a real build check.

```bash
./run.sh test
```

Successful output ends with:

```
ALL CHECKS PASSED (321 assertions).
```

### What the 321 checks cover

| Group | What it verifies |
|---|---|
| **1** | User create, read, update, delete; points; leaderboard order; admin accounts excluded |
| **2** | Every `data/*.json` file loads and parses correctly |
| **3** | All 132 text keys have both English and Khmer; no screen asks for a key that does not exist |
| **4a** | The hero card is centred and never clipped at 1920, 1366, 1024, 800, 640 and 466 px; no scrollbar appears; the gradient title has no raw HTML and fits |
| **4b** | The hero title also fits with **Khmer** active (the widest language) |
| **4c** | The Settings dialog is centred on the main window, fits inside it, and its theme cards are not clipped — checked at four window widths |
| **4d** | Play Now → game → Back to Dashboard works, asserted twice |
| **5** | A **pixel-by-pixel check** of every screen in all four themes — fails if a pale panel shows through a dark theme |
| **6** | Switching language six times does not leak duplicate components |
| **7** | **A complete playthrough of all 8 puzzles**, alternating drag and tap — every word found, progress bar full, grid locked, completion screen shown, points banked |
| **8** | Hint and Reveal Word are refused with no points; both work once affordable; the exact point maths is checked |

**Group 7 is the important one.** It does not mock anything — it plays the real
game by sending real `MouseEvent`s through the real hit-testing code, so the
actual input pipeline is genuinely exercised. And **group 5** renders the screens
to an image and inspects the pixels, which is how the four themes are verified.

---

## 18. Project Layout

```
Json_Game/
├── run.sh                      Launch script (macOS / Linux)
├── run.bat                     Launch script (Windows)
├── README.md                   This file
├── .gitignore
│
├── src/com/worldofwonder/
│   ├── Main.java               Entry point
│   ├── controller/             AuthController, GameController
│   ├── model/                  User, UserRepository, World, Level,
│   │                           WowLevel, GameRepository
│   ├── util/                   JsonUtil, ApiService, I18n
│   ├── view/                   MainUI, WelcomeScreen, Dashboard,
│   │                           WordsOfWondersGameScreen, SettingsModal,
│   │                           LeaderboardModal, EncyclopediaModal,
│   │                           AdminControlModal, UITheme, UIUtil, SoundUtil
│   └── test/                   SmokeTest
│
├── data/
│   ├── users.json              Live player accounts
│   ├── worlds.json             6 worlds
│   ├── levels.json             18 levels
│   ├── wow_levels.json         18 puzzle definitions
│   └── cache/avatars/          Downloaded avatars (generated)
│
├── assets/images/
│   └── Game4.jpeg              The only image asset
│
└── bin/                        Compiled .class files (generated)
```

**Line counts, largest first:**

| File | Lines |
|---|---|
| `UITheme.java` | 4,335 |
| `WordsOfWondersGameScreen.java` | 1,037 |
| `SmokeTest.java` | 1,104 |
| `WelcomeScreen.java` | 537 |
| `Dashboard.java` | 457 |
| `AdminControlModal.java` | 439 |
| `JsonUtil.java` | 386 |
| `LeaderboardModal.java` | 322 |
| `SettingsModal.java` | 312 |
| `I18n.java` | 292 |
| **Total** | **~11,100** |

---

## 19. Honest Notes and Known Limits

This section lists what does **not** work perfectly. It is here so nobody is
surprised during a demo, and so the limitations are honest rather than hidden.

### Gameplay

1. **The difficulty picker is a puzzle selector, not a difficulty system.** There
   is no timer, no lives, and no score multiplier. "Hard" is not measurably harder
   than "Medium" — both show 6 letters and 9 words. The real jump is Easy (5
   letters) to Medium (6 letters).

2. **Only 3 of the 8 puzzles are reachable from the difficulty picker.** The
   other five can only be reached by finishing a puzzle and pressing "Play Again",
   which picks a **random** one — so it abandons your chosen difficulty.

3. **Bonus points are counted twice.** A bonus word is added to your account
   immediately (+10), and then the final total is saved again at the end of the
   puzzle — but that total already includes the bonus. So a bonus word is
   effectively worth 20. The on-screen score is correct; only the saved total is
   too high.

4. **The rating ignores the Reveal Word power-up.** It only counts hint letters,
   so revealing every word still gives "PERFECT (3/3)".

5. **The "Reveal Word" button says −30 but costs −10 net**, because finding a
   word already gives +20. The confirmation message states the real figure.

6. **The "Reveal Word" button uses a magnifier icon**, not a hammer. It is
   really "show me the answer", so the icon is arguably right, but it is
   inconsistent with the name.

7. **Leaving mid-puzzle is silent.** The Back button returns to the Dashboard
   with no confirmation, and nothing is saved — but the unfinished board is still
   there when you come back, so you can carry on.

8. **"New Puzzle" returns to the difficulty picker** rather than starting a new
   puzzle. It is really a second "Change Difficulty" button.

### Data

9. **`levels.json`, `worlds.json` and `wow_levels.json` are not used by the
   game.** They are read and validated by the tests, but the real puzzles are
   written directly in the game file. Editing `wow_levels.json` changes nothing
   you can play.

10. **The `words` field in `wow_levels.json` is a string containing JSON, not a
    real list.** So `"words": "[\"PYRAMID\",...]"` — the inner array is never
    decoded anywhere.

11. **Two separate copies of the user list exist in memory.** Login and
    registration use one, while saving points and the admin panel use another.
    This is a real bug: if you register a new account, it may not appear in the
    admin panel, and a save from one side can overwrite the other's list. **For a
    single-user demo this is not a problem, but it is worth fixing before any
    real multi-user use.**

12. **A broken `users.json` stops the app from starting.** The reader only
    catches file errors, not malformed JSON, so one stray comma crashes startup
    with a stack trace. Easy to fix, but it is not fixed.

13. **Where the data folder is found depends on the working directory.** If you
    launch from the wrong folder, the app creates a fresh empty `data/` and seeds
    the three default accounts, which looks like "all my accounts vanished". The
    launch scripts protect against this by changing folder first.

14. **The `admin` account has two hard-coded passwords** (`admin123` and
    `admin67`) that work regardless of what is stored in the file. This is
    deliberate, so the demo can never get locked out — but it means the admin
    password cannot really be changed.

### Interface

15. **Switching language does not update every button.** The Dashboard and the
    Welcome Screen update fully, but the difficulty tiles and the game buttons
    keep the old language until you leave and re-enter the game.

16. **Theme, language and sound settings are not saved.** They reset every time
    the app starts.

17. **The Global leaderboard tab needs internet.** Without a connection it shows
    a clearly-labelled offline list.

18. **Some online features need internet** — the "Did you know?" fact, the
    Wikipedia bonus-word check, the encyclopedia summaries, and the global
    leaderboard. All of them fail quietly and the app carries on.

19. **There is one untranslated tooltip** ("Exit back to the main game hub"). Two
    of the 142 text keys are also never displayed (`btn_later` and
    `wow_solved_sub`, both left over from features that were removed). The other
    140 are all in use.

20. **Some leftover code from earlier versions is still in place** — a
    `hearts` / `hintCoins` system in the `User` model that nothing uses, and
    unused `GameController` methods. The Dashboard subtitle also still says
    *"spin the letters"*, wording left over from a removed spin-the-wheel
    feature; the game shuffles a ring of letters, it does not spin anything.
    Nothing is broken by these, but they are not used.

### What has been checked and is solid

- All 321 automated assertions pass.
- The build is clean — no errors, no warnings.
- All 8 puzzles are completable end to end, by both drag and tap.
- No leftover references to the removed wheel and daily-reward features. The
  test suite actively fails the build if any of them reappear.

---

## 20. Demo Script for a Presentation

A short, reliable path through the app.

**1. The login screen (30 seconds)**
Show the Login/Register tabs, register a new account, then log out and log back
in as that account. *Point out that the password is hashed — open `users.json`
and show that no plain password is stored.*

**2. The Dashboard (30 seconds)**
Show the single Words of Wonders card, the rank, and the points. *Open Settings
and switch the theme twice — instant, no flicker. Switch to Khmer and back —
every label changes at once.*

**3. Start a game (15 seconds)**
Press Play Now and pick Easy. *Point out the tile says "5 letters • 9 words" and
that this number comes from the puzzle data itself.*

**4. The core mechanic (90 seconds)**
This is the important part. Find `HEAR` — **do not tap Clear afterwards.** The
selection stays alive. Then tap one more letter to make `HEART`.

*"That is the key idea. Finding a short word does not reset you. The letters stay
selected so you can extend into a longer word instantly. Good players chain
three or four words without letting go."*

Then find a bonus word, and show the Bonus Jar badge going up.

**5. The power-ups (30 seconds)**
*Before finding anything, point at the greyed-out Hint button:* "You start on
zero points, so you cannot hint your way out — you have to find a word first."
Then find a word and use a Hint. *Point out the letter appears in amber in the
grid.*

**6. Shuffle and undo (30 seconds)**
Click the middle of the circle to shuffle. *Point out it is free and unlimited.*
Then make a mistake, drag backwards to undo, and show the selection peel off one
letter at a time.

**7. The word grid (20 seconds)**
*Point out the three states:* dark = not found, amber = hinted, teal = found.
*The progress bar fills as you find words.*

**8. Finishing (30 seconds)**
Find the remaining words. *The confetti fires, the fanfare plays, the points are
saved.* Show the rating line and say honestly: "the rating counts hint letters
used only."

**9. The other screens (45 seconds)**
Open the **Leaderboard** (your new score is there). Open the **Encyclopedia**
(*point out the offline Khmer text, then the live Wikipedia summary*). Log in as
`admin` / `admin123` and open the **Admin Panel** — create a user, add points,
and watch the list update.

**10. Close with the design (30 seconds)**
Mention these four points:
- Pure Java Swing, MVC, **no external libraries at all**.
- The sound effects and the 22 icons are **generated by code**, not files.
- The whole app speaks **English and Khmer**.
- **321 automated checks** pass, including a full playthrough of all 8 puzzles
  and a pixel check of every screen in all 4 themes.

---

*Built with Java Swing. No frameworks, no external libraries, no build tools.*
# WordsOfWonders-OOP-FinalProject-ISTAD
