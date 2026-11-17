package com.taipicala.controller;

import com.taipicala.dao.ClienteDAO;
import com.taipicala.model.dto.Cliente;
import com.taipicala.model.dto.Habitacion;
import com.taipicala.model.dto.Reserva;
import com.taipicala.service.HabitacionService;
import com.taipicala.service.ReservaService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named("adminRecepcionBean")
@ViewScoped
public class AdminRecepcionBean implements Serializable {

    private HabitacionService habitacionService;
    private ReservaService reservaService;
    private ClienteDAO clienteDAO;

    private List<Habitacion> habitaciones;
    private Habitacion habitacionSeleccionada;

    // KPI Counters
    private int checkinHoy = 8;
    private int checkoutHoy = 5;
    private int huespedesEnCasa = 25;
    private int llegadasHoy = 7;
    private int salidasHoy = 3;

    // Walk-in form fields
    private String walkinNombre;
    private String walkinApellidos;
    private String walkinEmail;
    private String walkinTelefono;
    private Integer walkinIdHabitacion;
    private int walkinDias = 1;

    @PostConstruct
    public void init() {
        habitacionService = new HabitacionService();
        reservaService = new ReservaService();
        clienteDAO = new ClienteDAO();
        
        cargarHabitaciones();
        calcularEstadisticas();
    }

    public void cargarHabitaciones() {
        habitaciones = habitacionService.obtenerTodas();
        
        // Si la base de datos tiene menos de las habitaciones esperadas, sembrar los registros iniciales
        if (habitaciones == null || habitaciones.size() < 10) {
            sembrarHabitacionesSiEsNecesario();
            habitaciones = habitacionService.obtenerTodas();
        }

        // Asignar responsables y fechas de limpieza para las habitaciones
        if (habitaciones != null) {
            for (Habitacion h : habitaciones) {
                if ("101".equals(h.getNumero())) {
                    h.setResponsable("Rosa Apaza");
                    h.setUltimaLimpieza("16/09/2025 08:30");
                } else if ("102".equals(h.getNumero())) {
                    h.setResponsable("Carlos Mamani");
                    h.setUltimaLimpieza("16/09/2025 10:00");
                } else if ("103".equals(h.getNumero())) {
                    h.setResponsable("María Condori");
                    h.setUltimaLimpieza("16/09/2025 09:15");
                } else if ("201".equals(h.getNumero())) {
                    h.setResponsable("-");
                    h.setUltimaLimpieza("15/09/2025 17:30");
                } else if ("202".equals(h.getNumero())) {
                    h.setResponsable("Ana Torres");
                    h.setUltimaLimpieza("16/09/2025 08:45");
                } else if ("203".equals(h.getNumero())) {
                    h.setResponsable("Luis Quispe");
                    h.setUltimaLimpieza("16/09/2025 09:50");
                } else if ("301".equals(h.getNumero())) {
                    h.setResponsable("Rosa Apaza");
                    h.setUltimaLimpieza("16/09/2025 08:20");
                } else if ("302".equals(h.getNumero())) {
                    h.setResponsable("-");
                    h.setUltimaLimpieza("15/09/2025 16:10");
                } else if ("401".equals(h.getNumero())) {
                    h.setResponsable("Carlos Mamani");
                    h.setUltimaLimpieza("16/09/2025 07:30");
                } else if ("402".equals(h.getNumero())) {
                    h.setResponsable("-");
                    h.setUltimaLimpieza("14/09/2025 11:00");
                }
            }
        }

        // Seleccionar habitación 102 por defecto (como en la imagen) o la primera
        if (habitacionSeleccionada == null && habitaciones != null && !habitaciones.isEmpty()) {
            for (Habitacion h : habitaciones) {
                if ("102".equals(h.getNumero())) {
                    habitacionSeleccionada = h;
                    break;
                }
            }
            if (habitacionSeleccionada == null) {
                habitacionSeleccionada = habitaciones.get(0);
            }
        }
    }

    private void sembrarHabitacionesSiEsNecesario() {
        String[][] datos = {
            {"101", "Simple", "120.00", "Habitación Simple Confort", "DISPONIBLE", "room-1.jpg"},
            {"102", "Simple", "120.00", "Habitación Simple Estándar", "EN_LIMPIEZA", "room-1.jpg"},
            {"103", "Doble", "180.00", "Habitación Doble Clásica", "DISPONIBLE", "room-2.jpg"},
            {"201", "Doble", "180.00", "Habitación Doble Superior", "PENDIENTE", "room-2.jpg"},
            {"202", "Doble", "180.00", "Habitación Doble Vista", "DISPONIBLE", "room-2.jpg"},
            {"203", "Doble", "180.00", "Habitación Doble King", "EN_LIMPIEZA", "room-2.jpg"},
            {"301", "Twin", "200.00", "Habitación Twin Ejecutiva", "DISPONIBLE", "room-3.jpg"},
            {"302", "Twin", "200.00", "Habitación Twin Balcón", "PENDIENTE", "room-3.jpg"},
            {"401", "Suite", "320.00", "Suite Presidencial Taipicala", "DISPONIBLE", "room-3.jpg"},
            {"402", "Suite", "320.00", "Suite Panorámica Lago", "MANTENIMIENTO", "room-3.jpg"}
        };

        for (String[] d : datos) {
            boolean existe = false;
            if (habitaciones != null) {
                for (Habitacion h : habitaciones) {
                    if (d[0].equals(h.getNumero())) {
                        existe = true;
                        break;
                    }
                }
            }
            if (!existe) {
                Habitacion h = new Habitacion();
                h.setNumero(d[0]);
                h.setTipo(d[1]);
                h.setPrecio(new BigDecimal(d[2]));
                h.setDescripcion(d[3]);
                h.setEstado(d[4]);
                h.setImagenUrl(d[5]);
                try {
                    com.taipicala.dao.HabitacionDAO dao = new com.taipicala.dao.HabitacionDAO();
                    dao.create(h);
                } catch (Exception ignored) {}
            }
        }
    }

    public void calcularEstadisticas() {
        try {
            List<Reserva> reservas = reservaService.obtenerTodas();
            if (reservas != null && !reservas.isEmpty()) {
                int activos = 0;
                for (Reserva r : reservas) {
                    if ("CONFIRMADA".equalsIgnoreCase(r.getEstado()) || "CHECK-IN".equalsIgnoreCase(r.getEstado())) {
                        activos++;
                    }
                }
                if (activos > 0) {
                    this.checkinHoy = activos;
                }
            }
        } catch (Exception ignored) {}
    }

    public void seleccionarHabitacion(Integer idHabitacion) {
        if (idHabitacion != null) {
            habitacionSeleccionada = habitacionService.buscarPorId(idHabitacion);
            if (habitacionSeleccionada != null) {
                // Preservar responsable si coincide
                for (Habitacion h : habitaciones) {
                    if (h.getIdHabitacion().equals(idHabitacion)) {
                        habitacionSeleccionada.setResponsable(h.getResponsable());
                        habitacionSeleccionada.setUltimaLimpieza(h.getUltimaLimpieza());
                        break;
                    }
                }
            }
        }
    }

    public void marcarComoLimpia(Integer idHabitacion) {
        if (idHabitacion == null && habitacionSeleccionada != null) {
            idHabitacion = habitacionSeleccionada.getIdHabitacion();
        }

        if (idHabitacion != null) {
            boolean exito = habitacionService.cambiarEstado(idHabitacion, "DISPONIBLE");
            if (exito) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "La habitación fue marcada como Limpia y Disponible en la Base de Datos."));
                cargarHabitaciones();
                seleccionarHabitacion(idHabitacion);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el estado de la habitación."));
            }
        }
    }

    public void marcarEnLimpieza(Integer idHabitacion) {
        if (idHabitacion != null) {
            habitacionService.cambiarEstado(idHabitacion, "EN_LIMPIEZA");
            cargarHabitaciones();
            seleccionarHabitacion(idHabitacion);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Habitación en proceso de limpieza."));
        }
    }

    public void registrarWalkin() {
        try {
            if (walkinNombre == null || walkinNombre.trim().isEmpty() || walkinIdHabitacion == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Campos Requeridos", "Debe ingresar el nombre del cliente y seleccionar una habitación."));
                return;
            }

            Cliente c = new Cliente();
            c.setNombre(walkinNombre);
            c.setApellidos(walkinApellidos != null ? walkinApellidos : "-");
            c.setEmail(walkinEmail != null && !walkinEmail.trim().isEmpty() ? walkinEmail : "walkin_" + System.currentTimeMillis() + "@taipicala.pe");
            c.setTelefono(walkinTelefono);

            Date entrada = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(entrada);
            cal.add(Calendar.DAY_OF_YEAR, walkinDias > 0 ? walkinDias : 1);
            Date salida = cal.getTime();

            boolean exito = reservaService.realizarReserva(c, walkinIdHabitacion, entrada, salida);
            if (exito) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Walk-in Exitoso", "Huésped registrado y Check-in realizado en la base de datos."));
                
                // Limpiar formulario
                walkinNombre = null;
                walkinApellidos = null;
                walkinEmail = null;
                walkinTelefono = null;
                walkinIdHabitacion = null;
                walkinDias = 1;

                cargarHabitaciones();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La habitación seleccionada no está disponible o ocurrió un error."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Base de Datos", e.getMessage()));
        }
    }

    // Getters and Setters
    public List<Habitacion> getHabitaciones() { return habitaciones; }
    public Habitacion getHabitacionSeleccionada() { return habitacionSeleccionada; }
    public void setHabitacionSeleccionada(Habitacion habitacionSeleccionada) { this.habitacionSeleccionada = habitacionSeleccionada; }

    public int getCheckinHoy() { return checkinHoy; }
    public int getCheckoutHoy() { return checkoutHoy; }
    public int getHuespedesEnCasa() { return huespedesEnCasa; }
    public int getLlegadasHoy() { return llegadasHoy; }
    public int getSalidasHoy() { return salidasHoy; }

    public String getWalkinNombre() { return walkinNombre; }
    public void setWalkinNombre(String walkinNombre) { this.walkinNombre = walkinNombre; }

    public String getWalkinApellidos() { return walkinApellidos; }
    public void setWalkinApellidos(String walkinApellidos) { this.walkinApellidos = walkinApellidos; }

    public String getWalkinEmail() { return walkinEmail; }
    public void setWalkinEmail(String walkinEmail) { this.walkinEmail = walkinEmail; }

    public String getWalkinTelefono() { return walkinTelefono; }
    public void setWalkinTelefono(String walkinTelefono) { this.walkinTelefono = walkinTelefono; }

    public Integer getWalkinIdHabitacion() { return walkinIdHabitacion; }
    public void setWalkinIdHabitacion(Integer walkinIdHabitacion) { this.walkinIdHabitacion = walkinIdHabitacion; }

    public int getWalkinDias() { return walkinDias; }
    public void setWalkinDias(int walkinDias) { this.walkinDias = walkinDias; }
}

