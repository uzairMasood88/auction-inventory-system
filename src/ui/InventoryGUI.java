package ui;

import core.Inventory;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class InventoryGUI extends JFrame {
    private Inventory inventory;
    private JTable table;
    private DefaultTableModel model;

    public InventoryGUI(Inventory inv) {
        this.inventory = inv;
        setTitle("Auction House Inventory Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        String[] columns = {"ID", "Type", "Owner", "Price ($)", "Condition", "Mid Year"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton moreInfoBtn = new JButton("More Info");
        moreInfoBtn.addActionListener(new MoreInfoListener());
        buttonPanel.add(moreInfoBtn);

        JButton editBtn = new JButton("Edit Price/Condition");
        editBtn.addActionListener(new EditListener());
        buttonPanel.add(editBtn);

        JButton sortIdBtn = new JButton("Sort by ID (Asc)");
        sortIdBtn.addActionListener(e -> sortTable(Collectible.byId()));
        buttonPanel.add(sortIdBtn);

        JButton sortPriceBtn = new JButton("Sort by Price (Desc)");
        sortPriceBtn.addActionListener(e -> sortTable(Collectible.byPrice().reversed()));
        buttonPanel.add(sortPriceBtn);

        JButton statsBtn = new JButton("Generate Stats");
        statsBtn.addActionListener(e -> {
            try {
                inventory.generateStats("stats.txt");
                JOptionPane.showMessageDialog(this, "Statistics saved to stats.txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(statsBtn);

        JButton saveBtn = new JButton("Save to CSV");
        saveBtn.addActionListener(e -> {
            try {
                inventory.saveToFile("data/inventory.csv");
                JOptionPane.showMessageDialog(this, "Data saved to data/inventory.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.SOUTH);
        refreshTable();
        pack();
        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Collectible c : inventory.getItems()) {
            model.addRow(new Object[]{
                c.getId(),
                c.getClass().getSimpleName(),
                c.getOwner(),
                String.format("%.2f", c.getStartingPrice()),
                c.getCondition(),
                c.getYear().getMid()
            });
        }
    }

    private void sortTable(Comparator<Collectible> comparator) {
        List<Collectible> sorted = new ArrayList<>(inventory.getItems());
        sorted.sort(comparator);
        inventory.setItems(sorted);
        refreshTable();
    }

    private class MoreInfoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Collectible c = inventory.getItems().get(row);
                JOptionPane.showMessageDialog(InventoryGUI.this, c.getDetails(), "Item Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(InventoryGUI.this, "Select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private class EditListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Collectible c = inventory.getItems().get(row);
                String priceStr = JOptionPane.showInputDialog(InventoryGUI.this, "New Starting Price:", String.format("%.2f", c.getStartingPrice()));
                if (priceStr != null && !priceStr.trim().isEmpty()) {
                    try {
                        double newPrice = Double.parseDouble(priceStr);
                        c.setStartingPrice(newPrice);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(InventoryGUI.this, "Invalid price: Enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                Object[] condOptions = Condition.values();
                Condition newCond = (Condition) JOptionPane.showInputDialog(InventoryGUI.this, "New Condition:", "Condition", JOptionPane.PLAIN_MESSAGE, null, condOptions, c.getCondition());
                if (newCond != null) {
                    c.setCondition(newCond);
                }
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(InventoryGUI.this, "Select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}