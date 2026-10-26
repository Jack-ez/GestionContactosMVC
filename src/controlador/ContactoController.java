
package controlador;

import modelo.Contacto;
import modelo.ListaContactos;
import vista.VistaContactos;

import javax.swing.JOptionPane;

public class ContactoController {

    private VistaContactos vista;
    private ListaContactos modelo;

    public ContactoController(VistaContactos vista, ListaContactos modelo) {

        this.vista = vista;
        this.modelo = modelo;

        vista.getBtnAgregar().addActionListener(e -> agregarContacto());
        vista.getBtnLimpiar().addActionListener(e -> vista.limpiarCampos());
        vista.getBtnEliminar().addActionListener(e -> eliminarContacto());
        vista.getBtnFavorito().addActionListener(e -> marcarFavorito());
        vista.getBtnExportar().addActionListener(e -> exportarContactosCSV());
    }

    private void eliminarContacto() {

        int filaVista = vista.getFilaSeleccionada();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un contacto de la tabla."
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Deseas eliminar el contacto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {

            // Convertir la fila visible al índice de la tabla
            int filaModelo = vista.convertirFilaAModelo(filaVista);

            Contacto contacto = modelo.getContacto(filaModelo);

            if (contacto == null) {
                return;
            }

            modelo.eliminarContacto(contacto);
            vista.eliminarFila(filaModelo);

            actualizarEstadisticas();

            JOptionPane.showMessageDialog(
                    vista,
                    "Contacto eliminado correctamente."
            );
        }
    }

    private void agregarContacto() {

        String nombre = vista.getNombre();
        String telefono = vista.getTelefono();
        String correo = vista.getCorreo();
        String categoria = vista.getCategoria();

        // Validar campos
        if (nombre.isEmpty() || telefono.isEmpty()
                || correo.isEmpty() || categoria.isEmpty()) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Completa todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Actualizar la barra de progreso
        vista.actualizarProgreso(25, "Preparando contacto...");

        Contacto contacto = new Contacto(
                nombre, telefono, correo, categoria, false
        );

        modelo.agregarContacto(contacto);
        vista.agregarFila(contacto);

        vista.actualizarProgreso(75, "Agregando contacto...");
        vista.actualizarProgreso(100, "Contacto agregado");

        actualizarEstadisticas();

        JOptionPane.showMessageDialog(
                vista,
                "Contacto agregado correctamente."
        );

        vista.limpiarCampos();

        System.out.println(
                "Total de contactos: " + modelo.getTotalContactos()
        );
    }

    private void marcarFavorito() {

        int filaVista = vista.getFilaSeleccionada();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Selecciona un contacto de la tabla."
            );
            return;
        }

        // Convertir la fila visible al índice del modelo de tabla
        int filaModelo = vista.convertirFilaAModelo(filaVista);

        Contacto contacto = modelo.getContacto(filaModelo);

        if (contacto == null) {
            return;
        }

        contacto.setFavorito(!contacto.isFavorito());

        vista.actualizarFila(filaModelo, contacto);

        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {

        int total = modelo.getTotalContactos();
        int favoritos = modelo.getTotalFavoritos();

        vista.actualizarEstadisticas(total, favoritos);
    }

    private void exportarContactosCSV() {

        javax.swing.JFileChooser selector =
                new javax.swing.JFileChooser();

        selector.setDialogTitle("Guardar contactos");
        selector.setSelectedFile(
                new java.io.File("contactos.csv")
        );

        int resultado = selector.showSaveDialog(vista);

        if (resultado != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new java.io.File(
                    archivo.getAbsolutePath() + ".csv"
            );
        }

        // Exportar en UTF-8
        try (java.io.PrintWriter escritor =
                new java.io.PrintWriter(
                        new java.io.OutputStreamWriter(
                                new java.io.FileOutputStream(archivo),
                                java.nio.charset.StandardCharsets.UTF_8))) {

            escritor.println(
                    "Nombre,Telefono,Correo,Categoria,Favorito"
            );

            for (Contacto contacto : modelo.getContactos()) {

                escritor.println(
                        "\"" + escaparCSV(contacto.getNombre()) + "\"," +
                        "\"" + escaparCSV(contacto.getTelefono()) + "\"," +
                        "\"" + escaparCSV(contacto.getCorreo()) + "\"," +
                        "\"" + escaparCSV(contacto.getCategoria()) + "\"," +
                        "\"" + (contacto.isFavorito() ? "Sí" : "No") + "\""
                );
            }

            JOptionPane.showMessageDialog(
                    vista,
                    "Contactos exportados correctamente.",
                    "Exportación completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (java.io.IOException ex) {

            JOptionPane.showMessageDialog(
                    vista,
                    "Error al exportar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Evitar errores CSV cuando los datos contienen comillas
    private String escaparCSV(String texto) {
        return texto.replace("\"", "\"\"");
    }
}

