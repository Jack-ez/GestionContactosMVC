
package principal;

import javax.swing.SwingUtilities;

import modelo.ListaContactos;
import vista.VistaContactos;
import controlador.ContactoController;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Crear el modelo
            ListaContactos modelo = new ListaContactos();

            // Crear la vista
            VistaContactos vista = new VistaContactos();

            // Conectar la vista con el modelo mediante el controlador
            ContactoController controlador =
                    new ContactoController(vista, modelo);

            // Mostrar la ventana
            vista.setVisible(true);
        });
    }
}

