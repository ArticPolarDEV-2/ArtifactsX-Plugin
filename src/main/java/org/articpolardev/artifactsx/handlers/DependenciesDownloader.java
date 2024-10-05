package org.articpolardev.artifactsx.handlers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DependenciesDownloader {

    private final JavaPlugin plugin;
    private final File pluginsFolder;

    public DependenciesDownloader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginsFolder = plugin.getServer().getUpdateFolderFile().getParentFile(); // Obtém a pasta de plugins
    }

    public void downloadDependencies(List<String> dependencyLinks) {
        for (String link : dependencyLinks) {
            String fileName = link.substring(link.lastIndexOf('/') + 1); // Extrai o nome do arquivo a partir do link
            File dependencyFile = new File(pluginsFolder, fileName);

            if (!dependencyFile.exists()) { // Verifica se o arquivo já existe
                plugin.getLogger().info("Baixando dependência: " + fileName);
                try {
                    downloadFile(link, dependencyFile);
                    plugin.getLogger().info("Dependência " + fileName + " baixada com sucesso.");
                } catch (Exception e) {
                    plugin.getLogger().severe("Falha ao baixar a dependência: " + fileName);
                    e.printStackTrace();
                }
            } else {
                plugin.getLogger().info("Dependência " + fileName + " já está presente.");
            }
        }
    }

    private void downloadFile(String link, File destination) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();

        try (InputStream input = connection.getInputStream();
             FileOutputStream output = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }
}
