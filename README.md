# Smart Student Record Management System

A JavaFX desktop application for managing student, advisor, and course records
at Gbekor Senior High School. Built for **INF811D: Object Oriented Programming**
(MSc Information Technology, University of Cape Coast, College of Distance Education).

## Features

- Add, update, delete, and search student records
- Add, update, and delete advisor (staff) records
- Create courses and enroll students in them
- Generate individual reports for any student or advisor
- Data is saved to CSV files in a local `data/` folder and reloaded on startup
- Input validation with user-friendly error dialogs
- Custom exceptions for duplicate records, missing records, and invalid data

## Object-Oriented Concepts Demonstrated

| Concept | Where |
|---|---|
| **Encapsulation** | All model classes (`Person`, `Student`, `Advisor`, `Course`) keep fields `private` and expose validated getters/setters. |
| **Inheritance** | `Student` and `Advisor` both extend the abstract class `Person`. |
| **Abstraction** | `Person` is abstract and declares `getRole()`; the `Reportable` interface declares `generateReport()`. |
| **Polymorphism** | Calling `generateReport()` or `getRole()` on a `Person` reference executes different code depending on whether the object is a `Student` or `Advisor` (runtime polymorphism). `SchoolDatabase` also has overloaded `findStudentById` / `findStudentByName` methods (compile-time polymorphism). |
| **Exception Handling** | Custom checked exceptions: `InvalidDataException`, `DuplicateRecordException`, `RecordNotFoundException`, all caught in the GUI layer and shown as alerts. |
| **Collections** | `ArrayList<Student>`, `ArrayList<Advisor>`, `ArrayList<Course>` inside `SchoolDatabase`; `List<String>` of enrolled student IDs inside `Course`. |
| **Event-Driven Programming** | JavaFX button/menu `setOnAction` handlers throughout `MainApp`. |

## Project Structure

```
StudentRecordSystem/
├── src/com/gbekor/srms/
│   ├── model/        Person, Student, Advisor, Course, Reportable
│   ├── exception/     InvalidDataException, DuplicateRecordException, RecordNotFoundException
│   ├── service/       SchoolDatabase (CRUD + CSV persistence)
│   └── ui/            MainApp (JavaFX entry point and GUI)
├── data/               CSV files created automatically at runtime
└── README.md
```

## Requirements

- JDK 17 or later
- JavaFX SDK 21 (download from https://openjfx.io/ — not bundled with the JDK since Java 11)

## How to Compile and Run

1. Download the JavaFX SDK for your OS from https://openjfx.io/ and unzip it, e.g. to `C:\javafx-sdk-21` or `~/javafx-sdk-21`.
2. From the project root, compile:

   ```bash
   javac --module-path "<path-to-javafx-sdk>/lib" --add-modules javafx.controls -d out $(find src -name "*.java")
   ```

3. Run:

   ```bash
   java --module-path "<path-to-javafx-sdk>/lib" --add-modules javafx.controls -cp out com.gbekor.srms.ui.MainApp
   ```

   (On Windows, replace `$(find src -name "*.java")` with a list of the `.java` files, or compile from an IDE like IntelliJ/Eclipse with the JavaFX SDK configured as a library — this is the easier route.)

### Easiest option: use an IDE

Import the project into IntelliJ IDEA or Eclipse, add the JavaFX SDK `lib` folder as a library, set VM options to:

```
--module-path "<path-to-javafx-sdk>/lib" --add-modules javafx.controls
```

and run `MainApp.java`.

## Author

James — ICT Teacher, Gbekor Senior High School, Adaklu District, Volta Region, Ghana.
MSc Information Technology, University of Cape Coast (College of Distance Education).
