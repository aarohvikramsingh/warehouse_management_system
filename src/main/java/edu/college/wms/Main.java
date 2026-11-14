package edu.college.wms;

import edu.college.wms.data.WarehouseData;
import edu.college.wms.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep Swing's default look and feel if the operating system style is unavailable.
            }
            WarehouseData data = new WarehouseData();
            new LoginFrame(data).setVisible(true);
        });
    }
}
