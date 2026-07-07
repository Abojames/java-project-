package com.gbekor.srms.model;

import com.gbekor.srms.exception.InvalidDataException;

/**
 * Abstract base class shared by every "person" record in the system
 * (Student, Advisor). Fields are kept private (encapsulation) and can
 * only be changed through validated setters. getRole() and generateReport()
 * are left abstract, forcing every subclass to define its own identity
 * and reporting behaviour (abstraction + polymorphism).
 */
public abstract class Person implements Reportable {

    private String id;
    private String fullName;
    private String contactNumber;
    private String email;

    protected Person(String id, String fullName, String contactNumber, String email) throws InvalidDataException {
        setId(id);
        setFullName(fullName);
        setContactNumber(contactNumber);
        setEmail(email);
    }

    // ----- Encapsulated accessors with validation -----

    public String getId() {
        return id;
    }

    public void setId(String id) throws InvalidDataException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("ID cannot be empty.");
        }
        this.id = id.trim().toUpperCase();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) throws InvalidDataException {
        if (fullName == null || fullName.trim().length() < 2) {
            throw new InvalidDataException("Full name must have at least 2 characters.");
        }
        this.fullName = fullName.trim();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) throws InvalidDataException {
        if (contactNumber == null || !contactNumber.matches("^[0-9+ ]{7,15}$")) {
            throw new InvalidDataException("Contact number must be 7-15 digits (may include + and spaces).");
        }
        this.contactNumber = contactNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InvalidDataException {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidDataException("Email address is not valid.");
        }
        this.email = email.trim();
    }

    /** Every concrete Person type must state what role it plays in the system. */
    public abstract String getRole();

    /** Common CSV-style row representation, refined further by subclasses. */
    public String toCsvRow() {
        return id + "," + fullName + "," + contactNumber + "," + email;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, fullName, getRole());
    }
}
