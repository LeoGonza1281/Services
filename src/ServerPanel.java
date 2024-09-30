import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // Acción para añadir el servidor
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addServer();
                System.out.println("hola"); // Imprime "hola" en la consola
            }
        });
    }

    private void addServer() {
        String serverName = serverNameField.getText().trim();
        System.out.println("Entrada");
        if (!serverName.isEmpty() && !registeredServers.contains(serverName)) {
            registeredServers.add(serverName);
            serverListModel.addElement(serverName);
            serverNameField.setText("");
            System.out.println("Lol");
        }
    }

    public List<String> getRegisteredServers() {
        return registeredServers;
    }
}
