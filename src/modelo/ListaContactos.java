
package modelo;

import java.util.ArrayList;
import java.util.List;

public class ListaContactos {

    private List<Contacto> contactos;

    public ListaContactos() {
        contactos = new ArrayList<>();
    }

    // Agregar un contacto
    public void agregarContacto(Contacto contacto) {
        contactos.add(contacto);
    }

    // Eliminar un contacto
    public void eliminarContacto(Contacto contacto) {
        contactos.remove(contacto);
    }

    // Obtener todos los contactos
    public List<Contacto> getContactos() {
        return new ArrayList<>(contactos);
    }

    // Obtener la cantidad de contactos
    public int getTotalContactos() {
        return contactos.size();
    }

    public int getTotalFavoritos() {
    int total = 0;

    for (Contacto contacto : contactos) {
        if (contacto.isFavorito()) {
            total++;
        }
    }

    return total;
    }
    // Buscar un contacto por su posición
    public Contacto getContacto(int indice) {
        if (indice >= 0 && indice < contactos.size()) {
            return contactos.get(indice);
        }

        return null;
    }
}

