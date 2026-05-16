package model;

public class Jewellery extends Collectible {
    private String type; // e.g., watch, necklace
    private String material; // e.g., gold, silver
    private String gems; // e.g., none, ruby, diamond

    public Jewellery(String id, int low, int high, String owner, Condition condition, double startingPrice,
                     String type, String material, String gems) {
        super(id, low, high, owner, condition, startingPrice);
        this.type = type;
        this.material = material;
        this.gems = gems;
    }

    // Getters
    public String getType() { return type; }
    public String getMaterial() { return material; }
    public String getGems() { return gems; }

    @Override
    public String getDetails() {
        return super.toString() + "\nType: " + type + "\nMaterial: " + material + "\nGems: " + gems;
    }
}