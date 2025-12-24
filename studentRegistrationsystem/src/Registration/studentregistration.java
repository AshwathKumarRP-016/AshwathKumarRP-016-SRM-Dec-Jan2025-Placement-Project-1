package Registration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;


public class studentregistration {
	private static final String URL = "jdbc:mysql://localhost:3306/student_db";
	private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    
    private static Connection connection = null;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   STUDENT REGISTRATION SYSTEM    ");
        System.out.println("========================================");
        
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(" MySQL JDBC Driver loaded successfully!");
            
            
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!");
            
            
            showMenu();
            
        } catch (ClassNotFoundException e) {
            System.err.println(" Error: MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-java.jar is in classpath.");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }
    
    private static void showMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n============ MAIN MENU ============\n");
            System.out.println("1. Register New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student by Roll Number");
            System.out.println("4. Update Student Details");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        registerStudent();
                        break;
                    case 2:
                        viewAllStudents();
                        break;
                    case 3:
                        searchStudent();
                        break;
                    case 4:
                        updateStudent();
                        break;
                    case 5:
                        deleteStudent();
                        break;
                    case 6:
                        System.out.println("👋 Thank you for using Student Registration System!");
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid choice! Please enter 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
            }
        }
    }
    
    //Method 1: Register New Student
    private static void registerStudent() {
        System.out.println("\n======== REGISTER NEW STUDENT ========\n");
        
        try {
            
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Roll Number: ");
            int roll = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Enter Department: ");
            String dept = scanner.nextLine();
            
            
            String sql = "INSERT INTO students (name, roll, dept) VALUES (?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                
                pstmt.setString(1, name);
                pstmt.setInt(2, roll);
                pstmt.setString(3, dept);
                
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Student registered successfully!");
                    
                    try (Statement stmt = connection.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
                        if (rs.next()) {
                            System.out.println("Student ID: " + rs.getInt(1));
                        }
                    }
                } else {
                    System.out.println("❌ Failed to register student.");
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid roll number! Please enter numeric value.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.out.println(" Error: Roll number already exists!");
            } else {
                System.out.println(" Database error: " + e.getMessage());
            }
        }
    }
    
    // Method 2: View All Students
    private static void viewAllStudents() {
        System.out.println("\n=========== ALL STUDENTS ===========\n");
        
        String sql = "SELECT * FROM students ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("|----|----------------------|------|----------------------|---------------------|");
            System.out.println("| ID | Name                 | Roll | Department           | Registration Date   |");
            System.out.println("|----|----------------------|------|----------------------|---------------------|");
            
            boolean hasRecords = false;
            
            while (rs.next()) {
                hasRecords = true;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int roll = rs.getInt("roll");
                String dept = rs.getString("dept");
                Timestamp regDate = rs.getTimestamp("registration_date");
                
                System.out.printf("| %-2d | %-20s | %-4d | %-20s | %-19s |\n", 
                                  id, name, roll, dept, regDate.toString().substring(0, 19));
            }
            
            System.out.println("|----|----------------------|------|----------------------|---------------------|");
            
            if (!hasRecords) {
                System.out.println("No students registered yet.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    
    // Method 3: Search Student by Roll Number
    private static void searchStudent() {
        System.out.println("\n======== SEARCH STUDENT ========");
        
        try {
            System.out.print("Enter Roll Number to search: ");
            int roll = Integer.parseInt(scanner.nextLine());
            
            String sql = "SELECT * FROM students WHERE roll = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, roll);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n Student Found!");
                        System.out.println("Student ID: " + rs.getInt("id"));
                        System.out.println("Name: " + rs.getString("name"));
                        System.out.println("Roll Number: " + rs.getInt("roll"));
                        System.out.println("Department: " + rs.getString("dept"));
                        System.out.println("Registered on: " + rs.getTimestamp("registration_date"));
                    } else {
                        System.out.println("❌ No student found with roll number: " + roll);
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid roll number! Please enter numeric value.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    
    // Method 4: Update Student Details
    private static void updateStudent() {
        System.out.println("\n======== UPDATE STUDENT ========");
        
        try {
            System.out.print("Enter Roll Number of student to update: ");
            int roll = Integer.parseInt(scanner.nextLine());
            
            
            String checkSql = "SELECT * FROM students WHERE roll = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, roll);
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    System.out.println("❌ No student found with roll number: " + roll);
                    return;
                }
                
                System.out.println("\nCurrent Details:");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Department: " + rs.getString("dept"));
            }
            
            
            System.out.print("\nEnter New Name (or press Enter to keep current): ");
            String newName = scanner.nextLine();
            
            System.out.print("Enter New Department (or press Enter to keep current): ");
            String newDept = scanner.nextLine();
            
            
            StringBuilder updateSql = new StringBuilder("UPDATE students SET ");
            boolean hasUpdate = false;
            
            if (!newName.trim().isEmpty()) {
                updateSql.append("name = ?");
                hasUpdate = true;
            }
            
            if (!newDept.trim().isEmpty()) {
                if (hasUpdate) updateSql.append(", ");
                updateSql.append("dept = ?");
                hasUpdate = true;
            }
            
            if (!hasUpdate) {
                System.out.println("❌ No changes made.");
                return;
            }
            
            updateSql.append(" WHERE roll = ?");
            
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql.toString())) {
                int paramIndex = 1;
                
                if (!newName.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, newName);
                }
                
                if (!newDept.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, newDept);
                }
                
                pstmt.setInt(paramIndex, roll);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Student details updated successfully!");
                } else {
                    System.out.println("Failed to update student.");
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println(" Invalid roll number! Please enter numeric value.");
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }
    
    // Method 5: Delete Student
    private static void deleteStudent() {
        System.out.println("\n======== DELETE STUDENT ========");
        
        try {
            System.out.print("Enter Roll Number of student to delete: ");
            int roll = Integer.parseInt(scanner.nextLine());
            
            
            System.out.print("Are you sure you want to delete this student? (yes/no): ");
            String confirmation = scanner.nextLine().toLowerCase();
            
            if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                System.out.println("❌ Deletion cancelled.");
                return;
            }
            
            String sql = "DELETE FROM students WHERE roll = ?";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, roll);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println(" Student deleted successfully!");
                } else {
                    System.out.println(" No student found with roll number: " + roll);
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println(" Invalid roll number! Please enter numeric value.");
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }
    
    
    private static void closeResources() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database connection closed.");
            }
            scanner.close();
        } catch (SQLException e) {
            System.err.println(" Error closing connection: " + e.getMessage());
        }
    }
}

