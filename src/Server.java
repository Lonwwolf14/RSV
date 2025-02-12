
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String[] args) {
        try {
            // Creating RMI registry
            LocateRegistry.createRegistry(1099);
            TicketServiceImpl ticketService = new TicketServiceImpl();
            Naming.rebind("rmi://localhost/TicketService", ticketService);
            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            System.err.println("Server Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
