package com.gbekor.srms.ui;

import com.gbekor.srms.exception.DuplicateRecordException;
import com.gbekor.srms.exception.InvalidDataException;
import com.gbekor.srms.exception.RecordNotFoundException;
import com.gbekor.srms.model.Advisor;
import com.gbekor.srms.model.Course;
import com.gbekor.srms.model.Person;
import com.gbekor.srms.model.Student;
import com.gbekor.srms.service.SchoolDatabase;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Entry point and GUI controller for the Smart Student Record Management System.
 * Single-file JavaFX UI (no FXML) for straightforward compilation.
 *
 * Data directory: ./data  (CSV files are created there on first save)
 */
public class MainApp extends Application {

    private final SchoolDatabase db = new SchoolDatabase();
    private static final String DATA_DIR = "data";

    private final ObservableList<Student> studentData = FXCollections.observableArrayList();
    private final ObservableList<Advisor> advisorData = FXCollections.observableArrayList();
    private final ObservableList<Course> courseData = FXCollections.observableArrayList();

    private TextArea reportArea;

    @Override
    public void start(Stage stage) {
        // Try to load any previously saved data; ignore if none exists yet.
        try {
            db.loadAll(DATA_DIR);
        } catch (IOException | InvalidDataException e) {
            // First run, or corrupt data - start empty, inform user only if data existed.
        }
        refreshAllLists();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                buildStudentTab(),
                buildAdvisorTab(),
                buildCourseTab(),
                buildReportsTab()
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar(stage));
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 620);
        stage.setTitle("Gbekor SHS - Smart Student Record Management System");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> autoSave());
        stage.show();
    }

    // ==================== MENU BAR ====================

    private MenuBar buildMenuBar(Stage stage) {
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save All");
        saveItem.setOnAction(e -> {
            autoSave();
            showInfo("Saved", "All records saved to the 'data' folder.");
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> { autoSave(); stage.close(); });
        fileMenu.getItems().addAll(saveItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showInfo("About",
                "Smart Student Record Management System\nGbekor Senior High School\n" +
                "Built with JavaFX for INF811D - Object Oriented Programming."));
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private void autoSave() {
        try {
            db.saveAll(DATA_DIR);
        } catch (IOException ex) {
            showError("Save Failed", "Could not write data files: " + ex.getMessage());
        }
    }

    private void refreshAllLists() {
        studentData.setAll(db.getAllStudents());
        advisorData.setAll(db.getAllAdvisors());
        courseData.setAll(db.getAllCourses());
    }

    // ==================== STUDENTS TAB ====================

    private Tab buildStudentTab() {
        TableView<Student> table = new TableView<>(studentData);
        table.getColumns().addAll(
                col("ID", "id", 80),
                col("Full Name", "fullName", 160),
                col("Program", "program", 160),
                col("Level", "level", 70),
                col("GPA", "gpa", 70),
                col("Contact", "contactNumber", 110),
                col("Email", "email", 160),
                col("Guardian Contact", "guardianContact", 120)
        );

        TextField idF = new TextField();
        TextField nameF = new TextField();
        TextField programF = new TextField();
        ComboBox<Integer> levelF = new ComboBox<>(FXCollections.observableArrayList(100, 200, 300, 400));
        TextField gpaF = new TextField();
        TextField contactF = new TextField();
        TextField emailF = new TextField();
        TextField guardianF = new TextField();

        GridPane form = formGrid(
                "Student ID:", idF, "Full Name:", nameF, "Program:", programF,
                "Level:", levelF, "GPA:", gpaF, "Contact:", contactF,
                "Email:", emailF, "Guardian Contact:", guardianF
        );

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");
        Button searchBtn = new Button("Search by Name");

        addBtn.setOnAction(e -> {
            try {
                Student s = new Student(idF.getText(), nameF.getText(), contactF.getText(), emailF.getText(),
                        programF.getText(), levelF.getValue() == null ? 0 : levelF.getValue(),
                        parseDouble(gpaF.getText()), guardianF.getText());
                db.addStudent(s);
                refreshAllLists();
                clearFields(idF, nameF, programF, gpaF, contactF, emailF, guardianF);
                levelF.setValue(null);
            } catch (InvalidDataException | DuplicateRecordException | NumberFormatException ex) {
                showError("Could Not Add Student", ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                Student s = new Student(idF.getText(), nameF.getText(), contactF.getText(), emailF.getText(),
                        programF.getText(), levelF.getValue() == null ? 0 : levelF.getValue(),
                        parseDouble(gpaF.getText()), guardianF.getText());
                db.updateStudent(s);
                refreshAllLists();
            } catch (InvalidDataException | RecordNotFoundException | NumberFormatException ex) {
                showError("Could Not Update Student", ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                db.deleteStudent(idF.getText());
                refreshAllLists();
                clearFields(idF, nameF, programF, gpaF, contactF, emailF, guardianF);
            } catch (RecordNotFoundException ex) {
                showError("Could Not Delete Student", ex.getMessage());
            }
        });

        clearBtn.setOnAction(e -> {
            clearFields(idF, nameF, programF, gpaF, contactF, emailF, guardianF);
            levelF.setValue(null);
        });

        searchBtn.setOnAction(e -> {
            List<Student> matches = db.findStudentByName(nameF.getText());
            studentData.setAll(matches.isEmpty() ? db.getAllStudents() : matches);
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idF.setText(sel.getId());
                nameF.setText(sel.getFullName());
                programF.setText(sel.getProgram());
                levelF.setValue(sel.getLevel());
                gpaF.setText(String.valueOf(sel.getGpa()));
                contactF.setText(sel.getContactNumber());
                emailF.setText(sel.getEmail());
                guardianF.setText(sel.getGuardianContact());
            }
        });

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, clearBtn, searchBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        VBox formBox = new VBox(8, form, buttons);
        formBox.setPadding(new Insets(10));

        SplitPane split = new SplitPane(table, formBox);
        split.setDividerPositions(0.62);

        Tab tab = new Tab("Students", split);
        return tab;
    }

    // ==================== ADVISORS TAB ====================

    private Tab buildAdvisorTab() {
        TableView<Advisor> table = new TableView<>(advisorData);
        table.getColumns().addAll(
                col("ID", "id", 80),
                col("Full Name", "fullName", 170),
                col("Department", "department", 160),
                col("Rank", "rank", 130),
                col("Contact", "contactNumber", 120),
                col("Email", "email", 180)
        );

        TextField idF = new TextField();
        TextField nameF = new TextField();
        TextField deptF = new TextField();
        TextField rankF = new TextField();
        TextField contactF = new TextField();
        TextField emailF = new TextField();

        GridPane form = formGrid(
                "Advisor ID:", idF, "Full Name:", nameF, "Department:", deptF,
                "Rank:", rankF, "Contact:", contactF, "Email:", emailF
        );

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");

        addBtn.setOnAction(e -> {
            try {
                Advisor a = new Advisor(idF.getText(), nameF.getText(), contactF.getText(), emailF.getText(),
                        deptF.getText(), rankF.getText());
                db.addAdvisor(a);
                refreshAllLists();
                clearFields(idF, nameF, deptF, rankF, contactF, emailF);
            } catch (InvalidDataException | DuplicateRecordException ex) {
                showError("Could Not Add Advisor", ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                Advisor a = new Advisor(idF.getText(), nameF.getText(), contactF.getText(), emailF.getText(),
                        deptF.getText(), rankF.getText());
                db.updateAdvisor(a);
                refreshAllLists();
            } catch (InvalidDataException | RecordNotFoundException ex) {
                showError("Could Not Update Advisor", ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                db.deleteAdvisor(idF.getText());
                refreshAllLists();
                clearFields(idF, nameF, deptF, rankF, contactF, emailF);
            } catch (RecordNotFoundException ex) {
                showError("Could Not Delete Advisor", ex.getMessage());
            }
        });

        clearBtn.setOnAction(e -> clearFields(idF, nameF, deptF, rankF, contactF, emailF));

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                idF.setText(sel.getId());
                nameF.setText(sel.getFullName());
                deptF.setText(sel.getDepartment());
                rankF.setText(sel.getRank());
                contactF.setText(sel.getContactNumber());
                emailF.setText(sel.getEmail());
            }
        });

        HBox buttons = new HBox(10, addBtn, updateBtn, deleteBtn, clearBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        VBox formBox = new VBox(8, form, buttons);
        formBox.setPadding(new Insets(10));

        SplitPane split = new SplitPane(table, formBox);
        split.setDividerPositions(0.62);

        return new Tab("Advisors", split);
    }

    // ==================== COURSES TAB ====================

    private Tab buildCourseTab() {
        TableView<Course> table = new TableView<>(courseData);
        table.getColumns().addAll(
                col("Code", "courseCode", 100),
                col("Title", "title", 220),
                col("Credit Hours", "creditHours", 100)
        );

        TextField codeF = new TextField();
        TextField titleF = new TextField();
        TextField creditF = new TextField();
        TextField enrollStudentIdF = new TextField();

        GridPane form = formGrid(
                "Course Code:", codeF, "Title:", titleF,
                "Credit Hours:", creditF, "Enroll Student ID:", enrollStudentIdF
        );

        Button addBtn = new Button("Add Course");
        Button deleteBtn = new Button("Delete Course");
        Button enrollBtn = new Button("Enroll Student");
        Button clearBtn = new Button("Clear");

        addBtn.setOnAction(e -> {
            try {
                Course c = new Course(codeF.getText(), titleF.getText(), parseInt(creditF.getText()));
                db.addCourse(c);
                refreshAllLists();
                clearFields(codeF, titleF, creditF, enrollStudentIdF);
            } catch (InvalidDataException | DuplicateRecordException | NumberFormatException ex) {
                showError("Could Not Add Course", ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                db.deleteCourse(codeF.getText());
                refreshAllLists();
                clearFields(codeF, titleF, creditF, enrollStudentIdF);
            } catch (RecordNotFoundException ex) {
                showError("Could Not Delete Course", ex.getMessage());
            }
        });

        enrollBtn.setOnAction(e -> {
            Course c = db.findCourseByCode(codeF.getText());
            Student s = db.findStudentById(enrollStudentIdF.getText());
            if (c == null) {
                showError("Enrollment Failed", "No course found with that code.");
            } else if (s == null) {
                showError("Enrollment Failed", "No student found with that ID.");
            } else {
                c.enrollStudent(s.getId());
                refreshAllLists();
                showInfo("Enrolled", s.getFullName() + " enrolled in " + c.getCourseCode() + ".");
            }
        });

        clearBtn.setOnAction(e -> clearFields(codeF, titleF, creditF, enrollStudentIdF));

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                codeF.setText(sel.getCourseCode());
                titleF.setText(sel.getTitle());
                creditF.setText(String.valueOf(sel.getCreditHours()));
            }
        });

        HBox buttons = new HBox(10, addBtn, deleteBtn, enrollBtn, clearBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        VBox formBox = new VBox(8, form, buttons);
        formBox.setPadding(new Insets(10));

        SplitPane split = new SplitPane(table, formBox);
        split.setDividerPositions(0.6);

        return new Tab("Courses", split);
    }

    // ==================== REPORTS TAB ====================
    // This tab demonstrates polymorphism directly: the same generateReport()
    // call resolves to different implementations depending on whether the
    // selected Person is a Student or an Advisor.

    private Tab buildReportsTab() {
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Student", "Advisor"));
        typeBox.setValue("Student");
        TextField idF = new TextField();
        idF.setPromptText("Enter ID");
        Button generateBtn = new Button("Generate Report");

        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPrefHeight(420);

        generateBtn.setOnAction(e -> {
            Person p = "Student".equals(typeBox.getValue())
                    ? db.findStudentById(idF.getText())
                    : db.findAdvisorById(idF.getText());
            if (p == null) {
                showError("Not Found", "No " + typeBox.getValue().toLowerCase() + " found with that ID.");
                return;
            }
            // Polymorphic call: actual method executed depends on p's real type.
            reportArea.setText(p.generateReport());
        });

        HBox controls = new HBox(10, new Label("Record type:"), typeBox, idF, generateBtn);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10));

        VBox box = new VBox(10, controls, reportArea);
        box.setPadding(new Insets(10));

        return new Tab("Reports", box);
    }

    // ==================== HELPERS ====================

    private <T> TableColumn<T, ?> col(String header, String property, double width) {
        TableColumn<T, Object> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private GridPane formGrid(Object... labelsAndFields) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        int row = 0, col = 0;
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            Label label = new Label((String) labelsAndFields[i]);
            javafx.scene.Node field = (javafx.scene.Node) labelsAndFields[i + 1];
            grid.add(label, col, row);
            grid.add(field, col + 1, row);
            row++;
        }
        return grid;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    private double parseDouble(String s) {
        return Double.parseDouble(s.trim());
    }

    private int parseInt(String s) {
        return Integer.parseInt(s.trim());
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
