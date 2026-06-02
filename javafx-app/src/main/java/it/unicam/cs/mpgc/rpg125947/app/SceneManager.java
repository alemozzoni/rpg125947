package it.unicam.cs.mpgc.rpg125947.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Unico gestore della navigazione tra le schermate (transizioni
 * Stage -> Scene -> Node). Ogni schermata e una coppia FXML (View) + Controller.
 *
 * <p>Inietta il {@link AppContext} nei controller tramite una
 * {@code controllerFactory}: cosi i controller ricevono le proprie dipendenze
 * dal costruttore (Dependency Injection) e restano testabili e disaccoppiati
 * dalla creazione delle viste.</p>
 */
public final class SceneManager {

    private static final int LARGHEZZA = 1280;
    private static final int ALTEZZA = 720;

    private final Stage stage;
    private final AppContext context;

    public SceneManager(Stage stage, AppContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void mostraCreazione() {
        carica("creazione.fxml");
    }

    public void mostraEsplorazione() {
        carica("esplorazione.fxml");
    }

    public void mostraAccusa() {
        carica("accusa.fxml");
    }

    public void mostraFinale() {
        carica("finale.fxml");
    }

    private void carica(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(RisorseGrafiche.fxml(fxml));
            loader.setControllerFactory(this::creaController);
            Parent root = loader.load();
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root, LARGHEZZA, ALTEZZA);
                applicaStile(scene);
                stage.setScene(scene);
            } else {
                scene.setRoot(root); // riuso della stessa scena: transizione fluida
            }
        } catch (IOException e) {
            throw new IllegalStateException("Errore nel caricamento della schermata " + fxml, e);
        }
    }

    private void applicaStile(Scene scene) {
        URL css = RisorseGrafiche.foglioStile();
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    /** Crea il controller richiesto dall'FXML iniettandogli il contesto. */
    private Object creaController(Class<?> tipo) {
        try {
            return tipo.getDeclaredConstructor(AppContext.class).newInstance(context);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Il controller " + tipo.getName() + " deve avere un costruttore (AppContext)", e);
        }
    }
}
