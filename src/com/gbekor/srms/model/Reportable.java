package com.gbekor.srms.model;

/**
 * Contract for any entity that can produce a human-readable summary report.
 * Implemented differently by each subclass of Person, giving us runtime
 * polymorphism when we call generateReport() through a Person reference.
 */
public interface Reportable {
    String generateReport();
}
