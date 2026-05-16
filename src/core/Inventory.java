package core;

import model.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Inventory {
    private List<Collectible> items = new ArrayList<>();

    public List<Collectible> getItems() { return new ArrayList<>(items); }
    public void setItems(List<Collectible> items) { this.items = new ArrayList<>(items); }

    public void add(Collectible c) { items.add(c); }

    public void loadFromFile(String filename) {
        List<String> errors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    errors.add("Line too short (needs at least 7 fields): " + line);
                    continue;
                }
                // Fix for BOM/extra chars: Remove non-letter chars, trim, lower
                String typeStr = parts[0].replaceAll("[^a-zA-Z]", "").trim().toLowerCase();
                String id = parts[1].trim();
                int low, high;
                String owner = parts[4].trim();
                Condition cond;
                double price;
                try {
                    low = Integer.parseInt(parts[2].trim());
                    high = Integer.parseInt(parts[3].trim());
                    cond = Condition.valueOf(parts[5].trim().toUpperCase());
                    price = Double.parseDouble(parts[6].trim());
                } catch (NumberFormatException e) {
                    errors.add("Invalid number in " + line + ": " + e.getMessage());
                    continue;
                } catch (IllegalArgumentException e) {
                    errors.add("Invalid enum or arg in " + line + ": " + e.getMessage());
                    continue;
                }
                Collectible c = null;
                try {
                    switch (typeStr) {
                        case "memorabilia":
                            if (parts.length != 11) throw new IllegalArgumentException("Expected 11 fields for memorabilia");
                            String personality = parts[7].trim();
                            String occupation = parts[8].trim();
                            String objType = parts[9].trim();
                            boolean autographed = Boolean.parseBoolean(parts[10].trim());
                            c = new Memorabilia(id, low, high, owner, cond, price, personality, occupation, objType, autographed);
                            break;
                        case "jewellery":
                            if (parts.length != 10) throw new IllegalArgumentException("Expected 10 fields for jewellery");
                            String jType = parts[7].trim();
                            String jMaterial = parts[8].trim();
                            String jGems = parts[9].trim();
                            c = new Jewellery(id, low, high, owner, cond, price, jType, jMaterial, jGems);
                            break;
                        case "toys":
                            if (parts.length != 10) throw new IllegalArgumentException("Expected 10 fields for toys");
                            String tType = parts[7].trim();
                            String tName = parts[8].trim();
                            boolean tCollection = Boolean.parseBoolean(parts[9].trim());
                            c = new Toys(id, low, high, owner, cond, price, tType, tName, tCollection);
                            break;
                        default:
                            errors.add("Unknown type: '" + typeStr + "' in line: " + line);
                            continue;
                    }
                    if (c != null) items.add(c);
                } catch (Exception e) {
                    errors.add("Failed to create " + typeStr + " from " + line + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("File read error: " + e.getMessage());
        }
        if (!errors.isEmpty()) {
            System.err.println("Parsing errors:\n" + String.join("\n", errors));
        }
        System.out.println("Loaded " + items.size() + " valid items.");
    }

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Collectible c : items) {
                String type = c.getClass().getSimpleName().toLowerCase();
                pw.print(type + "," + c.getId() + "," + c.getYear().getLow() + "," + c.getYear().getHigh() + "," +
                         c.getOwner() + "," + c.getCondition() + "," + c.getStartingPrice());
                if (c instanceof Memorabilia) {
                    Memorabilia m = (Memorabilia) c;
                    pw.print("," + m.getPersonality() + "," + m.getOccupation() + "," + m.getObjectType() + "," + m.isAutographed());
                } else if (c instanceof Jewellery) {
                    Jewellery j = (Jewellery) c;
                    pw.print("," + j.getType() + "," + j.getMaterial() + "," + j.getGems());
                } else if (c instanceof Toys) {
                    Toys t = (Toys) c;
                    pw.print("," + t.getType() + "," + t.getName() + "," + t.isPartOfCollection());
                }
                pw.println();
            }
        }
        System.out.println("Saved " + items.size() + " items to " + filename);
    }

    public void generateStats(String filename) throws IOException {
        if (items.isEmpty()) {
            System.out.println("No items for stats.");
            return;
        }

        int total = items.size();

        Collectible oldestLow = items.stream().min(Comparator.comparing(c -> c.getYear().getLow())).orElse(null);
        Collectible newestHigh = items.stream().max(Comparator.comparing(c -> c.getYear().getHigh())).orElse(null);

        double sumPrice = items.stream().mapToDouble(Collectible::getStartingPrice).sum();
        double meanPrice = sumPrice / total;
        double variance = items.stream().mapToDouble(c -> Math.pow(c.getStartingPrice() - meanPrice, 2)).average().orElse(0);
        double stdDev = Math.sqrt(variance);

        Collectible mostExp = items.stream().max(Collectible.byPrice()).orElse(null);
        Collectible leastExp = items.stream().min(Collectible.byPrice()).orElse(null);

        Map<Condition, Long> condBreakdown = items.stream()
                .collect(Collectors.groupingBy(Collectible::getCondition, Collectors.counting()));

        List<Collectible> topDiff = items.stream()
                .sorted(Comparator.comparingInt(c -> -(c.getYear().getHigh() - c.getYear().getLow())))
                .limit(3)
                .collect(Collectors.toList());

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("=== Auction House Inventory Statistics ===");
            pw.println("Total items: " + total);
            if (oldestLow != null) pw.println("Oldest (low year): " + oldestLow.toString());
            if (newestHigh != null) pw.println("Newest (high year): " + newestHigh.toString());
            if (mostExp != null) pw.println("Most expensive: " + mostExp.toString());
            if (leastExp != null) pw.println("Least expensive: " + leastExp.toString());
            pw.println("Average starting price: $" + String.format("%.2f", meanPrice));
            pw.println("Std dev of starting prices: $" + String.format("%.2f", stdDev));
            pw.println("Condition breakdown:");
            for (Map.Entry<Condition, Long> entry : condBreakdown.entrySet()) {
                pw.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            pw.println("Top 3 largest year estimate differences:");
            for (int i = 0; i < topDiff.size(); i++) {
                Collectible cd = topDiff.get(i);
                int diff = cd.getYear().getHigh() - cd.getYear().getLow();
                pw.println((i+1) + ". " + cd.toString() + " (diff: " + diff + " years)");
            }
        }
        System.out.println("Stats generated to " + filename);
    }
}