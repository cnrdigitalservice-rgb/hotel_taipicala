package com.taipicala.service;

import com.taipicala.dao.ClienteDAO;
import com.taipicala.dao.HabitacionDAO;
import com.taipicala.dao.ReservaDAO;
import com.taipicala.model.dto.Cliente;
import com.taipicala.model.dto.Habitacion;
import com.taipicala.model.dto.Reserva;

import java.util.Date;
import java.util.List;

public class ReservaService {
    
    private ReservaDAO reservaDAO;
    private ClienteDAO clienteDAO;
    private HabitacionDAO habitacionDAO;

    public ReservaService() {
        this.reservaDAO = new ReservaDAO();
        this.clienteDAO = new ClienteDAO();
        this.habitacionDAO = new HabitacionDAO();
    }

    public boolean realizarReserva(Cliente cliente, Integer idHabitacion, Date fechaEntrada, Date fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null || fechaSalida.before(fechaEntrada)) {
            return false;
        }

        // Buscar si el cliente ya existe por email
        Cliente clienteExistente = clienteDAO.findByEmail(cliente.getEmail());
        if (clienteExistente == null) {
            clienteDAO.create(cliente);
            clienteExistente = cliente;
        } else {
            // Actualizar datos del cliente si es necesario
            clienteExistente.setNombre(cliente.getNombre());
            clienteExistente.setApellidos(cliente.getApellidos());
            clienteExistente.setTelefono(cliente.getTelefono());
            clienteDAO.update(clienteExistente);
        }

        Habitacion habitacion = habitacionDAO.read(idHabitacion);
        if (habitacion == null || !"DISPONIBLE".equals(habitacion.getEstado())) {
            return false;
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(clienteExistente);
        reserva.setHabitacion(habitacion);
        reserva.setFechaEntrada(fechaEntrada);
        reserva.setFechaSalida(fechaSalida);
        reserva.setEstado("CONFIRMADA");

        reservaDAO.create(reserva);

        // Actualizar estado de la habitación
        habitacion.setEstado("OCUPADA");
        habitacionDAO.update(habitacion);

        return true;
    }

    public List<Reserva> obtenerTodas() {
        return reservaDAO.readAll();
    }

    public boolean cambiarEstado(Integer idReserva, String nuevoEstado) {
        Reserva reserva = reservaDAO.read(idReserva);
        if (reserva == null) return false;

        reserva.setEstado(nuevoEstado);
        reservaDAO.update(reserva);

        // Si se cancela, liberar la habitación
        if ("CANCELADA".equals(nuevoEstado)) {
            Habitacion hab = reserva.getHabitacion();
            if (hab != null) {
                hab.setEstado("DISPONIBLE");
                habitacionDAO.update(hab);
            }
        }
        // Si se confirma, marcar como ocupada (o reservada según lógica de negocio)
        if ("CONFIRMADA".equals(nuevoEstado)) {
            Habitacion hab = reserva.getHabitacion();
            if (hab != null) {
                hab.setEstado("OCUPADA");
                habitacionDAO.update(hab);
            }
        }
        return true;
    }
}
