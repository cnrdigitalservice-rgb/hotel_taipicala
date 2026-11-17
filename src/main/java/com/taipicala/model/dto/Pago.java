package com.taipicala.model.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_comprobante")
    private Comprobante comprobante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(name = "metodo_pago", length = 50, nullable = false)
    private String metodoPago = "Efectivo";

    @Column(name = "tipo_operacion", length = 30)
    private String tipoOperacion = "Pago";

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_pago")
    private Date fechaPago = new Date();

    @Column(name = "estado", length = 20)
    private String estado = "Completado";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public Pago() {
    }

    public Pago(Cliente cliente, Comprobante comprobante, BigDecimal monto, String metodoPago, String referencia) {
        this.cliente = cliente;
        this.comprobante = comprobante;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
        this.fechaPago = new Date();
        this.estado = "Completado";
        this.tipoOperacion = "Pago";
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFechaPagoFormateada() {
        if (fechaPago == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fechaPago);
    }
}

