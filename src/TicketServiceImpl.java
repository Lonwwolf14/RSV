
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketServiceImpl extends UnicastRemoteObject implements TicketService {

    private static final int TICKET_PRICE = 100;
    private static AtomicInteger totalTickets = new AtomicInteger(100);
    private ConcurrentHashMap<String, AtomicInteger> reservations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> userCredentials = new ConcurrentHashMap<>();  // Username -> Password
    private ThreadLocal<String> loggedInUser = ThreadLocal.withInitial(() -> null);  // Thread-local for managing sessions

    protected TicketServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String register(String username, String password) throws RemoteException {
        if (userCredentials.containsKey(username)) {
            return "Username already exists. Please choose a different username.";
        }
        userCredentials.put(username, password);
        reservations.put(username, new AtomicInteger(0));  // Initialize ticket count to 0 for the new user
        return "Registration successful!";
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            loggedInUser.set(username);  // Set the logged-in user
            return "Login successful!";
        }
        return "Invalid username or password.";
    }

    @Override
    public String logout() throws RemoteException {
        String user = loggedInUser.get();
        if (user != null) {
            loggedInUser.remove();  // Clear the logged-in user
            return user + " logged out successfully.";
        }
        return "No user is logged in.";
    }

    @Override
    public String bookTicket(String passengerName, int count, double amount) throws RemoteException {
        String user = loggedInUser.get();
        if (user == null || !user.equals(passengerName)) {
            return "You must be logged in to book tickets.";
        }

        double requiredAmount = count * TICKET_PRICE;
        if (amount < requiredAmount) {
            return "Insufficient payment. Required: ₹" + requiredAmount;
        }
        if (count <= 0) {
            return "Invalid ticket count.";
        }
        if (totalTickets.get() >= count) {
            totalTickets.addAndGet(-count);  // Deduct available tickets
            reservations.get(user).addAndGet(count);  // Add booked tickets for the user
            return "Booking successful for " + passengerName + " (" + count + " tickets). Payment: ₹" + amount;
        } else {
            return "Not enough tickets available.";
        }
    }

    @Override
    public String cancelTicket(String passengerName, int count) throws RemoteException {
        String user = loggedInUser.get();
        if (user == null || !user.equals(passengerName)) {
            return "You must be logged in to cancel tickets.";
        }

        AtomicInteger booked = reservations.get(user);
        if (booked == null || booked.get() == 0) {
            return "No booking found for " + passengerName;
        }
        if (count > booked.get()) {
            return "Cannot cancel more tickets than booked.";
        }
        booked.addAndGet(-count);
        totalTickets.addAndGet(count);  // Add the canceled tickets back to available stock
        double refundAmount = count * TICKET_PRICE;
        return "Cancellation successful. " + count + " tickets refunded. Refund amount: ₹" + refundAmount;
    }

    @Override
    public int availableTickets() throws RemoteException {
        return totalTickets.get();
    }

    @Override
    public double calculatePrice(int count) throws RemoteException {
        return count * TICKET_PRICE;
    }

    @Override
    public int getBookedTickets(String passengerName) throws RemoteException {
        String user = loggedInUser.get();
        if (user == null || !user.equals(passengerName)) {
            return 0;  // User is not logged in or does not match
        }
        AtomicInteger booked = reservations.get(user);
        return (booked != null) ? booked.get() : 0;  // Return the number of booked tickets for the logged-in user
    }
}
