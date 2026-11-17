package com.taipicala.controller;

import com.taipicala.model.dto.Habitacion;
import com.taipicala.service.HabitacionService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "habitacionBean")
@ViewScoped
public class HabitacionBean implements Serializable {

    private HabitacionService habitacionService;
    private List<Habitacion> habitacionesDisponibles;

    @PostConstruct
    public void init() {
        habitacionService = new HabitacionService();
        cargarHabitaciones();
    }

    public void cargarHabitaciones() {
        habitacionesDisponibles = habitacionService.obtenerHabitacionesDisponibles();
    }

    public List<Habitacion> getHabitacionesDisponibles() {
        return habitacionesDisponibles;
    }

    public void setHabitacionesDisponibles(List<Habitacion> habitacionesDisponibles) {
        this.habitacionesDisponibles = habitacionesDisponibles;
    }
}
