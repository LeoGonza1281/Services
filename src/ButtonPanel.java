import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ButtonPanel extends JPanel {
    public JButton setupServerButton;
    public JButton createServiceListButton;
    public JButton startRunningButton;
    public JButton manageReportButton;

    public ButtonPanel() {
        // Use GridBagLayout for flexible layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize buttons with tex t
        setupServerButton = new JButton("Setup Server");
        createServiceListButton = new JButton("Create ServiceList");
        startRunningButton = new JButton("Start Running");
        manageReportButton = new JButton("Manage Reports");

        // Set consistent button properties
        JButton[] buttons = {setupServerButton, createServiceListButton, startRunningButton, manageReportButton};
        for (JButton button : buttons) {
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 18));
        }

        // Add buttons to the panel
        for (JButton button : buttons) {
            add(button, gbc);
            gbc.gridy++;
        }

        // Resize buttons when the window is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustButtonSize(getWidth());
            }
        });
    }

    // Method to adjust button size based on panel width
    private void adjustButtonSize(int panelWidth) {
        int newButtonWidth = (panelWidth - 40) / 3;
        newButtonWidth = Math.max(newButtonWidth, 250);

        Dimension newSize = new Dimension(newButtonWidth, 60);

        setupServerButton.setPreferredSize(newSize);
        createServiceListButton.setPreferredSize(newSize);
        startRunningButton.setPreferredSize(newSize);
        manageReportButton.setPreferredSize(newSize);

        // Rearrange the panel to apply new sizes
        revalidate();
        repaint();
    }
}
