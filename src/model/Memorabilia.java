package model;

public class Memorabilia extends Collectible {
    private String personality;
    private String occupation;
    private String objectType; // e.g., playing cards, clothes
    private boolean autographed;

    public Memorabilia(String id, int low, int high, String owner, Condition condition, double startingPrice,
                       String personality, String occupation, String objectType, boolean autographed) {
        super(id, low, high, owner, condition, startingPrice);
        this.personality = personality;
        this.occupation = occupation;
        this.objectType = objectType;
        this.autographed = autographed;
    }

    // Getters
    public String getPersonality() { return personality; }
    public String getOccupation() { return occupation; }
    public String getObjectType() { return objectType; }
    public boolean isAutographed() { return autographed; }

    @Override
    public String getDetails() {
        return super.toString() + "\nPersonality: " + personality + "\nOccupation: " + occupation + "\nObject Type: " + objectType + "\nAutographed: " + autographed;
    }
}