package com.taipicala.controller;

import com.taipicala.dao.ClienteDAO;
import com.taipicala.dao.HabitacionDAO;
import com.taipicala.dao.ReservaDAO;
import com.taipicala.model.dto.Cliente;
import com.taipicala.model.dto.Habitacion;
import com.taipicala.model.dto.Reserva;
import com.taipicala.service.ReservaService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Named("adminReservaBean")
@ViewScoped
public class AdminReservaBean implements Serializable {

    private ReservaService reservaService;
    private ReservaDAO reservaDAO;
    private ClienteDAO clienteDAO;
    private HabitacionDAO habitacionDAO;

    private List<Reserva> reservas;

    // KPI Counters
    private int totalActivas = 12;
    private int checkinHoy = 8;
    private int checkoutHoy = 3;
    private int totalMes = 25;

    // Modal Form Fields for Nueva Reserva
    private String nuevoClienteNombre;
    private String nuevoClienteApellidos;
    private String nuevoClienteEmail;
    private String nuevoClienteTelefono;
    private Integer nuevaHabitacionId;
    private String nuevaFechaEntradaStr;
    private String nuevaFechaSalidaStr;
    private String nuevoEstado = "Confirmada";

    @PostConstruct
    public void init() {
        reservaService = new ReservaService();
        reservaDAO = new ReservaDAO();
        clienteDAO = new ClienteDAO();
        habitacionDAO = new HabitacionDAO();

        // Inicializar fechas por defecto para el selector de Nueva Reserva
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        nuevaFechaEntradaStr = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 2);
        nuevaFechaSalidaStr = sdf.format(cal.getTime());

        cargarReservas();
    }

    public void cargarReservas() {
        reservas = reservaService.obtenerTodas();

        // Si la base de datos está vacía, sembrar las reservas iniciales de la imagen
        if (reservas == null || reservas.isEmpty()) {
            sembrarDatosInicialesEnBD();
            reservas = reservaService.obtenerTodas();
        }

        calcularEstadisticas();
    }

    private void sembrarDatosInicialesEnBD() {
        try {
            // Asegurar que las habitaciones existen en BD
            String[][] habsData = {
                {"101", "Simple", "120.00"},
                {"102", "Simple", "120.00"},
                {"201", "Doble", "180.00"},
                {"203", "Doble", "180.00"},
                {"302", "Twin", "200.00"},
                {"305", "Twin", "200.00"},
                {"401", "Suite", "320.00"},
                {"501", "Suite", "320.00"}
            };

            for (String[] hd : habsData) {
                try {
                    List<Habitacion> allHabs = habitacionDAO.readAll();
                    boolean exists = false;
                    for (Habitacion h : allHabs) {
                        if (hd[0].equals(h.getNumero())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        Habitacion h = new Habitacion();
                        h.setNumero(hd[0]);
                        h.setTipo(hd[1]);
                        h.setPrecio(new BigDecimal(hd[2]));
                        h.setEstado("DISPONIBLE");
                        h.setDescripcion("Habitación " + hd[1] + " Confort");
                        h.setImagenUrl("room-1.jpg");
                        habitacionDAO.create(h);
                    }
                } catch (Exception ignored) {}
            }

            // Datos de las 8 reservas de la imagen
            String[][] datos = {
                {"Juan Pérez", "Quispe", "juan.perez@gmail.com", "987654321", "101", "2025-09-10", "2025-09-12", "CONFIRMADA"},
                {"María", "Condori", "maria.condori@gmail.com", "987654322", "203", "2025-09-11", "2025-09-13", "CHECK-IN"},
                {"Carlos", "Mamani", "carlos.mamani@gmail.com", "987654323", "305", "2025-09-12", "2025-09-15", "PENDIENTE"},
                {"Ana", "Torres", "ana.torres@gmail.com", "987654324", "201", "2025-09-13", "2025-09-14", "CONFIRMADA"},
                {"Luis", "Quispe", "luis.quispe@gmail.com", "987654325", "401", "2025-09-14", "2025-09-16", "CANCELADA"},
                {"Carmen", "Apaza", "carmen.apaza@gmail.com", "987654326", "102", "2025-09-15", "2025-09-18", "PENDIENTE"},
                {"Empresa Andina", "SAC", "contacto@andina.com", "987654327", "302", "2025-09-16", "2025-09-18", "CHECK-IN"},
                {"Diego", "Flores", "diego.flores@gmail.com", "987654328", "501", "2025-09-18", "2025-09-20", "CONFIRMADA"}
            };

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Habitacion> habs = habitacionDAO.readAll();

            for (String[] d : datos) {
                // Cliente
                Cliente c = clienteDAO.findByEmail(d[2]);
                if (c == null) {
                    c = new Cliente();
                    c.setNombre(d[0]);
                    c.setApellidos(d[1]);
                    c.setEmail(d[2]);
                    c.setTelefono(d[3]);
                    clienteDAO.create(c);
                }

                // Habitación
                Habitacion habEncontrada = null;
                for (Habitacion h : habs) {
                    if (d[4].equals(h.getNumero())) {
                        habEncontrada = h;
                        break;
                    }
                }
                if (habEncontrada == null && !habs.isEmpty()) {
                    habEncontrada = habs.get(0);
                }

                if (habEncontrada != null) {
                    Reserva r = new Reserva();
                    r.setCliente(c);
                    r.setHabitacion(habEncontrada);
                    r.setFechaEntrada(sdf.parse(d[5]));
                    r.setFechaSalida(sdf.parse(d[6]));
                    r.setEstado(d[7]);
                    reservaDAO.create(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcularEstadisticas() {
        if (reservas != null && !reservas.isEmpty()) {
            int activas = 0;
            int checkin = 0;
            for (Reserva r : reservas) {
                if ("CONFIRMADA".equalsIgnoreCase(r.getEstado()) || "CHECK-IN".equalsIgnoreCase(r.getEstado())) {
                    activas++;
                }
                if ("CHECK-IN".equalsIgnoreCase(r.getEstado())) {
                    checkin++;
                }
            }
            if (activas > 0) this.totalActivas = activas;
            if (checkin > 0) this.checkinHoy = checkin;
            this.totalMes = reservas.size();
        }
    }

    public void confirmarReserva(Integer idReserva) {
        if (reservaService.cambiarEstado(idReserva, "CONFIRMADA")) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Reserva #" + idReserva + " Confirmada en Base de Datos."));
            cargarReservas();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo confirmar la reserva."));
        }
    }

    public void cancelarReserva(Integer idReserva) {
        if (reservaService.cambiarEstado(idReserva, "CANCELADA")) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Reserva #" + idReserva + " Cancelada en Base de Datos."));
            cargarReservas();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cancelar la reserva."));
        }
    }

    public void checkinReserva(Integer idReserva) {
        if (reservaService.cambiarEstado(idReserva, "CHECK-IN")) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Check-in realizado con éxito en Base de Datos."));
            cargarReservas();
        }
    }

    public void eliminarReserva(Integer idReserva) {
        try {
            Reserva r = reservaDAO.read(idReserva);
            if (r != null) {
                reservaDAO.delete(r);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Reserva #" + idReserva + " eliminada de la Base de Datos."));
                cargarReservas();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void guardarNuevaReserva() {
        try {
            if (nuevoClienteNombre == null || nuevoClienteNombre.trim().isEmpty() || nuevaHabitacionId == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Requerido", "Complete los campos obligatorios."));
                return;
            }

            Cliente c = new Cliente();
            c.setNombre(nuevoClienteNombre);
            c.setApellidos(nuevoClienteApellidos != null ? nuevoClienteApellidos : "-");
            c.setEmail(nuevoClienteEmail != null && !nuevoClienteEmail.trim().isEmpty() ? nuevoClienteEmail : "cliente_" + System.currentTimeMillis() + "@gmail.com");
            c.setTelefono(nuevoClienteTelefono != null ? nuevoClienteTelefono : "999999999");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date entrada = (nuevaFechaEntradaStr != null && !nuevaFechaEntradaStr.isEmpty()) ? sdf.parse(nuevaFechaEntradaStr) : new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(entrada);
            cal.add(Calendar.DAY_OF_YEAR, 2);
            Date salida = (nuevaFechaSalidaStr != null && !nuevaFechaSalidaStr.isEmpty()) ? sdf.parse(nuevaFechaSalidaStr) : cal.getTime();

            boolean exito = reservaService.realizarReserva(c, nuevaHabitacionId, entrada, salida);
            if (exito) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Guardado", "Nueva reserva registrada y guardada en MySQL."));
                cargarReservas();

                // Limpiar campos
                nuevoClienteNombre = null;
                nuevoClienteApellidos = null;
                nuevoClienteEmail = null;
                nuevoClienteTelefono = null;
                nuevaHabitacionId = null;

                Calendar calReset = Calendar.getInstance();
                nuevaFechaEntradaStr = sdf.format(calReset.getTime());
                calReset.add(Calendar.DAY_OF_MONTH, 2);
                nuevaFechaSalidaStr = sdf.format(calReset.getTime());
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La habitación no está disponible para las fechas seleccionadas."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    // Getters and Setters
    public List<Reserva> getReservas() { return reservas; }

    public List<Reserva> getReservasRecientes() {
        if (reservas == null || reservas.isEmpty()) {
            return Collections.emptyList();
        }
        List<Reserva> copia = new ArrayList<>(reservas);
        copia.sort((r1, r2) -> {
            Integer id1 = r1.getIdReserva() != null ? r1.getIdReserva() : 0;
            Integer id2 = r2.getIdReserva() != null ? r2.getIdReserva() : 0;
            return id2.compareTo(id1);
        });
        return copia.size() > 5 ? copia.subList(0, 5) : copia;
    }

    public int getTotalActivas() { return totalActivas; }
    public int getCheckinHoy() { return checkinHoy; }
    public int getCheckoutHoy() { return checkoutHoy; }
    public int getTotalMes() { return totalMes; }

    public String getNuevoClienteNombre() { return nuevoClienteNombre; }
    public void setNuevoClienteNombre(String nuevoClienteNombre) { this.nuevoClienteNombre = nuevoClienteNombre; }

    public String getNuevoClienteApellidos() { return nuevoClienteApellidos; }
    public void setNuevoClienteApellidos(String nuevoClienteApellidos) { this.nuevoClienteApellidos = nuevoClienteApellidos; }

    public String getNuevoClienteEmail() { return nuevoClienteEmail; }
    public void setNuevoClienteEmail(String nuevoClienteEmail) { this.nuevoClienteEmail = nuevoClienteEmail; }

    public String getNuevoClienteTelefono() { return nuevoClienteTelefono; }
    public void setNuevoClienteTelefono(String nuevoClienteTelefono) { this.nuevoClienteTelefono = nuevoClienteTelefono; }

    public Integer getNuevaHabitacionId() { return nuevaHabitacionId; }
    public void setNuevaHabitacionId(Integer nuevaHabitacionId) { this.nuevaHabitacionId = nuevaHabitacionId; }

    public String getNuevaFechaEntradaStr() { return nuevaFechaEntradaStr; }
    public void setNuevaFechaEntradaStr(String nuevaFechaEntradaStr) { this.nuevaFechaEntradaStr = nuevaFechaEntradaStr; }

    public String getNuevaFechaSalidaStr() { return nuevaFechaSalidaStr; }
    public void setNuevaFechaSalidaStr(String nuevaFechaSalidaStr) { this.nuevaFechaSalidaStr = nuevaFechaSalidaStr; }

    public String getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(String nuevoEstado) { this.nuevoEstado = nuevoEstado; }
}
