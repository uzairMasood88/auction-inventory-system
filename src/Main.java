import core.Inventory;
import ui.InventoryGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        // Load from data/inventory.csv; fallback to sample if missing/errors
        try {
            inventory.loadFromFile("data/inventory.csv");
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
            createSampleData(inventory); // Creates minimal sample
            try {
                inventory.saveToFile("data/inventory.csv"); // Save sample
            } catch (Exception ex) {
                System.err.println("Error saving sample: " + ex.getMessage());
            }
        }
        SwingUtilities.invokeLater(() -> new InventoryGUI(inventory));
    }

    private static void createSampleData(Inventory inv) {
        inv.add(new model.Memorabilia("M001", 1950, 1960, "John Doe", model.Condition.MINT, 150.0, "Elvis Presley", "Singer", "Clothes", true));
        inv.add(new model.Jewellery("J001", 1800, 1850, "Jane Smith", model.Condition.RESTORED, 2000.0, "Necklace", "Gold", "Diamond"));
        inv.add(new model.Toys("T001", 1980, 1990, "Bob Wilson", model.Condition.NEEDS_RESTORING, 75.0, "Figurine", "Star Wars Yoda", true));
        inv.add(new model.Memorabilia("M002", 1970, 1980, "Alice Johnson", model.Condition.MINT, 300.0, "Muhammad Ali", "Boxer", "Gloves", false));
        System.out.println("Loaded sample data (4 items). Expand via CSV for full demo.");
    }
}