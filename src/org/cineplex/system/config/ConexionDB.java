/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
 
    private static ConexionDB instanciaConexionDB;
    private Connection connection;
 
    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection
                    = DriverManager
                            .getConnection(
                                    "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE,
                                    Enviroment.USER,
                                    Enviroment.PASSWORD);
        } catch (ClassNotFoundException classNotFound) {
            System.out.println("Error de clase no encontrada");
        } catch (SQLException sqlException) {
            System.out.println("Error de conexion sql");
        } catch (Exception e) {
            System.out.println("Error padre: " + e.getMessage());
        }
    }
 
    public static ConexionDB getInstanciaConexionDB() {
        if (instanciaConexionDB == null) {
            instanciaConexionDB = new ConexionDB();
        }
        return instanciaConexionDB;
    }
 
    public Connection getConnection() {
        return connection;
    }
 
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}