# Campus Lost and Found (simple Swing edition)

A small Java Swing + JDBC + MySQL application for student item reports and admin claim review.

## Requirements

- JDK 17 or newer
- MySQL 8
- MySQL Connector/J 8.4.x (the only external Java library)

## Set up the database

Run `sql/schema_simple.sql` in MySQL Workbench or with the MySQL command line. Set the connection environment variables if your local MySQL settings differ from the defaults:

- `LOSTFOUND_DB_URL` (default `jdbc:mysql://localhost:3306/campus_lost_found?serverTimezone=UTC`)
- `LOSTFOUND_DB_USER` (default `root`)
- `LOSTFOUND_DB_PASSWORD` (default empty)

Register the first account from the login window. To grant it admin access, run this SQL, replacing the email:

```sql
USE campus_lost_found;
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

## Compile and run

Download MySQL Connector/J and put its JAR at `lib/mysql-connector-j.jar`, then from this folder run:

```powershell
javac -cp "lib/mysql-connector-j.jar" -d out src/lostfound/*.java
java -cp "out;lib/mysql-connector-j.jar" lostfound.Main
```

No servlet container or application server is needed. Every database operation runs off the Swing event thread with `SwingWorker`.
