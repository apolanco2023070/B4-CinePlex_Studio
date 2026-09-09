/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cineplex.system.service;

import org.cineplex.system.repository.UsuarioRepository;
import org.cineplex.system.model.TypeRol;
import org.cineplex.system.model.Usuario;

public class AuthService {
    private final UsuarioRepository usuarioDAO = new UsuarioRepository();
    

    public static class ResultadoLogin {
        public final boolean exito;
        public final String mensaje;
        public final Usuario usuario;
        
        public ResultadoLogin(boolean exito, String mensaje, Usuario usuario) {
            this.exito = exito;
            this.mensaje = mensaje;
            this.usuario = usuario;
        }
    }
    
 
    public ResultadoLogin login(String nombreUsuario, String passwordPlano, TypeRol rolEsperado) {
       
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return new ResultadoLogin(false, "El usuario no puede estar vacío.", null);
        }
        if (passwordPlano == null || passwordPlano.isBlank()) {
            return new ResultadoLogin(false, "La contraseña no puede estar vacía.", null);
        }

        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
        
        if (usuario == null) {
       
            return new ResultadoLogin(false, "Usuario o contraseña incorrectos.", null);
        }
        

        boolean passwordValida = passwordPlano.equals(usuario.getPassword());
        if (!passwordValida) {
            return new ResultadoLogin(false, "Usuario o contraseña incorrectos.", null);
        }
        

        if (!usuario.tieneRol(rolEsperado)) {
            return new ResultadoLogin(
                false, 
                "Este usuario no tiene permisos de " + rolEsperado.getNombre() + ".", 
                null
            );
        }

        return new ResultadoLogin(true, "Inicio de sesión exitoso.", usuario);
    }
}