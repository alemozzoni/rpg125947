package it.unicam.cs.mpgc.rpg125947.logic.fase;

import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Verifica le transizioni del pattern State: ogni fase reagisce alle azioni del
 * giocatore decidendo la fase successiva, ignorando le azioni non pertinenti.
 */
class TransizioniFaseTest {

    @Test
    void dallEsplorazioneSiRaggiungonoLeAltreFasi() {
        assertEquals(FaseDialogo.INSTANCE,
                FaseEsplorazione.INSTANCE.gestisci(AzioneGiocatore.ENTRA_IN_DIALOGO, null));
        assertEquals(FaseIspezione.INSTANCE,
                FaseEsplorazione.INSTANCE.gestisci(AzioneGiocatore.ISPEZIONA, null));
        assertEquals(FaseAccusa.INSTANCE,
                FaseEsplorazione.INSTANCE.gestisci(AzioneGiocatore.APRI_ACCUSA, null));
    }

    @Test
    void dialogoEIspezioneTornanoAllEsplorazione() {
        assertEquals(FaseEsplorazione.INSTANCE,
                FaseDialogo.INSTANCE.gestisci(AzioneGiocatore.CHIUDI_INTERAZIONE, null));
        assertEquals(FaseEsplorazione.INSTANCE,
                FaseIspezione.INSTANCE.gestisci(AzioneGiocatore.CHIUDI_INTERAZIONE, null));
    }

    @Test
    void accusaConfermataPortaAlFinale() {
        assertEquals(FaseFinale.INSTANCE,
                FaseAccusa.INSTANCE.gestisci(AzioneGiocatore.CONFERMA_ACCUSA, null));
        assertEquals(FaseEsplorazione.INSTANCE,
                FaseAccusa.INSTANCE.gestisci(AzioneGiocatore.ANNULLA_ACCUSA, null));
    }

    @Test
    void leAzioniNonPertinentiNonCambianoFase() {
        assertSame(FaseEsplorazione.INSTANCE,
                FaseEsplorazione.INSTANCE.gestisci(AzioneGiocatore.CHIUDI_INTERAZIONE, null));
        assertSame(FaseFinale.INSTANCE,
                FaseFinale.INSTANCE.gestisci(AzioneGiocatore.CONFERMA_ACCUSA, null));
    }
}
