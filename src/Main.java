import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BunkSort v0.2 "CLI Pro"
 * - subjects persisted to subjects.json (survives restarts)
 * - menu: ledger / log a class / what-if planner / add subject
 * - engine (maxBunk, recoveryNeed) unchanged from v0.1 — tests still valid
 */
public class Main {

    static final double TARGET = 0.75;        // college minimum
    static final double WARN_LINE = 0.78;     // danger buffer: warn anywhere below TARGET + 3%
    static final String STORE_FILE = "subjects.json";

    // ---------- model ----------

    static class Subject {
        String name;
        int attended;
        int held;

        Subject(String name, int attended, int held) {
            this.name = name;
            this.attended = attended;
            this.held = held;
        }

        double pct() {
            return held == 0 ? 0.0 : attended / (double) held * 100;
        }

        /** max classes skippable right now while staying >= TARGET */
        int maxBunk() {
            int skip = 0;
            while (attended / (double) (held + skip + 1) >= TARGET) {
                skip++;
            }
            return skip;
        }

        /** min consecutive attends needed to climb back to >= TARGET */
        int recoveryNeed() {
            int attend = 0;
            while ((attended + attend) / (double) (held + attend) < TARGET) {
                attend++;
            }
            return attend;
        }

        boolean isSafe() {
            return held != 0 && attended / (double) held >= TARGET;
        }
    }

    // ---------- state ----------

    static final List<Subject> subjects = new ArrayList<>();
    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        load();

        if (subjects.isEmpty()) {
            System.out.println("BunkSort v0.2 — first run. Let's add your first subject.");
            addSubject();
        }

        int choice;
        do {
            System.out.println();
            System.out.println("==== BunkSort v0.2 ====");
            System.out.println("1) Ledger   2) Log a class   3) What-if planner   4) Add subject   0) Exit");
            choice = readInt("choose: ", 0, 4);
            switch (choice) {
                case 1 -> ledger();
                case 2 -> logClass();
                case 3 -> whatIf();
                case 4 -> addSubject();
                case 0 -> { }
                default -> System.out.println("? unknown option");
            }
        } while (choice != 0);

        save();
        System.out.println("Saved to " + STORE_FILE + ". BunkSort out. Good planning!");
    }

    // ---------- features ----------

    static void ledger() {
        System.out.println("\n--- ATTENDANCE LEDGER ---");
        if (subjects.isEmpty()) {
            System.out.println("(no subjects yet — add one with option 4)");
            return;
        }
        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            String verdict;
            if (s.held == 0) {
                verdict = "no classes yet";
            } else if (s.isSafe()) {
                verdict = "SAFE (can bunk " + s.maxBunk() + " more)";
            } else {
                verdict = "AT RISK (attend " + s.recoveryNeed() + " in a row)";
            }
            String warn = "";
            // warn only when NOT already flagged AT RISK: below the buffer but still above TARGET
            if (s.held > 0 && s.isSafe() && s.attended / (double) s.held < WARN_LINE) {
                warn = "  << within danger buffer, go easy on bunks";
            }
            System.out.printf("%d) %-14s %3d/%-3d %6.1f%%   %s%s%n",
                    i + 1, s.name, s.attended, s.held, s.pct(), verdict, warn);
        }
    }

    static void logClass() {
        Subject s = pickSubject("log a class for which subject?");
        if (s == null) return;

        System.out.print("(a)ttended or (b)unked? [a/b] ");
        String answer = sc.next();
        sc.nextLine(); // swallow newline

        if (answer.startsWith("a")) {
            s.attended++;
            s.held++;
        } else if (answer.startsWith("b")) {
            s.held++;
        } else {
            System.out.println("nothing logged.");
            return;
        }
        save();
        System.out.printf("%s -> %d/%d (%.1f%%)%n", s.name, s.attended, s.held, s.pct());
    }

    static void whatIf() {
        Subject s = pickSubject("what-if plan for which subject?");
        if (s == null) return;

        System.out.print("how many classes do you plan to BUNK? ");
        int n = readInt("", 0, Integer.MAX_VALUE);

        if (s.held == 0) {
            System.out.println(s.name + " has no classes yet — nothing to project.");
            return;
        }

        double after = s.attended / (double) (s.held + n) * 100;
        System.out.printf("%s: %d/%d (%.1f%%) now", s.name, s.attended, s.held, s.pct());
        System.out.printf("  ->  after bunking %d: %d/%d = %.1f%%%n", n, s.attended, s.held + n, after);

        if (!s.isSafe()) {
            // already below TARGET: recovery is the real answer
            int need = s.recoveryNeed();
            System.out.printf("careful: %s is ALREADY at risk — attend %d in a row first, then re-check.%n",
                    s.name, need);
            if (n > 0) {
                System.out.println("bunking while at risk only digs deeper.");
            }
            return;
        }

        if (s.attended / (double) (s.held + n) >= TARGET) {
            System.out.println("plan holds: still >= TARGET. enjoy the bunk(s).");
        } else {
            System.out.printf("plan breaks the line (%.1f%% < %.0f%%). stick to max %d bunk(s) instead.%n",
                    after, TARGET * 100, s.maxBunk());
        }
    }

    static void addSubject() {
        String name = readText("subject name: ");
        int attended = readInt("attended so far: ", 0, Integer.MAX_VALUE);
        int held;
        do {
            held = readInt("held so far: ", 0, Integer.MAX_VALUE);
            if (attended > held) {
                System.out.println("(attended can't exceed held — try again)");
            }
        } while (attended > held);

        subjects.add(new Subject(name, attended, held));
        save();
        System.out.println("added: " + name);
    }

    // ---------- helpers ----------

    /** pick a subject by number; auto-picks when there's only one. null = user aborted / none exist */
    static Subject pickSubject(String prompt) {
        if (subjects.isEmpty()) {
            System.out.println("(no subjects — add one first, option 4)");
            return null;
        }
        if (subjects.size() == 1) {
            return subjects.get(0);
        }
        System.out.println(prompt);
        for (int i = 0; i < subjects.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, subjects.get(i).name);
        }
        int idx = readInt("number: ", 1, subjects.size());
        return subjects.get(idx - 1);
    }

    /** reads an int within [min,max]; rejects non-numbers and out-of-range with attendant prompts */
    static int readInt(String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            try {
                int v = sc.nextInt();
                sc.nextLine(); // swallow newline
                if (v < min || v > max) {
                    System.out.printf("pick a number between %d and %d%n", min, max);
                    continue;
                }
                return v;
            } catch (InputMismatchException e) {
                sc.nextLine(); // discard the bad token
                System.out.println("(that wasn't a number — try again)");
            }
        }
    }

    /** reads a non-empty trimmed line */
    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("(can't be empty)");
        }
    }

    // ---------- persistence (hand-rolled minimal JSON, zero dependencies) ----------

    static void save() {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            sb.append("\t{\"name\":\"").append(s.name.replace("\\", "\\\\").replace("\"", "\\\""))
              .append("\",\"attended\":").append(s.attended)
              .append(",\"held\":").append(s.held)
              .append(i == subjects.size() - 1 ? "}" : "},")
              .append("\n");
        }
        sb.append("]\n");
        try {
            Files.writeString(Path.of(STORE_FILE), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("!! could not save: " + e.getMessage());
        }
    }

    static void load() {
        Path p = Path.of(STORE_FILE);
        if (!Files.exists(p)) return;
        try {
            String json = Files.readString(p, StandardCharsets.UTF_8);
            Matcher objs = Pattern.compile("\\{[^{}]*}").matcher(json);
            Pattern pName = Pattern.compile("\"name\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
            Pattern pAtt  = Pattern.compile("\"attended\"\\s*:\\s*(\\d+)");
            Pattern pHeld = Pattern.compile("\"held\"\\s*:\\s*(\\d+)");
            while (objs.find()) {
                String obj = objs.group();
                String name = first(pName, obj).replace("\\\\", "\\").replace("\\\"", "\"");
                int att  = first(pAtt,  obj).isEmpty() ? 0 : Integer.parseInt(first(pAtt, obj));
                int held = first(pHeld, obj).isEmpty() ? 0 : Integer.parseInt(first(pHeld, obj));
                if (!name.isEmpty()) subjects.add(new Subject(name, att, held));
            }
        } catch (Exception e) {
            System.out.println("!! could not read " + STORE_FILE + " (starting fresh): " + e.getMessage());
            subjects.clear();
        }
    }

    static String first(Pattern pat, String src) {
        Matcher m = pat.matcher(src);
        return m.find() ? m.group(1) : "";
    }
}
