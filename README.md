# DogApp

Agenda local de citas para mascotas, desarrollada como miniproyecto del curso Desarrollo de Aplicaciones para Dispositivos Móviles.

> Este README fija las reglas técnicas para mantener el proyecto alineado con la rúbrica y los ejemplos del curso. Describe el estado real de implementación; no marques como terminada una función que aún no esté implementada.

## Estado actual

- Existen una Activity anfitriona, cinco Fragments y sus layouts XML.
- Login implementa autenticación biométrica y navegación a Home retirando Login de la pila. Se comprobó manualmente que Atrás desde Home sale al escritorio.
- Home tiene encabezado, botón flotante hacia Nueva Cita y tarjetas con nombre, síntoma, turno e imagen mediante Glide. La selección navega a Detalle enviando `citaId`; Detalle consulta Room por ese ID y muestra nombre, turno, raza, síntoma, propietario, teléfono e imagen persistida.
- La lectura local está conectada mediante `CitaDao` → `CitaRepository` → `CitaViewModel` → LiveData → Home. Home consulta al entrar y al regresar mediante `onStart()`.
- Nueva Cita utiliza Data Binding, consulta razas y subrazas mediante Retrofit y permite autocompletar desde dos letras. El ViewModel valida la raza contra el catálogo y obtiene la URL de imagen antes de insertar mediante el Repository compartido. Solo tras confirmar la escritura regresa a Home.
- El ViewModel impide guardados simultáneos mediante `guardando: LiveData<Boolean>`. El botón observa ese estado y presenta texto e icono grises cuando está deshabilitado y blancos cuando está habilitado.
- Room, Glide, Retrofit y el convertidor Gson están configurados y utilizados. `webservice` contiene `DogApiService` y `RetrofitClient`; las URL y rutas se centralizan en `utils/Constants.kt`.
- HU-3 y sus ajustes están integrados en `main`. Home incorpora estados de carga, error con reintento y lista vacía mediante el PR #5, como mejora adicional del equipo.
- HU-4 implementa el detalle y la eliminación con confirmación. Tras completar la operación local regresa a Home, que vuelve a consultar Room. Editar navega a HU-5 enviando `citaId`.
- HU-5 implementa la precarga y edición de nombre, raza, propietario y teléfono. Conserva ID y síntoma, obtiene otra imagen solo si cambia la raza y regresa a Home tras comprobar que Room actualizó una fila. Esta descripción corresponde a la rama de HU-5; no implica que su PR ya esté integrado en `main`.
- Se reutilizan la entidad, el ViewModel, el Repository y el DAO compartidos, sin duplicar estas piezas.
- Los `.gitkeep` conservan paquetes vacíos en Git; pueden eliminarse cuando esos paquetes tengan archivos reales.

### Validación realizada y pendiente

- El responsable de HU-2 reportó compilación correcta y visualización de una cita insertada manualmente en la base local mediante Database Inspector, además de navegación hacia Detalle.
- Posteriormente, el responsable reportó compilación y funcionamiento del consumo de API y del flujo de creación, además de la verificación manual final de los ajustes de HU-3. Son comprobaciones manuales reportadas por el responsable, no pruebas automatizadas.
- Para los estados adicionales de Home, el responsable confirmó compilación y visualización del mensaje de lista vacía. La revisión estática comprobó la conexión de carga, error y reintento; su comportamiento visual sigue pendiente de prueba. No presentar esa revisión como una prueba de ejecución.
- Al documentar la verificación en el PR, distinguir creación normal, raza inválida, catálogo no disponible, fallo al obtener imagen, pulsaciones rápidas, rotación y persistencia. Un mensaje de catálogo no disponible no prueba el manejo del fallo de imagen: para este último se necesita haber cargado primero el catálogo.
- En el worktree aislado de auditoría de HU-4 se ejecutó `gradlew.bat assembleDebug` con resultado `BUILD SUCCESSFUL`, antes de los ajustes locales de colores. La compilación de esos ajustes en esta carpeta no se ha comprobado en esta revisión. Se revisó por código el recorrido del ID, la consulta por clave primaria, la presentación de campos, la eliminación por capas y la recarga de Home. No se ejecutaron pruebas unitarias ni instrumentadas.
- Sigue pendiente documentar la prueba manual con dos citas distintas: abrir cada detalle, comprobar sus campos e imagen, cancelar y confirmar el borrado y verificar que solo desaparece la cita seleccionada. Compilar no demuestra estos resultados en ejecución.
- En el worktree aislado de HU-5, `gradlew.bat assembleDebug` terminó con `BUILD SUCCESSFUL`. El responsable también reportó compilación y revisión visual. Después aplicó el ajuste de restauración del formulario en DogApp y reportó compilación correcta; se comprobó ese cambio mediante revisión de código. No se ejecutaron pruebas unitarias ni instrumentadas.
- Para HU-5, documentar por separado: edición sin cambiar raza, edición con nueva raza e imagen, conservación de ID y síntoma, ausencia de filas duplicadas, regreso a Home, errores de catálogo/imagen/actualización y conservación del borrador al rotar. La restauración tras recreación del proceso y la recepción tardía de la cita siguen pendientes de comprobación en ejecución.

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
- `utils`: constantes compartidas con una necesidad concreta; `Constants` define `NAME_BD`, `BASE_URL` y los endpoints de razas e imágenes.
- `webservice`: interfaz Retrofit y configuración del cliente. Sustituye al paquete vacío antes llamado `network`; no mantener ambos para la misma responsabilidad.

Base existente y piezas previstas:

| Paquete | Existente | Pendiente |
| --- | --- | --- |
| `ui.adapter` / `ui.viewholder` | `CitaAdapter`, `CitaViewHolder` con Glide | Reutilizar la presentación sin acceso directo a Room/API |
| `viewmodel` | `LoginViewModel`, `CitaViewModel` con consultas, guardado, detalle, eliminación y edición | Mantener contratos y documentar validaciones pendientes |
| `repository` | `CitaRepository` con consultas, inserción, actualización y eliminación; `RazaRepository` con razas e imágenes | Conservar el acceso a datos compartido |
| `model` | Entidad Room `Cita`; `DogBreedsResponse`, `DogImageResponse` | Conservar los contratos al ampliar las HU |
| `data` | `CitaDao` con consultas, inserción, `@Update` con filas afectadas y `@Delete`; `DogAppDB` | No duplicar entidad o base ni cambiar el esquema sin acuerdo |
| `utils` | `Constants` con nombre de base, URL y endpoints | Añadir constantes solo cuando sean necesarias |
| `webservice` | `DogApiService`, `RetrofitClient` | Reutilizar el cliente existente |

### Evidencia de clase y decisiones del equipo

Los proyectos de referencia se consultan en el entorno de clase; no forman parte de este repositorio:

- Clase 8: `data/InventoryDao.kt`, `repository/InventoryRepository.kt` y `viewmodel/InventoryViewModel.kt` muestran consultas suspendidas, Repository con `withContext(Dispatchers.IO)` y publicación con LiveData desde `viewModelScope.launch`. `view/fragment/HomeInventoryFragment.kt` observa con `viewLifecycleOwner`.
- Clase 9: bajo `app/src/main/java/com/example/clase8/`, `webservice/ApiService.kt`, `RetrofitClient.kt` y `ApiUtils.kt` definen el acceso Retrofit; `model/ProductModelResponse.kt` representa la respuesta; `InventoryRepository` consulta el servicio y `InventoryViewModel` publica sus resultados. `view/fragment/AddItemFragment.kt` muestra una URL con `Glide.with(...).load(...).into(...)`.
- Clase 8 usa `utils/Constants.kt` para `NAME_BD`; clase 9 también centraliza constantes de red. El paquete `webservice` adopta el nombre observado en clase 9.
- Son decisiones del equipo: separar `CitaRepository` y `RazaRepository` (el ejemplo de clase 9 usa un Repository para ambos accesos), devolver `List<Cita>` en lugar de `MutableList`, navegar por ID mediante callbacks del adaptador y cargar imágenes en el ViewHolder. No se presentan como exigencias del profesor.
- Clase 9 separa la creación del servicio en `ApiUtils`. DogApp la mantiene en `RetrofitClient.api`, reutilizando el cliente y el servicio del `object`; no necesita un tercer archivo para cubrir esa responsabilidad. Los modelos actuales usan `message` y `status`, iguales a las claves JSON, sin `@SerializedName`. La centralización de `BASE_URL` y los endpoints sí sigue la convención observada en `Constants` del profesor.
- La validación contra el catálogo, la protección contra guardados simultáneos, el selector visual del botón y la política de no guardar sin URL son decisiones del equipo para completar el comportamiento de DogApp; no se atribuyen al código del profesor.

## Cómo aplicar MVVM

Flujo de operaciones: **Fragment → ViewModel → Repository → Room/API**. La interfaz observa el estado publicado por el ViewModel mediante LiveData.

- El Fragment lee campos, registra eventos, muestra datos/mensajes y navega. No consulta directamente el DAO, la base de datos o Retrofit.
- El Adapter y el ViewHolder presentan las citas y notifican la selección al Fragment; no realizan operaciones de datos.
- `CitaViewModel` coordina las operaciones y validaciones y usa `viewModelScope`. Publica listas, estados de Home, `guardando` y resultados de creación, detalle, eliminación y edición mediante LiveData con MutableLiveData privados. HU-5 añade `citaActualizada` y `errorEditarCita`. No conserva referencias a Fragments, vistas o bindings.
- `CitaRepository` concentra las operaciones locales. `RazaRepository` concentra las consultas de razas e imágenes. Room y la red se usan de forma asíncrona, sin bloquear el hilo principal.
- Cada Fragment de citas obtiene su instancia de `CitaViewModel` con `by viewModels()`. No asumir que son la misma instancia: los datos actuales se recuperan desde Room a través del ViewModel y el Repository.
- Observar LiveData con `viewLifecycleOwner` y liberar la referencia al binding en `onDestroyView`.
- Solo mostrar éxito y navegar cuando Room confirme la escritura; ante un error de escritura, conservar el formulario y comunicar el fallo según el acuerdo de esa HU.
- Los estados de carga, error con reintento y lista vacía de Home se implementan como mejora adicional del equipo; no son criterios explícitos de HU-2. El indicador de progreso sigue el patrón observado en el ejemplo del profesor; distinguir vacío y error y ofrecer reintento son decisiones de DogApp.
- En HU-3, `guardando` protege la operación en el ViewModel antes de lanzar la corrutina y se libera en `finally`, también ante error o cancelación. El Fragment observa ese estado para habilitar el botón; un fallo de carga del catálogo no libera una inserción en curso. Esta protección cubre solicitudes simultáneas en la misma instancia del ViewModel, no identifica citas iguales creadas en momentos diferentes.

Antes de conectar las pantallas, acordar en la tarea compartida los nombres, parámetros y resultados de las operaciones del ViewModel y de los Repository. No crear contratos diferentes para cada Fragment.

### Estados adicionales de Home

- [CitaViewModel](app/src/main/java/com/example/dogapp/viewmodel/CitaViewModel.kt) activa `cargandoCitas` antes de consultar, limpia `errorCitas` y evita consultas simultáneas. Publica la lista si la consulta termina correctamente o un mensaje si falla; libera la carga en `finally` y respeta la cancelación de corrutinas.
- [HomeFragment](app/src/main/java/com/example/dogapp/ui/home/HomeFragment.kt) observa con `viewLifecycleOwner` y actualiza la presentación con prioridad carga → error → resultado. [fragment_home.xml](app/src/main/res/layout/fragment_home.xml) contiene el indicador, el mensaje vacío y el contenedor de error con Reintentar, además del RecyclerView y el botón de Nueva Cita.
- Una lista `null` significa que aún no se recibió un resultado; una lista vacía es una consulta correcta sin registros. El mensaje vacío solo aparece sin carga ni error y después de recibir una lista. Los fallos no se convierten en una lista vacía.
- Durante carga o error se oculta la lista anterior, sin borrar sus datos del ViewModel. Si hay resultados se muestra el RecyclerView. El encabezado y el botón para crear citas permanecen disponibles.
- Reintentar llama a `cargarCitas()` mediante el ViewModel; Home conserva la recarga en `onStart()`. No se añaden demoras artificiales ni cambios al DAO, Repository o esquema de Room.
- La consulta es local: desconectar Internet no simula un error de lectura de Room. El indicador puede durar muy poco si la consulta termina rápido. Las pruebas de carga, error y recuperación deben registrarse por separado de la prueba de lista vacía.

### Integración de HU-2 con HU-3

- Reutilizar [Cita](app/src/main/java/com/example/dogapp/model/Cita.kt), [CitaDao](app/src/main/java/com/example/dogapp/data/CitaDao.kt), [DogAppDB](app/src/main/java/com/example/dogapp/data/DogAppDB.kt), [CitaRepository](app/src/main/java/com/example/dogapp/repository/CitaRepository.kt) y [CitaViewModel](app/src/main/java/com/example/dogapp/viewmodel/CitaViewModel.kt). Ampliar estas piezas para el guardado; no crear entidades, bases o capas locales paralelas.
- La tabla actual es `citas` y el nombre de base es `dogapp_database`, definido en `Constants.NAME_BD`. Revisar conjuntamente cualquier cambio de esquema o versión de Room.
- El DAO y el Repository ofrecen `suspend fun obtenerCitas(): List<Cita>`, `suspend fun obtenerCitaPorId(citaId: Int): Cita?` y `suspend fun insertarCita(cita: Cita)` con retorno Unit. La inserción no modifica el ID del objeto recibido; Home obtiene el ID autogenerado al consultar Room.
- El ViewModel conserva `cargarCitas()` y `citas: LiveData<List<Cita>>`. Para HU-3 ofrece `cargarRazas()`, `guardarCita(cita: Cita)`, `consumirCitaGuardada()` y `consumirErrorNuevaCita()`. El Fragment consume los resultados antes de repetir acciones como navegar o mostrar el mismo mensaje tras recrear la vista.
- La lista se consulta por ID ascendente. `List` permite lectura sin exponer operaciones de modificación de la colección; no significa que la base sea inmutable. Insertar o quitar elementos de una colección en memoria no escribe en Room.
- Se utilizan consultas puntuales suspendidas, no Flow. La LiveData del ViewModel recibe el resultado de cada consulta; Home vuelve a consultar en `onStart()`. Cambiar a consultas observables requiere acuerdo del equipo.
- HU-3 implementa el servicio Retrofit, los modelos de respuesta y `RazaRepository`. El ViewModel coordina la obtención de la URL y el guardado local; el Fragment no accede directamente a la API ni al DAO.
- HU-3 persiste `imagenUrl` junto con la cita. Home recibe ese dato a través de Room y lo muestra con Glide; no solicita otra imagen aleatoria al mostrar cada tarjeta.
- El guardado correcto regresa al Home existente para que recargue sus citas. No se necesita enviar una lista entre Fragments ni compartir la misma instancia de ViewModel.
- El contrato detallado se mantiene en el documento compartido del equipo. La inserción, la consulta de detalle, la eliminación y la edición reutilizan las mismas piezas en esta rama. Sincronizar las firmas implementadas con el documento; las propuestas anteriores no son exigencias literales del profesor.

### Integración de HU-4 con Home y HU-5

- [DetalleFragment](app/src/main/java/com/example/dogapp/ui/detalle/DetalleFragment.kt) recibe `citaId: Int`, llama a `cargarCitaPorId(citaId)` y observa `citaSeleccionada: LiveData<Cita?>` con `viewLifecycleOwner`. Presenta todos los campos del registro recuperado y carga `imagenUrl` mediante Glide, con imagen de reemplazo durante la carga o ante un fallo de descarga.
- El borrado sigue `DetalleFragment` → `CitaViewModel.eliminarCita(cita)` → `CitaRepository.eliminarCita(cita)` → `CitaDao.eliminarCita(cita)`. Repository utiliza `withContext(Dispatchers.IO)` y el DAO declara `@Delete suspend fun eliminarCita(cita: Cita)` con retorno Unit. No se comprueba el número de filas afectadas.
- La confirmación previa es una opción permitida por el PDF. Al completar la operación, el ViewModel publica `citaEliminada = true`; el Fragment muestra el mensaje, llama a `consumirCitaEliminada()` y regresa mediante `navigateUp()`. Home recarga en `onStart()`; no se modifica manualmente la lista ni se comparte automáticamente el ViewModel entre pantallas.
- El botón Editar envía el mismo `citaId` mediante `action_detalleFragment_to_editarCitaFragment`. El destino declara el argumento entero y HU-5 consulta Room mediante su instancia de ViewModel y el Repository compartido.
- Limitaciones conocidas de Detalle: un ID ausente o igual a `-1` regresa a la pantalla anterior; si un ID consultado no existe, no hay respuesta visual específica. Los errores de consulta y eliminación se registran en Logcat, sin mensaje al usuario. Los botones de Detalle no tienen bloqueo de operación en curso, aunque sus listeners requieren una `citaActual` no nula.
- La presentación local utiliza fondo blanco, toolbar gris y el recurso `appointment_dark_blue` para el botón Editar. Estos ajustes de recursos no implican una prueba visual completada; sigue pendiente comprobar la apariencia y el margen interior de la imagen en ejecución.

### Edición de citas: HU-5

- [EditarCitaFragment](app/src/main/java/com/example/dogapp/ui/editarcita/EditarCitaFragment.kt) usa Data Binding, observa con `viewLifecycleOwner` y libera el binding en `onDestroyView()`. Precarga los cuatro campos desde `citaSeleccionada`; no accede al DAO ni a Retrofit.
- `formularioPrecargado` distingue la carga inicial del borrador restaurado. Se guarda en `onSaveInstanceState()` y se recupera al recrear la vista; Android conserva el texto mediante los IDs de los campos. El observador recalcula la habilitación del botón incluso si no debe repetir la precarga. Es un ajuste de ciclo de vida del equipo, no una exigencia literal del ejemplo del profesor.
- El ViewModel ofrece `actualizarCita(nombreMascota: String, raza: String, nombrePropietario: String, telefono: String)`. Valida campos y catálogo, bloquea operaciones simultáneas con `guardando` y usa `original.copy(...)` para conservar el ID y el síntoma.
- Si cambia la raza, reutiliza `RazaRepository.obtenerImagenRaza(...)`; un fallo o una URL vacía impiden actualizar. Si la raza no cambia, conserva `imagenUrl`. La validación actual requiere catálogo cargado incluso para editar únicamente nombre, propietario o teléfono; la edición sin conexión no está garantizada.
- DAO y Repository ofrecen `suspend fun actualizarCita(cita: Cita): Int`. El DAO usa `@Update` y el Repository `withContext(Dispatchers.IO)`. El ViewModel exige una fila afectada antes de publicar `citaActualizada = true`; no anuncia éxito si Room no encontró el registro.
- `consumirCitaActualizada()` y `consumirErrorEditarCita()` limpian los resultados atendidos. El Fragment muestra el éxito y ejecuta `popBackStack(R.id.homeFragment, false)`, retirando Editar y Detalle. Home consulta de nuevo en `onStart()`.
- Ante un error de actualización se conservan los campos y se muestra un mensaje; `finally` libera `guardando`. Los errores al cargar el catálogo se reciben mediante el estado existente `errorNuevaCita`. Si falla la consulta inicial de la cita o no existe, todavía no hay una respuesta visual específica y el botón permanece protegido por la ausencia de selección.
- El retorno de filas afectadas, las firmas concretas y la política de imagen son decisiones de DogApp compatibles con MVVM. El patrón del curso que se conserva es Fragment → ViewModel con LiveData/corrutinas → Repository → DAO/servicio; no se añaden capas ni dependencias.

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
- `raza`: autocompletado desde dos letras con el listado obtenido de la API. Antes de guardar, el ViewModel exige un catálogo disponible y una coincidencia completa, ignorando mayúsculas y espacios exteriores. Persiste el nombre del catálogo; escribir cualquier texto no lo convierte en una raza válida.
- `sintoma`: Solo duerme, No come, Fractura extremidad, Tiene pulgas, Tiene garrapatas o Bota demasiado pelo. El catálogo está centralizado en `R.array.sintomas`, dentro de [strings.xml](app/src/main/res/values/strings.xml).
- El botón de alta se habilita cuando nombre, raza, propietario y teléfono están diligenciados y no hay un guardado en curso. La pertenencia de la raza al catálogo se valida al intentar guardar. Si falta elegir síntoma, mostrar “Selecciona un síntoma”.
- El texto y el icono del botón cambian de gris deshabilitado a blanco habilitado mediante [guardar_cita_contenido.xml](app/src/main/res/color/guardar_cita_contenido.xml), manteniendo el texto bold y el fondo rosado.
- El turno mostrado es el ID autogenerado de Room, no la posición del RecyclerView. Al editar se conserva el ID y el síntoma; la HU de edición no incluye cambiar el síntoma.

## API de perros

- Listado de razas: `https://dog.ceo/api/breeds/list/all`
- Imagen aleatoria de una raza: `https://dog.ceo/api/breed/{breed}/images/random`
- Imagen aleatoria de una subraza: `https://dog.ceo/api/breed/{breed}/{subbreed}/images/random`

`RazaRepository` presenta las opciones como `raza subraza` cuando hay subrazas y elige la ruta correspondiente al obtener la imagen. `Constants` centraliza las rutas con sus parámetros `{raza}` y `{subraza}` usados por `@Path`.

La historia también menciona una imagen aleatoria global, que puede no corresponder a la raza elegida. Una sola persona consulta esta contradicción con el profesor. Mientras llega respuesta, usar el endpoint por raza para cumplir el criterio funcional de mostrar la raza seleccionada y dejar registrada esta interpretación.

Retrofit obtiene la respuesta JSON y el convertidor Gson la transforma al modelo de respuesta. Glide carga la imagen desde la URL; esta presentación no sustituye al consumo de API mediante Repository. Las dependencias están declaradas en [el catálogo de versiones](gradle/libs.versions.toml) y [el módulo app](app/build.gradle.kts), siguiendo las versiones de Retrofit/Gson y Glide observadas en clase 9.

**Política acordada para crear citas:** si falla la consulta de imagen o devuelve una URL vacía tras quitar espacios exteriores, no insertar. El ViewModel publica el error, conserva el formulario y libera el bloqueo para reintentar. Si la inserción en Room falla, informa del fallo de guardado y tampoco navega. Solo anuncia éxito después de finalizar la escritura.

Esta comprobación exige una URL no vacía; no verifica que la descarga posterior vaya a funcionar. Home usa Glide para descargar la imagen persistida. Una imagen de reemplazo o mensajes ante fallos de esa descarga siguen siendo mejoras pendientes de acuerdo, no criterios adicionales obligatorios de HU-2. Guardar una URL no garantiza que la imagen esté disponible sin Internet.

### Pendientes de mantenimiento

Los textos del formulario, el catálogo de síntomas y los mensajes de interfaz del Fragment y del ViewModel están centralizados en recursos. Los textos de diagnóstico permanecen en Logcat.
- Mantener sincronizado el documento de contratos compartido con esta política de imagen y las firmas implementadas. HU-4/HU-5 conservan sus propios criterios y responsabilidades.

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

Para probar Login se necesita autenticación biométrica disponible y configurada. La lectura de citas usa Room local; crear una cita requiere cargar el catálogo y obtener su URL de imagen mediante Internet. Editar requiere cargar el catálogo y, si cambia la raza, obtener otra URL. Ver la imagen con Glide también necesita acceso a su URL, salvo que ya esté disponible en caché. Esta rama implementa creación, detalle, eliminación y edición; consultar la sección de validación para distinguir implementación, pruebas reportadas y comprobaciones pendientes.

## Seguimiento

- [Repositorio GitHub](https://github.com/Sepul11/DogApp)
- [Proyecto Jira DOG](https://correounivalle-team-c1om7tv1.atlassian.net/jira/software/projects/DOG/list/)
