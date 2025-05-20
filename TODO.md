| Nº | Requisito                                                                                         | Estado                          |
|----|---------------------------------------------------------------------------------------------------|---------------------------------|
| 1  | Entorno de desarrollo: Android Studio                                                             | ✔ Detectado                    |
| 2  | Lenguaje de programación: Kotlin                                                                  | ✔ Detectado                    |
| 3  | Controles (usar todos):                                                                           |                                 |
| 3a | Botones (Button)                                                                                  | ✔ Implementado                 |
| 3b | Campos de texto (EditText)                                                                        | ✔ Implementado                 |
| 3c | Casillas de verificación (CheckBox)                                                               | ✔ Implementado                 |
| 3d | Botones de selección (RadioGroup, RadioButton)                                                    | ✔ Implementado                 |
| 3e | Botones de activación/desactivación (ToggleButton/Switch)                                         | ✔ Implementado (Switch)        |
| 3f | Control de número (Spinner)                                                                       | ✔ Implementado                 |
| 3g | Botón con imagen (ImageButton)                                                                    | ✔ Implementado                 |
| 4  | Selectores de fechas y horas (DatePicker/TimePicker) => ADICIONALES                               | ❌ No implementado              |
| 5  | Mensajes por pantalla:                                                                            |                                 |
| 5a | Cortos y simples (Toast)                                                                          | ✔ Implementado                 |
| 5b | Notificación + acción posible (SnackBar) => ADICIONALES                                           | ❌ No implementado              |
| 5c | Confirmación/errores/formularios (Dialog, Sheet) => ADICIONALES                                   | ❌ No implementado              |
| 6  | Foco: Cambiar color de fondo de EditText al obtener/perder foco                                   | ✔ Implementado              |
| 7  | Gestor de colocación: ConstraintLayout:                                                           |                                 |
| 7a | Posicionamiento relativo                                                                          | ✔ Implementado                 |
| 7b | Márgenes                                                                                          | ✔ Implementado                 |
| 7c | Centrado                                                                                          | ✔ Implementado                 |
| 7d | Cadenas (chains) - Usar al menos 1 de los 4 tipos.                                                | ⚠️ No se evidencia uso explícito|
| 8  | Usar dos diseños según la orientación del dispositivo:                                            |                                 |
| 8a | Vertical (portrait) - layout/activity_main.xml                                                    | ✔ Implementado                 |
| 8b | Horizontal (landscape) - layout-land/activity_main.xml                                            | ✔ Implementado                 |
| 9  | Activar el sensor giroscopio para cambio automático de diseño según orientación del móvil         | ⚠️ Revisar `Orientation.kt` y `AndroidManifest.xml` |
| 10 | Usar un recurso dibujable (drawable) para cambiar el color de fondo, trazo y redondear esquinas   | ❌ No implementado              |
| 11 | Localizar la app a tres idiomas distintos (español + otros 2)                                     | ✔ Implementado (es, en, de)    |
