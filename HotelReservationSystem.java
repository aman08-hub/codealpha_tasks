import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelReservationSystem {
    public static void main(String[] args) {
        HotelSystem hotel = new HotelSystem();
        initializeSampleData(hotel);
        runConsoleInterface(hotel);
    }

    private static void initializeSampleData(HotelSystem hotel) {
        // Add sample rooms
        hotel.addRoom(new Room(101, "Standard", 100.0, true));
        hotel.addRoom(new Room(102, "Deluxe", 150.0, true));
        hotel.addRoom(new Room(201, "Suite", 250.0, true));
    }

    private static void runConsoleInterface(HotelSystem hotel) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Make Reservation");
            System.out.println("3. View Booking Details");
            System.out.println("4. Process Payment");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    searchRooms(hotel, scanner);
                    break;
                case 2:
                    makeReservation(hotel, scanner);
                    break;
                case 3:
                    viewBookingDetails(hotel, scanner);
                    break;
                case 4:
                    processPayment(hotel, scanner);
                    break;
                case 5:
                    System.out.println("Exiting system...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void searchRooms(HotelSystem hotel, Scanner scanner) {
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = scanner.nextLine();

        List<Room> availableRooms = hotel.searchAvailableRooms(category, checkIn, checkOut);
        System.out.println("\nAvailable Rooms:");
        availableRooms.forEach(room -> System.out.println(
            "Room " + room.getRoomId() + " (" + room.getCategory() + 
            ") - $" + room.getPricePerNight() + "/night"
        ));
    }

    private static void makeReservation(HotelSystem hotel, Scanner scanner) {
        System.out.print("Enter room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        
        Room room = hotel.findRoomById(roomId);
        if (room == null || !room.isAvailable()) {
            System.out.println("Invalid or unavailable room!");
            return;
        }

        System.out.println("Enter customer details:");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        
        System.out.print("Check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());

        Customer customer = new Customer(hotel.getNextCustomerId(), name, email, phone);
        Booking booking = hotel.makeReservation(customer, room, checkIn, checkOut);
        
        if (booking != null) {
            System.out.println("Reservation successful! Booking ID: " + booking.getBookingId());
            processPaymentForBooking(hotel, booking, scanner);
        } else {
            System.out.println("Reservation failed!");
        }
    }

    private static void processPaymentForBooking(HotelSystem hotel, Booking booking, Scanner scanner) {
        double total = booking.calculateTotal();
        System.out.printf("Total amount due: $%.2f%n", total);
        System.out.print("Enter payment amount: ");
        double amount = scanner.nextDouble();
        
        if (hotel.processPayment(booking, amount)) {
            System.out.println("Payment successful!");
        } else {
            System.out.println("Payment failed! Insufficient amount.");
        }
    }

    private static void viewBookingDetails(HotelSystem hotel, Scanner scanner) {
        System.out.print("Enter booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        
        Booking booking = hotel.getBookingDetails(bookingId);
        if (booking != null) {
            System.out.println("\nBooking Details:");
            System.out.println("ID: " + booking.getBookingId());
            System.out.println("Room: " + booking.getRoom().getCategory() + " (ID: " + booking.getRoom().getRoomId() + ")");
            System.out.println("Dates: " + booking.getCheckInDate() + " to " + booking.getCheckOutDate());
            System.out.println("Customer: " + booking.getCustomer().getName());
            System.out.println("Payment Status: " + (booking.isPaid() ? "Paid" : "Pending"));
        } else {
            System.out.println("Booking not found!");
        }
    }

    private static void processPayment(HotelSystem hotel, Scanner scanner) {
        System.out.print("Enter booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        
        Booking booking = hotel.getBookingDetails(bookingId);
        if (booking == null) {
            System.out.println("Booking not found!");
            return;
        }
        
        processPaymentForBooking(hotel, booking, scanner);
    }
}

class Room {
    private int roomId;
    private String category;
    private double pricePerNight;
    private boolean isAvailable;

    public Room(int roomId, String category, double pricePerNight, boolean isAvailable) {
        this.roomId = roomId;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
    }

    // Getters and setters
    public int getRoomId() { return roomId; }
    public String getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}

class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;

    public Customer(int customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}

class Booking {
    private int bookingId;
    private Customer customer;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean isPaid;

    public Booking(int bookingId, Customer customer, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public double calculateTotal() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights * room.getPricePerNight();
    }

    // Getters and setters
    public int getBookingId() { return bookingId; }
    public Customer getCustomer() { return customer; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}

class HotelSystem {
    private List<Room> rooms = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private int nextCustomerId = 1;

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> searchAvailableRooms(String category, LocalDate checkIn, LocalDate checkOut) {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable() && room.getCategory().equalsIgnoreCase(category)) {
                available.add(room);
            }
        }
        return available;
    }

    public Room findRoomById(int roomId) {
        return rooms.stream()
                   .filter(room -> room.getRoomId() == roomId)
                   .findFirst()
                   .orElse(null);
    }

    public Booking makeReservation(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut) {
        if (!room.isAvailable()) return null;
        
        customers.add(customer);
        Booking booking = new Booking(bookings.size() + 1, customer, room, checkIn, checkOut);
        bookings.add(booking);
        room.setAvailable(false);
        return booking;
    }

    public Booking getBookingDetails(int bookingId) {
        return bookings.stream()
                      .filter(b -> b.getBookingId() == bookingId)
                      .findFirst()
                      .orElse(null);
    }

    public boolean processPayment(Booking booking, double amount) {
        double total = booking.calculateTotal();
        if (amount >= total) {
            booking.setPaid(true);
            return true;
        }
        return false;
    }

    public int getNextCustomerId() {
        return nextCustomerId++;
    }
}

