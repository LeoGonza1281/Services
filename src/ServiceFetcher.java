import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServiceFetcher {

    public static List<String> fetchServices() {
        List<String> services = new ArrayList<>();

        // Detectar sistema operativo
        String osName = System.getProperty("os.name").toLowerCase();
        String command;

        if (osName.contains("win")) {
            // Comando para Windows usando PowerShell
            command = "powershell.exe -Command \"Get-Service | Select-Object Name | Format-Table -AutoSize\"";
        } else if (osName.contains("mac")) {
            // Comando para macOS (ejemplo para listar servicios)
            command = "ps aux | grep -i 'service'";  // Comando de ejemplo
        } else if (osName.contains("nix") || osName.contains("nux")) {
            // Comando para Linux (ejemplo para listar servicios)
            command = "systemctl list-units --type=service --all"; // Comando para listar servicios en Linux
        } else {
            System.out.println("Unsupported operating system");
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
            e.printStackTrace();
        }

        return services;
    }
}
