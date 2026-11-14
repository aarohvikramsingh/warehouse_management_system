package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.model.Product;
import edu.college.wms.model.StockMovement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("serial")
public final class InventoryPanel extends JPanel {
    private final WarehouseData data;
    private final JTextField searchField = new JTextField(18);
    private final JList<String> categoryList = new JList<>(new String[]{
            "All categories", "Packaging", "Safety", "Office", "Medical", "Electronics", "Other"});
    private final JSlider lowStockSlider = new JSlider(0, 50, 10);
    private final JLabel cutoffLabel = new JLabel("Alert cutoff: 10 units");
    private final javax.swing.JCheckBox lowStockOnly = new javax.swing.JCheckBox("Show low-stock only");
    private final DefaultTableModel stockModel = new DefaultTableModel(
            new Object[]{"ID", "Product", "Category", "Available", "Alert at", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable stockTable = new JTable(stockModel);
    private final DefaultTableModel movementModel = new DefaultTableModel(
            new Object[]{"Time", "Product", "Change", "Balance", "Note"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable movementTable = new JTable(movementModel);
    private final JLabel selectedProductLabel = new JLabel("Select a product from the stock table.");
    private final JProgressBar stockProgress = new JProgressBar(0, 10);
    private final JComboBox<String> changeType = new JComboBox<>(new String[]{"Increase stock", "Decrease stock"});
    private final JSpinner amountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
    private final JTextField reasonField = new JTextField(18);
    private int selectedProductId = -1;

    public InventoryPanel(WarehouseData data) {
        this.data = data;
        setLayout(new BorderLayout(0, 14));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);

        categoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoryList.setSelectedIndex(0);
        categoryList.addListSelectionListener(event -> { if (!event.getValueIsAdjusting()) refreshStockTable(); });
        lowStockSlider.setMajorTickSpacing(10);
        lowStockSlider.setMinorTickSpacing(5);
        lowStockSlider.setPaintTicks(true);
        lowStockSlider.setPaintLabels(true);
        lowStockSlider.addChangeListener(event -> {
            cutoffLabel.setText("Alert cutoff: " + lowStockSlider.getValue() + " units");
            refreshStockTable();
            updateSelectedProduct();
        });
        lowStockOnly.addActionListener(event -> refreshStockTable());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { refreshStockTable(); }
            @Override public void removeUpdate(DocumentEvent event) { refreshStockTable(); }
            @Override public void changedUpdate(DocumentEvent event) { refreshStockTable(); }
        });
        stockTable.setRowHeight(28);
        stockTable.setAutoCreateRowSorter(true);
        stockTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stockTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) selectCurrentRow();
        });
        movementTable.setRowHeight(24);
        stockProgress.setStringPainted(true);
        refreshData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("Inventory Management");
        title.setForeground(AppTheme.NAVY);
        title.setFont(AppTheme.font(Font.BOLD, 22));
        JLabel subtitle = new JLabel("Monitor stock levels and record incoming or outgoing units.");
        subtitle.setForeground(AppTheme.MUTED);
        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        searchField.setPreferredSize(new Dimension(190, 30));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> refreshData());
        filters.add(new JLabel("Search"));
        filters.add(searchField);
        filters.add(lowStockOnly);
        filters.add(refresh);
        header.add(heading, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);
        return header;
    }

    private JSplitPane createWorkspace() {
        JPanel categoryPanel = new JPanel(new BorderLayout(0, 8));
        categoryPanel.setBackground(AppTheme.WHITE);
        categoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel categories = new JLabel("Categories");
        categories.setForeground(AppTheme.NAVY);
        categories.setFont(AppTheme.font(Font.BOLD, 13));
        categoryPanel.add(categories, BorderLayout.NORTH);
        categoryPanel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        categoryPanel.setPreferredSize(new Dimension(150, 0));

        JPanel tableAndHistory = new JPanel(new BorderLayout(0, 10));
        tableAndHistory.setOpaque(false);
        JPanel stockCard = card("Current stock", new JScrollPane(stockTable));
        JPanel historyCard = card("Recent stock movements", new JScrollPane(movementTable));
        historyCard.setPreferredSize(new Dimension(300, 190));
        tableAndHistory.add(stockCard, BorderLayout.CENTER);
        tableAndHistory.add(historyCard, BorderLayout.SOUTH);
        JSplitPane primarySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryPanel,
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableAndHistory, createAdjustmentPanel()));
        primarySplit.setResizeWeight(0.15);
        primarySplit.setBorder(null);
        primarySplit.setDividerSize(7);
        JSplitPane rightSplit = (JSplitPane) primarySplit.getRightComponent();
        rightSplit.setResizeWeight(0.72);
        rightSplit.setDividerSize(7);
        return primarySplit;
    }

    private JPanel createAdjustmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(AppTheme.WHITE);
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 16, 18, 16)));
        JLabel title = new JLabel("Stock adjustment");
        title.setForeground(AppTheme.NAVY);
        title.setFont(AppTheme.font(Font.BOLD, 16));
        selectedProductLabel.setForeground(AppTheme.MUTED);
        selectedProductLabel.setFont(AppTheme.font(Font.PLAIN, 13));
        stockProgress.setString("Select a product");

        JPanel threshold = new JPanel(new BorderLayout(0, 4));
        threshold.setOpaque(false);
        JLabel thresholdTitle = new JLabel("Low-stock review level");
        thresholdTitle.setFont(AppTheme.font(Font.BOLD, 12));
        threshold.add(thresholdTitle, BorderLayout.NORTH);
        threshold.add(cutoffLabel, BorderLayout.CENTER);
        threshold.add(lowStockSlider, BorderLayout.SOUTH);

        JPanel fields = new JPanel(new java.awt.GridLayout(0, 1, 0, 8));
        fields.setOpaque(false);
        fields.add(labelled("Action", changeType));
        fields.add(labelled("Units", amountSpinner));
        fields.add(labelled("Reason", reasonField));
        JButton apply = new JButton("Apply adjustment");
        apply.setBackground(AppTheme.BLUE);
        apply.setForeground(AppTheme.WHITE);
        apply.addActionListener(event -> applyAdjustment());

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(selectedProductLabel, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(stockProgress, BorderLayout.NORTH);
        center.add(threshold, BorderLayout.CENTER);
        center.add(fields, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(apply, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel card(String title, java.awt.Component content) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(AppTheme.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel heading = new JLabel(title);
        heading.setForeground(AppTheme.NAVY);
        heading.setFont(AppTheme.font(Font.BOLD, 13));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel labelled(String title, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setForeground(AppTheme.MUTED);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public void refreshData() {
        refreshStockTable();
        refreshMovementTable();
        updateSelectedProduct();
    }

    private void refreshStockTable() {
        int previouslySelected = selectedProductId;
        stockModel.setRowCount(0);
        String category = categoryList.getSelectedValue();
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        int cutoff = lowStockSlider.getValue();
        for (Product product : data.getProducts()) {
            boolean matchesCategory = category == null || category.equals("All categories") || category.equals(product.getCategory());
            boolean matchesSearch = (product.getId() + " " + product.getName() + " " + product.getCategory()).toLowerCase(Locale.ROOT).contains(query);
            boolean isLow = product.isLowStockTracked() && product.getQuantity() <= cutoff;
            if (matchesCategory && matchesSearch && (!lowStockOnly.isSelected() || isLow)) {
                String status = isLow ? "LOW STOCK" : (product.isActive() ? "In stock" : "Inactive");
                stockModel.addRow(new Object[]{product.getId(), product.getName(), product.getCategory(),
                        product.getQuantity(), product.getLowStockThreshold(), status});
            }
        }
        if (previouslySelected >= 0) {
            int modelRow = -1;
            for (int row = 0; row < stockModel.getRowCount(); row++) {
                if (((Number) stockModel.getValueAt(row, 0)).intValue() == previouslySelected) {
                    modelRow = row;
                    break;
                }
            }
            if (modelRow >= 0) {
                int viewRow = stockTable.convertRowIndexToView(modelRow);
                stockTable.setRowSelectionInterval(viewRow, viewRow);
            } else {
                selectedProductId = -1;
                selectedProductLabel.setText("Select a product from the stock table.");
                stockProgress.setValue(0);
                stockProgress.setString("Select a product");
            }
        }
    }

    private void refreshMovementTable() {
        movementModel.setRowCount(0);
        List<StockMovement> movements = data.getMovements();
        movements.sort(Comparator.comparing(StockMovement::getTime).reversed());
        for (StockMovement movement : movements.stream().limit(8).toList()) {
            movementModel.addRow(new Object[]{movement.getTime().toLocalTime().withNano(0).toString(),
                    movement.getProductName(), movement.getChange() > 0 ? "+" + movement.getChange() : movement.getChange(),
                    movement.getBalance(), movement.getNote()});
        }
    }

    private void selectCurrentRow() {
        int viewRow = stockTable.getSelectedRow();
        if (viewRow < 0) return;
        int row = stockTable.convertRowIndexToModel(viewRow);
        selectedProductId = ((Number) stockModel.getValueAt(row, 0)).intValue();
        updateSelectedProduct();
    }

    private void updateSelectedProduct() {
        if (selectedProductId < 0) return;
        data.findProduct(selectedProductId).ifPresentOrElse(product -> {
            int cutoff = Math.max(1, lowStockSlider.getValue());
            stockProgress.setMaximum(cutoff);
            stockProgress.setValue(Math.min(product.getQuantity(), cutoff));
            stockProgress.setString(product.getQuantity() <= lowStockSlider.getValue()
                    ? product.getQuantity() + " units • low" : product.getQuantity() + " units • above cutoff");
            selectedProductLabel.setText("#" + product.getId() + "  " + product.getName());
        }, () -> {
            selectedProductId = -1;
            selectedProductLabel.setText("Select a product from the stock table.");
            stockProgress.setValue(0);
            stockProgress.setString("Select a product");
        });
    }

    private void applyAdjustment() {
        if (selectedProductId < 0) {
            JOptionPane.showMessageDialog(this, "Select a product from the stock table first.", "Stock adjustment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int amount = (Integer) amountSpinner.getValue();
        int change = changeType.getSelectedIndex() == 0 ? amount : -amount;
        String reason = reasonField.getText().trim();
        if (reason.isEmpty()) reason = change > 0 ? "Stock received" : "Stock issued";
        try {
            data.adjustStock(selectedProductId, change, reason);
            refreshData();
            reasonField.setText("");
            JOptionPane.showMessageDialog(this, "Stock level updated.", "Inventory updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Stock adjustment", JOptionPane.ERROR_MESSAGE);
        }
    }
}
