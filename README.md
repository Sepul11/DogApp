# DogApp

Agenda local de citas para mascotas, desarrollada como miniproyecto del curso Desarrollo de Aplicaciones para Dispositivos Móviles.

> Este README fija las reglas técnicas para mantener el proyecto alineado con la rúbrica y los ejemplos del curso. Describe el estado real de implementación; no marques como terminada una función que aún no esté implementada.

## Estado actual

- Existen una Activity anfitriona, cinco Fragments y sus layouts XML.
- Login implementa autenticación biométrica y navegación a Home retirando Login de la pila. Se comprobó manualmente que Atrás desde Home sale al escritorio.
- Home tiene encabezado, botón flotante hacia Nueva Cita y tarjetas con nombre, síntoma, turno e imagen mediante Glide. La selección navega a Detalle enviando `citaId`; la pantalla de Detalle aún no presenta la cita.
- La lectura local está conectada mediante `CitaDao` → `CitaRepository` → `CitaViewModel` → LiveData → Home. Home consulta al entrar y al regresar mediante `onStart()`.
- Room, Data Binding, Glide, Retrofit y el convertidor Gson están configurados. Tener Retrofit disponible no significa que el consumo de API esté implementado: `webservice` conserva únicamente su `.gitkeep`.
- La creación, el detalle, la eliminación y la edición completos siguen pendientes de integración. No duplicar la base compartida para implementarlos.
- Los `.gitkeep` conservan paquetes vacíos en Git; pueden eliminarse cuando esos paquetes tengan archivos reales.

### Validación realizada y pendiente

- El responsable de HU-2 reportó compilación correcta y visualización de una cita insertada manualmente en la base local mediante Database Inspector, además de navegación hacia Detalle.
- Esta prueba valida lectura y presentación de datos locales; no demuestra el guardado desde HU-3 ni la consulta de la API.
- Pendiente: probar Crear → Home, persistencia tras reiniciar la app y recepción/uso del ID correcto en Detalle. No marcar estas pruebas como aprobadas por el solo hecho de compilar.

## Historias de usuario

- **HU 1:** Ingreso con autenticación biométrica.
- **HU 2:** Consulta de citas en el Home.
- **HU 3:** Creación de una cita.
- **HU 4:** Consulta del detalle y eliminación de una cita.
- **HU 5:** Edición de una cita existente.

## Reglas técnicas obligatorias

- Kotlin y layouts XML; la interfaz se implementa con Fragments.
- Una Activity anfitriona y Navigation Component para el flujo entre pantallas.
- MVVM con Repository.
- Room sobre SQLite para almacenar citas localmente.
- Retrofit para consultar razas e imágenes de perros.
- Data Binding para conectar Fragments con layouts XML, de manera uniforme y siguiendo el ejemplo de clase 8.
- LiveData y corrutinas siguiendo el ejemplo de clase 8.
- Jetpack Compose no forma parte del alcance acordado para este proyecto.

La rúbrica exige MVVM con Repository, Room/SQLite, consumo de API REST, Fragments y Navigation. Las HU especifican Retrofit para las razas y autenticación dactilar para el ingreso. Data Binding, LiveData y corrutinas siguen el ejemplo de clase 8 y la explicación de clase 9. Los nombres de paquetes, el contrato de `Cita`, el uso de una clase `CitaViewModel` y los argumentos por ID son reglas internas del equipo para aplicar ese patrón de forma uniforme.

Estas reglas son el estándar de trabajo del equipo. Los cambios de arquitectura, contrato de datos o dependencias deben discutirse y revisarse mediante PR antes de integrarse; no se deciden por separado en cada pantalla.

## Estructura del proyecto

Usar los paquetes ya creados bajo `app/src/main/java/com/example/dogapp`. Añadir las clases de cada tarea en su paquete correspondiente, sin duplicar estructuras. Los layouts permanecen en `app/src/main/res/layout` y el grafo en `app/src/main/res/navigation/nav_graph.xml`.

```text
com.example.dogapp/
├── MainActivity.kt
├── ui/
│   ├── login/
│   ├── home/
│   ├── nuevacita/
│   ├── detalle/
│   ├── editarcita/
│   ├── adapter/
│   └── viewholder/
├── viewmodel/
├── repository/
├── model/
├── data/
├── utils/
└── webservice/
```

Responsabilidades por paquete:

- `ui`: Fragments, adapters, ViewHolders y presentación XML.
- `viewmodel`: estado de pantalla y coordinación de operaciones.
- `repository`: acceso a Room y a la API.
- `data`: DAO y configuración Room.
- `model`: entidad `Cita` y respuestas de la API.
- `utils`: constantes compartidas con una necesidad concreta; actualmente `Constants.NAME_BD` define el nombre de la base.
- `webservice`: interfaz Retrofit y configuración del cliente. Sustituye al paquete vacío antes llamado `network`; no mantener ambos para la misma responsabilidad.

Base existente y piezas previstas:

| Paquete | Existente | Pendiente |
| --- | --- | --- |
| `ui.adapter` / `ui.viewholder` | `CitaAdapter`, `CitaViewHolder` | Validación con citas creadas por HU-3 |
| `viewmodel` | `LoginViewModel`, `CitaViewModel` con lectura de citas | Operaciones necesarias para las demás HU |
| `repository` | `CitaRepository` con consultas locales | Escrituras locales y `RazaRepository` |
| `model` | Entidad Room `Cita` | `RazasResponse`, `ImagenResponse`, según el JSON de la API |
| `data` | `CitaDao`, `DogAppDB` | Insertar, actualizar y eliminar según cada HU |
| `utils` | `Constants` con `NAME_BD` | Añadir constantes solo cuando sean necesarias |
| `webservice` | Paquete vacío con `.gitkeep` | `DogApiService`, `RetrofitClient` |

### Evidencia de clase y decisiones del equipo

Los proyectos de referencia se consultan en el entorno de clase; no forman parte de este repositorio:

- Clase 8: `data/InventoryDao.kt`, `repository/InventoryRepository.kt` y `viewmodel/InventoryViewModel.kt` muestran consultas suspendidas, Repository con `withContext(Dispatchers.IO)` y publicación con LiveData desde `viewModelScope.launch`. `view/fragment/HomeInventoryFragment.kt` observa con `viewLifecycleOwner`.
- Clase 9: bajo `app/src/main/java/com/example/clase8/`, `webservice/ApiService.kt`, `RetrofitClient.kt` y `ApiUtils.kt` definen el acceso Retrofit; `model/ProductModelResponse.kt` representa la respuesta; `InventoryRepository` consulta el servicio y `InventoryViewModel` publica sus resultados. `view/fragment/AddItemFragment.kt` muestra una URL con `Glide.with(...).load(...).into(...)`.
- Clase 8 usa `utils/Constants.kt` para `NAME_BD`; clase 9 también centraliza constantes de red. El paquete `webservice` adopta el nombre observado en clase 9.
- Son decisiones del equipo: separar `CitaRepository` y `RazaRepository` (el ejemplo de clase 9 usa un Repository para ambos accesos), devolver `List<Cita>` en lugar de `MutableList`, navegar por ID mediante callbacks del adaptador y cargar imágenes en el ViewHolder. No se presentan como exigencias del profesor.

## Cómo aplicar MVVM

Flujo de operaciones: **Fragment → ViewModel → Repository → Room/API**. La interfaz observa el estado publicado por el ViewModel mediante LiveData.

- El Fragment lee campos, registra eventos, muestra datos/mensajes y navega. No consulta directamente el DAO, la base de datos o Retrofit.
- El Adapter y el ViewHolder presentan las citas y notifican la selección al Fragment; no realizan operaciones de datos.
- `CitaViewModel` coordina las operaciones y validaciones, usa `viewModelScope` y expone LiveData. Actualmente publica `citas`; los resultados de escritura se añadirán al integrar las HU correspondientes. No conserva referencias a Fragments, vistas o bindings.
- `CitaRepository` concentra las operaciones locales. `RazaRepository` concentra las consultas de razas e imágenes. Room y la red se usan de forma asíncrona, sin bloquear el hilo principal.
- Cada Fragment de citas obtiene su instancia de `CitaViewModel` con `by viewModels()`. No asumir que son la misma instancia: los datos actuales se recuperan desde Room a través del ViewModel y el Repository.
- Observar LiveData con `viewLifecycleOwner` y liberar la referencia al binding en `onDestroyView`.
- Solo mostrar éxito y navegar cuando Room confirme la escritura; ante un error de escritura, conservar el formulario y comunicar el fallo según el acuerdo de esa HU.
- En HU-2 se aplazaron los indicadores de carga y los mensajes visuales de error/lista vacía: no son criterios explícitos de esa HU. Actualmente los errores de consulta se registran en Logcat, sin interfaz adicional. El indicador de progreso sí aparece en el ejemplo del profesor, pero no se exige por ello en Home.

Antes de conectar las pantallas, acordar en la tarea compartida los nombres, parámetros y resultados de las operaciones del ViewModel y de los Repository. No crear contratos diferentes para cada Fragment.

### Integración de HU-2 con HU-3

- Reutilizar [Cita](app/src/main/java/com/example/dogapp/model/Cita.kt), [CitaDao](app/src/main/java/com/example/dogapp/data/CitaDao.kt), [DogAppDB](app/src/main/java/com/example/dogapp/data/DogAppDB.kt), [CitaRepository](app/src/main/java/com/example/dogapp/repository/CitaRepository.kt) y [CitaViewModel](app/src/main/java/com/example/dogapp/viewmodel/CitaViewModel.kt). Ampliar estas piezas para el guardado; no crear entidades, bases o capas locales paralelas.
- La tabla actual es `citas` y el nombre de base es `dogapp_database`, definido en `Constants.NAME_BD`. Revisar conjuntamente cualquier cambio de esquema o versión de Room.
- El DAO y el Repository ya ofrecen `suspend fun obtenerCitas(): List<Cita>` y `suspend fun obtenerCitaPorId(citaId: Int): Cita?`. El ViewModel ofrece `cargarCitas()` y `citas: LiveData<List<Cita>>`. Conservar estas firmas al integrar HU-3.
- La lista se consulta por ID ascendente. `List` permite lectura sin exponer operaciones de modificación de la colección; no significa que la base sea inmutable. Insertar o quitar elementos de una colección en memoria no escribe en Room.
- Se utilizan consultas puntuales suspendidas, no Flow. La LiveData del ViewModel recibe el resultado de cada consulta; Home vuelve a consultar en `onStart()`. Cambiar a consultas observables requiere acuerdo del equipo.
- HU-3 implementa el servicio Retrofit, los modelos de respuesta y `RazaRepository`. El ViewModel coordina la obtención de la URL y el guardado local; el Fragment no accede directamente a la API ni al DAO.
- HU-3 persiste `imagenUrl` junto con la cita. Home recibe ese dato a través de Room y lo muestra con Glide; no solicita otra imagen aleatoria al mostrar cada tarjeta.
- El guardado correcto regresa al Home existente para que recargue sus citas. No se necesita enviar una lista entre Fragments ni compartir la misma instancia de ViewModel.
- El contrato detallado de escritura se mantiene en el documento compartido del equipo. Las operaciones de guardado aún no están implementadas en esta base.

## Navegación

- Login → Home únicamente tras autenticación biométrica correcta. Retirar Login de la pila para que Atrás desde Home salga de la app.
- Home → Nueva Cita. Guardar correctamente → Home con la lista actualizada.
- Home → Detalle → Editar Cita. Pasar `citaId` mediante Navigation/Bundle y recuperar la cita por ID.
- Atrás desde Nueva Cita o Detalle → Home. Atrás desde Editar Cita → Detalle.
- Editar correctamente o eliminar correctamente → Home, sin dejar un detalle obsoleto en la pila.
- No incluir confirmación de borrado como requisito obligatorio: el PDF la considera opcional.

## Reglas de contribución

1. Cada cambio debe relacionarse con una subtarea de Jira y sus criterios de aceptación.
2. Usar una rama corta por tarea, con responsable y clave Jira. Ejemplo: `feature/juan/DOG-17-room-citas`.
3. Escribir commits breves con tipo y componente: `feat(home): mostrar citas desde Room`, `fix(detalle): refrescar después de eliminar`, `docs(readme): documentar arquitectura`.
4. Integrar cambios a `main` mediante un Pull Request revisado por otro integrante.
5. Antes de marcar una tarea como terminada, ejecutar la app y comprobar los criterios de aceptación relacionados.
6. Quien presenta un cambio debe poder explicar el código y cómo cumple la historia de Jira.

Tipos de commit: `feat`, `fix`, `docs`, `chore` y `test`. El texto entre paréntesis indica la pantalla o componente: `login`, `home`, `nueva-cita`, `detalle`, `edicion`, `room`, `api`.

## Contrato de datos

La entidad local `Cita` debe tener un ID autogenerado, `nombreMascota`, `raza`, `nombrePropietario`, `telefono`, `sintoma` e `imagenUrl`. Guardar `telefono` como texto para conservar ceros iniciales. Persistir la URL de imagen para mostrar la misma imagen en Home y Detalle.

Los límites de los campos y el catálogo de síntomas deben seguir las historias de usuario de Jira y el PDF del proyecto.

- `nombreMascota`: texto, máximo 15 caracteres.
- `nombrePropietario`: texto, máximo 30 caracteres.
- `telefono`: solo dígitos, máximo 10 caracteres; no convertirlo a número.
- `raza`: autocompletado desde dos letras con el listado obtenido de la API.
- `sintoma`: Solo duerme, No come, Fractura extremidad, Tiene pulgas, Tiene garrapatas o Bota demasiado pelo. Centralizar este catálogo en recursos XML.
- El botón de alta se habilita cuando nombre, raza, propietario y teléfono están diligenciados. Si falta elegir síntoma, mostrar “Selecciona un síntoma”.
- El turno mostrado es el ID autogenerado de Room, no la posición del RecyclerView. Al editar se conserva el ID y el síntoma; la HU de edición no incluye cambiar el síntoma.

## API de perros

- Listado de razas: `https://dog.ceo/api/breeds/list/all`
- Imagen aleatoria de una raza: `https://dog.ceo/api/breed/{breed}/images/random`

La historia también menciona una imagen aleatoria global, que puede no corresponder a la raza elegida. Una sola persona consulta esta contradicción con el profesor. Mientras llega respuesta, usar el endpoint por raza para cumplir el criterio funcional de mostrar la raza seleccionada y dejar registrada esta interpretación.

Retrofit obtiene la respuesta JSON y el convertidor Gson la transforma al modelo de respuesta. Glide carga la imagen desde la URL; esta presentación no sustituye al consumo de API mediante Repository. Las dependencias están declaradas en [el catálogo de versiones](gradle/libs.versions.toml) y [el módulo app](app/build.gradle.kts), siguiendo las versiones de Retrofit/Gson y Glide observadas en clase 9.

**Pendiente de acordar antes de implementar el guardado:** definir qué ocurre si no se puede obtener una URL, si se permite persistir la cita sin ella y cómo se representaría ese caso en `imagenUrl`. La imagen de reemplazo y los mensajes ante fallos de descarga son mejoras pendientes de acuerdo, no criterios adicionales obligatorios de HU-2. Actualmente Home intenta cargar la URL con Glide, sin esos elementos visuales. No introducir valores ficticios ni confundir un fallo de imagen con un fallo de escritura en Room. Guardar una URL no garantiza que la imagen esté disponible sin Internet.

## Revisión de cada aporte

- Mantiene los paquetes, nombres y responsabilidades acordados.
- Usa Data Binding, XML y LiveData; no introduce acceso directo a datos desde la UI.
- Respeta el contrato de `Cita` y las reglas de navegación.
- Se comprobaron los criterios de la HU modificada y se documentaron los errores conocidos.
- El autor puede explicar el código y otro integrante revisó el PR.

El PDF oficial de historias y rúbrica es la fuente de requisitos; Jira organiza las tareas y su seguimiento. Estas comprobaciones son reglas de trabajo: este README no activa protecciones de rama ni verificaciones automáticas en GitHub.

## Ejecutar el proyecto

1. Abrir el repositorio en Android Studio.
2. Sincronizar Gradle con los archivos actuales del proyecto.
3. Ejecutar la aplicación en un emulador o dispositivo compatible con `minSdk 24`.

Para probar Login se necesita autenticación biométrica disponible y configurada. Para descargar imágenes desde sus URL se requiere conexión a Internet. La consulta local está implementada; el consumo de API y el CRUD completo siguen pendientes de integración.

## Seguimiento

- [Repositorio GitHub](https://github.com/Sepul11/DogApp)
- [Proyecto Jira DOG](https://correounivalle-team-c1om7tv1.atlassian.net/jira/software/projects/DOG/list/)
