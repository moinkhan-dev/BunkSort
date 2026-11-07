import java.util.Scanner;

    public class Main {
    static final double TARGET = 0.75;

        static int maxBunk(int attended, int held) {
        int skip = 0;
        while (attended / (double) (held + skip + 1) >= TARGET) {
            skip++;
        }
        return skip;
    }

    static int recoveryPlan(int attended, int held) {
        int attend = 0;
        while ((attended + attend) / (double) (held + attend) < TARGET) {
            attend++;
        }
        return attend;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String again = "y";

        do {
            System.out.print("attended: ");
        int attended = sc.nextInt();
            System.out.print("held: ");
        int held = sc.nextInt();

        double pct = attended / (double) held * 100;
            System.out.printf("Current: %.1f%%%n", pct);

            if (attended / (double) held >= TARGET) {
                System.out.println("SAFE -- you can skip " + maxBunk(attended, held) + " more classes");
        } else {
                System.out.println("AT RISK -- attend " + recoveryPlan(attended, held) + " in a row first");
        }

            System.out.print("check another subject? (y/n): ");
            again = sc.next();
        } while (again.equals("y"));

        System.out.println("BunkSort out. Good planning!");
    }

}
