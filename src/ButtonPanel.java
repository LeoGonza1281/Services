import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
            button.setFont(new Font("Arial", Font.BOLD, 18)); // Set a larger font size
        }

        // Add buttons to the panel
        for (JButton button : buttons) {
            add(button, gbc);
            gbc.gridy++;
        }

        // Redimensionar los botones cuando la ventana cambie de tamaño
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustButtonSize(getWidth());
            }
        });
    }

    // Método para ajustar el tamaño de los botones en función del ancho de la ventana
    private void adjustButtonSize(int panelWidth) {
        int newButtonWidth = (panelWidth - 40) / 3; // Hacer que los botones sean más grandes en función del ancho
        newButtonWidth = Math.max(newButtonWidth, 250); // Asegurarse de que los botones tengan un tamaño mínimo

        Dimension newSize = new Dimension(newButtonWidth, 60); // Ajustar el alto de los botones

        setupServerButton.setPreferredSize(newSize);
        createServiceListButton.setPreferredSize(newSize);
        startRunningButton.setPreferredSize(newSize);

        // Volver a organizar el panel para aplicar los nuevos tamaños
        revalidate();
        repaint();
    }
}
