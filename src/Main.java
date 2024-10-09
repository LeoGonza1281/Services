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
        // Configuración de la ventana principal
        setTitle("Server Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Crear el CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear los paneles
        ButtonPanel buttonPanel = new ButtonPanel(); // Panel principal con botones
        serverPanel = new ServerPanel(); // Panel para registrar servidores
        startRunningPanel = new StartRunningPanel(serverPanel.getRegisteredServers()); // Panel para iniciar los servicios en los servidores
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

        // Mostrar el panel principal (Home)
        cardLayout.show(mainPanel, "Home");

        // Configurar la acción de los botones del ButtonPanel para cambiar entre los paneles
        buttonPanel.setupServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Setup Server");
                // Llama a la función para crear los archivos de texto vacíos
                createEmptyTextFile("Developing.txt");
                createEmptyTextFile("Preproduction.txt");
                createEmptyTextFile("Production.txt");
            }
        });

        buttonPanel.createServiceListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Create ServiceList");
                createEmptyTextFile("ServersList.txt");
                System.out.println("Archivo creado correctamente");
            }
        });

        buttonPanel.startRunningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Antes de mostrar el panel Start Running, actualizamos la lista de servidores
                startRunningPanel.updateServerList(serverPanel.getRegisteredServers());
                cardLayout.show(mainPanel, "Start Running");
            }
        });
    }

    // Método para crear un archivo vacío (si no existe) o sobrescribirlo si ya existe
    private void createEmptyTextFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // No escribimos nada, simplemente creamos o vaciamos el archivo
            System.out.println("Archivo creado/vaciado: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crear la barra de navegación
    private void createNavbar() {
        JPanel navbar = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Alinear a la izquierda

        // Definir el color morado de fondo para la barra de navegación
        navbar.setBackground(Color.decode("#830051"));

        // Botón para volver a Home
        JButton homeButton = new JButton("Home");
        homeButton.setForeground(Color.WHITE);
        homeButton.setBackground(Color.decode("#830051"));
        homeButton.setBorderPainted(false); // Sin bordes para darle un estilo plano
        homeButton.setFocusPainted(false); // Quitar el borde de enfoque

        // Efecto reactivo para el botón "Home"
        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                homeButton.setBackground(Color.decode("#a03b7e")); // Color un poco más claro
                homeButton.setForeground(Color.decode("#e0e0e0")); // Color del texto más claro
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                homeButton.setBackground(Color.decode("#830051"));
                homeButton.setForeground(Color.WHITE);
            }
        });

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Home");
            }
        });
        navbar.add(homeButton);

        // Añadir la barra de navegación al JFrame
        add(navbar, BorderLayout.NORTH);
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main app = new Main();
                app.setVisible(true);
            }
        });

    }
}
