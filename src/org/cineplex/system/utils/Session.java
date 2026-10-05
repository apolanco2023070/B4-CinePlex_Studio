package org.cineplex.system.utils;

import org.cineplex.system.model.User;


public final class Session {

    private static User currentUser;

    private Session() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}
