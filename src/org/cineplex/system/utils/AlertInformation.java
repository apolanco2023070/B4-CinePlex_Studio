
package org.cineplex.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertInformation {
 
    public AlertInformation() {
    }
 
    /**
     * Muestra una alerta de JavaFX con el tipo, título, encabezado y mensaje especificados.
     *
     * @param tipoAlerta  Tipo de alerta como String (INFORMATION, WARNING, ERROR, CONFIRMATION, NONE)
     * @param titulo      Título de la ventana de alerta
     * @param encabezado  Texto del encabezado (puede ser null)
     * @param mensaje     Contenido principal del mensaje
     */
    public static void viewAlert(String tipoAlerta, String titulo, String encabezado, String mensaje) {

        // Variable local de tipo AlertType que almacenará el tipo de alerta a mostrar
        AlertType tipo;
 
        // Switch para determinar el tipo de alerta según el parámetro recibido
        switch (tipoAlerta.toUpperCase()) {
            case "INFORMATION":
                tipo = AlertType.INFORMATION;
                break;
            case "WARNING":
                tipo = AlertType.WARNING;
                break;
            case "ERROR":
                tipo = AlertType.ERROR;
                break;
            case "CONFIRMATION":
                tipo = AlertType.CONFIRMATION;
                break;
            case "NONE":
                tipo = AlertType.NONE;
                break;
            default:
                tipo = AlertType.INFORMATION;
                break;
        }
 
        // Creación y configuración de la alerta
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
 
        // Mostrar la alerta
        alert.showAndWait();
    }
    
}
