package com.mycompany.sdadesign;

import java.util.*;

// ========== OBSERVER INTERFACES ==========
interface Observer {
    void update(String message);
}

interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}

// ========== USER ==========
class User implements Observer {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    @Override
    public void update(String message) {
        System.out.println("[Notification] " + message);
    }
}


// ========= ABSTRACT BOOK CLASS =========
abstract class Book {
    protected String title;
    protected String author;
    protected double price;
    protected int quantity; // 

    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = 1; // 
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract void displayDetails();
}

// ========= BOOK TYPES =========
class FictionBook extends Book {
    public FictionBook(String title, String author, double price) {
        super(title, author, price);
    }

    @Override
    public void displayDetails() {
        System.out.println("[Fiction] " + title + " by " + author + " - $" + price + " | Qty: " + quantity);
    }
}

class NonFictionBook extends Book {
    public NonFictionBook(String title, String author, double price) {
        super(title, author, price);
    }

    @Override
    public void displayDetails() {
        System.out.println("[Non-Fiction] " + title + " by " + author + " - $" + price + " | Qty: " + quantity);
    }
}

class ScienceBook extends Book {
    public ScienceBook(String title, String author, double price) {
        super(title, author, price);
    }

    @Override
    public void displayDetails() {
        System.out.println("[Science] " + title + " by " + author + " - $" + price + " | Qty: " + quantity);
    }
}

// ========== BOOK FACTORIES ==========
interface BookFactory {
    Book createBook(String genre, String title, String author, double price);
}

class EBookFactory implements BookFactory {
    public Book createBook(String genre, String title, String author, double price) {
        return switch (genre.toLowerCase()) {
            case "fiction" -> new FictionBook(title, author, price);
            case "nonfiction" -> new NonFictionBook(title, author, price);
            case "science" -> new ScienceBook(title, author, price);
            default -> null;
        };
    }
}

class PhysicalBookFactory implements BookFactory {
    public Book createBook(String genre, String title, String author, double price) {
        return switch (genre.toLowerCase()) {
            case "fiction" -> new FictionBook(title, author, price);
            case "nonfiction" -> new NonFictionBook(title, author, price);
            case "science" -> new ScienceBook(title, author, price);
            default -> null;
        };
    }
}

// ========== PAYMENT STRATEGY ==========
interface PaymentStrategy {
    void pay(String username, double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(String username, double amount) {
        System.out.println(username + " paid $" + amount + " via Credit Card.");
    }
}

class PaypalPayment implements PaymentStrategy {
    public void pay(String username, double amount) {
        System.out.println(username + " paid $" + amount + " via PayPal.");
    }
}

class CryptoPayment implements PaymentStrategy {
    public void pay(String username, double amount) {
        System.out.println(username + " paid $" + amount + " via Crypto.");
    }
}

// ========== ORDER ==========
class Order {
    private final String orderId;
    private final String user;
    private final Date date;
    private final double price;
    private final int quantity;
    private final String status;
    private final String title;

    public Order(String orderId, String user, Date date, double price, int quantity, String status, String title) {
        this.orderId = orderId;
        this.user = user;
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Order by " + user + " for " + title + " x" + quantity + " [" + status + "]";
    }
}

// ========== ORDER FACTORY ==========
class OrderFactory {
    public static Order createOrder(String format, String genre, String orderId, String user,
                                    Date date, double price, int quantity, String status, String title) {
        return new Order(orderId, user, date, price, quantity, status, title);
    }
}

// ========== CART / PAYMENT MANAGER ==========
class PaymentCartNotificationManager {
    private static PaymentCartNotificationManager instance;
    private final Map<String, Map<String, Integer>> carts = new HashMap<>();
    private final Map<String, List<String>> notifications = new HashMap<>();
    private String loggedInUser;

    private PaymentCartNotificationManager() {}

    public static PaymentCartNotificationManager getInstance() {
        if (instance == null) {
            instance = new PaymentCartNotificationManager();
        }
        return instance;
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        carts.putIfAbsent(username, new HashMap<>());
        notifications.putIfAbsent(username, new ArrayList<>());
    }

    public void addToCart(String title, int qty, BookStoreManager manager) {
    Book book = manager.getBookByTitle(title);
    if (book == null) {
        System.out.println("❌ Book not found. Cannot add to cart.");
        return;
    }

    Map<String, Integer> cart = carts.getOrDefault(loggedInUser, new HashMap<>());
    cart.put(title, cart.getOrDefault(title, 0) + qty);
    carts.put(loggedInUser, cart);
    notifications.get(loggedInUser).add("Added " + qty + " of \"" + title + "\" to cart.");
    System.out.println("✅ " + qty + " copy/copies of \"" + title + "\" added to cart.");
}


    public void viewCart() {
        System.out.println("[Cart of " + loggedInUser + "]");
        carts.getOrDefault(loggedInUser, new HashMap<>()).forEach((title, qty) ->
            System.out.println("- " + title + ": " + qty));
    }

    public void checkout(BookStoreManager manager) {
        Map<String, Integer> cart = carts.getOrDefault(loggedInUser, new HashMap<>());
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        notifications.get(loggedInUser).add("Checked out cart with " + cart.size() + " items.");
        cart.clear();
    }

    public void pay(String username, PaymentStrategy strategy) {
    Map<String, Integer> cart = carts.getOrDefault(username, new HashMap<>());
    if (cart.isEmpty()) {
        System.out.println("Cart is empty.");
        return;
    }

    double total = 0.0;
    BookStoreManager manager = BookStoreManager.getInstance();
    List<Order> ordersToPlace = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : cart.entrySet()) {
        String title = entry.getKey();
        int quantity = entry.getValue();
        Book book = manager.getBookByTitle(title);
        if (book != null) {
            double subtotal = book.getPrice() * quantity;
            total += subtotal;

            Order order = OrderFactory.createOrder(
                "physical",  
                "fiction",  
                UUID.randomUUID().toString(),
                username,
                new Date(),
                subtotal,
                quantity,
                "PLACED",
                title
            );
            ordersToPlace.add(order);
        }
    }

    strategy.pay(username, total);
    notifications.get(username).add("Paid $" + total + " successfully.");

    // Place all orders
    for (Order o : ordersToPlace) {
        manager.placeOrder(o);
    }

    cart.clear();
}


    public void viewNotifications(String username) {
        List<String> userNotes = notifications.getOrDefault(username, new ArrayList<>());
        if (userNotes.isEmpty()) {
            System.out.println("No notifications.");
        } else {
            userNotes.forEach(System.out::println);
        }
    }
}

// ========== SINGLETON MANAGER ==========
class BookStoreManager implements Subject {
    private static BookStoreManager instance;
    private final List<Observer> observers = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private User loggedInUser;

    private BookStoreManager() {}

    public static BookStoreManager getInstance() {
        if (instance == null) instance = new BookStoreManager();
        return instance;
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("[Book Added] " + book.getTitle());
    }

    public void listBooks() {
        if (books.isEmpty()) {
            System.out.println("[Info] No books available.");
            return;
        }
        for (Book b : books) b.displayDetails();
    }

    public void addUser(User user) {
        users.add(user);
        registerObserver(user);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logoutUser() {
        System.out.println("User " + loggedInUser.getUsername() + " logged out.");
        loggedInUser = null;
    }

    public boolean userExists(String username, String password) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username) && u.getPassword().equals(password));
    }

    public void placeOrder(Order order) {
        orders.add(order);
        notifyObservers(order.toString());
    }

    public void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
        } else {
            orders.forEach(System.out::println);
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        observers.forEach(o -> o.update(message));
    }
    public Book getBookByTitle(String title) {
    for (Book book : books) {
        if (book.getTitle().equalsIgnoreCase(title)) {
            return book;
        }
    }
    return null;
}

}

// ========== MAIN ==========

public class Sdadesign {
    private static final Scanner scanner = new Scanner(System.in);
    static final List<User> registeredUsers = new ArrayList<>();
    static final List<User> registeredAdmins = new ArrayList<>();

    public static void main(String[] args) {
        BookStoreManager manager = BookStoreManager.getInstance();
        PaymentCartNotificationManager pcm = PaymentCartNotificationManager.getInstance();

        System.out.println();
        System.out.println("================================================");
        System.out.println("              Welcome to FolioVerse             ");
        System.out.println("        Your Online Bookstore Management        ");
        System.out.println("================================================");

        while (true) {
            System.out.println();
            System.out.println("Please select your role to continue:");
            System.out.println("------------------------------------------------");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> adminAuthMenu(manager);
                case 2 -> userMenu(manager, pcm);
                case 3 -> {
                    System.out.println("\nThank you for using FolioVerse. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void adminAuthMenu(BookStoreManager manager) {
        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.println("               Admin Authentication             ");
            System.out.println("------------------------------------------------");
            System.out.println("1. Register as Admin");
            System.out.println("2. Login as Admin");
            System.out.println("3. Back");
            System.out.print("Enter your choice: ");

            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> {
                    System.out.println("\n----- Admin Registration -----");
                    System.out.print("Enter Admin username: ");
                    String uname = scanner.nextLine();
                    System.out.print("Enter Admin password: ");
                    String pass = scanner.nextLine();
                    User admin = new User(uname, pass);
                    registeredAdmins.add(admin);
                    manager.addUser(admin);
                    System.out.println("Admin registered successfully. Please log in.");
                }
                case 2 -> {
                    System.out.println("\n----- Admin Login -----");
                    System.out.print("Username: ");
                    String uname = scanner.nextLine();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine();

                    boolean found = false;
                    for (User admin : registeredAdmins) {
                        if (admin.getUsername().equals(uname) && admin.getPassword().equals(pass)) {
                            manager.setLoggedInUser(admin);
                            System.out.println("Login successful. Welcome, Admin.");
                            adminMenu(manager);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Incorrect credentials. Please try again.");
                    }
                }
                case 3 -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void adminMenu(BookStoreManager manager) {
        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.println("                Admin Dashboard                 ");
            System.out.println("------------------------------------------------");
            System.out.println("1. Add Book");
            System.out.println("2. List Books");
            System.out.println("3. View Orders");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> {
                    System.out.println("\n----- Add New Book -----");
                    System.out.print("Enter format (ebook/physical): ");
                    String format = scanner.nextLine().toLowerCase();

                    BookFactory factory = switch (format) {
                        case "ebook" -> new EBookFactory();
                        case "physical" -> new PhysicalBookFactory();
                        default -> null;
                    };

                    if (factory == null) {
                        System.out.println("❌ Invalid format. Please try again.");
                        continue; // ✅ Go back to admin menu instead of exiting
                    }

                    System.out.print("Enter genre (fiction/nonfiction/science): ");
                    String genre = scanner.nextLine().toLowerCase();

                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // Clear newline

                    Book book = factory.createBook(genre, title, author, price);
                    if (book == null) {
                        System.out.println("❌ Invalid genre. Please try again.");
                        continue; // ✅ Go back to admin menu instead of exiting
                    }

                    book.setQuantity(quantity);
                    manager.addBook(book);
                    System.out.println("✅ Book added successfully.");
                }

                case 2 -> {
                    System.out.println("\n----- Available Books -----");
                    manager.listBooks();
                }
                case 3 -> {
                    System.out.println("\n----- Orders -----");
                    manager.viewOrders();
                }
                case 4 -> {
                    manager.logoutUser();
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void userMenu(BookStoreManager manager, PaymentCartNotificationManager pcm) {
        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.println("                 User Authentication            ");
            System.out.println("------------------------------------------------");
            System.out.println("1. Register as User");
            System.out.println("2. Login as User");
            System.out.println("3. Back");
            System.out.print("Enter your choice: ");

            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> {
                    System.out.println("\n----- User Registration -----");
                    System.out.print("Enter username: ");
                    String uname = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String pass = scanner.nextLine();
                    User user = new User(uname, pass);
                    registeredUsers.add(user);
                    manager.addUser(user);
                    System.out.println("Registered successfully. Please log in.");
                }
                case 2 -> {
                    System.out.println("\n----- User Login -----");
                    System.out.print("Username: ");
                    String uname = scanner.nextLine();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine();

                    boolean found = false;
                    for (User user : registeredUsers) {
                        if (user.getUsername().equals(uname) && user.getPassword().equals(pass)) {
                            manager.setLoggedInUser(user);
                            pcm.setLoggedInUser(uname);
                            System.out.println("Login successful.");
                            loggedInUserMenu(manager, pcm);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Incorrect credentials. Try again.");
                    }
                }
                case 3 -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void loggedInUserMenu(BookStoreManager manager, PaymentCartNotificationManager pcm) {
        while (true) {
            System.out.println("\n------------------------------------------------");
            System.out.println("                   User Dashboard               ");
            System.out.println("------------------------------------------------");
            System.out.println("1. View Books");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Notifications");
            System.out.println("6. Place Order");
            System.out.println("7. Make Payment");
            System.out.println("8. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    System.out.println("\n----- Available Books -----");
                    manager.listBooks();
                }
                case 2 -> {
                    System.out.println("\n----- Add to Cart -----");
                    System.out.print("Book title: ");
                    String title = scanner.nextLine();

                    Book book = manager.getBookByTitle(title);
                    if (book == null) {
                        System.out.println("❌ Book not found. Cannot add to cart.");
                        break; // Don't ask for quantity
                    }

                    System.out.println("✅ Book found: " + book.getTitle() + " by " + book.getAuthor() + " | Price: $" + book.getPrice());
                    System.out.println("Available quantity: " + book.getQuantity());

                    System.out.print("Quantity: ");
                    int qty = scanner.nextInt();
                    scanner.nextLine();

                    pcm.addToCart(title, qty, manager);
                }
                case 3 -> {
                    System.out.println("\n----- Your Cart -----");
                    pcm.viewCart();
                }
                case 4 -> {
                    System.out.println("\n----- Checkout -----");
                    pcm.checkout(manager);
                    System.out.println("Items checked out successfully.");
                }
                case 5 -> {
                    System.out.println("\n----- Notifications -----");
                    pcm.viewNotifications(manager.getLoggedInUser().getUsername());
                }
                case 6 -> {
                    System.out.println("\n----- Place Order -----");
                    System.out.print("Enter Book Title: ");
                    String bookTitle = scanner.nextLine();
                    Book selectedBook = manager.getBookByTitle(bookTitle);

                    if (selectedBook == null) {
                        System.out.println("Book not found.");
                        break;
                    }

                    System.out.print("Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Format (ebook/physical): ");
                    String format = scanner.nextLine().toLowerCase();

                    BookFactory factory = switch (format) {
                        case "ebook" -> new EBookFactory();
                        case "physical" -> new PhysicalBookFactory();
                        default -> null;
                    };

                    if (factory == null) {
                        System.out.println("Invalid format.");
                        break;
                    }

                    Book orderedBook = factory.createBook(
                        selectedBook instanceof FictionBook ? "fiction" :
                        selectedBook instanceof NonFictionBook ? "nonfiction" :
                        selectedBook instanceof ScienceBook ? "science" : "unknown",
                        selectedBook.title, selectedBook.author, selectedBook.price
                    );

                    Order order = OrderFactory.createOrder(
                        format,
                        "unknown",
                        UUID.randomUUID().toString(),
                        manager.getLoggedInUser().getUsername(),
                        new Date(),
                        selectedBook.price * quantity,
                        quantity,
                        "Pending",
                        selectedBook.title
                    );

                    manager.placeOrder(order);
                    System.out.println("Order placed successfully.");
                }
                case 7 -> {
                    System.out.println("\n----- Make Payment -----");
                    System.out.print("Choose payment method (1: Card, 2: PayPal, 3: Crypto): ");
                    int m = scanner.nextInt(); scanner.nextLine();
                    PaymentStrategy strategy = switch (m) {
                        case 1 -> new CreditCardPayment();
                        case 2 -> new PaypalPayment();
                        case 3 -> new CryptoPayment();
                        default -> null;
                    };
                    if (strategy != null) {
                        pcm.pay(manager.getLoggedInUser().getUsername(), strategy);
                    } else {
                        System.out.println("Invalid payment method.");
                    }
                }
                case 8 -> {
                    manager.logoutUser();
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
