import java.sql.*;
import java.util.Scanner;

public class TollGateApplication {

    
    // JDBC credentials
    private static final String URL = "jdbc:mysql://localhost:3306/toll_system"; // MySQL URL
    private static final String USER = "root"; // MySQL username (change it if needed)
    private static final String PASSWORD = ""; // MySQL password (change it accordingly)

    // Method to establish a connection to the database
    private static Connection connectToDatabase() throws SQLException {
        try {
            // Establishing connection to the database
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    // Method to collect toll based on vehicle type
    private static double collectToll(String vehicleType) {
        double tollAmount = 0.0;

        // Determine toll amount based on vehicle type
        switch (vehicleType.toLowerCase()) {
            case "car":
                tollAmount = 100.0; // Toll for car in INR
                break;
            case "bus":
                tollAmount = 250.0; // Toll for bus in INR
                break;
            case "truck":
                tollAmount = 500.0; // Toll for truck in INR
                break;
            case "motorcycle":
                tollAmount = 50.0; // Toll for motorcycle in INR
                break;
            case "van":
                tollAmount = 200.0; // Toll for van in INR
                break;
            default:
                System.out.println("Invalid vehicle type entered.");
                break;
        }

        return tollAmount;
    }

    // Method to save the toll record into the database (Create)
    private static void recordToll(String vehicleType, double tollAmount) {
        String query = "INSERT INTO toll_records (vehicle_type, toll_amount) VALUES (?, ?)";

        try (Connection conn = connectToDatabase(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set parameters for the query
            stmt.setString(1, vehicleType);
            stmt.setDouble(2, tollAmount);
            
            // Execute the update to insert the record into the database
            stmt.executeUpdate();
            System.out.println("Toll record saved successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving toll record: " + e.getMessage());
        }
    }

    // Method to display all toll records from the database (Read)
    private static void displayTollRecords() {
        String query = "SELECT * FROM toll_records"; // SQL query to fetch all records
        
        try (Connection conn = connectToDatabase(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- Toll Records ---");
            System.out.println("ID | Vehicle Type | Toll Amount | Time of Collection");
            System.out.println("--------------------------------------------------------");

            while (rs.next()) {
                // Retrieve and display each record from the ResultSet
                int id = rs.getInt("id");
                String vehicleType = rs.getString("vehicle_type");
                double tollAmount = rs.getDouble("toll_amount");
                Timestamp collectionTime = rs.getTimestamp("collection_time");

                // Display record
                System.out.printf("%-3d | %-12s | ₹%-10.2f | %s\n", id, vehicleType, tollAmount, collectionTime);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching toll records: " + e.getMessage());
        }
    }

    // Method to update a toll record in the database (Update)
    private static void updateTollRecord(int recordId, double newTollAmount) {
        String query = "UPDATE toll_records SET toll_amount = ? WHERE id = ?";

        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the new toll amount and the record ID
            stmt.setDouble(1, newTollAmount);
            stmt.setInt(2, recordId);
            
            // Execute the update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Toll record updated successfully.");
            } else {
                System.out.println("Record with ID " + recordId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating toll record: " + e.getMessage());
        }
    }

    // Method to delete a toll record from the database (Delete)
    private static void deleteTollRecord(int recordId) {
        String query = "DELETE FROM toll_records WHERE id = ?";

        try (Connection conn = connectToDatabase();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the record ID to delete
            stmt.setInt(1, recordId);
            
            // Execute the delete
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Toll record deleted successfully.");
            } else {
                System.out.println("Record with ID " + recordId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting toll record: " + e.getMessage());
        }
    }

    // Method to validate user input for vehicle type
    private static String getValidVehicleType(Scanner scanner) {
        String vehicleType = "";
        boolean isValid = false;

        while (!isValid) {
            System.out.print("Enter vehicle type (car, bus, truck, motorcycle, van): ");
            vehicleType = scanner.nextLine().trim().toLowerCase();

            // Validate the vehicle type
            if (vehicleType.equals("car") || vehicleType.equals("bus") || vehicleType.equals("truck")
                || vehicleType.equals("motorcycle") || vehicleType.equals("van")) {
                isValid = true;
            } else {
                System.out.println("Invalid vehicle type. Please try again.");
            }
        }

        return vehicleType;
    }

    // Method to prompt user to continue or exit
    private static boolean promptContinue(Scanner scanner) {
        String response = "";
        while (true) {
            System.out.print("Do you want to continue? (y/n): ");
            response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y")) {
                return true;
            } else if (response.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    // Main method to drive the application
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Welcome to the Toll Gate Collection System ---");

        while (true) {
            // Display the main menu options
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Collect Toll");
            System.out.println("2. View All Toll Records");
            System.out.println("3. Update Toll Record");
            System.out.println("4. Delete Toll Record");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            
            int option = 0;
            boolean validOption = false;

            // Validate menu option
            while (!validOption) {
                try {
                    option = Integer.parseInt(scanner.nextLine().trim());
                    if (option >= 1 && option <= 5) {
                        validOption = true;
                    } else {
                        System.out.println("Invalid option. Please enter a number between 1 and 5.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            switch (option) {
                case 1:
                    // Collect toll
                    String vehicleType = getValidVehicleType(scanner);
                    double tollAmount = collectToll(vehicleType); // Get toll for the vehicle
                    
                    if (tollAmount > 0) {
                        recordToll(vehicleType, tollAmount); // Save the toll record to the database
                        System.out.println("Toll collected: ₹" + tollAmount);
                    }
                    break;

                case 2:
                    // View all toll records
                    displayTollRecords();
                    break;

                case 3:
                    // Update toll record
                    System.out.print("Enter the record ID to update: ");
                    int recordId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter the new toll amount: ₹");
                    double newTollAmount = Double.parseDouble(scanner.nextLine().trim());
                    updateTollRecord(recordId, newTollAmount);
                    break;

                case 4:
                    // Delete toll record
                    System.out.print("Enter the record ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine().trim());
                    deleteTollRecord(deleteId);
                    break;

                case 5:
                    // Exit the program
                    System.out.println("Exiting system...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }

            // Ask the user if they want to continue
            if (!promptContinue(scanner)) {
                System.out.println("Exiting system...");
                scanner.close();
                break;
            }
        }
    }
}
