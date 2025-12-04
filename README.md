📔 Control de Stock y Ventas (Bebidas Don Jorge)

Este es un proyecto de aplicación móvil nativa para Android, desarrollado en Kotlin, diseñado para gestionar el inventario (stock) y registrar las ventas en un local de bebidas. Utiliza una base de datos local para operar sin conexión a Internet.

🎯 Objetivo del Proyecto

El objetivo principal es reemplazar los métodos manuales de gestión de inventario y ventas con una herramienta digital eficiente que permita:

    Alta de Productos: Registrar nuevos productos con precios de compra/venta y stock inicial.

    Control de Stock: Mantener el inventario de forma local (offline).

    Registro de Ventas: Interfaz rápida para sumar productos vendidos.

    Generación de Reportes (Pendiente).

💻 Tecnologías Utilizadas
Componente	Tecnología	Propósito
Plataforma	Android Nativo	Desarrollo de la aplicación móvil.
Lenguaje	Kotlin	Lenguaje de programación principal.
Base de Datos	Room Persistence Library	Base de datos local (SQLite) para stock y ventas.
Interfaz UI	Material Components & CardView	Diseño moderno tipo mosaico y campos de formulario.
Asincronía	Coroutines/LifecycleScope	Manejo de operaciones de BD en segundo plano (hilos de fondo).

🏗️ Estructura de la Base de Datos (Room)

La aplicación utiliza la base de datos AppDatabase con la siguiente entidad principal:
Entidad: Producto

Representa un artículo del inventario con todos los datos necesarios para el control de stock y cálculo de ganancia.
Campo	Tipo de Datos	Propósito
id	Int	Clave primaria auto-generada.
nombre	String	Nombre del producto (ej: Coca Cola 2.25Lts).
categoria	String	Clasificación (ej: Con Alcohol, Sin Alcohol).
stock	Int	Cantidad actual disponible.
costoCompra	Double	Precio pagado por unidad.
precioVenta	Double	Precio de venta al público.
rutaImagenLocal	String?	Ruta local de la imagen precargada (Opcional).
Componentes de Datos

    ProductoDao.kt: Interfaz DAO con funciones para insertarProducto, obtenerTodosProductos, y actualizarProducto.

    AppDatabase.kt: Clase Singleton que inicializa la base de datos Room.

🚀 Implementación de Funcionalidades Actuales
1. Pantalla Principal (MainActivity)

    Layout: Utiliza un GridLayout con CardView para mostrar 6 botones de mosaico (2 columnas).

    Navegación: La función onTileClicked() en MainActivity.kt maneja el clic y usa un Intent para ir a la pantalla correspondiente.

    Botón 'INGRESO': Navega a la actividad NewProduct.

2. Formulario de Ingreso (NewProduct.kt)

    Layout: activity_new_product.xml utiliza TextInputLayout para un formulario moderno y Spinner para selección de categorías y productos base. El botón de guardar utiliza el color temático del mosaico de Ingresos (@color/colorIngreso).

    Lógica de Guardado:

        La función guardarProducto() inicializa la base de datos Room.

        Validaciones: Se realizan validaciones estrictas para asegurar que el usuario:

            Seleccione un producto base.

            No deje campos vacíos.

            Ingrese números válidos (mayores a cero) en Stock, Costo y Venta.

        La inserción en la base de datos se ejecuta en un hilo de fondo (Dispatchers.IO) usando Coroutines para evitar congelar la interfaz de usuario.

📦 Pendientes / Próximos Pasos

    Listado de Productos: Crear la pantalla para ver, editar y eliminar los productos guardados en Room.

    Funcionalidad de Venta: Implementar la interfaz para registrar una venta y descontar el stock automáticamente.

    Cálculo de Ganancias: Mostrar reportes filtrables por día/mes.

    Imágenes: Implementar la lógica para mostrar las imágenes precargadas del producto base.
