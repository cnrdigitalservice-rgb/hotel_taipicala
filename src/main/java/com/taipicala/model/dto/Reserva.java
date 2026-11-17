package com.taipicala.model.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "reserva")
public class Reserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    @Column(name = "fecha_entrada", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaSalida;

    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_reserva", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReserva;

    public Reserva() {}

    public String getCodigo() {
        if (idReserva != null) {
            return String.format("RES-%04d", idReserva);
        }
        return "RES-0000";
    }

    public String getEstadoDisplay() {
        if (estado == null) return "Pendiente";
        if ("CONFIRMADA".equalsIgnoreCase(estado)) return "Confirmada";
        if ("CHECK-IN".equalsIgnoreCase(estado) || "CHECKIN".equalsIgnoreCase(estado)) return "Check-in";
        if ("CANCELADA".equalsIgnoreCase(estado)) return "Cancelada";
        return "Pendiente";
    }

    public String getTotalCalculado() {
        if (habitacion != null && habitacion.getPrecio() != null && fechaEntrada != null && fechaSalida != null) {
            long diff = fechaSalida.getTime() - fechaEntrada.getTime();
            long dias = diff / (1000 * 60 * 60 * 24);
            if (dias <= 0) dias = 1;
            BigDecimal total = habitacion.getPrecio().multiply(new BigDecimal(dias));
            return String.format("%.2f", total);
        }
        return "240.00";
    }

    public int getHuespedes() {
        if (habitacion != null && "Simple".equalsIgnoreCase(habitacion.getTipo())) {
            return 1;
        }
        return 2;
    }

    // Getters and Setters
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    public Date getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(Date fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public Date getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Date fechaSalida) { this.fechaSalida = fechaSalida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(Date fechaReserva) { this.fechaReserva = fechaReserva; }
}
