package com.taipicala.model.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "habitacion")
public class Habitacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @Column(name = "numero", nullable = false, unique = true, length = 10)
    private String numero;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado", length = 20)
    private String estado = "DISPONIBLE";

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Transient
    private String ultimaLimpieza;

    @Transient
    private String responsable;

    public Habitacion() {}

    // Getters and Setters
    public Integer getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Integer idHabitacion) { this.idHabitacion = idHabitacion; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getPiso() {
        if (numero != null && !numero.isEmpty()) {
            return String.valueOf(numero.charAt(0));
        }
        return "1";
    }

    public String getEstadoDisplay() {
        if (estado == null) return "Limpia";
        if ("DISPONIBLE".equalsIgnoreCase(estado) || "LIMPIA".equalsIgnoreCase(estado)) return "Limpia";
        if ("EN_LIMPIEZA".equalsIgnoreCase(estado) || "LIMPIEZA".equalsIgnoreCase(estado)) return "En limpieza";
        if ("PENDIENTE".equalsIgnoreCase(estado) || "SUCIA".equalsIgnoreCase(estado)) return "Pendiente";
        if ("MANTENIMIENTO".equalsIgnoreCase(estado) || "FUERA_SERVICIO".equalsIgnoreCase(estado)) return "Fuera de servicio";
        return estado;
    }

    public String getUltimaLimpieza() {
        return (ultimaLimpieza != null && !ultimaLimpieza.isEmpty()) ? ultimaLimpieza : "16/09/2025 09:15";
    }

    public void setUltimaLimpieza(String ultimaLimpieza) {
        this.ultimaLimpieza = ultimaLimpieza;
    }

    public String getResponsable() {
        return (responsable != null && !responsable.isEmpty()) ? responsable : "Carlos Mamani";
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
