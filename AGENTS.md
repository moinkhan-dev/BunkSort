# AGENTS.md — BunkSort session context (auto-read every session)

> This file is loaded automatically by any agent working in this folder.
> Treat it as the project's persistent memory: state, rules, commands, and discipline.
> **Planner is the scope authority:** see `planner.md` for phases; agent edits plans via planner.md only.
> User = Moin (BTech 1st yr). Exams end-sem **Nov 23 2026**, exam freeze **Nov 14–22**.

---

## 1. What BunkSort is

A **personal attendance advisor** the student opens daily:
given attended/held per subject, it answers —
- SAFE → how many classes can I still bunk?
- AT RISK → how many classes must I attend in a row to recover?
- and projects future outcomes ("if I bunk N, where do I land?")

End state (planner.md): Java CLI → Spring Boot API + SQLite → React PWA installable on the student's phone.

## 2. Rules that never change

1. **Push is done by the human only.** The agent may commit locally, but never runs `git push` (no stored auth; user's credentials are theirs).
2. **Tests are the scoreboard.** Engine math tests must always pass:
   - `17/20 → maxBunk 2` · `9/12 → maxBunk 0` (exact-75% edge) · `21/30 → recovery 6` · `6/12 → recovery 12`
   Any change that breaks these = broken build, no exceptions.
3. **Scope changes go through `planner.md`** — user owns WHAT, agent owns HOW.
4. **Freeze week (Nov 14–22):** no notifications/noises to the student about BunkSort; any building happens silently on a side branch, never on `main`.
5. **Commit style:** imperative, short, prefixed (`feat:` / `fix:` / `docs:` / `chore:`). One concern per commit.
6. **Teaching stance:** the student is learning Java by building. Where feasible: explain concepts when touching code, point out new concepts as they appear (agent-built phases are reviewed via diffs by the student).
7. **No framework magic before its phase:** v0.2 = zero dependencies; Spring Boot enters only at Phase 2 (v0.3); React at Phase 3 (v0.4).

## 3. Where things live

```
/home/moin/Documents/java-projects/BunkSort/     <- THE repo (all work happens here)
├── src/Main.java           <- CLI program (v0.1 engine → v0.2 CLI Pro)
├── bin/                    <- compiled .class (gitignored)
├── subjects.json           <- user data (gitignored)
├── planner.md              <- master plan + phase checklists + status box
├── README.md               <- product spec, formulas, roadmap
├── AGENTS.md               <- this file
└── .gitignore

/home/moin/Documents/java-playground/            <- scratch experiments only (not a repo)
/home/moin/Desktop/velox prototype/              <- separate project, own sessions, own rules (AGENTS.md there)
```

- The student works in the BunkSort folder now; playground is whiteboard-only.
- Branch naming: `feature/vX.Y-name` (e.g. `feature/v0.3-api`).

## 4. Build & test commands (plain JDK 25, no dependencies in v0.2)

```bash
cd /home/moin/Documents/java-projects/BunkSort
javac src/Main.java -d bin
java -cp bin Main
```

Engine output note: `subjects.json` is user data — do NOT commit it; delete or leave it, it is local.

## 5. Current state (agent: verify against git log; update after each session)

```
Branch:      feature/v0.2-cli-pro (v0.2 CLI Pro built: persistence, menu, log, what-if, danger buffer)
Main:        has planner.md + v0.1.1 (push entities BELOW this line are unpushed to GitHub!)
Unpushed:    entire history (user has not run git push yet — remind gently; never push yourself)
Next phase:  1) user reviews v0.2 diff -> merge to main -> user pushes
             2) Nov 14-22: agent silently builds feature/v0.3-api (Spring Boot + SQLite + REST + JUnit)
             3) Nov 23+: student reads v0.3 diff, runs tests, v0.4 PWA built together
Calendar:    exams Nov 23; freeze Nov 14-22 — study outweighs code this week
```

## 6. Voice & tone with the student (same as the rest of the workspace)

- Encouraging, no-guilt framing; friction framed as temporary.
- Students' wins are named concretely (bugs caught, tests passed, commits shipped).
- No fluff lectures; code + reasoning up front; occasional humor.
- The student's diffs/reviews are part of the learning loop — never skip phase 1 review.
