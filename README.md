# DADM 2020: Android Challenges

The weekly challenges ("retos") of an Android development course, from Hello World to maps and open-data web services.

| | |
|---|---|
| **Course** | *Desarrollo de Aplicaciones para Dispositivos Móviles* (Mobile App Development), Universidad Nacional de Colombia |
| **Term** | 2020-2 (Aug – Nov 2020) |
| **Author** | Nicolai Romero ([@anromerom](https://github.com/anromerom)), individual work |
| **Stack** | Android Studio, Java and Kotlin, SQLite/Room, Firebase Realtime Database, Google Maps, Volley |
| **Status** | Course deliverables, not maintained |

> **About the course:** a course on Android development. Each week brings a new challenge on one Android topic, and in parallel students build a semester project. Mine was [Krow (Clockrow)](https://github.com/anromerom/unal-mad-krow).

## Challenges

| # | Topic | What was built |
|---|---|---|
| [00](reto00) | Hello World | Android Studio sample project ([screenshot](reto00/reto00.png)) |
| [01](reto01) | Project proposal | Proposal and slides for *Clockrow* (PDF) |
| [02](reto02) | Business model | Business Model Canvas and mockups (PDF) |
| [03](reto03) | GUI | Tic-Tac-Toe vs. the computer (course tutorial) |
| [04](reto04) | Menus and dialogs | Tic-Tac-Toe: difficulty levels, menus, dialog boxes |
| [05](reto05) | Graphics and audio | Tic-Tac-Toe: custom board view, sounds |
| [06](reto06) | Preferences | Tic-Tac-Toe: saved settings and state |
| [07](reto07) | Online play | Tic-Tac-Toe: multiplayer with Firebase Realtime Database |
| [08](reto08) | SQLite | Company directory (name, URL, phone, email, products, type) with Room |
| [09](reto09) | GPS | Map with current location and nearby places (Google Places), radius in settings |
| [10](reto10) | Web services | Searches an open-data stations dataset (datos.gov.co) and plots it on a map |

<details>
<summary><b>Running a challenge</b></summary>

Each `retoNN/<Project>` folder is a standalone Android Studio project. Open that folder, not the repo root.
The projects were built in 2020 (Android Gradle Plugin 4.x), so a current Android Studio will ask to upgrade Gradle.
Retos 07, 09 and 10 need your own Firebase / Google Maps keys.

</details>

<details>
<summary><b>Known issues</b></summary>

- **No keys included:** API keys and the Firebase config were removed from the history in 2026. Retos 07, 09 and 10 need your own.
- **Duplicated code:** retos 03–07 are five full copies of the same Tic-Tac-Toe project, each one step further.
- **`reto10` no longer loads data:** the datos.gov.co dataset it queries (`ysq6-ri4e`) has been removed.

</details>
