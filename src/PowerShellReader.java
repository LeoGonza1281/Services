import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PowerShellReader {

    public static void main(String[] args) {
        try {
            // Comando PowerShell para ejecutar el script en servidores remotos
            String command = "powershell.exe Invoke-Command -ComputerName Server01,Server02 -FilePath c:/scripts/DiskCollect.ps1";

            // Usar ProcessBuilder para ejecutar el comando
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);

            // Iniciar el proceso
            Process process = processBuilder.start();

            // Leer la salida del comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

