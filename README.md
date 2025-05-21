# NOTEDespistes - Aplicación de Bloc de Notas (Proyecto ABP)

NOTEDespistes es una aplicación de bloc de notas para Android desarrollada con Jetpack Compose y Kotlin. Permite a los usuarios crear, editar, gestionar y organizar sus notas de manera eficiente, con funcionalidades clave como la persistencia de datos, soporte para tema oscuro y una interfaz intuitiva.

## Características Implementadas

* **Gestión Completa de Notas:**
    * Crear nuevas notas con títulos y contenido.
    * Editar notas existentes.
    * Marcar notas como "Importantes" con un indicador visual (icono de estrella).
    * Numeración inteligente para títulos de notas por defecto (ej. "Nota X", intentando rellenar huecos si se borran notas intermedias).
* **Organización y Visualización:**
    * **Notas Activas:** Vista principal donde se muestran las notas que no están en la papelera.
    * **Papelera:** Las notas pueden ser enviadas a la papelera en lugar de eliminarse permanentemente al instante.
        * Restaurar notas individualmente desde la papelera a la lista de notas activas.
        * Eliminar notas permanentemente de forma individual desde la papelera.
        * Vaciar toda la papelera para eliminar todas las notas contenidas en ella.
    * Mensajes contextuales cuando no hay notas o la papelera está vacía.
* **Búsqueda y Filtrado:**
    * Barra de búsqueda para encontrar notas rápidamente.
    * Filtrado de búsqueda por **título** o por **contenido** de la nota.
    * Opción para limpiar la consulta de búsqueda.
* **Persistencia de Datos:**
    * Las notas, su estado (en papelera, importante) y el contador de IDs se guardan y persisten entre sesiones de la aplicación utilizando Jetpack DataStore (Preferences DataStore con serialización JSON para las notas).
    * El estado del tema (claro/oscuro) también se guarda y persiste usando DataStore.
* **Interfaz de Usuario (UI) y Experiencia de Usuario (UX):**
    * Interfaz construida enteramente con **Jetpack Compose**.
    * **Tema Claro y Oscuro:** Soporte completo para tema claro y oscuro, con cambio manual desde los ajustes de la aplicación y persistencia de la preferencia.
    * **Material Design 3:** Uso de componentes y principios de Material 3 para una apariencia moderna y consistente.
    * **Menú de Navegación Lateral (Navigation Drawer):** Permite cambiar entre "Notas Activas", "Papelera", y acceder a "Ajustes" y "Acerca de".
    * Diálogos de confirmación para acciones destructivas (enviar a papelera, vaciar papelera, eliminar permanentemente desde la papelera).
    * Indicadores visuales para notas importantes.
    * Adaptación de colores de los campos de texto (resaltado, cursor, bordes) para una buena visibilidad tanto en modo claro como oscuro.
* **Ajustes:**
    * Pantalla de Ajustes para cambiar la preferencia del tema (Claro/Oscuro).
    * Sección de ayuda básica dentro de los ajustes.
* **Acerca de:**
    * Pantalla "Acerca de" con información de la aplicación, versión y autores.
* **Internacionalización (i18n):**
    * Textos de la interfaz de usuario extraídos a archivos de recursos (`strings.xml`).
    * Soporte para múltiples idiomas: Español (por defecto), Inglés y Alemán.

## Tecnologías Utilizadas

* **Kotlin** como lenguaje principal de programación.
* **Jetpack Compose** para la construcción de la interfaz de usuario.
* **Material Design 3** para los componentes de UI y el diseño visual.
* **ViewModel de Jetpack** para la gestión del estado y la lógica de UI.
* **Jetpack DataStore (Preferences)** para la persistencia de datos local (notas y preferencias de usuario).
* **Kotlinx Serialization** para serializar y deserializar la lista de notas a formato JSON.
* **Coroutines de Kotlin** para operaciones asíncronas (guardado/carga de datos, etc.).

## Autores

* Jose David Cabeza
* David San Martin
* Juan Mañanes
