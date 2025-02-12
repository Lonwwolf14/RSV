
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicketService extends Remote {

    // Authentication methods
    String register(String username, String password) throws RemoteException;

    String login(String username, String password) throws RemoteException;

    String logout() throws RemoteException;

    // Ticket operations
    String bookTicket(String passengerName, int count, double amount) throws RemoteException;

    String cancelTicket(String passengerName, int count) throws RemoteException;

    int availableTickets() throws RemoteException;

    double calculatePrice(int count) throws RemoteException;

    int getBookedTickets(String passengerName) throws RemoteException;
}
