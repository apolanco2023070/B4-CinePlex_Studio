/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.config;

import org.cineplex.system.config.Enviroment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author informatica
 */
public class DatabaseConnection {

    private static DatabaseConnection instanciaDataBaseConnection;
    private Connection connectionDB;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connectionDB = DriverManager.getConnection("jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE,
                    Enviroment.USER, Enviroment.PASSWORD);
        } catch (ClassNotFoundException classNotFound) {
            System.out.println("error de clases no encontrada");
        } catch (SQLException sqlException) {
            System.out.println("Error conexion sql");
        } catch (Exception e) {
            System.out.println("Error padre");
        }
    }

    public static DatabaseConnection getInstanciaDataBaseConnection() {
        if(instanciaDataBaseConnection == null)
            instanciaDataBaseConnection = new DatabaseConnection();
        return instanciaDataBaseConnection;
    }

    public Connection getConnectionDB() {
        return connectionDB;
    }

    public void setConnectionDB(Connection connectionDB) {
        this.connectionDB = connectionDB;
    }

}
