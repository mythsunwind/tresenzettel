package net.nostate.drugstore.tresenzettel.models;

public class CalculationLogEntry {

    public enum LogLevel {
        WARN, VERBOSE, ERROR, INFO, RESULT
    }

    private LogLevel level;
    private String entry;

    public CalculationLogEntry(LogLevel level, String entry) {
        this.level = level;
        this.entry = entry;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getEntry() {
        return entry;
    }
}
