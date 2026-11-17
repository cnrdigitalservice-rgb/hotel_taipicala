package com.taipicala.controller;

import com.taipicala.model.dto.Usuario;
import com.taipicala.service.AuthService;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    @Inject
    private AuthService authService;

    private String username;
    private String password;
    private boolean rememberMe;
    private Usuario usuarioAutenticado;

    public String login() {
        usuarioAutenticado = authService.authenticate(username, password);

        if (usuarioAutenticado != null) {
            // Put the user in session manually for the filter, although JSF session manages the bean
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogueado", usuarioAutenticado);
            return "/admin/dashboard?faces-redirect=true"; // Redirect to dashboard/home
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas o usuario inactivo"));
            return null; // Stay on the login page
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        usuarioAutenticado = null;
        return "login?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return usuarioAutenticado != null;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
