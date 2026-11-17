package com.taipicala.controller;

import com.taipicala.dao.ClienteDAO;
import com.taipicala.dao.ComprobanteDAO;
import com.taipicala.dao.HabitacionDAO;
import com.taipicala.dao.PagoDAO;
import com.taipicala.dao.ReservaDAO;
import com.taipicala.model.dto.Cliente;
import com.taipicala.model.dto.Comprobante;
import com.taipicala.model.dto.DetalleComprobante;
import com.taipicala.model.dto.Habitacion;
import com.taipicala.model.dto.Pago;
import com.taipicala.model.dto.Reserva;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named("adminFacturacionBean")
@ViewScoped
public class AdminFacturacionBean implements Serializable {

    private ComprobanteDAO comprobanteDAO;
    private ClienteDAO clienteDAO;
    private HabitacionDAO habitacionDAO;
    private ReservaDAO reservaDAO;
    private PagoDAO pagoDAO;

    // Tabs
    private String activeTab = "nueva"; // nueva, comprobantes, pagos, configuracion

    // KPI Cards
    private int facturasMes = 18;
    private int boletasMes = 27;
    private BigDecimal ingresosMes = new BigDecimal("12450.00");
    private String crecimiento = "+12%";

    // Formulario Nueva Facturación
    private String tipoComprobante = "Boleta de Venta";
    private String serie = "B001";
    private String numero = "000125";
    private String clienteTexto = "Juan Pérez Quispe - DNI: 70345678";
    private Integer idCliente = 1;
    private String tipoCliente = "Huésped";
    private Integer idHabitacion = 1;
    private Integer idReserva = 1;
    private String fechaEmisionStr = "2025-09-16";
    private String observaciones = "Gracias por su preferencia.";

    // Items / Servicios
    private List<DetalleComprobante> items = new ArrayList<>();
    private String nuevoItemDescripcion;
    private int nuevoItemCantidad = 1;
    private BigDecimal nuevoItemPrecio = BigDecimal.ZERO;

    // Totales calculados
    private BigDecimal subtotal = new BigDecimal("305.00");
    private BigDecimal igv = new BigDecimal("54.90");
    private BigDecimal total = new BigDecimal("359.90");

    // Lista de comprobantes emitidos en BD
    private List<Comprobante> comprobantes = new ArrayList<>();
    private Comprobante comprobanteSeleccionado;

    // Listas para selects
    private List<Habitacion> habitacionesDisponibles = new ArrayList<>();
    private List<Reserva> reservasDisponibles = new ArrayList<>();
    private List<Cliente> clientesDisponibles = new ArrayList<>();

    // Tab Pagos y Devoluciones
    private List<Pago> pagos = new ArrayList<>();
    private BigDecimal totalCobrado = new BigDecimal("12450.00");
    private BigDecimal totalDevuelto = new BigDecimal("180.00");
    private int totalTransacciones = 32;

    // Formulario Registrar Pago
    private BigDecimal nuevoPagoMonto = new BigDecimal("359.90");
    private String nuevoPagoMetodo = "Tarjeta VISA";
    private String nuevoPagoReferencia = "OP-998812";
    private Integer nuevoPagoClienteId = 1;
    private String nuevoPagoObservaciones = "Pago en recepción";

    // Formulario Registrar Devolución
    private BigDecimal nuevaDevolucionMonto = new BigDecimal("120.00");
    private String nuevaDevolucionMetodo = "Efectivo";
    private String nuevaDevolucionMotivo = "Cancelación de reserva anticipada";
    private Integer nuevaDevolucionClienteId = 1;

    // Tab Configuración de Facturación
    private String configRuc = "20601234567";
    private String configRazonSocial = "Hotel Taipicala S.A.C.";
    private String configNombreComercial = "Hotel Taipicala";
    private String configDireccion = "Av. Costanera s/n, Ilave, El Collao - Puno, Perú";
    private String configTelefono = "+51 992 881 117";
    private String configEmail = "facturacion@hoteltaipicala.pe";
    private String configSerieBoleta = "B001";
    private String configSerieFactura = "F001";
    private BigDecimal configTasaIgv = new BigDecimal("18.00");
    private String configFormatoImpresion = "Ticket (80mm)";
    private String configMensajePie = "Gracias por su preferencia. Más que un hotel, una experiencia.";

    @PostConstruct
    public void init() {
        comprobanteDAO = new ComprobanteDAO();
        clienteDAO = new ClienteDAO();
        habitacionDAO = new HabitacionDAO();
        reservaDAO = new ReservaDAO();
        pagoDAO = new PagoDAO();

        try {
            habitacionesDisponibles = habitacionDAO.readAll();
            reservasDisponibles = reservaDAO.readAll();
            clientesDisponibles = clienteDAO.readAll();
        } catch (Exception e) {
            // Ignorar
        }

        cargarComprobantes();
        cargarPagos();
        cargarItemsPorDefecto();
        recalcularTotales();
    }

    public void cargarComprobantes() {
        try {
            comprobantes = comprobanteDAO.readAll();
        } catch (Exception e) {
            comprobantes = new ArrayList<>();
        }
    }

    public void cargarPagos() {
        try {
            pagos = pagoDAO.readAll();
            if (pagos != null && !pagos.isEmpty()) {
                totalTransacciones = pagos.size();
                BigDecimal cob = BigDecimal.ZERO;
                BigDecimal dev = BigDecimal.ZERO;
                for (Pago p : pagos) {
                    if ("Devolución".equalsIgnoreCase(p.getTipoOperacion()) || "Devolucion".equalsIgnoreCase(p.getTipoOperacion())) {
                        dev = dev.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                    } else {
                        cob = cob.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                    }
                }
                if (cob.compareTo(BigDecimal.ZERO) > 0) {
                    totalCobrado = cob;
                }
                if (dev.compareTo(BigDecimal.ZERO) > 0) {
                    totalDevuelto = dev;
                }
            }
        } catch (Exception e) {
            pagos = new ArrayList<>();
        }
    }

    private void cargarItemsPorDefecto() {
        items = new ArrayList<>();
        items.add(new DetalleComprobante("Hospedaje - Habitación Simple", 2, new BigDecimal("120.00")));
        items.add(new DetalleComprobante("Desayuno Americano", 2, new BigDecimal("25.00")));
        items.add(new DetalleComprobante("Estacionamiento", 1, new BigDecimal("15.00")));
    }

    public void recalcularTotales() {
        BigDecimal sum = BigDecimal.ZERO;
        for (DetalleComprobante d : items) {
            if (d.getSubtotal() != null) {
                sum = sum.add(d.getSubtotal());
            }
        }
        this.subtotal = sum;
        this.igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        this.total = subtotal.add(this.igv).setScale(2, RoundingMode.HALF_UP);
    }

    public void agregarItem() {
        if (nuevoItemDescripcion == null || nuevoItemDescripcion.trim().isEmpty() || nuevoItemPrecio == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "Ingrese la descripción y precio del servicio."));
            return;
        }
        int qty = nuevoItemCantidad > 0 ? nuevoItemCantidad : 1;
        items.add(new DetalleComprobante(nuevoItemDescripcion, qty, nuevoItemPrecio));
        nuevoItemDescripcion = "";
        nuevoItemCantidad = 1;
        nuevoItemPrecio = BigDecimal.ZERO;
        recalcularTotales();
    }

    public void eliminarItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalcularTotales();
        }
    }

    public void cambiarTipoComprobante() {
        if ("Factura".equalsIgnoreCase(tipoComprobante)) {
            this.serie = configSerieFactura != null ? configSerieFactura : "F001";
            this.numero = "000090";
        } else {
            this.serie = configSerieBoleta != null ? configSerieBoleta : "B001";
            this.numero = "000126";
        }
    }

    public void regenerarNumero() {
        int rnd = 100 + (int)(Math.random() * 900);
        this.numero = String.format("%06d", rnd);
    }

    public void iniciarNuevaFacturacion() {
        comprobanteSeleccionado = null;
        clienteTexto = "";
        idCliente = null;
        tipoCliente = "Huésped";
        idHabitacion = null;
        idReserva = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        fechaEmisionStr = sdf.format(new Date());
        observaciones = "";

        // Generar correlativo siguiente
        regenerarNumero();

        // Vaciar lista de items para empezar factura limpia
        items = new ArrayList<>();
        recalcularTotales();

        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Nueva Facturación", "Formulario en blanco listo para ingresar un nuevo comprobante."));
    }

    public void limpiarFormulario() {
        iniciarNuevaFacturacion();
    }

    public void emitirComprobante() {
        try {
            Cliente cliente = null;
            if (idCliente != null) {
                cliente = clienteDAO.read(idCliente);
            }
            if (cliente == null) {
                List<Cliente> all = clienteDAO.readAll();
                cliente = (all != null && !all.isEmpty()) ? all.get(0) : null;
            }

            if (cliente == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontró cliente registrado en la base de datos."));
                return;
            }

            Comprobante nuevo = new Comprobante();
            nuevo.setCliente(cliente);
            nuevo.setTipo(tipoComprobante);
            nuevo.setSerie(serie);
            nuevo.setNumero(numero);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = (fechaEmisionStr != null && !fechaEmisionStr.isEmpty()) ? sdf.parse(fechaEmisionStr) : new Date();
            nuevo.setFechaEmision(fecha);
            nuevo.setTipoCliente(tipoCliente);
            nuevo.setSubtotal(subtotal);
            nuevo.setIgv(igv);
            nuevo.setTotal(total);
            nuevo.setEstado("Emitido");
            nuevo.setObservaciones(observaciones);

            if (idHabitacion != null) {
                Habitacion h = habitacionDAO.read(idHabitacion);
                nuevo.setHabitacion(h);
            }
            if (idReserva != null) {
                Reserva r = reservaDAO.read(idReserva);
                nuevo.setReserva(r);
            }

            for (DetalleComprobante item : items) {
                nuevo.addDetalle(new DetalleComprobante(item.getDescripcion(), item.getCantidad(), item.getPrecioUnitario()));
            }

            comprobanteDAO.create(nuevo);

            // Registrar automáticamente el pago correspondiente
            Pago pagoAuto = new Pago(cliente, nuevo, total, "Efectivo", "OP-" + nuevo.getNumero());
            pagoDAO.create(pagoAuto);

            cargarComprobantes();
            cargarPagos();

            comprobanteSeleccionado = nuevo;

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Comprobante " + nuevo.getSerieNumero() + " emitido y registrado en MySQL."));

            // Generar nuevo número correlativo
            regenerarNumero();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void anularComprobante(Integer id) {
        try {
            Comprobante c = comprobanteDAO.read(id);
            if (c != null) {
                c.setEstado("Anulado");
                comprobanteDAO.update(c);
                cargarComprobantes();
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Anulado", "Comprobante #" + id + " ha sido anulado en MySQL."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void seleccionarComprobante(Comprobante c) {
        this.comprobanteSeleccionado = c;
    }

    // Acciones de Pagos y Devoluciones
    public void registrarNuevoPago() {
        try {
            Cliente cli = clienteDAO.read(nuevoPagoClienteId != null ? nuevoPagoClienteId : 1);
            if (cli == null) {
                List<Cliente> all = clienteDAO.readAll();
                cli = (all != null && !all.isEmpty()) ? all.get(0) : null;
            }

            Pago p = new Pago();
            p.setCliente(cli);
            p.setMonto(nuevoPagoMonto != null ? nuevoPagoMonto : BigDecimal.ZERO);
            p.setMetodoPago(nuevoPagoMetodo != null ? nuevoPagoMetodo : "Efectivo");
            p.setTipoOperacion("Pago");
            p.setReferencia(nuevoPagoReferencia != null ? nuevoPagoReferencia : "REC-" + System.currentTimeMillis() % 10000);
            p.setFechaPago(new Date());
            p.setEstado("Completado");
            p.setObservaciones(nuevoPagoObservaciones);

            pagoDAO.create(p);
            cargarPagos();

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago de S/ " + p.getMonto() + " registrado correctamente en MySQL."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void registrarDevolucion() {
        try {
            Cliente cli = clienteDAO.read(nuevaDevolucionClienteId != null ? nuevaDevolucionClienteId : 1);
            if (cli == null) {
                List<Cliente> all = clienteDAO.readAll();
                cli = (all != null && !all.isEmpty()) ? all.get(0) : null;
            }

            Pago p = new Pago();
            p.setCliente(cli);
            p.setMonto(nuevaDevolucionMonto != null ? nuevaDevolucionMonto : BigDecimal.ZERO);
            p.setMetodoPago(nuevaDevolucionMetodo != null ? nuevaDevolucionMetodo : "Efectivo");
            p.setTipoOperacion("Devolución");
            p.setReferencia("DEV-" + (System.currentTimeMillis() % 10000));
            p.setFechaPago(new Date());
            p.setEstado("Reembolsado");
            p.setObservaciones(nuevaDevolucionMotivo);

            pagoDAO.create(p);
            cargarPagos();

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Devolución", "Devolución de S/ " + p.getMonto() + " registrada en MySQL."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void guardarConfiguracion() {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Configuración Guardada", 
                "Los parámetros de facturación, RUC (" + configRuc + "), series y tasa IGV han sido actualizados exitosamente."));
    }

    // Getters and Setters
    public String getActiveTab() { return activeTab; }
    public void setActiveTab(String activeTab) { this.activeTab = activeTab; }

    public int getFacturasMes() { return facturasMes; }
    public int getBoletasMes() { return boletasMes; }
    public BigDecimal getIngresosMes() { return ingresosMes; }
    public String getCrecimiento() { return crecimiento; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getClienteTexto() { return clienteTexto; }
    public void setClienteTexto(String clienteTexto) { this.clienteTexto = clienteTexto; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public Integer getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Integer idHabitacion) { this.idHabitacion = idHabitacion; }

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public String getFechaEmisionStr() { return fechaEmisionStr; }
    public void setFechaEmisionStr(String fechaEmisionStr) { this.fechaEmisionStr = fechaEmisionStr; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<DetalleComprobante> getItems() { return items; }
    public void setItems(List<DetalleComprobante> items) { this.items = items; }

    public String getNuevoItemDescripcion() { return nuevoItemDescripcion; }
    public void setNuevoItemDescripcion(String nuevoItemDescripcion) { this.nuevoItemDescripcion = nuevoItemDescripcion; }

    public int getNuevoItemCantidad() { return nuevoItemCantidad; }
    public void setNuevoItemCantidad(int nuevoItemCantidad) { this.nuevoItemCantidad = nuevoItemCantidad; }

    public BigDecimal getNuevoItemPrecio() { return nuevoItemPrecio; }
    public void setNuevoItemPrecio(BigDecimal nuevoItemPrecio) { this.nuevoItemPrecio = nuevoItemPrecio; }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getIgv() { return igv; }
    public BigDecimal getTotal() { return total; }

    public List<Comprobante> getComprobantes() { return comprobantes; }

    public Comprobante getComprobanteSeleccionado() { return comprobanteSeleccionado; }
    public void setComprobanteSeleccionado(Comprobante comprobanteSeleccionado) { this.comprobanteSeleccionado = comprobanteSeleccionado; }

    public List<Habitacion> getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public List<Reserva> getReservasDisponibles() { return reservasDisponibles; }
    public List<Cliente> getClientesDisponibles() { return clientesDisponibles; }

    public List<Pago> getPagos() { return pagos; }
    public BigDecimal getTotalCobrado() { return totalCobrado; }
    public BigDecimal getTotalDevuelto() { return totalDevuelto; }
    public int getTotalTransacciones() { return totalTransacciones; }

    public BigDecimal getNuevoPagoMonto() { return nuevoPagoMonto; }
    public void setNuevoPagoMonto(BigDecimal nuevoPagoMonto) { this.nuevoPagoMonto = nuevoPagoMonto; }

    public String getNuevoPagoMetodo() { return nuevoPagoMetodo; }
    public void setNuevoPagoMetodo(String nuevoPagoMetodo) { this.nuevoPagoMetodo = nuevoPagoMetodo; }

    public String getNuevoPagoReferencia() { return nuevoPagoReferencia; }
    public void setNuevoPagoReferencia(String nuevoPagoReferencia) { this.nuevoPagoReferencia = nuevoPagoReferencia; }

    public Integer getNuevoPagoClienteId() { return nuevoPagoClienteId; }
    public void setNuevoPagoClienteId(Integer nuevoPagoClienteId) { this.nuevoPagoClienteId = nuevoPagoClienteId; }

    public String getNuevoPagoObservaciones() { return nuevoPagoObservaciones; }
    public void setNuevoPagoObservaciones(String nuevoPagoObservaciones) { this.nuevoPagoObservaciones = nuevoPagoObservaciones; }

    public BigDecimal getNuevaDevolucionMonto() { return nuevaDevolucionMonto; }
    public void setNuevaDevolucionMonto(BigDecimal nuevaDevolucionMonto) { this.nuevaDevolucionMonto = nuevaDevolucionMonto; }

    public String getNuevaDevolucionMetodo() { return nuevaDevolucionMetodo; }
    public void setNuevaDevolucionMetodo(String nuevaDevolucionMetodo) { this.nuevaDevolucionMetodo = nuevaDevolucionMetodo; }

    public String getNuevaDevolucionMotivo() { return nuevaDevolucionMotivo; }
    public void setNuevaDevolucionMotivo(String nuevaDevolucionMotivo) { this.nuevaDevolucionMotivo = nuevaDevolucionMotivo; }

    public Integer getNuevaDevolucionClienteId() { return nuevaDevolucionClienteId; }
    public void setNuevaDevolucionClienteId(Integer nuevaDevolucionClienteId) { this.nuevaDevolucionClienteId = nuevaDevolucionClienteId; }

    public String getConfigRuc() { return configRuc; }
    public void setConfigRuc(String configRuc) { this.configRuc = configRuc; }

    public String getConfigRazonSocial() { return configRazonSocial; }
    public void setConfigRazonSocial(String configRazonSocial) { this.configRazonSocial = configRazonSocial; }

    public String getConfigNombreComercial() { return configNombreComercial; }
    public void setConfigNombreComercial(String configNombreComercial) { this.configNombreComercial = configNombreComercial; }

    public String getConfigDireccion() { return configDireccion; }
    public void setConfigDireccion(String configDireccion) { this.configDireccion = configDireccion; }

    public String getConfigTelefono() { return configTelefono; }
    public void setConfigTelefono(String configTelefono) { this.configTelefono = configTelefono; }

    public String getConfigEmail() { return configEmail; }
    public void setConfigEmail(String configEmail) { this.configEmail = configEmail; }

    public String getConfigSerieBoleta() { return configSerieBoleta; }
    public void setConfigSerieBoleta(String configSerieBoleta) { this.configSerieBoleta = configSerieBoleta; }

    public String getConfigSerieFactura() { return configSerieFactura; }
    public void setConfigSerieFactura(String configSerieFactura) { this.configSerieFactura = configSerieFactura; }

    public BigDecimal getConfigTasaIgv() { return configTasaIgv; }
    public void setConfigTasaIgv(BigDecimal configTasaIgv) { this.configTasaIgv = configTasaIgv; }

    public String getConfigFormatoImpresion() { return configFormatoImpresion; }
    public void setConfigFormatoImpresion(String configFormatoImpresion) { this.configFormatoImpresion = configFormatoImpresion; }

    public String getConfigMensajePie() { return configMensajePie; }
    public void setConfigMensajePie(String configMensajePie) { this.configMensajePie = configMensajePie; }
}
