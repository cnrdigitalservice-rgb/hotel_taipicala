package com.taipicala.service;

import com.taipicala.dao.UsuarioDAO;
import com.taipicala.model.dto.Usuario;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida las credenciales de un usuario.
     */
    public Usuario authenticate(String username, String rawPassword) {
        Usuario usuario = usuarioDAO.findByUsername(username);
        
        if (usuario != null && usuario.getActivo()) {
            // Hash the provided password and compare it with the stored hash
            String hashedInput = hashPassword(rawPassword);
            if (hashedInput.equals(usuario.getPasswordHash())) {
                return usuario; // Auth success
            }
        }
        return null; // Auth failed
    }

    /**
     * Simple SHA-256 hashing para contraseñas.
     * En un proyecto real de produccion se recomendaria BCrypt o Argon2.
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
