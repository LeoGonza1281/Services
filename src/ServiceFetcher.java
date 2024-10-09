import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServiceFetcher {

    public static List<String> fetchServices() {
        List<String> services = new ArrayList<>();

        // Aquí deberías reemplazar este comando con el que te permita obtener tus servicios.
        String command = "\"Get-Service | Select-Object Status,Name,DisplayName | Format-Table -AutoSize\";"; // Para PowerShell

        try {
            // Ejecutar el comando en PowerShell
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
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
