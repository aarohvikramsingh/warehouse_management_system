package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public final class DashboardFrame extends JFrame {
    private static final String OVERVIEW = "Overview";
    private final WarehouseData data;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JLabel pageTitle = new JLabel(OVERVIEW);
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();
    private final Map<String, JLabel> metricValues = new LinkedHashMap<>();
    private final ProductPanel productPanel;
    private final InventoryPanel inventoryPanel;
    private final SupplierPanel supplierPanel;
    private final ReportsPanel reportsPanel;

    public DashboardFrame(WarehouseData data) {
        super("Warehouse Management System");
        this.data = data;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 680));
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());

        productPanel = new ProductPanel(data);
        inventoryPanel = new InventoryPanel(data);
        supplierPanel = new SupplierPanel(data);
        reportsPanel = new ReportsPanel(data);
        cards.add(createOverview(), OVERVIEW);
        cards.add(productPanel, "Products");
        cards.add(inventoryPanel, "Inventory");
        cards.add(supplierPanel, "Suppliers");
        cards.add(reportsPanel, "Reports");

        JPanel root = new JPanel(new BorderLayout());
        root.add(createSidebar(), BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(AppTheme.BACKGROUND);
        content.add(createTopBar(), BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem logoutItem = new JMenuItem("Log out");
        logoutItem.addActionListener(event -> logout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(event -> dispose());
        fileMenu.add(logoutItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);

        JMenu navigateMenu = new JMenu("Navigate");
        for (String page : new String[]{OVERVIEW, "Products", "Inventory", "Suppliers", "Reports"}) {
            JMenuItem item = new JMenuItem(page);
            item.addActionListener(event -> showPage(page));
            navigateMenu.add(item);
        }

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Warehouse Management System\nJava Swing desktop application\nSample data resets when you close the app.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(fileMenu);
        menuBar.add(navigateMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppTheme.NAVY);
        sidebar.setPreferredSize(new Dimension(226, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 16, 14));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        JLabel name = new JLabel("  WMS");
        name.setForeground(AppTheme.WHITE);
        name.setFont(AppTheme.font(Font.BOLD, 23));
        JLabel sub = new JLabel("  Warehouse workspace");
        sub.setForeground(new java.awt.Color(191, 205, 222));
        sub.setFont(AppTheme.font(Font.PLAIN, 12));
        brand.add(name, BorderLayout.NORTH);
        brand.add(sub, BorderLayout.SOUTH);

        JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 8));
        navigation.setOpaque(false);
        navigation.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        for (String page : new String[]{OVERVIEW, "Products", "Inventory", "Suppliers", "Reports"}) {
            addNavigationButton(navigation, page);
        }

        JPanel footer = new JPanel(new BorderLayout(0, 10));
        footer.setOpaque(false);
        JLabel user = new JLabel("Signed in as  admin");
        user.setForeground(new java.awt.Color(215, 224, 236));
        user.setFont(AppTheme.font(Font.PLAIN, 12));
        JButton logout = new JButton("Log out");
        logout.setFocusPainted(false);
        logout.addActionListener(event -> logout());
        footer.add(user, BorderLayout.NORTH);
        footer.add(logout, BorderLayout.SOUTH);
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(navigation, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavigationButton(JPanel navigation, String page) {
        JButton button = new JButton(page);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(10, 14, 10, 10));
        button.setForeground(AppTheme.WHITE);
        button.setBackground(page.equals(OVERVIEW) ? AppTheme.BLUE : AppTheme.NAVY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(AppTheme.font(Font.PLAIN, 14));
        button.addActionListener(event -> showPage(page));
        navigationButtons.put(page, button);
        navigation.add(button);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppTheme.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 26, 18, 26)));
        pageTitle.setForeground(AppTheme.NAVY);
        pageTitle.setFont(AppTheme.font(Font.BOLD, 21));
        JLabel context = new JLabel("Warehouse operations");
        context.setForeground(AppTheme.MUTED);
        context.setFont(AppTheme.font(Font.PLAIN, 13));
        topBar.add(pageTitle, BorderLayout.WEST);
        topBar.add(context, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createOverview() {
        JPanel overview = new JPanel(new BorderLayout(0, 22));
        overview.setBackground(AppTheme.BACKGROUND);
        overview.setBorder(BorderFactory.createEmptyBorder(26, 28, 28, 28));

        JPanel intro = new JPanel(new BorderLayout(0, 6));
        intro.setOpaque(false);
        JLabel greeting = new JLabel("Good day, admin");
        greeting.setForeground(AppTheme.NAVY);
        greeting.setFont(AppTheme.font(Font.BOLD, 20));
        JLabel hint = new JLabel("Here is a quick overview of your warehouse.");
        hint.setForeground(AppTheme.MUTED);
        intro.add(greeting, BorderLayout.NORTH);
        intro.add(hint, BorderLayout.SOUTH);

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(createStatCard("Total products", () -> String.valueOf(data.getProducts().size()), "Catalog items"));
        stats.add(createStatCard("Available stock", () -> String.valueOf(data.getProducts().stream().mapToInt(Product::getQuantity).sum()), "Units across products"));
        stats.add(createStatCard("Low stock", () -> String.valueOf(data.getProducts().stream().filter(p -> p.isLowStockTracked() && p.getQuantity() <= p.getLowStockThreshold()).count()), "Items to review"));
        stats.add(createStatCard("Suppliers", () -> String.valueOf(data.getSuppliers().size()), "Supplier contacts"));

        JPanel shortcuts = new JPanel(new GridLayout(1, 3, 16, 0));
        shortcuts.setOpaque(false);
        shortcuts.add(createShortcut("Products", "Maintain your warehouse catalog", "Products"));
        shortcuts.add(createShortcut("Inventory", "Receive, issue, and monitor stock", "Inventory"));
        shortcuts.add(createShortcut("Reports", "Review product and stock summaries", "Reports"));

        JPanel main = new JPanel(new BorderLayout(0, 22));
        main.setOpaque(false);
        main.add(intro, BorderLayout.NORTH);
        main.add(stats, BorderLayout.CENTER);
        main.add(shortcuts, BorderLayout.SOUTH);
        overview.add(main, BorderLayout.NORTH);
        return overview;
    }

    private JPanel createStatCard(String heading, java.util.function.Supplier<String> value, String detail) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        JLabel label = new JLabel(heading);
        label.setForeground(AppTheme.MUTED);
        label.setFont(AppTheme.font(Font.BOLD, 13));
        JLabel number = new JLabel(value.get());
        number.setName("metric:" + heading);
        number.setForeground(AppTheme.NAVY);
        number.setFont(AppTheme.font(Font.BOLD, 28));
        metricValues.put(heading, number);
        JLabel caption = new JLabel(detail);
        caption.setForeground(AppTheme.MUTED);
        caption.setFont(AppTheme.font(Font.PLAIN, 12));
        card.add(label, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        card.add(caption, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createShortcut(String title, String description, String target) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        JLabel heading = new JLabel(title);
        heading.setForeground(AppTheme.NAVY);
        heading.setFont(AppTheme.font(Font.BOLD, 16));
        JLabel detail = new JLabel("<html>" + description + "</html>");
        detail.setForeground(AppTheme.MUTED);
        JButton open = new JButton("Open " + title);
        open.addActionListener(event -> showPage(target));
        JPanel text = new JPanel(new BorderLayout(0, 8));
        text.setOpaque(false);
        text.add(heading, BorderLayout.NORTH);
        text.add(detail, BorderLayout.CENTER);
        card.add(text, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private void showPage(String page) {
        cardLayout.show(cards, page);
        pageTitle.setText(page.equals(OVERVIEW) ? "Overview" : page);
        navigationButtons.forEach((name, button) -> button.setBackground(name.equals(page) ? AppTheme.BLUE : AppTheme.NAVY));
        if (page.equals("Products")) productPanel.refreshData();
        if (page.equals("Inventory")) inventoryPanel.refreshData();
        if (page.equals("Suppliers")) supplierPanel.refreshData();
        if (page.equals("Reports")) reportsPanel.refreshReport();
        refreshMetrics();
    }

    private void refreshMetrics() {
        metricValues.get("Total products").setText(String.valueOf(data.getProducts().size()));
        metricValues.get("Available stock").setText(String.valueOf(
                data.getProducts().stream().mapToInt(Product::getQuantity).sum()));
        metricValues.get("Low stock").setText(String.valueOf(data.getProducts().stream()
                .filter(product -> product.isLowStockTracked() && product.getQuantity() <= product.getLowStockThreshold()).count()));
        metricValues.get("Suppliers").setText(String.valueOf(data.getSuppliers().size()));
    }

    private void logout() {
        dispose();
        new LoginFrame(data).setVisible(true);
    }
}
