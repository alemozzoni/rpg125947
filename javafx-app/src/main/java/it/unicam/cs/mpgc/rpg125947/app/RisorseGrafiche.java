package it.unicam.cs.mpgc.rpg125947.app;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Punto unico di accesso alle risorse grafiche del front-end (FXML, CSS,
 * immagini), con cache delle immagini gia caricate.
 *
 * <p>Centralizzare qui i percorsi (DRY) evita stringhe sparse nel codice e
 * isola i controller dai dettagli di layout delle risorse.</p>
 */
public final class RisorseGrafiche {

    private static final String BASE = "/it/unicam/cs/mpgc/rpg125947/app/view/";
    private static final String IMMAGINI = BASE + "images/";

    private static final Map<String, Image> cache = new HashMap<>();

    private RisorseGrafiche() {
    }

    /** @return l'URL di un file FXML dato il suo nome (es. "creazione.fxml") */
    public static URL fxml(String nome) {
        return risorsa(BASE + nome);
    }

    /** @return l'URL del foglio di stile principale */
    public static URL foglioStile() {
        return risorsa(BASE + "stile.css");
    }

    /**
     * Carica un'immagine dalle risorse (con cache). Se l'immagine non esiste
     * restituisce {@code null}: i controller gestiscono l'assenza senza errori.
     *
     * @param nome nome del file (es. "atrio.png")
     */
    public static Image immagine(String nome) {
        return cache.computeIfAbsent(nome, n -> {
            URL url = RisorseGrafiche.class.getResource(IMMAGINI + n);
            return url == null ? null : new Image(url.toExternalForm());
        });
    }

    private static URL risorsa(String path) {
        return RisorseGrafiche.class.getResource(path);
    }
}
