import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StartRunningPanel startRunningPanel;
    private ServerPanel serverPanel;

    public Main() {
        // Configure the main window (JFrame)
        setTitle("Server Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window

        // Create CardLayout for switching between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create individual panels
        ButtonPanel buttonPanel = new ButtonPanel(); // Main panel with buttons
        serverPanel = new ServerPanel(); // Panel for registering servers
        startRunningPanel = new StartRunningPanel(serverPanel.getRegisteredServers()); // Panel to start services on servers
        ServiceListPanel serviceListPanel = new ServiceListPanel(); // Panel for creating the service list

        // Add panels to the CardLayout
        mainPanel.add(buttonPanel, "Home");
        mainPanel.add(serverPanel, "Setup Server");
        mainPanel.add(serviceListPanel, "Create ServiceList");
        mainPanel.add(startRunningPanel, "Start Running");

        // Add the main panel to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Create the navigation bar (navbar)
        createNavbar();

        // Show the main panel (Home) on startup
        cardLayout.show(mainPanel, "Home");

        // Configure the actions of buttons in ButtonPanel to switch between panels
        buttonPanel.setupServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Setup Server");
                // Create empty text files for the different environments when navigating to the Setup Server page
                createEmptyTextFile("Developing.txt");
                createEmptyTextFile("Preproduction.txt");
                createEmptyTextFile("Production.txt");
            }
        });

        buttonPanel.createServiceListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Create ServiceList");
                createEmptyTextFile("ServersList.txt"); // Create an empty server list file
                System.out.println("File created successfully");
            }
        });

        buttonPanel.startRunningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the list of servers before showing the Start Running panel
                startRunningPanel.updateServerList(serverPanel.getRegisteredServers());
                cardLayout.show(mainPanel, "Start Running");
            }
        });
    }

    // Method to create an empty text file (or overwrite if it already exists)
    private void createEmptyTextFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // No content is written; just create or clear the file
            System.out.println("File created/emptied: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create the navigation bar
    private void createNavbar() {
        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align navbar to the left

        // Set a purple background color for the navbar
        navbar.setBackground(Color.decode("#830051"));

        // Button to navigate back to the Home page
        JButton homeButton = new JButton("Home");
        homeButton.setForeground(Color.WHITE); // White text
        homeButton.setBackground(Color.decode("#830051")); // Purple background
        homeButton.setBorderPainted(false); // No border for a flat style
        homeButton.setFocusPainted(false); // Remove focus border

        // Add hover effect for the "Home" button
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Lighten background and text color when hovering
                homeButton.setBackground(Color.decode("#a03b7e"));
                homeButton.setForeground(Color.decode("#e0e0e0"));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Revert to original color when not hovering
                homeButton.setBackground(Color.decode("#830051"));
                homeButton.setForeground(Color.WHITE);
            }
        });

        // Action to switch back to the Home panel when the button is clicked
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Home");
            }
        });

        // Add the Home button to the navbar
        navbar.add(homeButton);

        // Add the navbar to the JFrame at the top (NORTH)
        add(navbar, BorderLayout.NORTH);
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main app = new Main();
                app.setVisible(true); // Display the window
            }
        });
    }
}
