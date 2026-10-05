
package org.cineplex.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertInformation {
 
    public AlertInformation() {
    }
 
    /**
     * Shows a JavaFX alert with the specified type, title, header and message.
     *
     * @param alertType  Alert type as a String (INFORMATION, WARNING, ERROR, CONFIRMATION, NONE)
     * @param title      Alert window title
     * @param header     Header text (may be null)
     * @param message    Main message content
     */
    public static void viewAlert(String alertType, String title, String header, String message) {

        AlertType type;
 
        switch (alertType.toUpperCase()) {
            case "INFORMATION":
                type = AlertType.INFORMATION;
                break;
            case "WARNING":
                type = AlertType.WARNING;
                break;
            case "ERROR":
                type = AlertType.ERROR;
                break;
            case "CONFIRMATION":
                type = AlertType.CONFIRMATION;
                break;
            case "NONE":
                type = AlertType.NONE;
                break;
            default:
                type = AlertType.INFORMATION;
                break;
        }
 
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
 
        alert.showAndWait();
    }
    
}
