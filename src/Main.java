import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StartRunningPanel startRunningPanel;
    private ServerPanel serverPanel;
    private ServerListPanel serverListPanel; // Añadimos el panel de lista de servidores

    public Main() {
        // Configuración principal de la ventana
        setTitle("Server Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Centrar ventana

        // Crear un CardLayout para cambiar entre los paneles
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear los paneles individuales
        ButtonPanel buttonPanel = new ButtonPanel(); // Panel principal con los botones
        serverPanel = new ServerPanel(); // Panel para registrar servidores
        serverListPanel = new ServerListPanel(); // Panel para mostrar la lista de servidores
        startRunningPanel = new StartRunningPanel(); // Panel para iniciar servicios en los servidores
        ServiceListPanel serviceListPanel = new ServiceListPanel(); // Panel para crear la lista de servicios

        // Añadir los paneles al CardLayout
        mainPanel.add(buttonPanel, "Home");
        mainPanel.add(serverPanel, "Setup Server");
        mainPanel.add(serverListPanel, "Server List");
        mainPanel.add(serviceListPanel, "Create ServiceList");
        mainPanel.add(startRunningPanel, "Start Running");

        // Añadir el panel principal al JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Crear la barra de navegación
        createNavbar();

        // Mostrar el panel principal (Home) al inicio
        cardLayout.show(mainPanel, "Home");

        // Configurar las acciones de los botones en el ButtonPanel para cambiar entre paneles
        buttonPanel.setupServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Setup Server");

                // Crear archivos de texto vacíos para los diferentes entornos
                createEmptyTextFile("Environments.txt");
            }
        });

        buttonPanel.createServiceListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Create ServiceList");
                createEmptyTextFile("ServersList.txt"); // Crear un archivo de lista de servidores vacío
                System.out.println("File created successfully");
            }
        });

        buttonPanel.startRunningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Actualizar la lista de servidores antes de mostrar el panel Start Running
                List<String> registeredServers = serverListPanel.getRegisteredServers(); // Obtener la lista de servidores
                startRunningPanel.updateServerList(registeredServers); // Actualizar el panel de inicio
                cardLayout.show(mainPanel, "Start Running");
            }
        });
    }

    // Método para crear un archivo de texto vacío (o sobrescribir si ya existe)
    private void createEmptyTextFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            System.out.println("File created/emptied: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crear la barra de navegación (navbar)
    private void createNavbar() {
        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Alinear la navbar a la izquierda
        navbar.setBackground(Color.decode("#830051")); // Color de fondo púrpura

        // Botón para volver a la página principal
        JButton homeButton = new JButton("Home");
        homeButton.setForeground(Color.WHITE); // Texto en blanco
        homeButton.setBackground(Color.decode("#830051")); // Fondo púrpura
        homeButton.setBorderPainted(false); // Sin borde
        homeButton.setFocusPainted(false); // Sin borde de enfoque

        // Efecto hover para el botón "Home"
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                homeButton.setBackground(Color.decode("#a03b7e"));
                homeButton.setForeground(Color.decode("#e0e0e0"));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                homeButton.setBackground(Color.decode("#830051"));
                homeButton.setForeground(Color.WHITE);
            }
        });

        // Acción para volver al panel Home al presionar el botón
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Home");
            }
        });

        // Añadir el botón Home a la barra de navegación
        navbar.add(homeButton);

        // Añadir la barra de navegación al JFrame en la parte superior
        add(navbar, BorderLayout.NORTH);
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main app = new Main();
                app.setVisible(true); // Mostrar la ventana
            }
        });
    }
}
