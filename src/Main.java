import java.util.Scanner;


public class Main {

    static int maxBunk(int attended, int held) {
        int skip = 0;
        while (attended / (double) (held + skip + 1) >= 0.75) {
            skip++;
        }
        return skip;
    }


    static int recoveryPlan(int attended, int held) {
        int attend = 0;
        while ((attended + attend) / (double) (held + attend) < 0.75) {
            attend++;
        }
        return attend;
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("attended: ");
        int attended = sc.nextInt();
        System.out.println("held: ");
        int held = sc.nextInt();

        if (attended / (double) held >= 0.75) {
            System.out.println("SAFE -- you can skip " + maxBunk(attended, held) + " more classes");   
        } else {
            System.out.println("AT RISK -- attend " + recoveryPlan(attended, held) + " in a row first");    
        }
    }
}
