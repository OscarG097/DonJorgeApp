# 📔 Don Jorge - Control de Stock y Ventas

Aplicación móvil nativa para Android, desarrollada en **Kotlin**, diseñada para la gestión integral de inventario y registro de ventas en un local de bebidas. La aplicación está optimizada para funcionar de manera **offline** utilizando una base de datos local.

---

## 🚀 Funcionalidades Principales

### 1. Gestión de Inventario (Stock)
- **Alta de Productos:** Registro de nuevos artículos con nombre, stock inicial, costo de compra y precio de venta.
- **Edición Rápida:** Modificación de datos existentes (precios, cantidades, nombres) desde la lista de productos.
- **Listado en Tiempo Real:** Visualización completa del stock con actualización automática mediante `Flows`.

### 2. Registro de Ventas Avanzado
- **Carrito de Compras:** Interfaz intuitiva para sumar múltiples productos a una venta.
- **Buscador Integrado:** Filtro rápido por nombre para agilizar la atención al cliente.
- **Gestión de Combos:** Lógica inteligente que permite vender "combos" (ej. Fernet + Coca) descontando automáticamente el stock de cada ingrediente individual.
- **Métodos de Pago:** Soporte para registro de ventas en **Efectivo** y **Transferencia**.

### 3. Experiencia de Usuario (UX)
- **Formato de Moneda Automático:** Implementación de `CurrencyTextWatcher` para formatear precios en pesos argentinos ($ 0,00) mientras el usuario escribe.
- **Validaciones Inteligentes:** El sistema impide vender productos sin stock suficiente y valida que los campos de precio sean correctos.
- **Interfaz Moderna:** Uso de Material Design, CardViews y Diálogos de confirmación personalizados.

---

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología | Propósito |
| :--- | :--- | :--- |
| **Lenguaje** | Kotlin | Desarrollo robusto y moderno. |
| **Base de Datos** | Room (SQLite) | Persistencia de datos local y segura. |
| **Asincronía** | Coroutines & Flow | Operaciones fluidas sin bloquear la interfaz. |
| **Arquitectura** | MVVM (Lite) | Separación de lógica de datos y UI. |
| **UI** | Material 3 & Jetpack | Componentes visuales modernos y responsivos. |

---

## 🏗️ Estructura de Datos (Room)

La base de datos se compone de tres entidades principales:
- **Producto:** Información base del artículo (Nombre, Stock, Precios).
- **Sales (Ventas):** Historial de transacciones con fecha, monto y método de pago.
- **ComboDetail:** Definición de los ingredientes que componen un producto tipo "Combo".

---

## 📅 Próximos Pasos

- [ ] **Reportes y Estadísticas:** Implementar una pantalla de resumen diario/mensual con cálculos de ganancia neta.
- [ ] **Alertas de Stock Bajo:** Notificaciones visuales cuando un producto está por agotarse.
- [ ] **Copia de Seguridad:** Exportación de la base de datos a Excel o Google Drive.
