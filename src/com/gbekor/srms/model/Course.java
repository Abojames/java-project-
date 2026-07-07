package com.gbekor.srms.model;

import com.gbekor.srms.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course. Holds a collection of enrolled student IDs,
 * demonstrating aggregation (a Course "has" Students) alongside the
 * inheritance hierarchy above.
 */
public class Course {

    private String courseCode;
    private String title;
    private int creditHours;
    private final List<String> enrolledStudentIds = new ArrayList<>();

    public Course(String courseCode, String title, int creditHours) throws InvalidDataException {
        setCourseCode(courseCode);
        setTitle(title);
        setCreditHours(creditHours);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) throws InvalidDataException {
        if (courseCode == null || !courseCode.matches("^[A-Za-z]{2,6}\\d{3}$")) {
            throw new InvalidDataException("Course code must look like 'ICT101'.");
        }
        this.courseCode = courseCode.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Course title cannot be empty.");
        }
        this.title = title.trim();
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) throws InvalidDataException {
        if (creditHours < 1 || creditHours > 6) {
            throw new InvalidDataException("Credit hours must be between 1 and 6.");
        }
        this.creditHours = creditHours;
    }

    public List<String> getEnrolledStudentIds() {
        return enrolledStudentIds;
    }

    public void enrollStudent(String studentId) {
        if (!enrolledStudentIds.contains(studentId)) {
            enrolledStudentIds.add(studentId);
        }
    }

    public void unenrollStudent(String studentId) {
        enrolledStudentIds.remove(studentId);
    }

    public String toCsvRow() {
        return courseCode + "," + title + "," + creditHours + "," + String.join("|", enrolledStudentIds);
    }

    @Override
    public String toString() {
        return courseCode + " - " + title + " (" + creditHours + " credit hrs, "
                + enrolledStudentIds.size() + " enrolled)";
    }
}
