package edu.college.wms.ui;

import edu.college.wms.data.WarehouseData;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@SuppressWarnings("serial")
public final class LoginFrame extends JFrame {
    private final WarehouseData data;
    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JCheckBox showPasswordCheck = new JCheckBox("Show password");

    public LoginFrame(WarehouseData data) {
        super("Warehouse Management System | Sign in");
        this.data = data;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(480, 520));
        setSize(540, 580);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        showPasswordCheck.addActionListener(event -> passwordField.setEchoChar(
                showPasswordCheck.isSelected() ? (char) 0 : '\u2022'));
    }

    private JPanel createContent() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(AppTheme.BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        JPanel brand = new JPanel(new BorderLayout(0, 12));
        brand.setOpaque(false);
        JLabel mark = new JLabel("WMS", SwingConstants.CENTER);
        mark.setOpaque(true);
        mark.setBackground(AppTheme.NAVY);
        mark.setForeground(AppTheme.WHITE);
        mark.setFont(AppTheme.font(Font.BOLD, 20));
        mark.setPreferredSize(new Dimension(74, 64));
        JLabel title = new JLabel("Warehouse Management", SwingConstants.CENTER);
        title.setFont(AppTheme.font(Font.BOLD, 25));
        title.setForeground(AppTheme.NAVY);
        JLabel subtitle = new JLabel("Sign in to manage your warehouse", SwingConstants.CENTER);
        subtitle.setFont(AppTheme.font(Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.MUTED);
        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.CENTER);
        heading.add(subtitle, BorderLayout.SOUTH);
        brand.add(mark, BorderLayout.NORTH);
        brand.add(heading, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridy = 0;
        card.add(fieldLabel("Username"), c);
        c.gridy++;
        card.add(usernameField, c);
        c.gridy++;
        c.insets = new Insets(14, 0, 6, 0);
        card.add(fieldLabel("Password"), c);
        c.gridy++;
        c.insets = new Insets(6, 0, 2, 0);
        card.add(passwordField, c);
        c.gridy++;
        c.insets = new Insets(4, 0, 12, 0);
        card.add(showPasswordCheck, c);
        c.gridy++;
        JButton signIn = new JButton("Sign in");
        signIn.setBackground(AppTheme.BLUE);
        signIn.setForeground(AppTheme.WHITE);
        signIn.setFont(AppTheme.font(Font.BOLD, 14));
        signIn.setFocusPainted(false);
        signIn.setPreferredSize(new Dimension(100, 42));
        signIn.addActionListener(event -> attemptLogin());
        card.add(signIn, c);
        getRootPane().setDefaultButton(signIn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(28, 0, 20, 0));
        center.add(card, BorderLayout.CENTER);
        JLabel footer = new JLabel("Local desktop application", SwingConstants.CENTER);
        footer.setForeground(AppTheme.MUTED);
        footer.setFont(AppTheme.font(Font.PLAIN, 12));
        page.add(brand, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.font(Font.BOLD, 13));
        label.setForeground(AppTheme.NAVY);
        return label;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both your username and password.",
                    "Missing sign-in details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("admin".equals(username) && "admin123".equals(password)) {
            new DashboardFrame(data).setVisible(true);
            dispose();
            return;
        }
        JOptionPane.showMessageDialog(this, "The username or password is incorrect.",
                "Sign-in failed", JOptionPane.ERROR_MESSAGE);
    }
}
