package it.unicam.cs.mpgc.rpg125947.controller;

import it.unicam.cs.mpgc.rpg125947.app.AppContext;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller della schermata iniziale di creazione del personaggio.
 *
 * <p>Raccoglie il nome dell'investigatore, crea la {@link it.unicam.cs.mpgc.rpg125947.model.Partita}
 * e passa alla prima stanza. La validazione e fatta vicino all'input tramite
 * <strong>binding</strong>: il bottone resta disabilitato finche il nome e vuoto.</p>
 */
public final class CreazioneController {

    private final AppContext context;

    @FXML private TextField campoNome;
    @FXML private Button inizia;
    @FXML private Label messaggioErrore;

    public CreazioneController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        // Binding: niente nome, niente partita. Validazione dichiarativa.
        inizia.disableProperty().bind(campoNome.textProperty().isEmpty());
    }

    @FXML
    private void onInizia() {
        try {
            Caso caso = context.getCaso();
            context.getMotore().nuovaPartita(new Investigatore(campoNome.getText().trim()), caso);
            context.getSceneManager().mostraEsplorazione();
        } catch (RuntimeException e) {
            messaggioErrore.setText("Impossibile avviare il gioco: " + e.getMessage());
        }
    }
}
