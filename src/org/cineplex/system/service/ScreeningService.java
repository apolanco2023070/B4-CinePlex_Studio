/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package org.cineplex.system.service;
 
import java.time.LocalDate;
import java.time.LocalTime;
 
import org.cineplex.system.model.Screening;
import org.cineplex.system.repository.ScreeningRepository;
 
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    public ScreeningService() {
        this.screeningRepository = new ScreeningRepository();
    }
    public boolean validateScreening(Screening screening) throws Exception {
        return screeningRepository.hasTimeConflict(
            screening.getAuditoriumId(),
            screening.getShowDate(),
            screening.getShowTime()
        );
    }

    public void createScreening(Screening screening) throws Exception {
        // Primero verificar si hay conflicto
        if (validateScreening(screening)) {
            throw new Exception("Ya existe una función programada en esta sala, fecha y hora. Por favor, seleccione otro horario.");
        }
        // Si no hay conflicto, guardar
        screeningRepository.saveScreening(screening);
    }
}