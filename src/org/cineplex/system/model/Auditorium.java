/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.model;

/**
 *
 * @author informatica
 */
public class Auditorium {

    private Integer auditoriumId;
    private String name;
    private Integer capacity;

    public Auditorium() {
    }

    public Auditorium(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    // Getters y Setters
    public Integer getAuditoriumId() {
        return auditoriumId;
    }

    public void setAuditoriumId(Integer auditoriumId) {
        this.auditoriumId = auditoriumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public String toString() {
        return name + " (Capacidad: " + capacity + ")";
    }
}
