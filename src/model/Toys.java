package model;

public class Toys extends Collectible {
    private String type; // e.g., car, figurine
    private String name;
    private boolean partOfCollection; // or String if needed, but bool per brief

    public Toys(String id, int low, int high, String owner, Condition condition, double startingPrice,
                String type, String name, boolean partOfCollection) {
        super(id, low, high, owner, condition, startingPrice);
        this.type = type;
        this.name = name;
        this.partOfCollection = partOfCollection;
    }

    // Getters
    public String getType() { return type; }
    public String getName() { return name; }
    public boolean isPartOfCollection() { return partOfCollection; }

    @Override
    public String getDetails() {
        return super.toString() + "\nType: " + type + "\nName: " + name + "\nPart of Collection: " + partOfCollection;
    }
}