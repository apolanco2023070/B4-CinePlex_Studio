package org.cineplex.system.service;

import org.cineplex.system.model.RoleType;
import org.cineplex.system.repository.UserRepository;
import org.cineplex.system.model.User;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public static class LoginResult {

        public final boolean success;
        public final String message;
        public final User user;

        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
    }

    /**
     * Validates credentials and returns the user with whatever role it has.
     */
    public LoginResult authenticate(String username, String plainPassword) {
        if (username == null || username.isBlank()) {
            return new LoginResult(false, "El usuario no puede estar vacío.", null);
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            return new LoginResult(false, "La contraseña no puede estar vacía.", null);
        }

        User user;
        try {
            user = userRepository.findByUsername(username.trim());
        } catch (RuntimeException e) {
            return new LoginResult(false,
                    "No se pudo conectar con la base de datos. Verifique que MySQL esté en ejecución.", null);
        }

        if (user == null || !plainPassword.equals(user.getPassword())) {
            return new LoginResult(false, "Usuario o contraseña incorrectos.", null);
        }

        if (user.getRole() == null || user.getRole().getRoleType() == null) {
            return new LoginResult(false, "El usuario no tiene un rol válido asignado.", null);
        }

        return new LoginResult(true, "Inicio de sesión exitoso.", user);
    }

   
    public LoginResult login(String username, String plainPassword, RoleType expectedRole) {
        LoginResult result = authenticate(username, plainPassword);
        if (!result.success) {
            return result;
        }
        if (result.user.getRole().getRoleType() != expectedRole) {
            return new LoginResult(
                    false,
                    "Este usuario no tiene permisos de " + expectedRole.getName() + ".",
                    null
            );
        }
        return result;
    }
}
