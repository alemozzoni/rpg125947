package it.unicam.cs.mpgc.rpg125947.controller;

import it.unicam.cs.mpgc.rpg125947.app.AppContext;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Optional;

/**
 * Controller della schermata finale: mostra l'epilogo (vittoria o sconfitta) e
 * propone come proseguire. In caso di fallimento permette di ricaricare un
 * salvataggio e riprovare, come previsto dal flusso di gioco.
 */
public final class FinaleController {

    private final AppContext context;

    @FXML private Label titolo;
    @FXML private Label narrazione;
    @FXML private HBox bottoni;

    public FinaleController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        Partita partita = context.getMotore().getPartita();
        EsitoAccusa esito = partita.getEsito().orElseThrow(
                () -> new IllegalStateException("Schermata finale senza esito"));

        boolean successo = esito.successo();
        titolo.setText(successo ? "CASO RISOLTO" : "ACCUSA FALLITA");
        titolo.getStyleClass().add(successo ? "finale-successo" : "finale-fallimento");
        narrazione.setText(esito.narrazione());

        Button nuova = new Button("Nuova indagine");
        nuova.getStyleClass().add("bottone-principale");
        nuova.setOnAction(e -> context.getSceneManager().mostraCreazione());
        bottoni.getChildren().add(nuova);

        if (!successo) {
            Button carica = new Button("Carica salvataggio e riprova");
            carica.getStyleClass().add("bottone-hud");
            carica.setOnAction(e -> ricarica());
            bottoni.getChildren().add(carica);
        }
    }

    private void ricarica() {
        Optional<Partita> caricata = context.getRepository().carica("slot1", context.getCaso());
        if (caricata.isPresent()) {
            context.getMotore().riprendi(caricata.get());
            context.getSceneManager().mostraEsplorazione();
        } else {
            narrazione.setText("Nessun salvataggio disponibile: avvia una nuova indagine.");
        }
    }
}
