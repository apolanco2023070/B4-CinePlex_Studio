/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.cineplex.system.config.DatabaseConnection;
import org.cineplex.system.model.Auditorium;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author informatica
 */
public class AuditoriumRepository {
    
     public void saveAuditorium(Auditorium auditorium) {
        String sql = "{call sp_insert_auditorium(?,?)}";

        // Try-with-resources: Connection y CallableStatement se cierran solos al terminar
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
     CallableStatement callSP = conn.prepareCall(sql)) {

            callSP.setString(1, auditorium.getName());
            callSP.setInt(2, auditorium.getCapacity());
           
            callSP.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar la sala: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar la sala. Verifica los datos.", e);
        }
    }
    
     
     public List<Auditorium> getAuditoriums() {
        List<Auditorium> auditoriums = new ArrayList<>();
        String sql = "{call sp_get_all_auditoriums()}";
        
        try (Connection conn = DatabaseConnection.getDatabaseInstance().getConnectionDB();
             CallableStatement callSP = conn.prepareCall(sql);
             ResultSet rs = callSP.executeQuery()) {
            
            while (rs.next()) {
                Auditorium auditorium = new Auditorium();
                auditorium.setAuditoriumId(rs.getInt("auditorium_id"));
                auditorium.setName(rs.getString("name"));
                auditorium.setCapacity(rs.getInt("capacity"));
                auditoriums.add(auditorium);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al consultar salas: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar las salas.", e);
        }
        return auditoriums;
    }
}
