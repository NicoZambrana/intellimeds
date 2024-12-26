# IntelliMeds

**IntelliMeds** es una aplicación para Android desarrollada en Java que actúa como un pastillero inteligente. Permite a los usuarios gestionar sus medicamentos y establecer recordatorios para saber cuándo consumirlos.

---

## Funcionalidades Principales

### Gestión de Medicamentos

**1. Agregar Medicamento**
1. Ve a la sección "Medicamentos".
2. Pulsa el botón "+" para añadir un nuevo medicamento.
3. Introduce el nombre del medicamento, dosis, frecuencia y duración del tratamiento.
4. Selecciona la hora usando el selector de tiempo.
5. Marca los días de la semana en los que se debe tomar el medicamento.
6. Pulsa "Guardar".

**2. Editar Medicamento**
1. Selecciona el medicamento que deseas editar.
2. Pulsa el botón "Editar".
3. Realiza los cambios necesarios y pulsa "Guardar".

**3. Eliminar Medicamento**
1. Selecciona el medicamento que deseas eliminar.
2. Pulsa el botón "Eliminar".

### Recordatorios

La aplicación permite configurar recordatorios automáticos basados en los medicamentos agregados. Los recordatorios se muestran tanto como mensajes de alerta en la aplicación o como notificaciones en el dispositivo.

---

## Base de Datos

### Estructura de la Base de Datos

La base de datos SQLite se utiliza para almacenar la información de los medicamentos. La estructura de la tabla es la siguiente:

**Tabla Medicamentos:**
- id: Identificador único del medicamento.
- nombre: Nombre del medicamento.
- dosis: Dosis del medicamento.
- horario: Hora de toma (formato HH:mm).
- dias: Días de la semana (cadena separada por comas).

### Operaciones en la Base de Datos

1. **Agregar Medicamento:** Inserta un nuevo registro en la tabla de medicamentos.
2. **Obtener Medicamento por ID:** Recupera un medicamento específico usando su ID.
3. **Obtener Todos los Medicamentos:** Recupera todos los registros de medicamentos.
4. **Modificar Medicamento:** Actualiza un registro existente.
5. **Eliminar Medicamento:** Elimina un registro usando su ID.
6. **Obtener Medicamentos por Horario y Día:** Recupera medicamentos que coinciden con un horario y día específicos.
