# BunkSort

> *Decide your bunk without gambling your percentage.*

A personal academic companion for Moin — logs classes, tracks live attendance
levels, and answers the two questions that actually matter:

- **How many classes can I safely skip right now?**
- **How many in a row must I attend to recover?**

---

## Product spec

- Subject-wise attendance log (held / attended), interactive menu
- Live report table: per-subject % + overall %
- **The Advisor**: safe-bunk count & recovery plan per subject (given a target %)
- Data persists between runs (CSV)
- Themed console UI: ANSI colors, Unicode box tables, danger/safe status dots
- Later: reminders, GUI (JavaFX) with real typography and trend charts, marks module, PDF export

## The math (core engine)

Given: `attended` = A, `held` = H, target fraction = T (e.g. 0.75)

- **Max sequential bunks while staying ≥ T** (only valid when A/H ≥ T):
  `maxBunk = floor(A/T − H)`
- **Minimum consecutive attends to climb back to ≥ T** (when below target):
  `minAttend = ceil((T·H − A) / (1 − T))`
- Cross-check both with a brute-force simulation of future days — the simulator
  is the app's unit test. Formula says 4? Simulator must say 4.

## Roadmap

- [ ] v0.1 — Log & Report (Subject class, menu, report table)
- [ ] v0.2 — The Advisor (safe-bunk + recovery plan + brute-force cross-check)
- [ ] v0.3 — Persistence (save/load CSV between runs)
- [ ] v0.4 — Theme pass (ANSI colors, box-drawing tables, status dots)
- [ ] v0.5 — Reminders (Linux `notify-send`, scheduled)
- [ ] v1.0 — GUI (JavaFX): typography, charts, click-to-log

## Stack

Pure JDK, no frameworks. Layers added only when needed:
core → CSV file I/O → ANSI console styling → JavaFX.

## Build & run (Linux)

```bash
javac -d bin src/*.java
java -cp bin Main
```

## Status

🌱 v0.1 under construction.

## Timeline (live)

- **Nov 2**: plan locked. End-sem = **Nov 23** → project freeze from **Nov 14**
- **Nov 2–8**: practical file first; Java basics (Day 1–3) in spare slots only
- **Nov 9–13**: v0.1 build window (~3–4 sessions)
- **Nov 14–22**: 🧊 exam-freeze — no code, JAVA notes instead
- **Nov 24 →**: post-exam runway — basics completion, then v0.2 → v0.4 over winter
- Session ritual: every chat opens with today's date + plan day so progress stays honest 💪
