import client.Client;

import java.util.List;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Client c = new Client();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n" +
                    "┌─────────────────┬───┬─────┐\n" +
                    "│ Read all        │ 1 │     │\n" +
                    "│ Read all (desc) │ 2 │     │\n" +
                    "│ Tail            │ 3 │ <n> │\n" +
                    "│ Head            │ 4 │ <n> │\n" +
                    "│ Count           │ 5 │     │\n" +
                    "│ Exit            │ 0 │     │\n" +
                    "└─────────────────┴───┴─────┘\n" +
                    "\n");
            switch(sc.nextInt()){
                case 1 -> System.out.println(c.readFile());
                case 2 -> System.out.println(c.readFile(List::reversed));
                case 3 -> {
                    System.out.println("Limit tail to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.reversed().subList(0, n)));
                }
                case 4 -> {
                    System.out.println("Limit tail to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.subList(0, n)));
                }

                case 5 -> System.out.println(c.readFile().size());

                default -> {
                    System.out.println("exiting");
                    c.shutdown();
                    System.exit(0);
                }
            }
        }
    }
}
