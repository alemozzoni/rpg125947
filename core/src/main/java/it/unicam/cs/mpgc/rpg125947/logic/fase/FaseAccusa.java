package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;

/**
 * Fase di formulazione dell'accusa: il giocatore sceglie sospettato e prove.
 * Confermando si passa al finale; annullando si torna a esplorare.
 */
public final class FaseAccusa implements FaseDiGioco {

    public static final FaseAccusa INSTANCE = new FaseAccusa();

    private FaseAccusa() {
    }

    @Override
    public String nome() {
        return "Accusa";
    }

    @Override
    public FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita) {
        return switch (azione) {
            case CONFERMA_ACCUSA -> FaseFinale.INSTANCE;
            case ANNULLA_ACCUSA -> FaseEsplorazione.INSTANCE;
            default -> this;
        };
    }
}
