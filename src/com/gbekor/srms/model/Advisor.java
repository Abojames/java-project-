package com.gbekor.srms.model;

import com.gbekor.srms.exception.InvalidDataException;

/**
 * Concrete Person subclass representing an academic advisor/lecturer.
 * A second sibling of Student under Person, so calling generateReport()
 * or getRole() on a Person[] array shows true runtime polymorphism.
 */
public class Advisor extends Person {

    private String department;
    private String rank; // e.g. "Lecturer", "Senior Lecturer"

    public Advisor(String id, String fullName, String contactNumber, String email,
                    String department, String rank) throws InvalidDataException {
        super(id, fullName, contactNumber, email);
        setDepartment(department);
        setRank(rank);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) throws InvalidDataException {
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidDataException("Department cannot be empty.");
        }
        this.department = department.trim();
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) throws InvalidDataException {
        if (rank == null || rank.trim().isEmpty()) {
            throw new InvalidDataException("Rank/title cannot be empty.");
        }
        this.rank = rank.trim();
    }

    @Override
    public String getRole() {
        return "Advisor";
    }

    @Override
    public String generateReport() {
        return String.format(
            "ADVISOR REPORT%nID: %s%nName: %s%nDepartment: %s%nRank: %s%nContact: %s%nEmail: %s",
            getId(), getFullName(), department, rank, getContactNumber(), getEmail()
        );
    }

    @Override
    public String toCsvRow() {
        return super.toCsvRow() + "," + department + "," + rank;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + rank + ", " + department;
    }
}
