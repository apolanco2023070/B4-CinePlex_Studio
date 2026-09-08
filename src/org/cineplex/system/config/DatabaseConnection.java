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

    private static DatabaseConnection databaseInstance;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de MySQL no encontrado. " + e.getMessage());
            throw new RuntimeException("Driver de MySQL no encontrado", e);
        }
    }


    public static DatabaseConnection getDatabaseInstance() {
        if (databaseInstance == null) {
            databaseInstance = new DatabaseConnection();
        }
        return databaseInstance;
    }

   
    public Connection getConnectionDB() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://" + Enviroment.LOCATION_SERVICE + "/" + Enviroment.DATA_BASE + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            Enviroment.USER, 
            Enviroment.PASSWORD
        );
    }

}
