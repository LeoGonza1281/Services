import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private List<String> registeredServers;
    private JTextField serverNameField;
    private JButton addButton;
    private DefaultListModel<String> serverListModel;

    public ServerPanel() {
        registeredServers = new ArrayList<>();
        serverNameField = new JTextField(15);
        addButton = new JButton("Add Server");
        serverListModel = new DefaultListModel<>();
        JList<String> serverList = new JList<>(serverListModel);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Server Name:"));
        add(serverNameField);
        add(addButton);
        add(new JScrollPane(serverList));

        // Action for adding the server
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addServer(); // Uncomment this line to trigger the server addition logic
                System.out.println("hola"); // This should print "hola" to the console
            }
        });
    }
    private void addServer() {
        String serverName = serverNameField.getText().trim() + "\n";
        System.out.println("Entrada");
        if (!serverName.isEmpty() && !registeredServers.contains(serverName)) {
            registeredServers.add(serverName);
            serverListModel.addElement(serverName);
            serverNameField.setText("");
            System.out.println("Lol");
       //     writeToFile(serverName);
        }
    }

    public List<String> getRegisteredServers() {
        return registeredServers;
    }

    // Main method to test the panel
    public static void main(String[] args) {
        JFrame frame = new JFrame("Server Panel Tessdfghjt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        ServerPanel panel = new ServerPanel();
        frame.add(panel);

        frame.setVisible(true);
    }
}
