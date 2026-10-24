# Campus Lost and Found System

## Introduction

Campus communities frequently rely on word of mouth, notice boards, or informal messages when personal belongings are lost or found. These methods can make it difficult to discover a report, contact the right person, and confirm whether an item has been returned. The Campus Lost and Found System is a desktop application that gives students and campus administrators one place to record and review lost and found items.

The application is built with Java Swing for its graphical interface and JDBC for database access. MySQL stores user accounts and item reports. The project is intentionally limited to the core campus workflow: log in, report an item, search reports, submit a claim, and let an administrator accept or reject that claim.

## Project Description

### Purpose

The purpose of the project is to provide a simple, central record of campus lost and found items. Students can report items and browse existing reports. A student who recognizes an item can submit a short claim note. An administrator can review pending claims and either mark an item as returned or reject the claim.

### Problem Statement

Without a shared system, lost and found information may be scattered across offices, social media, chat groups, or physical notice boards. Students may not know where to look, reports can be overlooked, and staff may find it difficult to track which items are still available or have an active claim. The project addresses this problem with a searchable database and a straightforward workflow for recording reports and managing claims.

### Scope

The application supports:

- Student registration and login, plus administrator login.
- Lost and found reports containing a title, description, category, location, and date.
- A searchable item table with Lost, Found, and All filters.
- Student claims with a short explanatory note.
- An administrator Claims tab to mark an item returned or reject a claim.
- Item states of `OPEN`, `CLAIMED`, and `RETURNED`.

The project does not include notifications, automatic item matching, or a web interface. A servlet is not included because the plan describes it as optional.

## System Requirements

### Hardware

- A computer capable of running a JDK and MySQL Server.
- At least 4 GB RAM recommended for a comfortable desktop development environment.
- Sufficient disk space for the JDK, MySQL, and the application files.

### Software

- JDK 17 or newer.
- MySQL 8 database server.
- MySQL Connector/J 8.4.x JDBC driver.
- Windows, macOS, or Linux with a graphical desktop environment for Swing.
- MySQL Workbench or the MySQL command-line client to execute the schema.

### Database Configuration

The application reads connection settings from environment variables:

| Variable | Default |
| --- | --- |
| `LOSTFOUND_DB_URL` | `jdbc:mysql://localhost:3306/campus_lost_found?serverTimezone=UTC` |
| `LOSTFOUND_DB_USER` | `root` |
| `LOSTFOUND_DB_PASSWORD` | Empty string |

Run `sql/schema_simple.sql` to create the database and tables. Register an account through the application. To grant administrator access, update that account's role in MySQL:

```sql
USE campus_lost_found;
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

## Method and Implementation

### Development Method

The implementation follows the plan's incremental build order: define the database schema, add database connectivity and user access, implement item reporting and search, then add claim and administrator workflows. The Swing interface is divided into small classes so each screen and data operation has a clear responsibility.

### Architecture

The program has three main layers:

1. **Presentation layer:** Swing frames, panels, table, and dialogs collect input and display results.
2. **Data access layer:** `UserDao` and `ItemDao` execute SQL statements using JDBC `PreparedStatement` objects and try-with-resources.
3. **Database layer:** MySQL stores users and item reports in the `users` and `items` tables.

### Main Workflows

1. `Main` schedules the login window on Swing's Event Dispatch Thread and selects Nimbus look and feel.
2. A user logs in with an email and password. `UserDao` hashes the submitted password with SHA-256 and queries the matching account.
3. A logged-in user can add an item report or browse reports. Search supports title, description, category, and location, along with an item-type filter.
4. A student can claim an open item they did not report. The database update checks that the item is still open and is not reported by that student.
5. An administrator can view claimed items. Marking one returned updates its status within a transaction; rejecting a claim resets it to open and clears the claim fields.

### Responsiveness and Input Handling

Database work is performed in `SwingWorker` background tasks so the Swing event thread remains available to repaint and respond to input. SQL values are passed through prepared statements. The interface checks required report fields and the date format before saving.

### Database Design

- `users`: `id`, `name`, `email`, SHA-256 password hash, and `role` (`STUDENT` or `ADMIN`).
- `items`: item details, `status`, the reporting user's ID, optional claimant ID, and claim note. Foreign keys connect the reporter and claimant to `users`.

The application stores passwords as SHA-256 hashes to follow the project plan. For a production authentication system, a salted password hashing function such as bcrypt or Argon2 would be more appropriate.

## Project Codes

The implementation is available in the adjacent `src/lostfound/` folder. The main source files and their responsibilities are:

| File | Responsibility |
| --- | --- |
| `Main.java` | Starts the application on the Swing event thread. |
| `Db.java` | Creates MySQL JDBC connections. |
| `User.java`, `Item.java` | Hold user and item data. |
| `UserDao.java` | Registers users, authenticates logins, and hashes passwords. |
| `ItemDao.java` | Adds, searches, claims, and moderates item reports. |
| `LoginFrame.java` | Provides login and student registration screens. |
| `MainFrame.java` | Shows Browse and Report tabs, plus Claims for administrators. |
| `BrowsePanel.java` | Displays searchable items and supports student claims. |
| `ReportPanel.java` | Collects and saves lost or found item reports. |
| `AdminPanel.java` | Lists claims for administrator review. |
| `ItemTableModel.java` | Supplies item values to the Swing table. |
| `sql/schema_simple.sql` | Creates the MySQL database and tables. |

To compile and run from the `lostfound` project directory, place MySQL Connector/J at `lib/mysql-connector-j.jar`, execute the SQL schema, and use:

```powershell
javac -cp "lib/mysql-connector-j.jar" -d out src/lostfound/*.java
java -cp "out;lib/mysql-connector-j.jar" lostfound.Main
```

## Conclusion

The Campus Lost and Found System provides a compact workflow for recording and finding campus items. It combines a Swing desktop interface with JDBC and a MySQL database, and gives administrators a basic way to process claims. The project demonstrates Java GUI programming, event handling, background database work, prepared statements, relational tables, and transaction handling. It can be extended later with stronger password storage, audit history, or a web-based public list if those features become requirements.

## Bibliography

1. Oracle. “The Java Tutorials: Creating a GUI With Swing.” https://docs.oracle.com/javase/tutorial/uiswing/
2. Oracle. “JDBC Basics.” https://docs.oracle.com/javase/tutorial/jdbc/basics/
3. Oracle. “Class `SwingWorker`.” Java Platform API Specification. https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/SwingWorker.html
4. Oracle. “Class `MessageDigest`.” Java Platform API Specification. https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/security/MessageDigest.html
5. MySQL. “MySQL Connector/J Developer Guide.” https://dev.mysql.com/doc/connector-j/en/
6. MySQL. “MySQL 8.0 Reference Manual.” https://dev.mysql.com/doc/refman/8.0/en/
