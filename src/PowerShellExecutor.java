import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PowerShellExecutor {

    public void executePowerShellScript(List<String> servers, String s) {
        String scriptPath = "C:\\Scripts\\DiskCollect.ps1"; // La ruta al script PowerShell

        String command = buildPowerShellCommand(servers, scriptPath);

        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Comando finalizado con código de salida: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String buildPowerShellCommand(List<String> servers, String scriptPath) {
        StringBuilder command = new StringBuilder("powershell.exe Invoke-Command -ComputerName ");

        String serverList = String.join(",", servers);
        command.append(serverList);
        command.append(" -FilePath \"").append(scriptPath).append("\"");

        return command.toString();
    }
}
