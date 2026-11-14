package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.model.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import java.util.Locale;

@SuppressWarnings("serial")
public final class SupplierPanel extends JPanel {
    private final WarehouseData data;
    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JTextField companyField = new JTextField(18);
    private final JTextArea addressArea = new JTextArea(3, 32);
    private final JTextField searchField = new JTextField(20);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Supplier name", "Company", "Phone", "Email", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable supplierTable = new JTable(tableModel);

    public SupplierPanel(WarehouseData data) {
        this.data = data;
        setLayout(new BorderLayout(0, 14));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);
        idField.setEditable(false);
        idField.setBackground(new java.awt.Color(239, 243, 248));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        supplierTable.setRowHeight(28);
        supplierTable.setAutoCreateRowSorter(true);
        supplierTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        supplierTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelectedSupplier();
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent event) { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent event) { refreshTable(); }
        });
        refreshTable();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);
        JLabel title = new JLabel("Supplier Management");
        title.setForeground(AppTheme.NAVY);
        title.setFont(AppTheme.font(Font.BOLD, 22));
        JLabel subtitle = new JLabel("Keep supplier contact and company details up to date.");
        subtitle.setForeground(AppTheme.MUTED);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        search.setOpaque(false);
        searchField.setPreferredSize(new Dimension(250, 30));
        search.add(new JLabel("Search suppliers"));
        search.add(searchField);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> refreshTable());
        search.add(refresh);
        header.add(heading, BorderLayout.NORTH);
        header.add(search, BorderLayout.SOUTH);
        return header;
    }

    private JSplitPane createWorkspace() {
        JPanel form = createForm();
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(AppTheme.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        tableCard.add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, tableCard);
        splitPane.setResizeWeight(0.40);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        return splitPane;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AppTheme.BORDER), "Supplier details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 7, 5, 7);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        addField(form, c, 0, 0, "Supplier ID", idField, 1);
        addField(form, c, 2, 0, "Supplier name", nameField, 1);
        addField(form, c, 0, 1, "Phone", phoneField, 1);
        addField(form, c, 2, 1, "Email", emailField, 1);
        addField(form, c, 0, 2, "Company", companyField, 1);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(400, 58));
        addField(form, c, 2, 2, "Address", addressScroll, 1);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton clear = new JButton("Clear");
        JButton add = new JButton("Add supplier");
        JButton update = new JButton("Update selected");
        JButton delete = new JButton("Delete selected");
        clear.addActionListener(event -> clearForm());
        add.addActionListener(event -> addSupplier());
        update.addActionListener(event -> updateSupplier());
        delete.addActionListener(event -> deleteSupplier());
        actions.add(clear);
        actions.add(add);
        actions.add(update);
        actions.add(delete);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 4;
        c.weightx = 1;
        form.add(actions, c);
        return form;
    }

    private void addField(JPanel panel, GridBagConstraints c, int x, int y,
                          String title, Component field, int span) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.weightx = 0;
        JLabel label = new JLabel(title);
        label.setFont(AppTheme.font(Font.BOLD, 12));
        panel.add(label, c);
        c.gridx = x + 1;
        c.gridwidth = span;
        c.weightx = 1;
        panel.add(field, c);
    }

    private Supplier readForm(int id) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String company = companyField.getText().trim();
        String address = addressArea.getText().trim();
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || company.isEmpty()) {
            throw new IllegalArgumentException("Name, phone, email, and company are required.");
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (phone.length() < 7) throw new IllegalArgumentException("Enter a valid phone number.");
        return new Supplier(id, name, phone, email, company, address);
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        for (Supplier supplier : data.getSuppliers()) {
            String text = (supplier.getId() + " " + supplier.getName() + " " + supplier.getCompany() + " " + supplier.getEmail() + " " + supplier.getPhone()).toLowerCase(Locale.ROOT);
            if (text.contains(query)) {
                tableModel.addRow(new Object[]{supplier.getId(), supplier.getName(), supplier.getCompany(),
                        supplier.getPhone(), supplier.getEmail(), supplier.getAddress()});
            }
        }
    }

    public void refreshData() {
        refreshTable();
    }

    private void addSupplier() {
        try {
            data.addSupplier(readForm(0));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Supplier was added.", "Supplier saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void updateSupplier() {
        if (idField.getText().isBlank()) { showError("Select a supplier from the table before updating it."); return; }
        try {
            data.updateSupplier(readForm(Integer.parseInt(idField.getText())));
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Supplier details were updated.", "Supplier saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteSupplier() {
        if (idField.getText().isBlank()) { showError("Select a supplier from the table before deleting it."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Delete supplier " + idField.getText() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        data.deleteSupplier(Integer.parseInt(idField.getText()));
        refreshTable();
        clearForm();
    }

    private void loadSelectedSupplier() {
        int viewRow = supplierTable.getSelectedRow();
        if (viewRow < 0) return;
        int row = supplierTable.convertRowIndexToModel(viewRow);
        int id = ((Number) tableModel.getValueAt(row, 0)).intValue();
        data.findSupplier(id).ifPresent(supplier -> {
            idField.setText(String.valueOf(supplier.getId()));
            nameField.setText(supplier.getName());
            phoneField.setText(supplier.getPhone());
            emailField.setText(supplier.getEmail());
            companyField.setText(supplier.getCompany());
            addressArea.setText(supplier.getAddress());
        });
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        companyField.setText("");
        addressArea.setText("");
        supplierTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Supplier management", JOptionPane.ERROR_MESSAGE);
    }
}
