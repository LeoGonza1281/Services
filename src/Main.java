import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StartRunningPanel startRunningPanel;
    private ServerPanel serverPanel;
    private File appDirectory; // Directorio donde se guardarán los archivos
    private EmailPanel emailPanel;
    private ManageReportsPanel manageReportsPanel;


    public Main() {
        setTitle("Server Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        createAppDirectory();
        createEnvironmentsFile("SetupServer");
        createServiceListFile("CreateServiceList");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        ButtonPanel buttonPanel = new ButtonPanel();
        serverPanel = new ServerPanel(cardLayout, mainPanel);
        startRunningPanel = new StartRunningPanel();
        ServiceListPanel serviceListPanel = new ServiceListPanel();
        emailPanel = new EmailPanel();
        manageReportsPanel = new ManageReportsPanel(); // Panel de Manage Reports

        serverPanel.setPreferredSize(new Dimension(1000, 600));
        startRunningPanel.setPreferredSize(new Dimension(1000, 600));
        serviceListPanel.setPreferredSize(new Dimension(1000, 600));
        emailPanel.setPreferredSize(new Dimension(1000, 600));
        manageReportsPanel.setPreferredSize(new Dimension(1000, 600)); // Tamaño del panel

        mainPanel.add(buttonPanel, "Home");
        mainPanel.add(serverPanel, "Setup Server");
        mainPanel.add(serviceListPanel, "Create ServiceList");
        mainPanel.add(startRunningPanel, "Start Running");
        mainPanel.add(emailPanel, "Email");
        mainPanel.add(manageReportsPanel, "Manage Reports"); // Agregar el nuevo panel

        add(mainPanel, BorderLayout.CENTER);

        createNavbar();

        cardLayout.show(mainPanel, "Home");

        buttonPanel.setupServerButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Setup Server");
            createEnvironmentsFile("SetupServer");
        });

        buttonPanel.createServiceListButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Create ServiceList");
            createServiceListFile("Create ServiceList");
        });

        buttonPanel.startRunningButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Start Running");
            startRunningPanel.updateAllComboBoxes();
        });

        // Acción del botón "Manage Reports"
        buttonPanel.manageReportButton.addActionListener(e -> cardLayout.show(mainPanel, "Manage Reports"));
    }


    private void createAppDirectory() {
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents");
        appDirectory = new File(documentsDir, "StartServices");
        if (!appDirectory.exists()) {
            if (appDirectory.mkdir()) {
                System.out.println("Directorio 'StartServices' creado en Documentos.");
            } else {
                System.err.println("No se pudo crear el directorio 'StartServices'.");
            }
        }
        System.out.println("El directorio 'StartServices' está disponible en: " + appDirectory.getAbsolutePath());
    }

    private void createEnvironmentsFile(String folderName) {
        File folder = new File(appDirectory, folderName);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Carpeta '" + folderName + "' creada en StartServices.");
            } else {
                System.err.println("No se pudo crear la carpeta '" + folderName + "'.");
            }
        }

        File file = new File(folder, "Environments.txt");
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                System.out.println("Environments.txt creado en " + folder.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Environments.txt ya existe en " + folder.getAbsolutePath());
        }
    }

    private void createServiceListFile(String folderName) {
        File folder = new File(appDirectory, folderName);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Carpeta '" + folderName + "' creada en StartServices.");
            } else {
                System.err.println("No se pudo crear la carpeta '" + folderName + "'.");
            }
        }

        File file = new File(folder, "List.txt");
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                System.out.println("List.txt creado en " + folder.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("List.txt ya existe en " + folder.getAbsolutePath());
        }
    }

    private void createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.decode("#830051"));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        JButton homeButton = createNavButton("Home");
        homeButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        leftPanel.add(homeButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton emailButton = createNavButton("Email");
        emailButton.addActionListener(e -> cardLayout.show(mainPanel, "Email"));
        rightPanel.add(emailButton);

        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(rightPanel, BorderLayout.EAST);

        add(navbar, BorderLayout.NORTH);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#830051"));
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(Color.decode("#a03b7e"));
                button.setForeground(Color.decode("#e0e0e0"));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.decode("#830051"));
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
