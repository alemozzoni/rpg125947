package it.unicam.cs.mpgc.rpg125947.controller;

import it.unicam.cs.mpgc.rpg125947.app.AppContext;
import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller della schermata di accusa: il giocatore sceglie un sospettato e gli
 * indizi a supporto, poi conferma. La valutazione e delegata al motore (che usa
 * la Strategy {@link it.unicam.cs.mpgc.rpg125947.model.interfaces.ValutatoreAccusa}).
 */
public final class AccusaController {

    private final AppContext context;
    private final ToggleGroup gruppoSospettati = new ToggleGroup();

    @FXML private VBox contenitoreSospettati;
    @FXML private VBox contenitoreProve;
    @FXML private Label messaggio;

    public AccusaController(AppContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        for (Sospettato sospettato : context.getCaso().getSospettati()) {
            RadioButton scelta = new RadioButton(sospettato.getNome() + " — " + sospettato.getRuolo());
            scelta.setUserData(sospettato);
            scelta.setToggleGroup(gruppoSospettati);
            scelta.getStyleClass().add("scelta-sospettato");
            contenitoreSospettati.getChildren().add(scelta);
        }

        Partita partita = context.getMotore().getPartita();
        if (partita.getTaccuino().getIndizi().isEmpty()) {
            Label vuoto = new Label("Non hai ancora raccolto indizi: torna a indagare.");
            vuoto.getStyleClass().add("taccuino-vuoto");
            vuoto.setWrapText(true);
            contenitoreProve.getChildren().add(vuoto);
        }
        for (Indizio indizio : partita.getTaccuino().getIndizi()) {
            CheckBox prova = new CheckBox(indizio.getNome());
            prova.setUserData(indizio);
            prova.getStyleClass().add("scelta-prova");
            contenitoreProve.getChildren().add(prova);
        }
    }

    @FXML
    private void onConferma() {
        Toggle selezionato = gruppoSospettati.getSelectedToggle();
        if (selezionato == null) {
            messaggio.setText("Seleziona il sospettato che vuoi accusare.");
            return;
        }
        Sospettato accusato = (Sospettato) selezionato.getUserData();
        context.getMotore().accusa(accusato, proveSelezionate());
        context.getSceneManager().mostraFinale();
    }

    @FXML
    private void onAnnulla() {
        context.getMotore().eseguiAzione(AzioneGiocatore.ANNULLA_ACCUSA);
        context.getSceneManager().mostraEsplorazione();
    }

    private Set<Indizio> proveSelezionate() {
        Set<Indizio> prove = new HashSet<>();
        for (Node nodo : contenitoreProve.getChildren()) {
            if (nodo instanceof CheckBox cb && cb.isSelected()) {
                prove.add((Indizio) cb.getUserData());
            }
        }
        return prove;
    }
}
