# Informe técnico: generación de documentos judiciales por plantilla Word completa

**Sistema:** Sistema Judicial IA — Corte Superior de Justicia de Áncash
**Componentes intervenidos:** `ms-jurisia-judicial`, `ms-jurisia-consultaia`, `ms-jurisia-metricas`, `ms-jurisia-gateway`
**Fecha de implementación:** 26/09/2026
**Fecha del informe:** 29/09/2026
**Estado:** implementado, desplegado en el entorno local de desarrollo y validado funcionalmente por el usuario responsable

---

## Índice

1. [Resumen ejecutivo](#1-resumen-ejecutivo)
2. [Situación inicial](#2-situación-inicial)
3. [Objetivo](#3-objetivo)
4. [Decisiones de diseño](#4-decisiones-de-diseño)
5. [Arquitectura de la solución](#5-arquitectura-de-la-solución)
6. [Implementación por microservicio](#6-implementación-por-microservicio)
7. [Modelo de datos](#7-modelo-de-datos)
8. [Integración asíncrona (Kafka)](#8-integración-asíncrona-kafka)
9. [Reglas de negocio y algoritmos](#9-reglas-de-negocio-y-algoritmos)
10. [Migración del flujo por secciones](#10-migración-del-flujo-por-secciones)
11. [Ajustes realizados](#11-ajustes-realizados)
12. [Scripts generados y ejecutados](#12-scripts-generados-y-ejecutados)
13. [Pruebas y validaciones](#13-pruebas-y-validaciones)
14. [Hallazgos del entorno](#14-hallazgos-del-entorno)
15. [Pendientes y recomendaciones](#15-pendientes-y-recomendaciones)
16. [Anexo A: inventario de archivos](#anexo-a-inventario-de-archivos)
17. [Anexo B: endpoints](#anexo-b-endpoints)

---

## 1. Resumen ejecutivo

Se implementó un nuevo flujo de generación de documentos judiciales a partir de **plantillas Word completas** con variables `${nombre}`. Reemplaza, para los documentos que lo adopten, el mecanismo anterior basado en secciones. El flujo anterior se mantuvo **sin modificaciones** y ambos conviven.

Principales resultados:

- **Mantenimiento de plantillas sin programación:** el administrador carga el Word completo por un endpoint. Las variables se detectan automáticamente y se resuelven desde un catálogo del sistema, sin código Java por plantilla. En el flujo anterior, cada plantilla requería un método Java específico (35 métodos `ReemplazarSeccionesTemplateN`).
- **Una sola llamada a la IA por documento:** en el flujo anterior se hacía una llamada por sección. Las correcciones de la IA se validan antes de aplicarse y se conserva el formato del Word.
- **Tolerancia a fallos de la IA:** si Gemini no responde, el documento se entrega sin corrección y el estado se informa. En el flujo anterior, la falla de una sección abortaba la generación completa.
- **Métricas enriquecidas:** se creó un tópico Kafka nuevo y tablas nuevas de métricas, con datos de usuario, expediente, plantilla, variables, tokens reales de la IA y tiempos. Las generaciones fallidas también se registran.
- **Administración completa:** endpoints para tipos de documento, documentos, plantillas y variables, pensados para un frontend administrativo.
- **Migración ejecutada:** las **35 plantillas** del flujo anterior se migraron al nuevo flujo y generaron **76 plantillas** (una por documento), sin errores.
- **Gateway:** se verificó el paso de archivos grandes y se corrigió la configuración CORS.

---

## 2. Situación inicial

Estado del flujo de generación de documentos antes de la intervención (`DocumentoController` → `GenDocumentoServiceImpl` en `ms-jurisia-judicial`):

| Aspecto | Situación encontrada |
|---|---|
| Plantillas Word | 35 archivos `template_auto_NN.docx` dentro del jar (`src/main/resources/templates`), con marcadores `${sectionN}`. Cambiar una plantilla requería recompilar y desplegar |
| Contenido | Tabla `SectionTemplates` con el texto de cada sección, partido manualmente por quien mantiene las plantillas |
| Reemplazo de datos | `GenDocumentoServiceImpl.java` (3 048 líneas) con un `switch` de 35 métodos `ReemplazarSeccionesTemplateN` que aplicaban `String.replace` sobre cada sección |
| Datos sin fuente en el SIJ | Numerosos valores fijados en `"..."` (nombre del menor, monto de pensión, fecha de audiencia, número de resolución, entre otros) |
| IA | `POST /v1/gemini-chat/documento` de consultaia: una llamada a Gemini por cada sección con `isSendIA = 1`, en serie |
| Asociación | 35 templates asociados a **76 documentos**, porque la misma plantilla se repite por instancia (por ejemplo, `template_auto_01` en los documentos 1, 16 y 31 de las instancias 044, 701 y 702) |
| Métricas | Tópico `judicial-documentos-generado`. Se detectó que en `generarDocumentoToKafka` los DNI de demandantes y demandados se asignan de forma cruzada. Por indicación del usuario, el flujo anterior no se modificó y la corrección se aplicó solo en el nuevo |

---

## 3. Objetivo

1. Simplificar la gestión de plantillas, eliminando la descomposición manual en secciones.
2. Implementar el nuevo mecanismo en tablas, controllers y services nuevos, sin alterar lo existente.
3. Implementar en `ms-jurisia-consultaia` el procesamiento con IA del nuevo flujo.
4. Replicar y enriquecer en `ms-jurisia-metricas` la información de cada documento generado, vía Kafka, para reportes.
5. Exponer endpoints de administración de plantillas, documentos y tipos de documento para un frontend administrativo.
6. Migrar las plantillas del flujo anterior al nuevo, para comparar ambos flujos y migrar el frontend.

---

## 4. Decisiones de diseño

### 4.1 Enfoque adoptado

Se evaluó la alternativa de entregar el modelo completo de la plantilla a la IA para que redactara el documento con los datos del expediente. Se descartó como mecanismo principal por tres razones:

- **Fidelidad:** existe riesgo de que el modelo altere texto que debe permanecer literal (artículos, plazos, apercibimientos).
- **Formato:** la salida de la IA es texto; el formato del Word (márgenes, membrete, negritas, tabulaciones) tendría que reconstruirse.
- **Datos faltantes:** la IA no puede suplir los datos que no existen en el SIJ.

Se adoptó un **enfoque híbrido**:

- Reemplazo determinístico de variables sobre el Word completo.
- Uso de la IA solo para corregir la concordancia de los párrafos que reciben datos y, opcionalmente, para redactar bloques marcados explícitamente como de origen IA.

### 4.2 Decisiones tomadas con el usuario

| # | Tema | Decisión |
|---|---|---|
| 1 | Enfoque | Híbrido |
| 2 | Almacenamiento del `.docx` | MySQL, columna `LONGBLOB`, cargado por endpoint de administración |
| 3 | Variables sin dato en el SIJ | Se mantienen como `"..."`, igual que en el flujo anterior |
| 4 | Catálogos | Se reutilizan las tablas existentes `TipoDocumento` y `Documento`, sin crear tablas nuevas para ellos |
| 5 | Salidas | Se mantienen DOCX y HTML. No se implementa PDF ni el almacenamiento de los documentos generados (riesgo de saturación de disco) |
| 6 | Métricas | Tópico Kafka nuevo y tablas nuevas, con el mayor detalle disponible |
| 7 | Permisos | Mismo criterio que los endpoints de administración existentes: validación de sesión |
| 8 | DNIs cruzados | Corregidos en el flujo nuevo; el flujo anterior no se modifica |
| 9 | Envío a Kafka | No bloqueante (`try/catch`) |
| 10 | Migración | Plantillas **duplicadas por documento** (una plantilla por cada uno de los 76 documentos), sin modificar el diseño de tablas |
| 11 | Pruebas | Instancias paralelas con el código nuevo en puertos alternos, sin interrumpir las instancias de desarrollo en ejecución |

---

## 5. Arquitectura de la solución

```
Frontend
   │  Authorization (Keycloak) + SessionId
   ▼
ms-jurisia-gateway (8020) ── CORS: GET, POST, PUT, PATCH, DELETE, OPTIONS
   │
   ▼
ms-jurisia-judicial (8012)
   │ 1. Valida sesión ───────────────────────────► ms-jurisia-security (8010)
   │ 2. Documento, plantilla (.docx) y variables ◄─ MySQL JURISDB_JUDICIAL
   │ 3. Datos del expediente (solo lectura) ◄────── Sybase SIJ
   │ 4. Resuelve variables y reemplaza en el Word (docx4j)
   │ 5. UNA llamada: corrección + bloques IA ────► ms-jurisia-consultaia (8011)
   │                                                  │ Gemini (Vertex AI), JSON con esquema
   │                                                  │ auditoría: GeminiDocumentoPlantilla
   │ 6. Salida DOCX o HTML ◄──────────────────────────┘
   │ 7. Evento (no bloqueante) ──► Kafka: judicial-documentos-generado-v2
   ▼                                       │
Frontend                                   ▼
                              ms-jurisia-metricas (8013)
                              CabDocumentoPlantillaGenerado + DetDocumentoPlantillaVariable
```

La base Sybase del SIJ solo se lee mediante el método existente `ExpedienteService.getDataExpediente`; no se realizan escrituras.

---

## 6. Implementación por microservicio

### 6.1 `ms-jurisia-judicial`

Se siguió la separación de capas del módulo para MySQL (`controller` → `service/mysql` → `repository/mysql`), con los servicios externos en `service/externals`.

| Capa | Componente | Responsabilidad |
|---|---|---|
| Controller | `TipoDocumentoAdminController` (`/v1/admin/tipodocumento`) | Mantenimiento de tipos de documento |
| Controller | `DocumentoAdminController` (`/v1/admin/documento`) | Mantenimiento de documentos |
| Controller | `PlantillaDocumentoController` (`/v1/admin/plantillas`) | Plantillas, archivo `.docx`, variables, catálogo y migración |
| Controller | `DocumentoPlantillaController` (`/v1/documento-plantilla`) | Documentos generables y generación DOCX/HTML |
| Service | `TipoDocumentoAdminServiceImpl`, `DocumentoAdminServiceImpl` | Altas, bajas lógicas y validaciones de integridad |
| Service | `PlantillaDocumentoServiceImpl` | Carga del Word, detección y sincronización de variables, versionado, advertencias |
| Service | `GenDocumentoPlantillaServiceImpl` | Orquestación de la generación y del evento de métricas |
| Service | `MigracionPlantillaServiceImpl` | Migración del flujo por secciones |
| Service externo | `ConsultaiaPlantillaServiceImpl` | Cliente `RestClient` hacia `POST /v1/gemini-documento/procesar` |
| Repository | `PlantillaDocumentoRepository`, `PlantillaArchivoRepository`, `PlantillaVariableRepository` | Acceso a las tablas nuevas |
| Repository | `TipoDocumentoAdminRepository`, `DocumentoAdminRepository` | Consultas derivadas sobre las entidades existentes, en repositorios separados para no modificar las consultas nativas del flujo anterior |
| Motor | `DocxPlantillaProcessor` | Detección y reemplazo de variables sobre el Word (cuerpo, encabezados y pies) |
| Motor | `DocxHtmlConverter` | Conversión del Word generado a HTML para la vista web |
| Motor | `VariableSistema`, `ContextoExpediente`, `ResolucionVariable`, `ConstantesPlantilla` | Catálogo de variables, datos normalizados del expediente y constantes del flujo |

### 6.2 `ms-jurisia-consultaia`

Se siguió la separación de capas del módulo (`controller` → `service/business` → `dao` → `repository`).

| Componente | Responsabilidad |
|---|---|
| `ServiceGeminiDocumentoPlantillaController` (`POST /v1/gemini-documento/procesar`) | Endpoint asíncrono (`Callable`) con respuesta envuelta en `ApiResponse`, igual que los demás endpoints Gemini |
| `GeminiDocumentoPlantillaServiceImpl` | Arma el prompt, invoca Gemini con respuesta JSON y esquema fijo (`responseSchema`), valida las correcciones y registra la auditoría |
| `GeminiDocumentoPlantilla` + DAO + repositorio | Registro de cada llamada: request, response, tokens, tiempos, estado |

La invocación reutiliza el patrón técnico existente del módulo: SDK de Vertex AI con transporte REST, credenciales cacheadas, proxy configurable (`sij.proxy.google`) y reintentos por operación (`ReintentoUtil`). La configuración del modelo se lee de la tabla `Configurations` con el `serviceCode` `gemini_document_2`.

### 6.3 `ms-jurisia-metricas`

| Componente | Responsabilidad |
|---|---|
| `DocumentoPlantillaKafkaConsumerConfig` | Consumidor del tópico nuevo, en una clase separada para no modificar `KafkaConsumerConfig` (mismo criterio que `GeminiChatKafkaConsumerConfig`) |
| `DocumentoPlantillaConsumerComponent` | `@KafkaListener` del tópico `judicial-documentos-generado-v2` |
| `DocumentoPlantillaGeneradoServiceImpl` | Registro idempotente por `sessionUID` y consultas de reportes |
| DAO, repositorios y `CabDocumentoPlantillaGeneradoCustomRepoImpl` | Persistencia y filtros dinámicos con Criteria API |
| `DocumentoPlantillaGeneradoController` (`/v1/documento-plantilla-generado`) | Listado paginado, detalle de variables y resumen |

### 6.4 `ms-jurisia-gateway`

Único cambio: la configuración CORS en `ProjectSecurityConfig` (ver sección 11.1).

---

## 7. Modelo de datos

### 7.1 `JURISDB_JUDICIAL` (tablas nuevas)

| Tabla | Descripción | Campos principales |
|---|---|---|
| `PlantillaDocumento` | Metadata de la plantilla. FK a `Documento`; como máximo una plantilla no borrada por documento | `idPlantilla`, `idDocumento`, `codigo`, `nombreOut`, `descripcion`, `nombreArchivo`, `tamanioBytes`, `hashArchivo` (SHA-256), `version`, `corregirIA`, auditoría, `activo`, `borrado` |
| `PlantillaArchivo` | Binario `.docx` vigente, relación 1 a 1 con misma PK. Separado para que los listados no carguen el `LONGBLOB` | `idPlantilla`, `archivo` (`LONGBLOB`), auditoría |
| `PlantillaVariable` | Variables del Word y origen de su valor. FK a `PlantillaDocumento` | `idVariable`, `idPlantilla`, `nombre`, `descripcion`, `origen` (`SISTEMA`/`MANUAL`/`IA`), `campoSistema`, `valorDefecto`, `instruccionIA`, `detectada`, `orden`, auditoría, `activo`, `borrado` |

Las tablas existentes `TipoDocumento` y `Documento` se reutilizaron sin cambios de estructura.

### 7.2 `JURISDB_CONSULTATIONIA`

| Objeto | Descripción |
|---|---|
| `Configurations` (registro nuevo) | `serviceCode = gemini_document_2`, id 7, modelo `gemini-3.7-flash` (copiado de `gemini_document_1`), `temperature = 0.2`, `maxOutputTokens = 16384`, `roleSystem` propio |
| `GeminiDocumentoPlantilla` (tabla nueva) | Auditoría por llamada: `sessionUID`, `nUnico`, `idDocumento`, `idPlantilla`, `codigoPlantilla`, `versionPlantilla`, `userId`, `model`, `roleSystem`, `temperature`, `request` y `response` (`MEDIUMTEXT`), párrafos y bloques enviados y obtenidos, tokens (`prompt`, `candidates`, `thoughts`, `cached`, `total`), `finishReason`, fechas, `timeSeconds`, `ConfigurationsId` (FK), `status` |

### 7.3 `JURISDB_METRICS` (tablas nuevas)

| Tabla | Descripción |
|---|---|
| `CabDocumentoPlantillaGenerado` | Una fila por generación, exitosa o fallida; índice único por `sessionUID`. Agrupa los datos de: generación (`typedoc`, `status`, `mensajeError`); usuario (id, username, nombre, cargo, dependencia); expediente (sede, instancia, especialidad, materia, número, año, formato, ubicación, juez, especialista, estado, partes y DNIs, cantidad de partes); documento y plantilla (tipo, documento, código, versión, `corregirIA`, tamaños); variables (totales por tipo, sin valor, reemplazos); IA (estado, mensaje, párrafos y bloques, modelo, temperatura, tokens, `finishReason`); tiempos (SIJ, IA, total) |
| `DetDocumentoPlantillaVariable` | Detalle por variable de cada generación: `nombre`, `tipo` (`SIJ`, `CALCULADA`, `MANUAL`, `IA`, `NO_DEFINIDA`), `campoSistema`, `tieneValor`. No almacena el valor, para no replicar datos personales |

---

## 8. Integración asíncrona (Kafka)

| Elemento | Detalle |
|---|---|
| Tópico | `judicial-documentos-generado-v2` (creación automática; el clúster no desactiva `auto.create.topics.enable`) |
| Productor | `GenDocumentoPlantillaServiceImpl` (`ms-jurisia-judicial`) |
| Payload | `DocumentoPlantillaGeneradoToKafka` + lista de `VariableDocumentoToKafka`, con clases espejo en `ms-jurisia-metricas` |
| Mapeo de tipos | Header `__TypeId__` `pj.gob.pe.judicial.model.beans.DocumentoPlantillaGeneradoToKafka` → clase local de métricas |
| Momento de envío | En generaciones exitosas y fallidas (campo `status`) |
| Envío | No bloqueante: `try/catch` más registro asíncrono de fallos (`whenComplete`); una falla de Kafka no interrumpe la generación |
| Consumo | Idempotente: se descarta el mensaje si ya existe su `sessionUID` |
| Grupo de consumo | El existente (`jurisia-group`) |

---

## 9. Reglas de negocio y algoritmos

### 9.1 Resolución de variables

| Origen configurado | Valor resultante |
|---|---|
| `SISTEMA` | Catálogo `VariableSistema` (clave `campoSistema` o el propio nombre). Si el SIJ no trae dato, `valorDefecto`; si tampoco hay, `"..."` |
| `MANUAL` | `valorDefecto` si existe; si no, `"..."` |
| `IA` | Texto redactado por Gemini según `instruccionIA`. Sin instrucción o ante falla de la IA, `"..."` |
| Sin configuración | Si la variable existe en el catálogo, se resuelve como `SISTEMA`; si no, `"..."` (`NO_DEFINIDA`) |

**Catálogo del sistema** (`VariableSistema`), consultable por `GET /v1/admin/plantillas/variables-sistema`:

- **Datos del SIJ:** `juzgado`, `expediente`, `materia`, `juez`, `especialista`, `sede`, `especialidad`, `estado`, `ubicacion`, `numero_expediente`, `anio_expediente`, `incidente`, `fecha_inicio`, `tipo_expediente`, `demandantes`, `demandados`, `dni_demandantes`, `dni_demandados`.
- **Calculadas:** `ciudad`, `fecha_actual`, `fecha_dia_mes`, `fecha_anio`, `fecha_larga`, `fecha_larga_del`.
- **Compatibilidad con el flujo anterior:** `title.juzgado`, `title.expediente`, `title.materia`, `title.juez`, `title.especialista`, `title.demandante`, `title.demandado`, `top.ciudad`, `top.diamesletter`, `top.anioletter`.

### 9.2 Carga y versionado de plantillas

- Validaciones del archivo: extensión `.docx`, tamaño máximo de 20 MB y apertura correcta con docx4j.
- Al cargar o reemplazar el Word se detectan las variables (`${...}`) y se sincroniza `PlantillaVariable`:
  - se crean las variables nuevas;
  - las existentes conservan su configuración;
  - las ausentes quedan con `detectada = 0`.
- Cada reemplazo del archivo incrementa `version`, que viaja a métricas.
- El detalle de la plantilla devuelve **advertencias**: variables sin dato, de IA sin instrucción, apuntando a campos inexistentes o ausentes del Word.

### 9.3 Reemplazo sobre el Word

- El reemplazo se hace **a nivel de párrafo**, reconociendo una variable aunque Word la haya fragmentado en varios *runs* (tramos de texto con formato propio). El valor se escribe en el tramo donde empieza la variable, con su formato.
- Los valores se asignan mediante JAXB, lo que evita problemas de escape XML.
- Los saltos de línea se convierten en `w:br` y las tabulaciones en `w:tab`.
- Se procesan el cuerpo, los encabezados y los pies de página.

### 9.4 Procesamiento con IA

- **Una sola solicitud por documento**, con dos tareas:
  1. corrección de los párrafos que recibieron variables;
  2. redacción de los bloques de origen IA.
- Cada párrafo se envía partido en **segmentos**, uno por tramo de texto del Word, y la IA debe devolver la misma cantidad; así la corrección se aplica sin perder negritas ni subrayados.
- La IA devuelve solo los párrafos corregidos.
- Respuesta en JSON con esquema fijo (`responseMimeType = application/json` y `responseSchema`).
- El contexto enviado para los bloques de IA excluye DNIs y códigos internos.

**Validaciones previas a aplicar una corrección** (se descarta si ocurre alguna):

| Regla | Motivo del descarte |
|---|---|
| Id de párrafo inexistente o repetido | Corrección no correspondiente a lo enviado |
| Cantidad de segmentos distinta | Se perdería el formato |
| Segmento nulo | Respuesta incompleta |
| Marcadores `${...}` alterados | Se perdería una variable pendiente |
| Cantidad de `"..."` alterada | La IA no puede inventar datos inexistentes |
| Variación de longitud mayor a max(20 caracteres, 30 %) | Evita reescrituras del contenido |

**Degradación:** si la llamada a la IA falla, el documento se entrega sin corrección, con `estadoIA = ERROR` y el mensaje correspondiente. Los bloques de IA quedan en `"..."`.

### 9.5 Salidas

| Salida | Contenido de la respuesta |
|---|---|
| DOCX | Archivo con `Content-Disposition` (nombre de salida de la plantilla) y los headers `X-Estado-IA`, `X-Variables-Sin-Valor` y `X-Session-UID` |
| HTML | Contenido HTML (alineación, negrita, cursiva, subrayado, tabulaciones, saltos y tablas), la lista `variablesSinValor`, `estadoIA`, `mensajeIA` y `sessionUID` |

---

## 10. Migración del flujo por secciones

### 10.1 Método

Se implementó como endpoint reutilizable en otros entornos: `POST /v1/admin/plantillas/migrar-flujo-secciones?simular={true|false}`. Para cada documento con `codigoTemplate`:

1. **Armado del Word completo:** se toma el Word base `templates/{codigoTemplate}.docx` y cada `${sectionN}` se reemplaza con el contenido de su registro en `SectionTemplates`.
2. **Configuración de variables:** se aplica el archivo `migracion/mapeo_variables_secciones.json`. Este mapeo se generó analizando los 35 métodos `ReemplazarSeccionesTemplateN` y contiene 35 plantillas y **475 variables**, todas clasificadas.

   | Expresión en el flujo anterior | Configuración en el flujo nuevo |
   |---|---|
   | `getNombreInstancia`, `getFormato`, `getDescMateria`, `getNombreJuez`, `getNombreSecretario`, demandantes, demandados, DNIs | `SISTEMA` con el campo equivalente del catálogo |
   | `"Huaraz"` y los formatos de fecha | `SISTEMA` (`ciudad`, `fecha_dia_mes`, `top.anioletter`, `fecha_larga_del`) |
   | `"..."` o `""` | `MANUAL` sin valor por defecto |
   | Literales (`"UNICO"`, `"CORTE SUPERIOR DE JUSTICIA DE ANCASH"`, `"AÑO DE LA RECUPERACIÓN Y CONSOLIDACIÓN DE LA ECONOMÍA PERUANA"`) | `MANUAL` con `valorDefecto` igual al literal |

3. **Creación:** una plantilla por documento, con código `{codigoTemplate}_doc{idDocumento}`, `version = 1` y `corregirIA = 1`. Cada documento se procesa en una transacción independiente.
4. **Idempotencia:** se omiten los documentos que ya tienen plantilla.

### 10.2 Resultados

| Etapa | Resultado |
|---|---|
| Simulación previa (sin escritura) | 35 Word armados. En 34, todos los marcadores `${sectionN}` tienen su sección en la base de datos |
| Simulación por el endpoint | 76 documentos evaluados, 76 simulados, 0 omitidos, 0 errores |
| Ejecución real | **76 plantillas creadas**, 0 omitidas, 0 errores |
| Estado final | 76 plantillas, 76 archivos (2,00 MB en total), 1 013 variables configuradas |

### 10.3 Observaciones de la migración

| Plantillas | Observación | Tratamiento |
|---|---|---|
| `template_auto_19` (documentos 49 y 55) | El Word base contiene `${section09}` y la sección en base de datos es `section9`, por lo que en el flujo anterior esa sección no se insertaba | La migración empareja las secciones por número, ignorando ceros a la izquierda, y lo reporta en `seccionesEmparejadas` |
| `template_auto_31` a `template_auto_34` | El texto de las secciones usa `${title.materia}`, `${title.especialista}` y `${top.numero}` (y `${body.main.resolucion}`/`${body.main.resolucionfecha}` en la 31), pero los métodos del flujo anterior no los reemplazaban | En el flujo nuevo, `title.materia` y `title.especialista` se resuelven con datos del SIJ; las demás quedan como `"..."` |
| 7 plantillas con `title.fiscalia` | El flujo anterior la reemplazaba por cadena vacía | Queda como `MANUAL` (`"..."`); se puede asignar un valor por defecto desde la administración de variables |
| `template_auto_30` | Algunos marcadores de sección aparecen más de una vez en el Word base (27 reemplazos para 23 secciones) | Todas las ocurrencias se reemplazan |

---

## 11. Ajustes realizados

### 11.1 `ms-jurisia-gateway`

- **Verificación de la subida de archivos.** Se revisó el código fuente de Spring Cloud Gateway Server MVC 4.2.0: para las rutas del gateway, `GatewayMvcMultipartResolver` no parsea el cuerpo multipart y lo reenvía en streaming. Por eso el límite de 1 MB de `spring.servlet.multipart` no aplica al tráfico proxificado. Tampoco hay configurado un tiempo máximo de lectura (`read-timeout`) hacia los microservicios. No fue necesario modificar límites.
- **Corrección de CORS** en `ProjectSecurityConfig`:
  - se agregó `PATCH` a los métodos permitidos (lo usan los endpoints `/activation/{id}/{valor}`);
  - se agregaron a los headers expuestos `Content-Disposition`, `X-Estado-IA`, `X-Variables-Sin-Valor` y `X-Session-UID`, necesarios para que el navegador lea el nombre del archivo descargado y el estado de la generación.

### 11.2 Archivos existentes de `ms-jurisia-judicial` (solo agregados)

| Archivo | Cambio |
|---|---|
| `configuration/ConfigProperties.java` | Propiedad `pathProcessPlantillaGemini` |
| `application.yml` | Ruta `api.consultaia.post.processplantilla.path` y límites multipart (`max-file-size: 20MB`, `max-request-size: 25MB`) |
| `messages.properties` | Mensajes de validación de los inputs nuevos, en ASCII con escapes Unicode para no alterar la codificación del archivo |

### 11.3 Ajustes durante la implementación

| Ajuste | Motivo |
|---|---|
| Conversión de tabulaciones a `w:tab` (antes se reemplazaban por espacio) | Fidelidad de las secciones migradas, que contienen tabulaciones (por ejemplo, `EXPEDIENTE\t: ...`) |
| Nueva variable `fecha_larga_del` (`dd 'de' MMMM 'del' yyyy`) | Formato de fecha usado en 19 reemplazos del flujo anterior |
| Se eliminó la conversión a mayúsculas de las descripciones de tipos de documento y documentos | Evitar transformaciones de datos no solicitadas |
| Se eliminó el header `Access-Control-Expose-Headers` del controller de generación | Pasó a gestionarlo el gateway (sección 11.1), evitando duplicarlo |

---

## 12. Scripts generados y ejecutados

Todos se ejecutaron el 26/09/2026 en el servidor MySQL de desarrollo (contenedor `microservicio-mysql8`, puerto 3307), con `--default-character-set=utf8mb4`.

| Script | Base de datos | Contenido | Resultado |
|---|---|---|---|
| `ms-jurisia-judicial/src/main/resources/data/plantillas_docx.sql` | `JURISDB_JUDICIAL` | Tablas `PlantillaDocumento`, `PlantillaArchivo` y `PlantillaVariable`, con índices y FK | Ejecutado sin errores |
| `ms-jurisia-consultaia/src/main/resources/data/gemini_documento_plantilla.sql` | `JURISDB_CONSULTATIONIA` | Registro `gemini_document_2` en `Configurations` (modelo copiado de `gemini_document_1`) y tabla `GeminiDocumentoPlantilla` | Ejecutado sin errores (configuración id 7) |
| `ms-jurisia-metricas/src/main/resources/data/backup_7.sql` | `JURISDB_METRICS` | Tablas `CabDocumentoPlantillaGenerado` y `DetDocumentoPlantillaVariable`, con índices | Ejecutado sin errores |

Recursos de datos adicionales:

| Recurso | Descripción |
|---|---|
| `ms-jurisia-judicial/src/main/resources/migracion/mapeo_variables_secciones.json` | Mapeo de las 475 variables del flujo anterior, usado por el endpoint de migración |

**Orden de ejecución en otros entornos:**

1. Los tres scripts SQL.
2. Despliegue de los microservicios.
3. `POST /v1/admin/plantillas/migrar-flujo-secciones?simular=true` para revisar el resultado.
4. La misma llamada con `simular=false`.

---

## 13. Pruebas y validaciones

### 13.1 Compilación

Los cuatro microservicios compilan con Maven (`compile`) sin errores tras los cambios.

### 13.2 Pruebas técnicas aisladas

| Prueba | Resultado |
|---|---|
| Motor DOCX sobre `template_auto_01.docx` real | Detectó los 89 marcadores `${sectionN}` |
| Variable fragmentada en 3 tramos con distinto formato | Reemplazada correctamente |
| Valores con `&`, `<` y `>` | Escapados correctamente |
| Valores con salto de línea | Convertidos en `w:br` |
| Documento guardado y vuelto a abrir | Texto íntegro, sin variables pendientes |
| Contrato JSON judicial ↔ consultaia | Campos (incluido `nUnico`) serializados y deserializados correctamente en ambos sentidos |
| Reglas de validación de correcciones | Seis casos probados, cada uno con el resultado esperado (sección 9.4) |
| Contrato Kafka judicial → métricas (`JsonSerializer`/`JsonDeserializer` con mapeo de tipos) | Campos íntegros en la entidad de métricas, incluidos `nUnico`, `xFormato`, fechas, DNIs asociados correctamente y la lista de variables |

### 13.3 Pruebas integradas

Se ejecutaron sobre instancias paralelas con el código nuevo (consultaia 18011, judicial 18012, métricas 18013, gateway 18020), con sesión y token reales, contra MySQL, Redis, Kafka, Keycloak y Sybase de desarrollo. Las instancias se detuvieron al finalizar.

**Gateway:**

| Prueba | Resultado |
|---|---|
| Subida multipart de un `.docx` válido de 5 140 576 bytes | HTTP 201; 7 variables detectadas |
| Descarga del mismo archivo | HTTP 200; `Content-Disposition` presente; archivo idéntico (SHA-256) |
| Preflight CORS `OPTIONS` con `PATCH` | HTTP 200; `Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS` |
| `PATCH` de activación y desactivación | HTTP 200 |
| Acceso sin token | HTTP 401 |

Los registros de prueba creados (tipo de documento 21, documento 77 y plantilla 77) se eliminaron al finalizar.

**Comparación de ambos flujos** — expediente `nUnico 2025000120201153` (`00012-2025-0-0201-JP-FC-02`), documento 16 (AUTO ADMISORIO, `template_auto_01`, 89 secciones), salida DOCX:

| Indicador | Flujo anterior | Flujo nuevo |
|---|---|---|
| Tiempo total | 20,2 s | 26,0 s |
| Llamadas a Gemini | 2 (una por sección enviada) | 1 (14 párrafos) |
| Párrafos corregidos | — | 2 |
| Tokens | No registrados por el flujo anterior | 6 571 |
| Párrafos del documento | 26 | 26 (17 idénticos, 9 con diferencias) |
| Ante falla de Gemini | Error HTTP 500; no se entrega el documento | Documento entregado sin corrección (`estadoIA = ERROR`) |

Diferencias observadas en el texto:

- **Encabezado (6 párrafos):** el flujo anterior dejaba el carácter de tabulación dentro del texto; el nuevo lo representa como tabulación de Word (`w:tab`).
- **Formato markdown:** el flujo anterior insertó asteriscos (`**solicitada**`, `**la**`, `**el**`) dentro del documento.
- **Concordancia:** el flujo anterior mantuvo "el demandado" con una parte demandada de sexo femenino; el nuevo corrigió la concordancia ("la demandada", "la obligada", "el demandante", "la emplazada").
- **Citas:** el flujo anterior modificó la notación de citas legales ("675.°", comillas « »); el nuevo conservó el texto original.
- **Cambio de sentido a validar:** el flujo nuevo cambió "paterno filial" por "materno-filial" (ver sección 15).

**Métricas:**

| Prueba | Resultado |
|---|---|
| Registro de los eventos | 3 eventos registrados en `CabDocumentoPlantillaGenerado`, cada uno con 31 variables en `DetDocumentoPlantillaVariable`. Dos corresponden a intentos con la IA inaccesible (`estadoIA = ERROR`) y uno exitoso |
| Auditoría en consultaia | Las 3 llamadas registradas en `GeminiDocumentoPlantilla` (2 con `status = 2`, 1 con `status = 1`, `finishReason = STOP`) |
| Endpoints de reportes | `get-data` (con filtro), `{id}/variables` y `resumen` responden con los datos esperados |

### 13.4 Validación funcional

El usuario responsable probó los endpoints implementados y confirmó su correcto funcionamiento.

---

## 14. Hallazgos del entorno

| # | Hallazgo | Impacto |
|---|---|---|
| 1 | Desde el equipo de desarrollo no son alcanzables los proxies `proxycsjan.pj.gob.pe:3128` ni `proxycsjan2.pj.gob.pe:3128`, pero existe salida directa a `aiplatform.googleapis.com` | Con `sij.proxy.google.enabled: true`, consultaia no llega a Gemini desde ese equipo. Para las pruebas se desactivó el proxy **solo en la instancia de prueba**, por argumento de arranque, sin modificar la configuración |
| 2 | `/v3/api-docs` responde HTTP 500 en judicial, consultaia y métricas, incluidas las instancias previas a esta intervención | Swagger UI no disponible. No está relacionado con estos cambios |
| 3 | `ms-jurisia-consultaia/src/main/resources/application.yml` contiene credenciales en texto plano, lo que ya documenta el `CLAUDE.md` del workspace | Riesgo de exposición si el archivo se versiona o se comparte |
| 4 | El gateway se ejecuta en el puerto 8020, mientras el `CLAUDE.md` del workspace indica 8027 | Documentación desactualizada |
| 5 | El flujo anterior inserta formato markdown (`**`) generado por la IA en el documento Word | Defecto del flujo anterior; no se modificó |

---

## 15. Pendientes y recomendaciones

1. **Control de versiones:** no se realizaron commits. Los cambios quedan en el árbol de trabajo de cada repositorio (judicial y métricas en `master`; consultaia y gateway en `dev`). Antes de versionar, hay que separar los archivos de esta implementación de los que ya estaban modificados (ver Anexo A).
2. **Despliegue:** reiniciar judicial, consultaia, métricas y gateway con el nuevo código, y ejecutar los scripts de la sección 12 en cada entorno antes de la migración.
3. **Proxy por entorno:** definir `sij.proxy.google.enabled` según la red de cada entorno (hallazgo 1).
4. **Revisión jurídica de la IA:** en la prueba comparativa, la IA cambió "paterno filial" por "materno-filial", coherente con el sexo de la parte pero con impacto en el sentido. Se recomienda que un especialista revise una muestra de documentos antes de habilitar `corregirIA` de forma general; el indicador se puede desactivar por plantilla.
5. **Credenciales:** rotar las credenciales presentes en texto plano en `application.yml` de consultaia y trasladarlas a variables de entorno (hallazgo 3).
6. **Listado del flujo anterior:** los documentos creados desde la nueva administración también aparecen en `GET /v1/documento` del flujo anterior. Separarlos requiere un campo adicional en `Documento` y ajustar la consulta nativa existente; queda sujeto a decisión.
7. **Variables manuales:** 16 variables del AUTO ADMISORIO migrado quedan como `"..."` porque no provienen del SIJ. Se pueden asignar valores por defecto desde la administración de variables, o evaluar a futuro su ingreso desde el frontend.
8. **Swagger:** evaluar la actualización de `springdoc-openapi` para restablecer `/v3/api-docs` (hallazgo 2).

---

## Anexo A: inventario de archivos

### A.1 `ms-jurisia-judicial` (rama `master`)

**Nuevos:**

- `controller/`: `TipoDocumentoAdminController`, `DocumentoAdminController`, `PlantillaDocumentoController`, `DocumentoPlantillaController`
- `service/`: `GenDocumentoPlantillaService`
- `service/impl/`: `GenDocumentoPlantillaServiceImpl`
- `service/mysql/`: `TipoDocumentoAdminService`, `DocumentoAdminService`, `PlantillaDocumentoService`, `MigracionPlantillaService`
- `service/mysql/impl/`: `TipoDocumentoAdminServiceImpl`, `DocumentoAdminServiceImpl`, `PlantillaDocumentoServiceImpl`, `MigracionPlantillaServiceImpl`
- `service/externals/`: `ConsultaiaPlantillaService`, `impl/ConsultaiaPlantillaServiceImpl`
- `repository/mysql/`: `PlantillaDocumentoRepository`, `PlantillaArchivoRepository`, `PlantillaVariableRepository`, `TipoDocumentoAdminRepository`, `DocumentoAdminRepository`
- `model/mysql/entities/`: `PlantillaDocumento`, `PlantillaArchivo`, `PlantillaVariable`
- `model/beans/`: `DocumentoPlantillaGeneradoToKafka`, `VariableDocumentoToKafka`
- `utils/plantilla/`: `ConstantesPlantilla`, `ContextoExpediente`, `DocxHtmlConverter`, `DocxPlantillaProcessor`, `ResolucionVariable`, `VariableSistema`
- `utils/beans/plantilla/`: `ApiResponseProcesarPlantillaIA`, `BloquePlantillaIA`, `InputAdminDocumento`, `InputAdminTipoDocumento`, `InputPlantillaDocumento`, `InputPlantillaVariable`, `InputProcesarPlantillaIA`, `ParrafoPlantillaIA`, `ResponseDocumentoGenerable`, `ResponseDocumentoPlantillaHTML`, `ResponseMigracionPlantillas`, `ResponsePlantillaDetalle`, `ResponseProcesarPlantillaIA`, `ResponseVariableSistema`, `ResultadoDocumentoPlantilla`, `ResultadoMigracionDocumento`
- `resources/data/plantillas_docx.sql`
- `resources/migracion/mapeo_variables_secciones.json`
- `docs/ENDPOINTS_Flujo_Documentos_Plantilla.md`, `docs/INFORME_TECNICO_Flujo_Documentos_Plantilla.md`

**Modificados (solo agregados):** `configuration/ConfigProperties.java`, `resources/application.yml`, `resources/messages.properties`

### A.2 `ms-jurisia-consultaia` (rama `dev`)

**Nuevos:**

- `controller/ServiceGeminiDocumentoPlantillaController`
- `service/business/`: `GeminiDocumentoPlantillaService`, `impl/GeminiDocumentoPlantillaServiceImpl`
- `dao/mysql/`: `GeminiDocumentoPlantillaDAO`, `impl/GeminiDocumentoPlantillaDAOImpl`
- `repository/GeminiDocumentoPlantillaRepo`
- `model/entities/GeminiDocumentoPlantilla`
- `utils/beans/`: `ParrafoPlantilla`, `BloquePlantilla`, `inputs/InputDocumentoPlantilla`, `responses/ResponseDocumentoPlantilla`
- `resources/data/gemini_documento_plantilla.sql`

**Modificados por esta implementación:** ninguno.

> El repositorio registra modificaciones previas **ajenas a esta implementación**: `application.yml`, `docs/INFORME_TECNICO_Calificacion_Demandas_Gemini_RAG.md`, `ChatTestMultimodalController`, `DemandaTestControllerV2`, `GeminiExpedienteChats`, `data/gemini_chat.sql` y `data/gemini_document.sql`.

### A.3 `ms-jurisia-metricas` (rama `master`)

**Nuevos:**

- `configuration/DocumentoPlantillaKafkaConsumerConfig`
- `controller/DocumentoPlantillaGeneradoController`
- `service/business/`: `DocumentoPlantillaGeneradoService`, `impl/DocumentoPlantillaGeneradoServiceImpl`
- `service/kafka/consumer/DocumentoPlantillaConsumerComponent`
- `dao/`: `CabDocumentoPlantillaGeneradoDAO`, `DetDocumentoPlantillaVariableDAO` y sus `impl`
- `repository/`: `CabDocumentoPlantillaGeneradoRepo`, `DetDocumentoPlantillaVariableRepo`, `custom/CabDocumentoPlantillaGeneradoCustomRepo`, `custom/impl/CabDocumentoPlantillaGeneradoCustomRepoImpl`
- `model/entities/`: `CabDocumentoPlantillaGenerado`, `DetDocumentoPlantillaVariable`
- `model/beans/`: `DocumentoPlantillaGeneradoToKafka`, `VariableDocumentoToKafka`
- `utils/inputs/docplantilla/InputDocumentoPlantillaGenerado`
- `utils/responses/docplantilla/`: `ResponseResumenDocPlantilla`, `ResponseResumenItemDocPlantilla`
- `resources/data/backup_7.sql`

**Modificados por esta implementación:** ninguno.

> El repositorio registra modificaciones previas **ajenas a esta implementación**: `CabGeminiChat` y `DetailGeminiChat`.

### A.4 `ms-jurisia-gateway` (rama `dev`)

**Modificado:** `config/ProjectSecurityConfig.java` (CORS).

---

## Anexo B: endpoints

Se implementaron **33 endpoints**, documentados con ejemplos `curl` en `docs/ENDPOINTS_Flujo_Documentos_Plantilla.md`:

| Grupo | Ruta base | Cantidad | Microservicio |
|---|---|---|---|
| Tipos de documento | `/v1/admin/tipodocumento` | 6 | judicial |
| Documentos | `/v1/admin/documento` | 6 | judicial |
| Plantillas | `/v1/admin/plantillas` | 8 | judicial |
| Variables y catálogo | `/v1/admin/plantillas/{id}/variables`, `/variables-sistema` | 5 | judicial |
| Migración | `/v1/admin/plantillas/migrar-flujo-secciones` | 1 | judicial |
| Generación | `/v1/documento-plantilla` | 3 | judicial |
| IA (interno) | `/v1/gemini-documento/procesar` | 1 | consultaia |
| Reportes | `/v1/documento-plantilla-generado` | 3 | métricas |
