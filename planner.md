# 📘 BUNKSORT — Master Build Planner

> Product: personal attendance advisor for a 1st-yr engineering student.
> Philosophy: **the student keeps engineering oversight; the agent does machine-room construction.**
> Source of truth for scope = this file. Everything else (README spec ≠ scope, planner wins when they disagree).

---

## 🎯 Mission (one line)

Turn BunkSort from a terminal calculator (v0.1.1, shipped) into a **phone-first attendance companion** the student opens every morning — while the student's main job until Nov 23 is excams, and the learning continues via "read the diff, question everything, extend it."

---

## 🧱 Tech Stack (locked unless vetoed)

| Layer | Choice | Notes |
|---|---|---|
| Language | Java | continuity with v0.1 engine |
| Backend | Spring Boot | starts at v0.3 |
| Database | SQLite (JDBC) | single file, zero admin |
| Frontend | React (Vite) + TailwindCSS, shipped as PWA | installable on phone |
| Testing | JUnit 5 + Maven | every feature lands with a test |
| Packaging | PWA now → Android native (stretch, later) | one codebase first |

**Non-features for v1 (do not creep):** user accounts, cloud sync, social features, admin panels.

---

## 🗺️ Phase Map

```
v0.1.1 (✅ shipped) → v0.2 CLI Pro → v0.3 API server → v0.4 Web PWA → v0.5 companion features → v0.6 Android (stretch)
        your engine      terminal tool      brain online       phone UI         real companion           native app
```

---

## ✅ Phase 0 — v0.1 / v0.1.1 (DONE)

- [x] `maxBunk` + `recoveryPlan` engine, brute-force loops
- [x] TARGET constant, % display, multi-subject session loop
- [x] 4/4 tests (17/20→2, 9/12→0, 21/30→6, 6/12→12)
- [x] Repo: src/Main.java + .gitignore + changelog-style commits
- [ ] **PENDING:** user runs `git push` (blocked on user's GitHub auth — standing rule: user's hand only)

---

## 🖥️ Phase 1 — v0.2 "CLI Pro" (terminal-native, NO new stack)

*Goal: a tool the student actually uses during exam week (Nov 14–22) to guard attendance.*

- [ ] Persist subjects to `subjects.json` (plain Java file I/O + a tiny JSON writer — no dependencies yet)
- [ ] Load-on-start: `java -cp bin Main` resumes where you left off
- [ ] Menu loop: `1) list subjects  2) update attendance (attend/skip/bunk)  3) what-if planner  4) exit`
- [ ] What-if planner: "if I bunk N next sessions, where does % land?" (engine already knows how)
- [ ] Warning line when any subject enters the danger buffer (< TARGET + 3%)
- [ ] Weekly ledger printout: per-subject attended/held/%, next safe-bunk count
- [ ] Tests: rerun 4 engine tests via a scripted run; JSON survives exit/restart
- [ ] Commit style: one feature per commit (`feat: persist subjects to json`, ...)

**Estimated effort: 1 agent session + 1 review session.**

---

## 🌐 Phase 2 — v0.3 "API server" (Spring Boot + SQLite + JUnit)

*Goal: the brain gets an HTTP door. Built SILENTLY during the exam freeze on a branch — no push, no noise.*

- [ ] `git checkout -b feature/v0.3-api` (zero interference with main)
- [ ] Maven project layout: `pom.xml`, `src/main/java`, `src/test/java`
- [ ] Data model: `Subject` (id, name, attended, held, schedule pattern) — SQLite table + tiny JDBC repo class
- [ ] REST endpoints:
      - `GET  /api/subjects`            → list all with computed status
      - `POST /api/subjects`            → add subject
      - `PATCH /api/subjects/{id}/log`  → record attend / skip / bunk
      - `GET  /api/subjects/{id}/whatif?bunks=N` → projected % + verdict
- [ ] Engine extracted to a pure class (`AttendanceMath`) — the same math, now testable in isolation
- [ ] JUnit: all 4 legacy tests + edge: exact-75%, brand-new subject (0 classes yet), over-100% inputs rejected
- [ ] `curl` smoke-test checklist documented in README
- [ ] Branch merged to main ONLY on Nov 23+ with the student reading the diff first

---

## 📱 Phase 3 — v0.4 "Web PWA" (phone-first UI)

*Goal: installable on the student's phone; opens the morning and answers "what can I skip today?"*

- [ ] Vite + React scaffold, `npm run dev` behind a proxy to the Spring API
- [ ] Dashboard: one card per subject (name, %, colored SAFE/AT RISK state, attended/held)
- [ ] One-tap log buttons: attended ✓ / skipped / bunked
- [ ] What-if panel: slider for N → projected % + verdict live
- [ ] Service worker + manifest → installs to home screen, works offline (reads cached state)
- [ ] Looks good on a 6" phone and on the Legion 24-10 (24" desktop) — responsive single column
- [ ] Deploy = run on the laptop; phone talks over LAN (no cloud hosting needed for v1)

---

## 🔔 Phase 4 — v0.5 "Companion"

- [ ] Semester reset (one command; archives old data to `semester-archive.json`)
- [ ] Per-subject weekly schedule (e.g., "Physics: Mon/Wed/Fri") so what-if uses real days
- [ ] Morning "anything at risk today?" banner computed from schedule
- [ ] CSV export (spreadsheet-friendly, for the semester review)

---

## 📲 Phase 5 — v0.6 "Android native" (stretch, optional)

- [ ] Kotlin + Jetpack Compose wrapper around the same API
- [ ] Only if PWA hasn't already made this unnecessary (it probably will 😄)

---

## 📅 Calendar (locked to the student's reality)

| Window | What | Who |
|---|---|---|
| Nov 9–13 | v0.2 CLI Pro built; student reviews diffs (20 min/evening, optional) | agent builds / student reviews |
| Nov 14–22 | **EXAM FREEZE.** Student: exams only. Agent: builds v0.3 silently on branch, commits only, no push | student exams / agent silent |
| Nov 23–30 | Student reads v0.3 diff, runs tests, asks everything; v0.4 built together, student driving increasingly | student learns+drives / agent pairs |
| Dec | v0.4 → v0.5 polish; phone install; real subjects loaded | both |

---

## ⚖️ Standing Rules

1. **Tests are the scoreboard** — no feature lands without one; the 4 legacy tests never break.
2. **No push without the student's hand** (auth rule, inherited from Velox).
3. **Student owns WHAT, agent owns HOW** — scope changes only via this file.
4. **Freeze-week silence** — Nov 14–22 the student gets zero BunkSort notifications; the branch holds everything.
5. **Every phase ends with the changelog reading like a product** (`feat:` / `fix:` / `docs:`).

---

## 📌 Current Status (update this box at every session end)

```
Phase reached:   Phase 0 complete (v0.1.1) — push pending (user auth)
Next up:         Phase 1 — v0.2 CLI Pro
Branch:          main
Tests:           4/4 passing (manual runs)
```
