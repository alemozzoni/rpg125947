package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;

/**
 * Fase di ispezione di un oggetto della scena (visualizzazione di un indizio).
 * Si esce chiudendo l'interazione.
 */
public final class FaseIspezione implements FaseDiGioco {

    public static final FaseIspezione INSTANCE = new FaseIspezione();

    private FaseIspezione() {
    }

    @Override
    public String nome() {
        return "Ispezione";
    }

    @Override
    public FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita) {
        return azione == AzioneGiocatore.CHIUDI_INTERAZIONE
                ? FaseEsplorazione.INSTANCE
                : this;
    }
}
