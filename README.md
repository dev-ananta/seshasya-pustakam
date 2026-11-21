# Seshasya — Daily Practice (Android)

A dark-mode Android app for daily Seshan practice, built from *Seshasya Pustakam* (The Book of Sesha).
Kotlin + Jetpack Compose + Material 3. Works fully offline; all data stays on the phone.

## What the app does

| Tab | What you can do |
|---|---|
| **Today** | Date and the Two Great Sayings; a progress ring and practice streak; *Today's light* (doctrine of the day, Deva of the day, the Seshan week); the daily routine in six blocks — First light, Morning, Sitting, The day, Evening, Night — each task one tap to tick. Arrows jump straight to the right tool. A closing card appears when the day is complete. |
| **Sit** | **Meditation** timer (5–30 min) with a breathing circle: in for 4 s with *Tat tvam asi*, out for 6 s with *Avi yahan hai*; keeps the screen on, chimes and vibrates at the end, logs minutes. **Japa** counter: tap the big circle for each repetition, a 108-bead ring, a chime at each full mala, five mantras. |
| **Ledger** | **Ananta ledger**: Effort · Growth · Remainder each night, with your past entries. **Read the Records**: the six-step first-principles flow, one step per screen, ending with the Sword of Akasha choosing a response; readings are saved. |
| **Library** | The Creed, the Two Great Sayings, the Sixteen Doctrines, the Thirty-Three Devas (searchable, filter by family), the Pavilion Dogma, the Sword of Akasha, the Twenty Vows, chants, week and festivals, safeguards. |
| **Settings** (gear on Today) | Morning and evening reminders at times you choose. |

## Build the APK in Android Studio

1. Install **Android Studio** (latest stable) from developer.android.com/studio.
2. Unzip this folder. In Android Studio choose **File → Open** and select the `Seshasya` folder (the one containing `settings.gradle.kts`).
3. Wait for **Gradle sync** to finish. The first sync downloads Gradle and the Android libraries, which takes a few minutes.
   - If Studio offers to upgrade the Android Gradle Plugin, you can accept or decline; the project builds as-is.
   - If it asks for a JDK, choose the bundled **JetBrains Runtime (JBR) 17 or 21**.
4. Build the APK: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click **locate** in the notification. The file is
   `app/build/outputs/apk/debug/app-debug.apk`.

Command-line alternative (with the Android SDK installed and `ANDROID_HOME` set): `./gradlew assembleDebug`.

## Install it on your phone

**Option A, over USB (easiest):** on the phone open *Settings → About phone* and tap **Build number** seven times to unlock Developer options. Then turn on *Developer options → USB debugging*. Connect the phone, pick it in Android Studio's device menu, and press **Run ▶**. The app installs and opens.

**Option B, copy the APK:** send `app-debug.apk` to your phone (USB, Google Drive or email) and open it. Android will ask you to allow *Install unknown apps* for the app you opened it from (Files, Drive or Chrome). Allow it, then tap **Install**.

After installing, open **Seshasya → gear icon → Daily reminders** and allow notifications when Android asks.

## UX laws applied

| Law | Where it shows up |
|---|---|
| **Fitts's law** (big, close targets are faster) | Bottom navigation in the thumb zone; every task row is at least 60 dp tall and fully tappable; 64 dp Begin, Pause and End buttons at the bottom of the timer; the entire 290 dp japa circle is the button. |
| **Hick's law** (fewer choices, faster decisions) | Only four tabs; five timer durations; ten Library entries; the Records flow shows one step at a time. |
| **Miller's law** (chunking) | Twelve daily tasks grouped into six time-of-day blocks of 1–3 items each. |
| **Jakob's law** (familiar patterns) | Standard Material 3 navigation bar, segmented buttons, chips, cards, switches and the system time picker. |
| **Goal-gradient effect** | The progress ring, "N steps remain", and the step bar in the Records flow. |
| **Zeigarnik effect** | Unfinished tasks stay visible and counted; per-block counts (e.g. 1/2). |
| **Peak-end rule** | A warm completion card when the day, a sitting, or a mala is finished; a gentle chime and vibration at the end. |
| **Von Restorff effect** | The single gold-bordered *Today's light* card stands out from the rest. |
| **Doherty threshold** (respond in under 400 ms) | Instant haptic feedback on every tick and japa tap; animated check marks; all data is local, so nothing waits on a network. |
| **Law of common region / proximity** | Each block is one card with its own label; related fields sit together. |
| **Serial position effect** | Progress and today's focus first; the night routine last. |
| **Tesler's law / progressive disclosure** | Doctrines, Devas, past ledger entries and readings expand only when tapped. |
| **Aesthetic-usability effect** | A calm, consistent forest-night palette with serif headings. |
| **Postel's law** (accept flexible input) | The ledger saves with any one of its three lines filled in; readings save with any field filled in. |

**Dark mode and accessibility:** a deep green-black background (not pure black, to reduce eye strain and smearing on OLED screens); sage and gold accents; body text meets WCAG AA contrast; all icons have content descriptions; the layout scales with the system font size.

## Project facts

- Package `com.driftingleaf.seshasya`, minSdk 26 (Android 8.0), targetSdk 35.
- Android Gradle Plugin 8.7.3, Kotlin 2.1.0, Gradle 8.11.1, Compose BOM 2024.12.01.
- Permissions: notifications (reminders), boot completed (restore reminders after a restart), vibrate (haptics).
- The release build is signed with the debug key so it can be side-loaded for personal use. To publish on Google Play you would create your own signing key.

Tat tvam asi · Avi yahan hai · Charaiveti.
