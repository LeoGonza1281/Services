import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {
    public JButton setupServerButton;
    public JButton createServiceListButton;
    public JButton startRunningButton;

    public ButtonPanel() {
        // Use GridBagLayout for flexible layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill horizontally
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize buttons with text
        setupServerButton = new JButton("Setup Server");
        createServiceListButton = new JButton("Create ServiceList");
        startRunningButton = new JButton("Start Running");

        // Set consistent button properties
        JButton[] buttons = {setupServerButton, createServiceListButton, startRunningButton};
        for (JButton button : buttons) {
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 14)); // Set a readable font size
            button.setPreferredSize(new Dimension(200, 40)); // Set a preferred size
            button.setMinimumSize(new Dimension(200, 40)); // Minimum size to ensure text fits
            button.setMaximumSize(new Dimension(200, 40)); // Maximum size to prevent overflow
        }

        // Add buttons to the panel
        for (JButton button : buttons) {
            add(button, gbc);
            gbc.gridy++;
        }

    }
}
