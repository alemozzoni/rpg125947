package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;

/**
 * Fase di conversazione con un personaggio. Si esce chiudendo l'interazione e
 * tornando all'esplorazione.
 */
public final class FaseDialogo implements FaseDiGioco {

    public static final FaseDialogo INSTANCE = new FaseDialogo();

    private FaseDialogo() {
    }

    @Override
    public String nome() {
        return "Dialogo";
    }

    @Override
    public FaseDiGioco gestisci(AzioneGiocatore azione, Partita partita) {
        return azione == AzioneGiocatore.CHIUDI_INTERAZIONE
                ? FaseEsplorazione.INSTANCE
                : this;
    }
}
