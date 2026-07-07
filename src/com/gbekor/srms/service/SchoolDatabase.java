package com.gbekor.srms.service;

import com.gbekor.srms.exception.DuplicateRecordException;
import com.gbekor.srms.exception.InvalidDataException;
import com.gbekor.srms.exception.RecordNotFoundException;
import com.gbekor.srms.model.Advisor;
import com.gbekor.srms.model.Course;
import com.gbekor.srms.model.Student;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central service/repository class. Keeps the in-memory collections
 * private (encapsulation) and only exposes controlled operations.
 * Provides overloaded search methods -> compile-time polymorphism.
 */
public class SchoolDatabase {

    private final List<Student> students = new ArrayList<>();
    private final List<Advisor> advisors = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();

    private static final String STUDENTS_FILE = "students.csv";
    private static final String ADVISORS_FILE = "advisors.csv";
    private static final String COURSES_FILE = "courses.csv";

    // ---------------- STUDENT CRUD ----------------

    public void addStudent(Student s) throws DuplicateRecordException {
        if (findStudentById(s.getId()) != null) {
            throw new DuplicateRecordException("A student with ID " + s.getId() + " already exists.");
        }
        students.add(s);
    }

    public void updateStudent(Student updated) throws RecordNotFoundException {
        Student existing = findStudentById(updated.getId());
        if (existing == null) {
            throw new RecordNotFoundException("No student found with ID " + updated.getId());
        }
        students.set(students.indexOf(existing), updated);
    }

    public void deleteStudent(String id) throws RecordNotFoundException {
        Student existing = findStudentById(id);
        if (existing == null) {
            throw new RecordNotFoundException("No student found with ID " + id);
        }
        students.remove(existing);
    }

    // Overloaded search: by ID (single result) ...
    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    // ... or by name fragment (multiple results). Same method name, different
    // signature and return type family -> demonstrates overloading.
    public List<Student> findStudentByName(String nameFragment) {
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getFullName().toLowerCase().contains(nameFragment.toLowerCase())) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students); // defensive copy
    }

    // ---------------- ADVISOR CRUD ----------------

    public void addAdvisor(Advisor a) throws DuplicateRecordException {
        if (findAdvisorById(a.getId()) != null) {
            throw new DuplicateRecordException("An advisor with ID " + a.getId() + " already exists.");
        }
        advisors.add(a);
    }

    public void updateAdvisor(Advisor updated) throws RecordNotFoundException {
        Advisor existing = findAdvisorById(updated.getId());
        if (existing == null) {
            throw new RecordNotFoundException("No advisor found with ID " + updated.getId());
        }
        advisors.set(advisors.indexOf(existing), updated);
    }

    public void deleteAdvisor(String id) throws RecordNotFoundException {
        Advisor existing = findAdvisorById(id);
        if (existing == null) {
            throw new RecordNotFoundException("No advisor found with ID " + id);
        }
        advisors.remove(existing);
    }

    public Advisor findAdvisorById(String id) {
        for (Advisor a : advisors) {
            if (a.getId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }

    public List<Advisor> getAllAdvisors() {
        return new ArrayList<>(advisors);
    }

    // ---------------- COURSE CRUD ----------------

    public void addCourse(Course c) throws DuplicateRecordException {
        if (findCourseByCode(c.getCourseCode()) != null) {
            throw new DuplicateRecordException("A course with code " + c.getCourseCode() + " already exists.");
        }
        courses.add(c);
    }

    public void deleteCourse(String code) throws RecordNotFoundException {
        Course existing = findCourseByCode(code);
        if (existing == null) {
            throw new RecordNotFoundException("No course found with code " + code);
        }
        courses.remove(existing);
    }

    public Course findCourseByCode(String code) {
        for (Course c : courses) {
            if (c.getCourseCode().equalsIgnoreCase(code)) return c;
        }
        return null;
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    // ---------------- PERSISTENCE (CSV, with exception handling) ----------------

    public void saveAll(String directory) throws IOException {
        Files.createDirectories(Paths.get(directory));
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(directory, STUDENTS_FILE))) {
            for (Student s : students) w.write(s.toCsvRow() + System.lineSeparator());
        }
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(directory, ADVISORS_FILE))) {
            for (Advisor a : advisors) w.write(a.toCsvRow() + System.lineSeparator());
        }
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(directory, COURSES_FILE))) {
            for (Course c : courses) w.write(c.toCsvRow() + System.lineSeparator());
        }
    }

    public void loadAll(String directory) throws IOException, InvalidDataException {
        students.clear();
        advisors.clear();
        courses.clear();

        Path studentsPath = Paths.get(directory, STUDENTS_FILE);
        if (Files.exists(studentsPath)) {
            for (String line : Files.readAllLines(studentsPath)) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                students.add(new Student(p[0], p[1], p[2], p[3], p[4],
                        Integer.parseInt(p[5]), Double.parseDouble(p[6]), p[7]));
            }
        }
        Path advisorsPath = Paths.get(directory, ADVISORS_FILE);
        if (Files.exists(advisorsPath)) {
            for (String line : Files.readAllLines(advisorsPath)) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                advisors.add(new Advisor(p[0], p[1], p[2], p[3], p[4], p[5]));
            }
        }
        Path coursesPath = Paths.get(directory, COURSES_FILE);
        if (Files.exists(coursesPath)) {
            for (String line : Files.readAllLines(coursesPath)) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                Course c = new Course(p[0], p[1], Integer.parseInt(p[2]));
                if (p.length > 3 && !p[3].isEmpty()) {
                    for (String sid : p[3].split("\\|")) c.enrollStudent(sid);
                }
                courses.add(c);
            }
        }
    }
}
