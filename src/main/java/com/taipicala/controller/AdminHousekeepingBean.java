package com.taipicala.controller;

import com.taipicala.model.dto.Habitacion;
import com.taipicala.service.HabitacionService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@Named("adminHousekeepingBean")
@ViewScoped
public class AdminHousekeepingBean implements Serializable {

    private HabitacionService habitacionService;
    private List<Habitacion> habitaciones;

    @PostConstruct
    public void init() {
        habitacionService = new HabitacionService();
        cargarHabitaciones();
    }

    private void cargarHabitaciones() {
        habitaciones = habitacionService.obtenerTodas();
    }

    public void marcarLimpia(Integer idHabitacion) {
        cambiarEstado(idHabitacion, "DISPONIBLE", "Habitación marcada como Limpia y Disponible.");
    }

    public void marcarSucia(Integer idHabitacion) {
        cambiarEstado(idHabitacion, "SUCIA", "Habitación marcada como Sucia para limpieza.");
    }

    public void marcarMantenimiento(Integer idHabitacion) {
        cambiarEstado(idHabitacion, "MANTENIMIENTO", "Habitación enviada a Mantenimiento.");
    }

    private void cambiarEstado(Integer idHabitacion, String estado, String mensajeExito) {
        if (habitacionService.cambiarEstado(idHabitacion, estado)) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensajeExito));
            cargarHabitaciones(); // refrescar
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la habitación."));
        }
    }

    // Getters
    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }
}
