package org.cineplex.system.service;

import org.cineplex.system.model.RoleType;
import org.cineplex.system.repository.UserRepository;
import org.cineplex.system.model.User;

public class AuthService {

    private final UserRepository usuarioDAO = new UserRepository();

    public static class ResultadoLogin {

        public final boolean exito;
        public final String mensaje;
        public final User usuario;

        public ResultadoLogin(boolean exito, String mensaje, User usuario) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.usuario = usuario;
        }
    }

    public ResultadoLogin login(String nombreUsuario, String passwordPlano, RoleType rolEsperado) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return new ResultadoLogin(false, "El usuario no puede estar vacío.", null);
        }
        if (passwordPlano == null || passwordPlano.isBlank()) {
            return new ResultadoLogin(false, "La contraseña no puede estar vacía.", null);
        }

        User usuario = usuarioDAO.findByUsername(nombreUsuario);

        if (usuario == null) {
            return new ResultadoLogin(false, "Usuario o contraseña incorrectos.", null);
        }

        boolean passwordValida = passwordPlano.equals(usuario.getPassword());
        if (!passwordValida) {
            return new ResultadoLogin(false, "Usuario o contraseña incorrectos.", null);
        }

        if (usuario.getRole() == null || usuario.getRole().getRoleType() != rolEsperado) {
            return new ResultadoLogin(
                    false,
                    "Este usuario no tiene permisos de " + rolEsperado.getName() + ".",
                    null
            );
        }

        return new ResultadoLogin(true, "Inicio de sesión exitoso.", usuario);
    }
}
