package com.taipicala.controller;

import com.taipicala.model.dto.Cliente;
import com.taipicala.model.dto.Reserva;
import com.taipicala.service.ReservaService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

@ManagedBean(name = "reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private ReservaService reservaService;
    private Cliente cliente;
    private Reserva reserva;
    private Integer idHabitacionSeleccionada;

    @PostConstruct
    public void init() {
        reservaService = new ReservaService();
        cliente = new Cliente();
        reserva = new Reserva();
        
        // Verificar si se pasó el ID por URL
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String idHabParam = params.get("idHabitacion");
        if (idHabParam != null && !idHabParam.isEmpty()) {
            idHabitacionSeleccionada = Integer.parseInt(idHabParam);
        }
    }

    public void guardarReserva() {
        boolean exito = reservaService.realizarReserva(
            cliente, 
            idHabitacionSeleccionada, 
            reserva.getFechaEntrada(), 
            reserva.getFechaSalida()
        );

        if (exito) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "La reserva se ha confirmado correctamente."));
            // Limpiar formulario
            cliente = new Cliente();
            reserva = new Reserva();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la reserva. Verifique las fechas o la disponibilidad."));
        }
    }

    // Getters and Setters
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Integer getIdHabitacionSeleccionada() { return idHabitacionSeleccionada; }
    public void setIdHabitacionSeleccionada(Integer idHabitacionSeleccionada) { this.idHabitacionSeleccionada = idHabitacionSeleccionada; }
}
