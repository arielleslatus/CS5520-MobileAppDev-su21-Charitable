package edu.neu.charitable;

public class EventCard {

    private String name;
    private String description;

    public EventCard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}