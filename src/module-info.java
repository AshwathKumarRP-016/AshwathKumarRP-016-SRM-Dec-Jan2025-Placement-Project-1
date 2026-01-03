import java.sql.*;
import java.util.Scanner;

public class StudentRegistrationSystem {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;
        
        try {
            // 1. Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded successfully!");
            
            // 2. Establish database connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connection.setAutoCommit(true); // Enable auto-commit
            System.out.println("Connected to database successfully!");
            
            // 3. Create students table if it doesn't exist
            createStudentsTable(connection);
            
            // 4. Get student details from user
            System.out.println("\n=== Student Registration System ===");
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter roll number: ");
            int roll = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            System.out.print("Enter department: ");
            String dept = scanner.nextLine();
            
            // 5. Insert student into database
            boolean success = insertStudent(connection, name, roll, dept);
            
            if (success) {
                System.out.println("Student registered successfully!");
            } else {
                System.out.println("Failed to register student!");
            }
            
            // 6. Display all registered students
            displayAllStudents(connection);
            
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection error!");
            e.printStackTrace();
        } finally {
            // 7. Close resources
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }
    
    /**
     * Creates the students table if it doesn't exist
     */
    private static void createStudentsTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                "name VARCHAR(50) NOT NULL, " +
                                "roll INT NOT NULL UNIQUE, " +
                                "dept VARCHAR(50) NOT NULL)";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Students table ready!");
        }
    }
    
    /**
     * Inserts a new student record into the database
     * Uses PreparedStatement to prevent SQL injection
     */
    private static boolean insertStudent(Connection connection, String name, int roll, String dept) {
        String insertSQL = "INSERT INTO students (name, roll, dept) VALUES (?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            // Set parameters for PreparedStatement
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, roll);
            preparedStatement.setString(3, dept);
            
            // Execute the insert operation
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Error: Roll number " + roll + " already exists!");
            } else {
                System.out.println("Database error: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Displays all students from the database
     */
    private static void displayAllStudents(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM students ORDER BY id";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            
            System.out.println("\n=== All Registered Students ===");
            System.out.println("ID\tName\t\tRoll\tDepartment");
            System.out.println("----------------------------------------");
            
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int roll = resultSet.getInt("roll");
                String dept = resultSet.getString("dept");
                
                System.out.printf("%d\t%-15s\t%d\t%s%n", id, name, roll, dept);
            }
        }
    }
}
