# DogApp

Agenda local de citas para mascotas, desarrollada como miniproyecto del curso Desarrollo de Aplicaciones para Dispositivos Móviles.

> Este README fija las reglas técnicas para mantener el proyecto alineado con la rúbrica y los ejemplos del curso. Describe el estado real de implementación; no marques como terminada una función que aún no esté implementada.

## Estado actual

- Existen una Activity anfitriona, cinco Fragments y sus layouts XML.
- El grafo de Navigation registra Login, Home, Nueva Cita, Detalle y Editar Cita, pero aún falta conectar el flujo con acciones y eventos.
- Los paquetes de la arquitectura están preparados con archivos vacíos `.gitkeep` para que Git conserve la estructura. Estos archivos no contienen código; pueden eliminarse cuando el paquete tenga archivos reales.
- Las pantallas son una base inicial. La autenticación biométrica, el CRUD de citas, Room, Retrofit, el ViewModel y la conexión mediante Data Binding están pendientes de implementación/configuración.

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
└── network/
```

Responsabilidades por paquete:

- `ui`: Fragments, adapters, ViewHolders y presentación XML.
- `viewmodel`: estado de pantalla y coordinación de operaciones.
- `repository`: acceso a Room y a la API.
- `data`: DAO y configuración Room.
- `model`: entidad `Cita` y respuestas de la API.
- `network`: interfaz Retrofit y configuración del cliente.

Clases previstas, todavía pendientes de implementación:

| Paquete | Clases |
| --- | --- |
| `ui.adapter` / `ui.viewholder` | `CitaAdapter`, `CitaViewHolder` |
| `viewmodel` | `CitaViewModel` |
| `repository` | `CitaRepository`, `RazaRepository` |
| `model` | `Cita`, `RazasResponse`, `ImagenResponse` |
| `data` | `CitaDao`, `DogAppDB` |
| `network` | `DogApiService`, `RetrofitClient` |

## Cómo aplicar MVVM

Flujo de operaciones: **Fragment → ViewModel → Repository → Room/API**. La interfaz observa el estado publicado por el ViewModel mediante LiveData.

- El Fragment lee campos, registra eventos, muestra datos/mensajes y navega. No consulta directamente el DAO, la base de datos o Retrofit.
- El Adapter y el ViewHolder presentan las citas y notifican la selección al Fragment; no realizan operaciones de datos.
- `CitaViewModel` coordina las operaciones y validaciones, usa `viewModelScope` y expone LiveData de datos, carga y resultados. No conserva referencias a Fragments, vistas o bindings.
- `CitaRepository` concentra las operaciones locales. `RazaRepository` concentra las consultas de razas e imágenes. Room y la red se usan de forma asíncrona, sin bloquear el hilo principal.
- Cada Fragment de citas obtiene su instancia de `CitaViewModel` con `by viewModels()`. No asumir que son la misma instancia: los datos actuales se recuperan desde Room a través del ViewModel y el Repository.
- Observar LiveData con `viewLifecycleOwner` y liberar la referencia al binding en `onDestroyView`.
- Mostrar errores entendibles. Solo mostrar éxito y navegar cuando Room confirme la escritura; ante un error de escritura, conservar el formulario.

Antes de conectar las pantallas, acordar en la tarea compartida los nombres, parámetros y resultados de las operaciones del ViewModel y de los Repository. No crear contratos diferentes para cada Fragment.

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

Si falla la carga de una imagen, mostrar una imagen de reemplazo y un mensaje. **Pendiente de cerrar antes de implementar el guardado:** definir si se permite persistir una cita sin URL y cómo se representa ese caso en `imagenUrl`. No introducir valores ficticios ni confundir un fallo de imagen con un fallo de escritura en Room. Guardar una URL no garantiza que la imagen esté disponible sin Internet.

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

Las funciones de Room, Retrofit y biometría se irán habilitando durante el desarrollo; este README no afirma que ya estén implementadas.

## Seguimiento

- [Repositorio GitHub](https://github.com/Sepul11/DogApp)
- [Proyecto Jira DOG](https://correounivalle-team-c1om7tv1.atlassian.net/jira/software/projects/DOG/list/)
