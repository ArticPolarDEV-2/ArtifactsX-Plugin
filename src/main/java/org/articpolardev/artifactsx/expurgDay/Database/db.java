package org.articpolardev.artifactsx.expurgDay.Database;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;

public class db {
    private Connection connection;

    public void connect() throws SQLException {
        String pluginDir = "plugins/ExpurgDay";
        String dbPath = pluginDir + "/database.db";

        File directory = new File(pluginDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String url = "jdbc:sqlite:" + dbPath;
        connection = DriverManager.getConnection(url);
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTable() {
        String sql1 = "CREATE TABLE IF NOT EXISTS timeTb ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "time INTEGER NOT NULL"
                + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Exemplo de criação de tabela
        String createTableSQL = "CREATE TABLE IF NOT EXISTS expurgo_players (player_uuid VARCHAR(36) PRIMARY KEY, activated BOOLEAN)";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void insertRemainingTime(long remainingTime) {
        String sql = "INSERT INTO timeTb(time) VALUES(?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, remainingTime); // Salva o tempo restante como long
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRemainingTime(long remainingTime) {
        String sql = "UPDATE timeTb SET time = ? WHERE id = 1"; // Certifique-se de ter um controle claro do id.

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, remainingTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public long fetchRemainingTime() {
        String sql = "SELECT time FROM timeTb WHERE id = 1"; // Certifique-se de que a tabela está sendo usada corretamente.
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Se nenhum tempo for encontrado, retorne 0
    }


    public void deleteRemainingTime() {
        String sql = "DELETE FROM timeTb WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Bukkit.getLogger().info("Tempo restante deletado");
            } else {
                Bukkit.getLogger().warning("Dia do Expurgo não foi ativado ainda");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPlayerActivatedExpurgo(String playerUUID) throws SQLException {
        String query = "SELECT activated FROM expurg_activation WHERE player_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            ResultSet rs = stmt.executeQuery();

            // Se houver resultado, retorne o valor da coluna "activated"
            if (rs.next()) {
                return rs.getBoolean("activated");
            }
        }
        // Se não houver registro, considere que o jogador não ativou o Expurgo
        return false;
    }


    public void setPlayerActivatedExpurgo(String playerUUID) throws SQLException {
        String sql = "INSERT OR REPLACE INTO expurgo_players (player_uuid) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUUID);
            stmt.executeUpdate();
        }
    }
}
