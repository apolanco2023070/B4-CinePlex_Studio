/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://" + Enviroment.LOCATION_SERVICE
            + "/" + Enviroment.DATA_BASE + "?useSSL=false&serverTimezone=UTC";
    private static final String USER = Enviroment.USER;
    private static final String PASSWORD = Enviroment.PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver de MySQL no encontrado: " + e.getMessage());
        }
    }

    public static Connection getConexion() throws SQLException {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Nueva conexión creada exitosamente");
            return con;
        } catch (SQLException e) {
            System.err.println("Error al crear conexión: " + e.getMessage());
            throw e;
        }
    }
}
