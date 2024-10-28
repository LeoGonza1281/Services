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
    private File appDirectory; // Directorio donde se guardarán los archivos

    public Main() {
        // Configuración principal de la ventana
        setTitle("Server Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Centrar ventana

        // Crear el directorio de la aplicación
        createAppDirectory();

        // Crear un CardLayout para cambiar entre los paneles
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear los paneles individuales
        ButtonPanel buttonPanel = new ButtonPanel(); // Panel principal con los botones
        serverPanel = new ServerPanel(cardLayout, mainPanel, appDirectory); // Pasar cardLayout y mainPanel al ServerPanel
        startRunningPanel = new StartRunningPanel(); // Panel para iniciar servicios en los servidores
        ServiceListPanel serviceListPanel = new ServiceListPanel(); // Panel para crear la lista de servicios

        // Añadir los paneles al CardLayout
        mainPanel.add(buttonPanel, "Home");
        mainPanel.add(serverPanel, "Setup Server");
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
                createEnvironmentsFile("SetupServer"); // Crear el archivo en la nueva carpeta
            }
        });

        buttonPanel.createServiceListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Create ServiceList");
                createServiceListFile("CreateServiceList"); // Crear el archivo en la nueva carpeta
            }
        });

        buttonPanel.startRunningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Start Running");
            }
        });
    }

    // Método para crear la carpeta de la aplicación dependiendo del sistema operativo
    private void createAppDirectory() {
        // Obtener el directorio principal del usuario
        String userHome = System.getProperty("user.home");

        // Crear el directorio dependiendo del sistema operativo
        String osName = System.getProperty("os.name").toLowerCase();
        File appDirectory;

        if (osName.contains("win")) {
            // Para Windows, creamos la carpeta en "Documents"
            appDirectory = new File(userHome, "Documents/StartServices");
        } else if (osName.contains("mac")) {
            // Para macOS, creamos en la carpeta de documentos del usuario
            appDirectory = new File(userHome, "Documents/StartServices");
        } else if (osName.contains("nix") || osName.contains("nux")) {
            // Para Linux, puedes usar una carpeta de configuración oculta en el home
            appDirectory = new File(userHome, ".startservices"); // Carpeta oculta
        } else {
            // Por defecto, crear la carpeta en el home del usuario
            appDirectory = new File(userHome, "StartServices");
        }

        if (!appDirectory.exists()) {
            if (appDirectory.mkdirs()) {
                System.out.println("Directorio '" + appDirectory.getAbsolutePath() + "' creado.");
            } else {
                System.err.println("No se pudo crear el directorio '" + appDirectory.getAbsolutePath() + "'.");
            }
        } else {
            System.out.println("El directorio ya existe: " + appDirectory.getAbsolutePath());
        }

        this.appDirectory = appDirectory; // Asignar a la variable de la clase
    }

    // Método para crear el archivo de texto de entornos solo si no existe
    private void createEnvironmentsFile(String folderName) {
        // Crear una carpeta específica dentro de StartServices
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

    // Método para crear el archivo List.txt solo si no existe
    private void createServiceListFile(String folderName) {
        // Crear una carpeta específica dentro de StartServices
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
