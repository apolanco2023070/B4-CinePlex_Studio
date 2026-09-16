
package org.cineplex.system.utils;

/*
 * Clase de utilidades para validaciones de datos.
 * @author informatica
 */
public class Validations {

    public Validations() {
    }

    public boolean equalsText(String textOriginal, String textCompare) {
        if (textOriginal == null || textCompare == null) return false;
        return textOriginal.equals(textCompare);
    }

    public boolean emptyText(String text) {
        return text == null || text.isEmpty() || text.isBlank();
    }

    public boolean validateLengthText(String text, int lengthMax) {
        if (text == null) return false;
        return text.length() <= lengthMax;
    }

    public boolean isNumeric(String text) {
        if (emptyText(text)) return false;
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isPositiveNumber(String text) {
        if (!isNumeric(text)) {
            return false;
        }
        return Integer.parseInt(text) > 0;
    }

    public boolean isValidRating(String rating) {
        if (emptyText(rating)) return false;
        String r = rating.trim().toUpperCase();
        return r.equals("A") || r.equals("B") || r.equals("C");
    }

    public boolean isValidGenre(String genre) {
        if (emptyText(genre)) return false;
        String g = genre.trim().toLowerCase();
        return g.equals("action") || g.equals("acción") || 
               g.equals("drama") || 
               g.equals("comedy") || g.equals("comedia");
    }
}

