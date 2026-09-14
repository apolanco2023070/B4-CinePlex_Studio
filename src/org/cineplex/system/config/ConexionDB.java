/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static ConexionDB instanciaConexionDB;
    private Connection connection;

    
    private ConexionDB() {
        conectar();
    }

    
    private void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE,
                    Enviroment.USER,
                    Enviroment.PASSWORD);
        } catch (ClassNotFoundException classNotFound) {
            System.err.println("Error de clase no encontrada: " + classNotFound.getMessage());
        } catch (SQLException sqlException) {
            System.err.println("Error de conexión SQL: " + sqlException.getMessage());
        } catch (Exception e) {
            System.err.println("Error padre: " + e.getMessage());
        }
    }

    public static ConexionDB getInstanciaConexionDB() {
        if (instanciaConexionDB == null) {
            instanciaConexionDB = new ConexionDB();
        }
        return instanciaConexionDB;
    }

 
    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                System.out.println(" La conexión estaba cerrada. Reconectando a la base de datos...");
                conectar();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar el estado de la conexión: " + e.getMessage());
        }
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

  
    public void cerrarConexion() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("Conexión cerrada correctamente al salir de la aplicación.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}