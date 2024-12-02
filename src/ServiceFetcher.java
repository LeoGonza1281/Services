import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceFetcher {

    private static final Logger logger = Logger.getLogger(ServiceFetcher.class.getName());

    public static List<String> fetchServices() {
        List<String> services = new ArrayList<>();

        // Detectar sistema operativo
        String osName = System.getProperty("os.name").toLowerCase();
        String command;

        if (osName.contains("win")) {
            // Comando para Windows usando PowerShell
            command = "powershell.exe -Command \"Get-Service | Select-Object Name | Format-Table -AutoSize\"";
        } else if (osName.contains("mac")) {
            // Comando para macOS usando ps -fea
            command = "ps -fea";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            // Comando para Linux usando systemctl
            command = "systemctl list-units --type=service --all";
        } else {
            logger.log(Level.WARNING, "Unsupported operating system");
            return services;
        }

        try {
            // Ejecutar el comando
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                services.add(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error fetching services", e);
        }

        return services;
    }
}
