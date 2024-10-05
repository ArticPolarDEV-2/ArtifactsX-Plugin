package org.articpolardev.artifactsx.handlers;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class dbTPConnect {
    private Connection connection;

    public void connect() throws SQLException {
        String pluginDir = "plugins/ArtifactsX";
        String dbPath = pluginDir + "/teleportrelicdb.db";

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
        String sql = "CREATE TABLE IF NOT EXISTS warp ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "x INTEGER NOT NULL,"
                + "y INTEGER NOT NULL,"
                + "z INTEGER NOT NULL"
                + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertWarp(String x, String y, String z) {
        String sql = "INSERT INTO warp(x, y, z) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, x);
            pstmt.setString(2, y);
            pstmt.setString(3, z);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fetchWarps() {
        String sql = "SELECT * FROM warp";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Bukkit.getLogger().info(rs.getInt("id") + "\t" +
                        rs.getString("x") + "\t" +
                        rs.getString("y") + "\t" +
                        rs.getString("z"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Nova função para retornar todas as coordenadas como um array
    public List<int[]> getWarpsAsArray() {
        List<int[]> warps = new ArrayList<>();
        String sql = "SELECT x, y, z FROM warp";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                warps.add(new int[]{x, y, z});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return warps;
    }

    public void deleteWarp(int x, int y, int z) {
        String sql = "DELETE FROM warp WHERE x = ? AND y = ? AND z = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Bukkit.getLogger().info("Warp deletado: x=" + x + " y=" + y + " z=" + z);
            } else {
                Bukkit.getLogger().warning("Nenhum warp encontrado com as coordenadas: x=" + x + " y=" + y + " z=" + z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
