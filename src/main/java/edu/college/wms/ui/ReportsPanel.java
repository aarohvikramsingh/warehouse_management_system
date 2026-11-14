package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.model.Product;
import edu.college.wms.model.StockMovement;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public final class ReportsPanel extends JPanel {
    private final WarehouseData data;
    private final JList<String> reportList = new JList<>(new String[]{
            "Product catalog", "Low-stock items", "Stock by category", "Stock movements"});
    private final DefaultTableModel reportModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable reportTable = new JTable(reportModel);
    private final JTextArea summaryArea = new JTextArea();
    private final JLabel generatedLabel = new JLabel(" ");

    public ReportsPanel(WarehouseData data) {
        this.data = data;
        setLayout(new BorderLayout(0, 14));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportList.setSelectedIndex(0);
        reportList.setFixedCellHeight(36);
        reportList.addListSelectionListener(event -> { if (!event.getValueIsAdjusting()) refreshReport(); });
        reportTable.setRowHeight(27);
        reportTable.setAutoCreateRowSorter(true);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(AppTheme.font(Font.PLAIN, 13));
        refreshReport();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        JLabel title = new JLabel("Reports");
        title.setForeground(AppTheme.NAVY);
        title.setFont(AppTheme.font(Font.BOLD, 22));
        JLabel subtitle = new JLabel("Review product, inventory, and stock movement snapshots.");
        subtitle.setForeground(AppTheme.MUTED);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JSplitPane createWorkspace() {
        JPanel reportTypes = new JPanel(new BorderLayout(0, 8));
        reportTypes.setBackground(AppTheme.WHITE);
        reportTypes.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel typeHeading = new JLabel("Report type");
        typeHeading.setForeground(AppTheme.NAVY);
        typeHeading.setFont(AppTheme.font(Font.BOLD, 13));
        reportTypes.add(typeHeading, BorderLayout.NORTH);
        reportTypes.add(new JScrollPane(reportList), BorderLayout.CENTER);
        reportTypes.setPreferredSize(new Dimension(190, 0));

        JPanel reportContent = new JPanel(new BorderLayout(0, 12));
        reportContent.setOpaque(false);
        JPanel summary = new JPanel(new BorderLayout(0, 6));
        summary.setBackground(AppTheme.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel summaryHeading = new JLabel("Report summary");
        summaryHeading.setForeground(AppTheme.NAVY);
        summaryHeading.setFont(AppTheme.font(Font.BOLD, 13));
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setPreferredSize(new Dimension(450, 90));
        summary.add(summaryHeading, BorderLayout.NORTH);
        summary.add(summaryScroll, BorderLayout.CENTER);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(AppTheme.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        tableCard.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton refresh = new JButton("Refresh report");
        refresh.addActionListener(event -> refreshReport());
        JButton copy = new JButton("Copy summary");
        copy.addActionListener(event -> copySummary());
        JButton print = new JButton("Print table");
        print.addActionListener(event -> printReport());
        buttons.add(refresh);
        buttons.add(copy);
        buttons.add(print);
        generatedLabel.setForeground(AppTheme.MUTED);
        generatedLabel.setFont(AppTheme.font(Font.PLAIN, 11));
        actions.add(generatedLabel, BorderLayout.WEST);
        actions.add(buttons, BorderLayout.EAST);
        reportContent.add(summary, BorderLayout.NORTH);
        reportContent.add(tableCard, BorderLayout.CENTER);
        reportContent.add(actions, BorderLayout.SOUTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, reportTypes, reportContent);
        split.setResizeWeight(0.18);
        split.setDividerSize(8);
        split.setBorder(null);
        return split;
    }

    public void refreshReport() {
        String type = reportList.getSelectedValue();
        if (type == null) type = "Product catalog";
        switch (type) {
            case "Low-stock items" -> showLowStockReport();
            case "Stock by category" -> showCategoryReport();
            case "Stock movements" -> showMovementReport();
            default -> showProductReport();
        }
        generatedLabel.setText("Updated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
    }

    private void showProductReport() {
        setColumns("Product ID", "Product name", "Category", "Price", "Quantity", "Type", "Status");
        List<Product> products = data.getProducts();
        for (Product product : products) addProductRow(product);
        int units = products.stream().mapToInt(Product::getQuantity).sum();
        summaryArea.setText("Products in catalog: " + products.size() + "\nUnits available: " + units
                + "\nSuppliers on file: " + data.getSuppliers().size());
    }

    private void showLowStockReport() {
        setColumns("Product ID", "Product name", "Category", "Available", "Alert at", "Status");
        List<Product> lowStock = data.getProducts().stream()
                .filter(product -> product.isLowStockTracked() && product.getQuantity() <= product.getLowStockThreshold())
                .toList();
        for (Product product : lowStock) {
            reportModel.addRow(new Object[]{product.getId(), product.getName(), product.getCategory(),
                    product.getQuantity(), product.getLowStockThreshold(), "Reorder review"});
        }
        int shortfall = lowStock.stream().mapToInt(p -> Math.max(0, p.getLowStockThreshold() - p.getQuantity())).sum();
        summaryArea.setText("Products at or below their alert level: " + lowStock.size()
                + "\nTotal units currently in those products: " + lowStock.stream().mapToInt(Product::getQuantity).sum()
                + "\nCombined quantity below configured alerts: " + shortfall + " units");
    }

    private void showCategoryReport() {
        setColumns("Category", "Product count", "Available units", "Low-stock items");
        Map<String, List<Product>> byCategory = data.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getCategory, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<String, List<Product>> entry : byCategory.entrySet()) {
            List<Product> products = entry.getValue();
            long low = products.stream().filter(p -> p.isLowStockTracked() && p.getQuantity() <= p.getLowStockThreshold()).count();
            reportModel.addRow(new Object[]{entry.getKey(), products.size(), products.stream().mapToInt(Product::getQuantity).sum(), low});
        }
        summaryArea.setText("Categories represented: " + byCategory.size() + "\nTotal products: " + data.getProducts().size()
                + "\nTotal units in stock: " + data.getProducts().stream().mapToInt(Product::getQuantity).sum());
    }

    private void showMovementReport() {
        setColumns("Time", "Product ID", "Product", "Change", "Balance", "Note");
        List<StockMovement> movements = data.getMovements();
        movements.sort(java.util.Comparator.comparing(StockMovement::getTime).reversed());
        for (StockMovement movement : movements) {
            reportModel.addRow(new Object[]{movement.getTime().format(DateTimeFormatter.ofPattern("dd MMM HH:mm")),
                    movement.getProductId(), movement.getProductName(), movement.getChange() > 0 ? "+" + movement.getChange() : movement.getChange(),
                    movement.getBalance(), movement.getNote()});
        }
        summaryArea.setText("Stock movements recorded this session: " + movements.size()
                + "\nIncoming units: " + movements.stream().filter(m -> m.getChange() > 0).mapToInt(StockMovement::getChange).sum()
                + "\nOutgoing units: " + movements.stream().filter(m -> m.getChange() < 0).mapToInt(StockMovement::getChange).sum());
    }

    private void addProductRow(Product product) {
        reportModel.addRow(new Object[]{product.getId(), product.getName(), product.getCategory(),
                product.getPrice().toPlainString(), product.getQuantity(), product.getType(),
                product.isActive() ? "Active" : "Inactive"});
    }

    private void setColumns(String... columns) {
        reportModel.setRowCount(0);
        reportModel.setColumnIdentifiers(columns);
    }

    private void copySummary() {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(summaryArea.getText()), null);
            JOptionPane.showMessageDialog(this, "Report summary copied to the clipboard.", "Reports", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, "The clipboard is not available right now.", "Reports", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void printReport() {
        try {
            reportTable.print(JTable.PrintMode.FIT_WIDTH, null, null);
        } catch (java.awt.print.PrinterException exception) {
            JOptionPane.showMessageDialog(this, "Could not print report: " + exception.getMessage(), "Reports", JOptionPane.ERROR_MESSAGE);
        }
    }
}
