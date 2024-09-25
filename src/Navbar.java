import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Navbar {
    private JPanel navbar;

    public Navbar(CardLayout cardLayout, JPanel mainPanel) {
        navbar = new JPanel();
        navbar.setBackground(new Color(131, 0, 81));
        navbar.setPreferredSize(new Dimension(800, 32));
        navbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton homeButton = createNavbarButton("Home");
        navbar.add(homeButton);

        // Add action to the Home button
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "ButtonPanel");
            }
        });
    }

    // Method to create a styled navigation button
    private JButton createNavbarButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(131, 0, 81));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 0, 81));
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(131, 0, 81));
                button.setOpaque(false);
            }
        });
        return button;
    }

    public JPanel getNavbar() {
        return navbar;
    }
}
