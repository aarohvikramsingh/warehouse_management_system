package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.model.Product;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.Locale;

@SuppressWarnings("serial")
public final class ProductPanel extends JPanel {
    private final WarehouseData data;
    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(18);
    private final JComboBox<String> categoryField = new JComboBox<>(
            new String[]{"Packaging", "Safety", "Office", "Medical", "Electronics", "Other"});
    private final JTextField priceField = new JTextField(10);
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
    private final JSpinner thresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1_000_000, 1));
    private final JRadioButton standardRadio = new JRadioButton("Standard", true);
    private final JRadioButton perishableRadio = new JRadioButton("Perishable");
    private final JCheckBox activeCheck = new JCheckBox("Active product", true);
    private final JCheckBox lowStockCheck = new JCheckBox("Track low-stock alert", true);
    private final JTextArea descriptionArea = new JTextArea(3, 35);
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> categoryFilter = new JComboBox<>(
            new String[]{"All categories", "Packaging", "Safety", "Office", "Medical", "Electronics", "Other"});
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Product name", "Category", "Price", "Quantity", "Type", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable productTable = new JTable(tableModel);

    public ProductPanel(WarehouseData data) {
        this.data = data;
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        add(createHeaderAndFilters(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        idField.setEditable(false);
        idField.setBackground(new java.awt.Color(239, 243, 248));
        categoryField.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        productTable.setRowHeight(28);
        productTable.setAutoCreateRowSorter(true);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelectedProduct();
        });
        addSearchListeners();
        refreshTable();
    }

    private JPanel createHeaderAndFilters() {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        JPanel titleBlock = new JPanel(new BorderLayout(0, 4));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("Product Management");
        title.setForeground(AppTheme.NAVY);
        title.setFont(AppTheme.font(Font.BOLD, 22));
        JLabel subtitle = new JLabel("Create and maintain the warehouse product catalog.");
        subtitle.setForeground(AppTheme.MUTED);
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        searchField.setPreferredSize(new Dimension(190, 30));
        filters.add(new JLabel("Search"));
        filters.add(searchField);
        filters.add(new JLabel("Category"));
        filters.add(categoryFilter);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> refreshTable());
        filters.add(refresh);
        header.add(titleBlock, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false);
        main.add(createForm(), BorderLayout.NORTH);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(AppTheme.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        tableCard.add(new JScrollPane(productTable), BorderLayout.CENTER);
        main.add(tableCard, BorderLayout.CENTER);
        return main;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AppTheme.BORDER), "Product details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        addField(form, c, 0, 0, "Product ID", idField, 1);
        addField(form, c, 2, 0, "Product name", nameField, 1);
        addField(form, c, 0, 1, "Category", categoryField, 1);
        addField(form, c, 2, 1, "Price", priceField, 1);
        addField(form, c, 0, 2, "Quantity", quantitySpinner, 1);

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(standardRadio);
        typeGroup.add(perishableRadio);
        JPanel typeOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        typeOptions.setOpaque(false);
        typeOptions.add(standardRadio);
        typeOptions.add(perishableRadio);
        addField(form, c, 2, 2, "Product type", typeOptions, 1);

        JPanel stockOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        stockOptions.setOpaque(false);
        stockOptions.add(activeCheck);
        stockOptions.add(lowStockCheck);
        stockOptions.add(new JLabel("Alert at"));
        stockOptions.add(thresholdSpinner);
        stockOptions.add(new JLabel("units"));
        addField(form, c, 0, 3, "Status / alert", stockOptions, 3);

        descriptionArea.setFont(AppTheme.font(Font.PLAIN, 13));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(500, 56));
        addField(form, c, 0, 4, "Description", descriptionScroll, 3);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        actions.setOpaque(false);
        JButton clear = new JButton("Clear");
        JButton add = new JButton("Add product");
        JButton update = new JButton("Update selected");
        JButton delete = new JButton("Delete selected");
        add.addActionListener(event -> addProduct());
        update.addActionListener(event -> updateProduct());
        delete.addActionListener(event -> deleteProduct());
        clear.addActionListener(event -> clearForm());
        actions.add(clear);
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        c.weightx = 1;
        c.insets = new Insets(4, 4, 0, 4);
        form.add(actions, c);
        return form;
    }

    private void addField(JPanel form, GridBagConstraints c, int x, int y,
                          String title, Component component, int fieldSpan) {
        c.gridy = y;
        c.gridx = x;
        c.gridwidth = 1;
        c.weightx = 0;
        JLabel label = new JLabel(title);
        label.setFont(AppTheme.font(Font.BOLD, 12));
        form.add(label, c);
        c.gridx = x + 1;
        c.gridwidth = fieldSpan;
        c.weightx = 1;
        form.add(component, c);
    }

    private void addSearchListeners() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent event) { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent event) { refreshTable(); }
        });
        categoryFilter.addActionListener(event -> refreshTable());
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        String category = String.valueOf(categoryFilter.getSelectedItem());
        for (Product product : data.getProducts()) {
            boolean matchesCategory = category.equals("All categories") || product.getCategory().equals(category);
            String searchable = (product.getId() + " " + product.getName() + " " + product.getCategory() + " " + product.getDescription()).toLowerCase(Locale.ROOT);
            if (matchesCategory && searchable.contains(query)) {
                tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getCategory(),
                        product.getPrice().toPlainString(), product.getQuantity(), product.getType(),
                        product.isActive() ? "Active" : "Inactive"});
            }
        }
    }

    public void refreshData() {
        refreshTable();
    }

    private Product readForm(int id) {
        String name = nameField.getText().trim();
        String category = String.valueOf(categoryField.getEditor().getItem()).trim();
        if (name.isEmpty() || category.isEmpty()) throw new IllegalArgumentException("Product name and category are required.");
        BigDecimal price;
        try {
            price = new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid price, for example 12.50.");
        }
        if (price.signum() < 0) throw new IllegalArgumentException("Price cannot be negative.");
        String type = perishableRadio.isSelected() ? "Perishable" : "Standard";
        return new Product(id, name, category, price, (Integer) quantitySpinner.getValue(),
                descriptionArea.getText().trim(), type, activeCheck.isSelected(), lowStockCheck.isSelected(),
                (Integer) thresholdSpinner.getValue());
    }

    private void addProduct() {
        try {
            Product created = data.addProduct(readForm(0));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Product " + created.getId() + " was added.", "Product saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void updateProduct() {
        if (idField.getText().isBlank()) { showError("Select a product from the table before updating it."); return; }
        try {
            data.updateProduct(readForm(Integer.parseInt(idField.getText())));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Product details were updated.", "Product saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteProduct() {
        if (idField.getText().isBlank()) { showError("Select a product from the table before deleting it."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Delete product " + idField.getText() + "? This cannot be undone.",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        data.deleteProduct(Integer.parseInt(idField.getText()));
        refreshTable();
        clearForm();
    }

    private void loadSelectedProduct() {
        int selectedViewRow = productTable.getSelectedRow();
        if (selectedViewRow < 0) return;
        int row = productTable.convertRowIndexToModel(selectedViewRow);
        int id = ((Number) tableModel.getValueAt(row, 0)).intValue();
        data.findProduct(id).ifPresent(product -> {
            idField.setText(String.valueOf(product.getId()));
            nameField.setText(product.getName());
            categoryField.setSelectedItem(product.getCategory());
            priceField.setText(product.getPrice().toPlainString());
            quantitySpinner.setValue(product.getQuantity());
            standardRadio.setSelected(product.getType().equals("Standard"));
            perishableRadio.setSelected(product.getType().equals("Perishable"));
            activeCheck.setSelected(product.isActive());
            lowStockCheck.setSelected(product.isLowStockTracked());
            thresholdSpinner.setValue(product.getLowStockThreshold());
            descriptionArea.setText(product.getDescription());
        });
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        categoryField.setSelectedIndex(0);
        priceField.setText("");
        quantitySpinner.setValue(0);
        standardRadio.setSelected(true);
        activeCheck.setSelected(true);
        lowStockCheck.setSelected(true);
        thresholdSpinner.setValue(10);
        descriptionArea.setText("");
        productTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Product management", JOptionPane.ERROR_MESSAGE);
    }
}
