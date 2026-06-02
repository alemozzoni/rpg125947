package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;

/**
 * Fase di esplorazione libera del museo: da qui il giocatore puo entrare in
 * dialogo, ispezionare oggetti o aprire la schermata d'accusa. E lo "snodo"
 * verso le altre fasi.
 */
public final class FaseEsplorazione implements FaseDiGioco {

    public static final FaseEsplorazione INSTANCE = new FaseEsplorazione();

    private FaseEsplorazione() {
    }

    @Override
    public String nome() {
        return "Esplorazione";
    }

    @Override
    public FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita) {
        return switch (azione) {
            case ENTRA_IN_DIALOGO -> FaseDialogo.INSTANCE;
            case ISPEZIONA -> FaseIspezione.INSTANCE;
            case APRI_ACCUSA -> FaseAccusa.INSTANCE;
            default -> this; // le altre azioni non hanno effetto in esplorazione
        };
    }
}
