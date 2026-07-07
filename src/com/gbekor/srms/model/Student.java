package com.gbekor.srms.model;

import com.gbekor.srms.exception.InvalidDataException;

/**
 * Concrete Person subclass representing a student record.
 * Demonstrates INHERITANCE (extends Person) and POLYMORPHISM
 * (overrides getRole(), generateReport(), toString(), toCsvRow()).
 */
public class Student extends Person {

    private String program;      // e.g. "BSc. Computer Science"
    private int level;           // e.g. 100, 200, 300, 400
    private double gpa;          // 0.0 - 4.0
    private String guardianContact;

    public Student(String id, String fullName, String contactNumber, String email,
                    String program, int level, double gpa, String guardianContact) throws InvalidDataException {
        super(id, fullName, contactNumber, email);
        setProgram(program);
        setLevel(level);
        setGpa(gpa);
        setGuardianContact(guardianContact);
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) throws InvalidDataException {
        if (program == null || program.trim().isEmpty()) {
            throw new InvalidDataException("Program of study cannot be empty.");
        }
        this.program = program.trim();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) throws InvalidDataException {
        if (level != 100 && level != 200 && level != 300 && level != 400) {
            throw new InvalidDataException("Level must be one of 100, 200, 300, 400.");
        }
        this.level = level;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) throws InvalidDataException {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new InvalidDataException("GPA must be between 0.0 and 4.0.");
        }
        this.gpa = gpa;
    }

    public String getGuardianContact() {
        return guardianContact;
    }

    public void setGuardianContact(String guardianContact) throws InvalidDataException {
        if (guardianContact == null || !guardianContact.matches("^[0-9+ ]{7,15}$")) {
            throw new InvalidDataException("Guardian contact must be 7-15 digits.");
        }
        this.guardianContact = guardianContact.trim();
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String generateReport() {
        return String.format(
            "STUDENT REPORT%n" +
            "ID: %s%nName: %s%nProgram: %s%nLevel: %d%nGPA: %.2f%n" +
            "Contact: %s%nEmail: %s%nGuardian Contact: %s%nStanding: %s",
            getId(), getFullName(), program, level, gpa,
            getContactNumber(), getEmail(), guardianContact, academicStanding()
        );
    }

    /** Small piece of business logic unique to Student. */
    private String academicStanding() {
        if (gpa >= 3.5) return "First Class";
        if (gpa >= 3.0) return "Second Class Upper";
        if (gpa >= 2.0) return "Second Class Lower";
        return "Pass";
    }

    @Override
    public String toCsvRow() {
        return super.toCsvRow() + "," + program + "," + level + "," + gpa + "," + guardianContact;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + program + ", Level " + level;
    }
}
