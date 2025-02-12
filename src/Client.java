
import java.rmi.Naming;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            TicketService service = (TicketService) Naming.lookup("rmi://localhost/TicketService");
            Scanner scanner = new Scanner(System.in);

            // Authentication section (Login or Register)
            String username = null;
            String password = null;
            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.println("\n===== Ticket Reservation System =====");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                if (choice == 1) {
                    System.out.print("Enter a username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter a password: ");
                    password = scanner.nextLine();
                    System.out.println(service.register(username, password));
                } else if (choice == 2) {
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    String loginMessage = service.login(username, password);
                    System.out.println(loginMessage);
                    if (loginMessage.contains("successful")) {
                        loggedIn = true;
                        System.out.println("You are now logged in.");
                    }
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }

            // After login, allow ticket operations
            while (true) {
                System.out.println("\n===== Ticket Reservation System =====");
                System.out.println("1. Book Ticket");
                System.out.println("2. Cancel Ticket");
                System.out.println("3. Check Availability");
                System.out.println("4. Check My Booked Tickets");
                System.out.println("5. Logout");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter number of tickets: ");
                        int count = scanner.nextInt();
                        double price = service.calculatePrice(count);
                        System.out.println("Total cost: ₹" + price + ". Confirm payment? (yes/no): ");
                        scanner.nextLine();
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("yes")) {
                            System.out.println(service.bookTicket(username, count, price));
                        } else {
                            System.out.println("Booking cancelled.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter number of tickets to cancel: ");
                        int cancelCount = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Refund amount: ₹" + service.calculatePrice(cancelCount) + ". Confirm cancellation? (yes/no): ");
                        confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("yes")) {
                            System.out.println(service.cancelTicket(username, cancelCount));
                        } else {
                            System.out.println("Cancellation aborted.");
                        }
                        break;
                    case 3:
                        System.out.println("Available tickets: " + service.availableTickets());
                        break;
                    case 4:
                        System.out.println("Your booked tickets: " + service.getBookedTickets(username));
                        break;
                    case 5:
                        System.out.println(service.logout());
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("Client Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
