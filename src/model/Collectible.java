package model;

import java.util.Comparator;

public abstract class Collectible {
    private String id;
    private YearEstimate year;
    private String owner;
    private Condition condition;
    private double startingPrice;

    public Collectible(String id, int low, int high, String owner, Condition condition, double startingPrice) {
        this.id = id;
        this.year = new YearEstimate(low, high);
        this.owner = owner;
        this.condition = condition;
        this.startingPrice = startingPrice;
    }

    // Getters
    public String getId() { return id; }
    public YearEstimate getYear() { return year; }
    public String getOwner() { return owner; }
    public Condition getCondition() { return condition; }
    public double getStartingPrice() { return startingPrice; }

    // Setters for edit
    public void setStartingPrice(double price) { this.startingPrice = price; }
    public void setCondition(Condition condition) { this.condition = condition; }

    // Short descriptor
    @Override
    public String toString() {
        return id + " (" + year.getMid() + ", $" + String.format("%.2f", startingPrice) + ")";
    }

    // Abstract full details
    public abstract String getDetails();

    // Static comparators for sorting
    public static Comparator<Collectible> byId() {
        return Comparator.comparing(Collectible::getId);
    }

    public static Comparator<Collectible> byPrice() {
        return Comparator.comparing(Collectible::getStartingPrice);
    }
}