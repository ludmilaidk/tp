package entidades;
import java.time.LocalDate;
import java.util.*;

public class HomeSolution implements IHomeSolution {
    private HashMap<Integer, Empleado> empleados;
    private HashMap<Integer, Proyecto> proyectos;
    private HashMap<Integer, Cliente> clientes;
    private int contadorDeProyectos;
    private int contadorLegajo;

    public HomeSolution() {
        this.empleados = new HashMap<>();
        this.proyectos = new HashMap<>();
        this.clientes = new HashMap<>();
        this.contadorDeProyectos = 1;
        this.contadorLegajo = 1;
    }

    /**
     * Registra un empleado con un nombre y un valor base por hora.     *
     *
     * @param nombre Nombre del empleado.
     * @param valor  Valor de trabajo del empleado.
     * @throws IllegalArgumentException Si el nombre es nulo o vacío, o el valor es negativo.
     */
    public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("El valor debe ser mayor que 0.");
        }
        int nuevoLegajo = generarLegajoEmpleado();
        // Crear empleado contratado (por hora)
        Empleado empleado = new Contratado(nombre, nuevoLegajo, false, 0, valor);
        empleados.put(empleado.getLegajo(), empleado);
    }

    /**
     * Registra un empleado con nombre, valor y categoría.     *
     *
     * @param nombre    Nombre del empleado.
     * @param valor     Valor de trabajo del empleado.
     * @param categoria Categoría del empleado (por ejemplo, "Junior", "Senior").
     * @throws IllegalArgumentException Si alguno de los parámetros es inválido.
     */
    public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        if (valor <= 0) {
            throw new IllegalArgumentException("El valor debe ser mayor que 0.");
        }

        if (categoria == null || categoria.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar una categoría.");
        }

        categoria = categoria.toUpperCase();
        if (!categoria.equals("EXPERTO") &&
                !categoria.equals("INICIAL") &&
                !categoria.equals("TECNICO")) {
            throw new IllegalArgumentException("Categoria de empleado no válido: " + categoria);
        }


        // Si es de planta permanente
        Empleado empleado = new PlantaPermanente(nombre, generarLegajoEmpleado(), false, 0, valor, categoria);
        empleados.put(empleado.getLegajo(), empleado);
    }

    // ============================================================
    // REGISTRO Y GESTIÓN DE PROYECTOS
    // ============================================================

    /**
     * Registra un nuevo proyecto en el sistema.
     *
     * @param titulos     Títulos de las tareas del proyecto.
     * @param descripcion Descripciones de cada tarea.
     * @param dias        Días estimados de duración de cada tarea.
     * @param domicilio   Domicilio donde se desarrollará el proyecto.
     * @param cliente     Datos del cliente (nombre, mail, teléfono).
     * @param inicio      Fecha de inicio del proyecto (formato YYYY-MM-DD).
     * @param fin         Fecha de finalización estimada (formato YYYY-MM-DD).
     * @throws IllegalArgumentException Si los datos son inconsistentes o faltan.
     */
    public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias,
                                  String domicilio, String[] cliente, String inicio, String fin)
            throws IllegalArgumentException {
        // validaciones básicas
        if (titulos == null || descripcion == null || dias == null || cliente == null)
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        if (titulos.length != descripcion.length || titulos.length != dias.length)
            throw new IllegalArgumentException("Arrays de tareas desalineados.");

        // convertir fechas con try-catch
        LocalDate fechaInicio;
        LocalDate fechaFin;
        try {
            fechaInicio = LocalDate.parse(inicio);
            fechaFin = LocalDate.parse(fin);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto. Debe ser YYYY-MM-DD.");
        }

        if (fechaFin.isBefore(fechaInicio))
            throw new IllegalArgumentException("La fecha estimada de finalización no puede ser anterior a la de inicio.");

        // crear las tareas
        HashMap<String, Tarea> tareas = new HashMap<>();
        for (int i = 0; i < titulos.length; i++) {
            tareas.put(titulos[i], new Tarea(titulos[i], descripcion[i], (int) dias[i], 0, false, null, 0, 0));
        }

        // crear el proyecto
        Proyecto proyecto = new Proyecto(
                generarCodigoProyecto(), // método que genera un código único
                Estado.pendiente,
                domicilio,
                tareas,
                fechaInicio,
                fechaFin,
                null, // fechaFinalización real todavía no existe
                0,
                0,
                cliente[1] // mail para identificar al cliente
        );

        // guardar el proyecto en tu colección
        proyectos.put(proyecto.getCodigo(), proyecto);
    }

    // ============================================================
    // ASIGNACIÓN Y GESTIÓN DE TAREAS
    // ============================================================

    /**
     * Asigna un empleado responsable a una tarea específica dentro de un proyecto.     *
     *
     * @param numero Número o código del proyecto.
     * @param titulo Título de la tarea a asignar.
     * @throws Exception si intenta asignar a una tarea ya asignada o el proyecto esta finalizado
     */
    public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con número " + numero);}

        if (proyecto.estaFinalizado()) {
            throw new Exception("No se puede asignar empleados a un proyecto finalizado.");}

        Tarea tarea = proyecto.getTareas().get(titulo);
        if ( proyecto.getTareas().get(titulo)== null) {
            throw new IllegalArgumentException("No existe una tarea con el título '" + titulo + "' en el proyecto " + numero);}

        if (tarea.getEmpleadoAsociado() != null) {
            throw new Exception("La tarea '" + titulo + "' ya tiene un responsable asignado.");}

        // Buscar cualquier empleado disponible
        Empleado empleadoDisponible = null;
        for (Empleado e : empleados.values()) {
            if (!e.getAsignado()) {
                empleadoDisponible = e;
                break;
            }
        }
        if (empleadoDisponible == null) {
            throw new Exception("No hay empleados disponibles para asignar.");
        }

        proyecto.asignarEmpleado(titulo, empleadoDisponible);
    }


    /**
     * Asigna a la tarea el empleado con menos retrasos acumulados.
     *
     * @param numero Número o código del proyecto.
     * @param titulo Título de la tarea.
     * @throws Exception si no hay empleados disponibles o la tarea ya fue asignada o el proyecto esta finalizado
     */
    public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("No existe un proyecto con el número: " + numero);}

        if (proyecto.estaFinalizado()) {
            throw new Exception("No se puede asignar, el proyecto ya está finalizado.");}

        Tarea tarea = proyecto.getTareas().get(titulo);
        if (tarea == null) {
            throw new Exception("No existe una tarea con el título: " + titulo);}

        if (tarea.getEmpleadoAsociado() != null) {
            throw new Exception("La tarea ya tiene un empleado asignado.");}

        Empleado empleadoConMenosRetrasos= empleadoConMenosRetrasos();

        if (empleadoConMenosRetrasos == null) {
            throw new Exception("No hay empleados disponibles para asignar.");}

        // Asignar al empleado a la tarea
        proyecto.asignarEmpleado(titulo, empleadoConMenosRetrasos);
    }





    private Empleado empleadoConMenosRetrasos() {
        Empleado empleado = null;
        int minRetrasos = Integer.MAX_VALUE;
        for (Empleado e : empleados.values()) {
            if (!e.getAsignado()) {
                int cantidadRetrasos = e.getCantRetrasos();
                if (cantidadRetrasos < minRetrasos) {
                    minRetrasos = cantidadRetrasos;
                    empleado = e;
                }
            }
        }
        return empleado;}


    /**
     * Registra un retraso en una tarea de un proyecto.
     * Un retraso modifica la fecha real de finalización.     *
     * @param numero Número o código del proyecto.
     * @param titulo Título de la tarea.
     * @param cantidadDias Días de retraso acumulados.
     * @throws IllegalArgumentException Si los valores son incorrectos.
     */
    public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias) throws IllegalArgumentException{
        Proyecto proyecto= proyectos.get(numero);

        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con el número: " + numero);
        }
        Tarea tarea = proyecto.getTareas().get(titulo);
        if (tarea == null) {
            throw new IllegalArgumentException("No existe una tarea con el título: " + titulo);
        }

        proyecto.registrarRetrasoTarea(cantidadDias, titulo);
    }

    /**
     * Agrega una nueva tarea a un proyecto existente, tener en cuenta que se modifica
     * la fecha de finalización tanto la real como la prevista.
     * @param numero Número o código del proyecto.
     * @param titulo Título de la nueva tarea.
     * @param descripcion Descripción de la tarea.
     * @param dias Días estimados de duración.
     * @throws IllegalArgumentException Si los valores son incorrectos o el proyecto ya esta finalizado.
     */
    public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias) throws  IllegalArgumentException{
        Proyecto proyecto = proyectos.get(numero);

        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con el número: " + numero);
        }

        if (dias <= 0) {
            throw new IllegalArgumentException("La cantidad de días debe ser positiva.");
        }

        proyecto.agregarTarea(titulo, descripcion, (int) dias);
    }

    /**
     * Marca una tarea como finalizada.     *
     * @param numero Número o código del proyecto.
     * @param titulo Título de la tarea a finalizar.
     * @throws Exception Si la tarea ya estaba finalizada.
     */
    public void finalizarTarea(Integer numero, String titulo)
            throws Exception{
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con el número: " + numero);
        }

        // buscar la tarea
        Tarea tarea = proyecto.getTareas().get(titulo);
        if (tarea == null) {
            throw new IllegalArgumentException("No existe una tarea con el título: " + titulo);
        }

        // si esta finalizada lanza exc
        if (tarea.isFinalizado()) {
            throw new Exception("La tarea '" + titulo + "' ya estaba finalizada.");
        }
        proyecto.establecerTareaFinalizada(titulo);
    }

    /**
     * Marca un proyecto completo como finalizado.
     * @param numero Número o código del proyecto.
     * @param fin Fecha de inicio de finalización (formato YYYY-MM-DD).
     * @throws IllegalArgumentException si la fecha es incorrecta( anterior a la fecha de inicio)
     */
    public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException{
        Proyecto proyecto = proyectos.get(numero);

        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con el número: " + numero);}

        if (proyecto.estaFinalizado()) {
            throw new IllegalArgumentException("El proyecto ya fue finalizado.");}

        //el try/catch es lo unico que me asegura la excepcion en los errores de paseo
        LocalDate fechaFin;
        try {
            fechaFin = LocalDate.parse(fin); // convierte el string a LocalDate
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto. Debe ser YYYY-MM-DD.");}

        // Validar que la fecha no sea anterior al inicio
        if (fechaFin.isBefore(proyecto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha de inicio.");}

        // Validar que la fecha no sea anterior a la estimada
        if (fechaFin.isBefore(proyecto.getFechaEstimada())) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha estimada.");}


        // liberar empleados asignados a este proyecto
        for (Tarea tarea: proyecto.getTareas().values()) {
            proyecto.establecerTareaFinalizada(tarea.getNombre());
            if (tarea.getEmpleadoAsociado() != null) {
                Empleado empleado= tarea.getEmpleadoAsociado();
                empleado.desasignar();
            }

        }
        // Actualizar los datos del proyecto
        proyecto.setFechaFinalizacion(fechaFin);
        proyecto.setFinalizado();

    }


    // ============================================================
    // REASIGNACIÓN DE EMPLEADOS
    // ============================================================

    /** (O)1    !!!!!!!!!!!!!!!!!!#####
     *
     * Reasigna un empleado a una tarea determinada dentro de un proyecto.
     * Libera al empleado anterior.
     * @param numero Número o código del proyecto.
     * @param legajo Legajo del empleado a reasignar.
     * @param titulo Título de la tarea.
     * @throw  Exception si no hay empleados disponibles o si no tiene asignado un empleado previamente
     */
    public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con número " + numero);}

        Tarea tarea = proyecto.getTareas().get(titulo);
        if (tarea == null) {
            throw new IllegalArgumentException("No existe una tarea con el título '" + titulo + "' en el proyecto " + numero);}

        Empleado empleadoActual = tarea.getEmpleadoAsociado();
        if (empleadoActual == null) {
            throw new Exception("La tarea '" + titulo + "' no tiene un empleado asignado previamente.");}

        Empleado nuevoEmpleado = empleados.get(legajo);
        if (nuevoEmpleado == null) {
            throw new IllegalArgumentException("No existe un empleado con legajo " + legajo);}

        if (proyecto.estaFinalizado()) {
            throw new Exception("No se puede reasignar empleados en un proyecto finalizado.");}

        empleadoActual.desasignar();
        proyecto.asignarEmpleado(titulo, nuevoEmpleado);
    }



    /**
     * Reasigna al empleado con menos retrasos acumulados a una tarea.
     * Libera al empleado anterior.
     * @param numero Número o código del proyecto.
     * @param titulo Título de la tarea.
     * @throw  Exception si no hay empleados disponibles o si no tiene asignado un empleado previamente
     */
    public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
        Proyecto proyecto = proyectos.get(numero);
        if (proyecto == null) {
            throw new Exception("No existe un proyecto con el número: " + numero);}

        Tarea tarea = proyecto.getTareas().get(titulo);
        if (tarea == null) {
            throw new Exception("No existe una tarea con el título: " + titulo);}

        Empleado empleadoActual = tarea.getEmpleadoAsociado();
        if (empleadoActual == null) {
            throw new Exception("La tarea '" + titulo + "' no tiene un empleado asignado para reasignar.");}

        Empleado empleadoConMenosRetrasos= empleadoConMenosRetrasos();

        if (empleadoConMenosRetrasos == null) {
            throw new Exception("No hay empleados disponibles para reasignar.");}

        empleadoActual.desasignar();
        proyecto.asignarEmpleado(titulo, empleadoConMenosRetrasos);
    }


    // ============================================================
    // CONSULTAS Y REPORTES
    // ============================================================

    /**
     * Calcula el costo total del proyecto (activo, pendiente o finalizado).
     * @return Costo total acumulado.
     */
    public double costoProyecto(Integer numeroProyecto) throws IllegalArgumentException {
        Proyecto proyecto = proyectos.get(numeroProyecto); // obtenemos el proyecto del HashMap
        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con ese código.");
        }

        if (proyecto.getEstado().equals(Estado.pendiente)) {
            throw new IllegalArgumentException("No se puede calcular el costo de un proyecto pendiente.");
        }

        return proyecto.calcularCostoProyecto(); // O(1), ya definido en Proyecto
    }


    /**
     * Devuelve una lista de proyectos finalizados (número y domicilio).
     * @return Lista de tuplas (número, domicilio).
     */
    public List<Tupla<Integer, String>> proyectosFinalizados() {
        List<Tupla<Integer, String>> lista = new ArrayList<>();
        for (Proyecto p : proyectos.values()) {
            if (p.estaFinalizado()) {
                lista.add(new Tupla<>(p.getCodigo(), p.getVivienda()));
            }
        }
        return lista;
    }

    /**
     * Devuelve una lista de proyectos pendientes (aún no iniciados o incompletos).
     * @return Lista de tuplas (número, domicilio).
     */
    public List<Tupla<Integer, String>> proyectosPendientes() {
        List<Tupla<Integer, String>> lista = new ArrayList<>();
        for (Proyecto p : proyectos.values()) {
            if (p.getEstado().equals(Estado.pendiente)) {
                lista.add(new Tupla<>(p.getCodigo(), p.getVivienda()));
            }
        }
        return lista;
    }


    /**
     * Devuelve una lista de proyectos actualmente activos.
     * @return Lista de tuplas (número, domicilio).
     */
    public List<Tupla<Integer, String>> proyectosActivos() {
        List<Tupla<Integer, String>> lista = new ArrayList<>();
        for (Proyecto p : proyectos.values()) {
            if (p.getEstado().equals(Estado.activo)) {
                lista.add(new Tupla<>(p.getCodigo(), p.getVivienda()));
            }
        }
        return lista;
    }

    /**
     * Devuelve los empleados que no están asignados a ningún proyecto.
     * @return Arreglo de empleados no asignados.
     */
    public Object[] empleadosNoAsignados() {
        List<Empleado> libres = new ArrayList<>();

        for (Empleado e : empleados.values()) {
            if (!e.getAsignado()) {
                libres.add(e);
            }
        }
        return libres.toArray();
    }


    /**
     * Indica si un proyecto ya fue finalizado.
     * @param numero Número o código del proyecto.
     * @return true si está finalizado, false en caso contrario.
     */
    public boolean estaFinalizado(Integer numero){
        Proyecto proyecto=proyectos.get(numero);
        return proyecto.estaFinalizado();
    }

    /**
     * Consulta la cantidad total de retrasos acumulados por un empleado.
     * @param legajo Legajo del empleado.
     * @return Cantidad de retrasos.
     */
    public int consultarCantidadRetrasosEmpleado(Integer legajo){
        Empleado empleado= empleados.get(legajo);
        return empleado.getCantRetrasos();
    }

    /**
     * Devuelve los empleados asignados a un proyecto determinado.
     * @param numero Número o código del proyecto.
     * @return Lista de tuplas (legajo, nombre del empleado).
     */
    public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
        Proyecto proyecto = proyectos.get(numero);
        List<Tupla<Integer, String>> lista = new ArrayList<>();
        if (proyecto != null) {
            for (Tarea tarea : proyecto.getTareas().values()) {
                Empleado empleado = tarea.getEmpleadoAsociado();
                if (empleado != null) {
                    lista.add(new Tupla<>(empleado.getLegajo(), empleado.getNombre()));
                }
            }
        }
        return lista;
    }


    // ============================================================
    // NUEVOS REQUERIMIENTOS
    // ============================================================

    /**
     * Devuelve las tareas no asignadas de un proyecto.
     * @param numero Número o código del proyecto.
     * @return Arreglo de tareas sin asignar.
     */
    public Object[] tareasProyectoNoAsignadas(Integer numero){
        List<Tarea> lista=new ArrayList<>();
        Proyecto proyecto=proyectos.get(numero);

        if (proyecto == null) {
            throw new IllegalArgumentException("No existe un proyecto con número " + numero);}

        if (proyecto.estaFinalizado()){
            throw new IllegalArgumentException("No se pueden obtener tareas no asignadas de un proyecto finalizado.");}

        for (Tarea tarea: proyecto.getTareas().values()){
            if (tarea.getEmpleadoAsociado()==null){
                lista.add(tarea);
            }
        }
        return lista.toArray();
    }
    /**
     * Devuelve las tareas asignadas de un proyecto.
     * @param numero Número o código del proyecto.
     * @return Arreglo de tareas de un proyecto.
     */
    public Object[] tareasDeUnProyecto(Integer numero){
        Proyecto proyecto=proyectos.get(numero);
        return proyecto.getTareas().values().toArray();
    }

    /**
     * Consulta el domicilio del proyecto.
     * @param numero Número o código del proyecto.
     * @return Dirección donde se realiza el proyecto.
     */
    public String consultarDomicilioProyecto(Integer numero){
        Proyecto proyecto= proyectos.get(numero);
        if (proyecto!=null) {
            return proyecto.getVivienda();
        }else{
            throw new IllegalArgumentException("No existe un proyecto con ese código.");
        }
    }

    /**
     * Indica si un empleado tiene retrasos en tareas asignadas.
     * @param legajo Legajo del empleado.
     * @return true si tiene retrasos, false en caso contrario.
     */
    public boolean tieneRestrasos(Integer legajo) {
        for (Proyecto proyecto : proyectos.values()) { // recorre todos los proyectos
            for (Tarea tarea : proyecto.getTareas().values()) { // recorre las tareas del proyecto
                Empleado empleado = tarea.getEmpleadoAsociado();
                if (empleado != null && empleado.getLegajo()==legajo && tarea.getCantDiasRetrasos() > 0) {
                    return true; // encontró una tarea con retraso de ese empleado
                }
            }
        }
        return false; // ninguna tarea con retrasos de ese empleado
    }


    /**
     * Devuelve la lista de todos los empleados registrados.
     * @return Lista de tuplas (legajo, nombre del empleado).
     */
    public List<Tupla<Integer, String>> empleados() {
        List<Tupla<Integer, String>> lista = new ArrayList<>();
        for (Empleado e : empleados.values()) {
            lista.add(new Tupla<>(e.getLegajo(), e.getNombre()));
        }
        return lista;
    }

    /**
     * Devuelve la informacion generada en el toString.
     * @numero numero de proyecto.
     */
    public String consultarProyecto(Integer numero){
        Proyecto proyecto = proyectos.get(numero);
        return proyecto.toString();
    }

    public int generarCodigoProyecto(){
        int codigo= contadorDeProyectos;
        contadorDeProyectos++;
        return codigo;
    }
    public int generarLegajoEmpleado(){
        int codigo= contadorLegajo;
        contadorLegajo++;
        return codigo;
    }




    @Override
    public String toString() {
        // Empleados
        String empleadosInfo = "";
        for (Empleado e : empleados.values()) {
            empleadosInfo += e.toString() + ", ";
        }
        if (!empleadosInfo.isEmpty()) {
            empleadosInfo = empleadosInfo.substring(0, empleadosInfo.length() - 2);
        }

        // Proyectos
        String proyectosInfo = "";
        for (Proyecto p : proyectos.values()) {
            proyectosInfo += p.toString() + "\n";
        }

        // Clientes
        String clientesInfo = "";
        for (Cliente c : clientes.values()) {
            clientesInfo += c.toString() + ", ";
        }
        if (!clientesInfo.isEmpty()) {
            clientesInfo = clientesInfo.substring(0, clientesInfo.length() - 2);
        }
        return "HomeSolution{\n" +
                "contadorDeProyectos=" + contadorDeProyectos + "\n" +
                ", empleados=[" + empleadosInfo + "]\n" +
                ", proyectos=[\n" + proyectosInfo + "]\n" +
                ", clientes=[" + clientesInfo + "]\n" +
                '}';
    }
}
