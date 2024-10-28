import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServiceFetcher {

    public static List<String> fetchServices() {
        List<String> services = new ArrayList<>();
        String osName = System.getProperty("os.name").toLowerCase();
        String command;

        try {
            ProcessBuilder processBuilder;

            // Verificar el sistema operativo
            if (osName.contains("win")) {
                // Comando para Windows (PowerShell)
                command = "Get-Service | Select-Object Name | Format-Table -AutoSize";
                processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            } else {
                // Comando para macOS o Linux (bash/sh)
                command = "service --status-all";  // Puedes ajustar este comando para listar servicios en Unix
                processBuilder = new ProcessBuilder("sh", "-c", command);
            }

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
