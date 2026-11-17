package com.taipicala.service;

import com.taipicala.dao.HabitacionDAO;
import com.taipicala.model.dto.Habitacion;
import java.util.List;

public class HabitacionService {
    
    private HabitacionDAO habitacionDAO;

    public HabitacionService() {
        this.habitacionDAO = new HabitacionDAO();
    }

    public List<Habitacion> obtenerHabitacionesDisponibles() {
        // En un caso real, podríamos filtrar por estado aquí o en el DAO.
        // Por ahora obtenemos todas las habitaciones y luego filtramos
        List<Habitacion> todas = habitacionDAO.readAll();
        todas.removeIf(h -> !"DISPONIBLE".equals(h.getEstado()));
        return todas;
    }
    
    public List<Habitacion> obtenerTodas() {
        return habitacionDAO.readAll();
    }
    
    public Habitacion buscarPorId(Integer id) {
        return habitacionDAO.read(id);
    }
    
    public boolean cambiarEstado(Integer idHabitacion, String nuevoEstado) {
        Habitacion hab = habitacionDAO.read(idHabitacion);
        if (hab == null) {
            return false;
        }
        hab.setEstado(nuevoEstado);
        habitacionDAO.update(hab);
        return true;
    }
}
