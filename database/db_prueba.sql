CREATE DATABASE IF NOT EXISTS hotel_taipicala CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel_taipicala;

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS habitacion (
    id_habitacion INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL, -- Sencilla, Doble, Twin, Suite
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'DISPONIBLE', -- DISPONIBLE, EN LIMPIEZA, OCUPADA, MANTENIMIENTO
    imagen_url VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_habitacion INT NOT NULL,
    fecha_entrada DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, CONFIRMADA, CHECK-IN, CANCELADA
    fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_habitacion) REFERENCES habitacion(id_habitacion) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS contacto (
    id_contacto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    asunto VARCHAR(150),
    mensaje TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================================
-- Datos iniciales del Hotel Taipicala
-- ====================================================================

-- 1. Habitaciones
INSERT INTO habitacion (id_habitacion, numero, tipo, precio, descripcion, estado, imagen_url) VALUES 
(1,  '101', 'Simple', 120.00, 'Habitación Simple - 1 huésped, vista al jardín', 'DISPONIBLE', 'room-1.jpg'),
(2,  '102', 'Simple', 120.00, 'Habitación Simple - 1 huésped, cómoda y luminosa', 'EN LIMPIEZA', 'room-1.jpg'),
(3,  '103', 'Doble',  180.00, 'Habitación Doble - 2 huéspedes, cama Queen', 'DISPONIBLE', 'room-2.jpg'),
(4,  '201', 'Doble',  180.00, 'Habitación Doble - 2 huéspedes, vista panorámica', 'DISPONIBLE', 'room-2.jpg'),
(5,  '202', 'Doble',  180.00, 'Habitación Doble - 2 huéspedes, TV 55", frigobar', 'DISPONIBLE', 'room-2.jpg'),
(6,  '203', 'Doble',  180.00, 'Habitación Doble - 2 huéspedes, cama King', 'OCUPADA', 'room-2.jpg'),
(7,  '301', 'Twin',   200.00, 'Habitación Twin - 2 camas individuales', 'DISPONIBLE', 'room-2.jpg'),
(8,  '302', 'Twin',   200.00, 'Habitación Twin - 2 camas, balcón exterior', 'OCUPADA', 'room-2.jpg'),
(9,  '305', 'Twin',   200.00, 'Habitación Twin ejecutiva', 'DISPONIBLE', 'room-2.jpg'),
(10, '401', 'Suite',  320.00, 'Suite de lujo - Jacuzzi privado, sala de estar', 'DISPONIBLE', 'room-3.jpg'),
(11, '402', 'Suite',  320.00, 'Suite presidencial - Terraza y vista al lago', 'MANTENIMIENTO', 'room-3.jpg'),
(12, '501', 'Suite',  320.00, 'Master Suite - Piso exclusivo con minibar premium', 'DISPONIBLE', 'room-3.jpg')
ON DUPLICATE KEY UPDATE numero=VALUES(numero);

-- 2. Clientes
INSERT INTO cliente (id_cliente, nombre, apellidos, email, telefono) VALUES
(1, 'Juan', 'Pérez Quispe', 'juan.perez@example.com', '951234567'),
(2, 'María', 'Condori', 'maria.condori@example.com', '952345678'),
(3, 'Carlos', 'Mamani', 'carlos.mamani@example.com', '953456789'),
(4, 'Ana', 'Torres', 'ana.torres@example.com', '954567890'),
(5, 'Luis', 'Quispe', 'luis.quispe@example.com', '955678901'),
(6, 'Carmen', 'Apaza', 'carmen.apaza@example.com', '956789012'),
(7, 'Empresa Andina', 'SAC', 'contacto@empresaandina.pe', '957890123'),
(8, 'Diego', 'Flores', 'diego.flores@example.com', '958901234')
ON DUPLICATE KEY UPDATE email=VALUES(email);

-- 3. Reservas
INSERT INTO reserva (id_reserva, id_cliente, id_habitacion, fecha_entrada, fecha_salida, estado, fecha_reserva) VALUES
(1, 1, 1,  '2025-09-10', '2025-09-12', 'CONFIRMADA', '2025-09-01 10:00:00'),
(2, 2, 6,  '2025-09-11', '2025-09-13', 'CHECK-IN',   '2025-09-02 11:30:00'),
(3, 3, 9,  '2025-09-12', '2025-09-15', 'PENDIENTE',  '2025-09-03 09:15:00'),
(4, 4, 4,  '2025-09-13', '2025-09-14', 'CONFIRMADA', '2025-09-04 14:20:00'),
(5, 5, 10, '2025-09-14', '2025-09-16', 'CANCELADA',  '2025-09-05 16:45:00'),
(6, 6, 2,  '2025-09-15', '2025-09-18', 'PENDIENTE',  '2025-09-06 08:50:00'),
(7, 7, 8,  '2025-09-16', '2025-09-18', 'CHECK-IN',   '2025-09-07 12:10:00'),
(8, 8, 12, '2025-09-18', '2025-09-20', 'CONFIRMADA', '2025-09-08 17:30:00')
ON DUPLICATE KEY UPDATE estado=VALUES(estado);


-- 4. Facturación
CREATE TABLE IF NOT EXISTS comprobante (
    id_comprobante INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NULL,
    id_cliente INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    fecha_emision DATE NOT NULL,
    tipo_cliente VARCHAR(50) DEFAULT 'Huésped',
    id_habitacion INT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    igv DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Emitido',
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva) ON DELETE SET NULL,
    FOREIGN KEY (id_habitacion) REFERENCES habitacion(id_habitacion) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_comprobante (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_comprobante INT NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_comprobante) REFERENCES comprobante(id_comprobante) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO comprobante (id_comprobante, id_cliente, id_reserva, id_habitacion, tipo, serie, numero, fecha_emision, tipo_cliente, subtotal, igv, total, estado, observaciones) VALUES
(1, 1, 1, 1, 'Boleta de Venta', 'B001', '000125', '2025-09-16', 'Huésped', 305.00, 54.90, 359.90, 'Emitido', 'Gracias por su preferencia.'),
(2, 2, 2, 6, 'Factura',         'F001', '000089', '2025-09-15', 'Empresa', 355.93, 64.07, 420.00, 'Emitido', 'Atención corporativa.'),
(3, 3, 3, 9, 'Boleta de Venta', 'B001', '000124', '2025-09-15', 'Huésped', 152.54, 27.46, 180.00, 'Anulado', 'Anulado por cambio de fecha.'),
(4, 4, 4, 4, 'Boleta de Venta', 'B001', '000123', '2025-09-14', 'Huésped', 220.34, 39.66, 260.00, 'Emitido', 'Pago en efectivo.')
ON DUPLICATE KEY UPDATE estado=VALUES(estado);

INSERT INTO detalle_comprobante (id_detalle, id_comprobante, descripcion, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 'Hospedaje - Habitación Simple', 2, 120.00, 240.00),
(2, 1, 'Desayuno Americano',            2, 25.00,  50.00),
(3, 1, 'Estacionamiento',               1, 15.00,  15.00),
(4, 2, 'Hospedaje - Habitación Doble',  2, 180.00, 360.00),
(5, 2, 'Room Service',                  1, 60.00,  60.00),
(6, 3, 'Hospedaje - Habitación Simple', 1, 180.00, 180.00),
(7, 4, 'Hospedaje - Habitación Doble',  1, 260.00, 260.00)
ON DUPLICATE KEY UPDATE subtotal=VALUES(subtotal);


-- 5. Pagos y Devoluciones
CREATE TABLE IF NOT EXISTS pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_comprobante INT NULL,
    id_cliente INT NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    tipo_operacion VARCHAR(30) DEFAULT 'Pago',
    referencia VARCHAR(100),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'Completado',
    observaciones TEXT,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_comprobante) REFERENCES comprobante(id_comprobante) ON DELETE SET NULL
) ENGINE=InnoDB;

INSERT INTO pago (id_pago, id_comprobante, id_cliente, monto, metodo_pago, tipo_operacion, referencia, fecha_pago, estado, observaciones) VALUES
(1, 1, 1, 359.90, 'Tarjeta VISA', 'Pago', 'OP-883921', '2025-09-16 10:45:00', 'Completado', 'Pago con tarjeta POS 1'),
(2, 2, 2, 420.00, 'Transferencia', 'Pago', 'TR-BCP-99231', '2025-09-15 14:20:00', 'Completado', 'Abono en cuenta corriente'),
(3, 3, 3, 180.00, 'Efectivo', 'Devolución', 'DEV-0012', '2025-09-15 16:30:00', 'Reembolsado', 'Devolución por cancelación de viaje'),
(4, 4, 4, 260.00, 'Yape / Plin', 'Pago', 'YP-773120', '2025-09-14 11:10:00', 'Completado', 'Pago mediante billetera digital')
ON DUPLICATE KEY UPDATE estado=VALUES(estado);
