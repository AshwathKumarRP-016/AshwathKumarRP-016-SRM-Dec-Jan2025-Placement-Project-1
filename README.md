# Student Registration System

A simple Java console application to manage student records using MySQL database.

## 📌 What This Project Does
- Add new students to database
- View all registered students
- Search students by roll number
- Update student information
- Delete student records

## 🛠️ Technologies Used
- Java
- MySQL Database
- JDBC (Java Database Connectivity)

## 📁 Files Needed
1. `StudentRegistrationSystem.java` - Main program file
2. `mysql-connector-j-8.0.33.jar` - MySQL driver

## ⚙️ Setup Instructions

### 1. Setup MySQL Database
```sql
-- Run these commands in MySQL:
CREATE DATABASE student_db;

USE student_db;

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    roll INT UNIQUE,
    dept VARCHAR(50)
);
```

When you run the program, you'll see this menu:
```
STUDENT REGISTRATION SYSTEM
==========================
1. Register New Student
2. View All Students
3. Search Student
4. Update Student
5. Delete Student
6. Exit
```
