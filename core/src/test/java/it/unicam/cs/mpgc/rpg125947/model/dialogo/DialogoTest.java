package it.unicam.cs.mpgc.rpg125947.model.dialogo;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la ramificazione dei dialoghi per stile investigativo: ogni profilo
 * vede le domande universali piu quelle riservate al proprio attributo dominante.
 */
class DialogoTest {

    private Testimonianza risposta(String testo) {
        return new Testimonianza("Tizio", testo, null);
    }

    private OpzioneDialogo universale(String d) {
        return new OpzioneDialogo(d, risposta(d));
    }

    private OpzioneDialogo perStile(String d, Attributo stile) {
        return new OpzioneDialogo(d, risposta(d), null, 0, stile);
    }

    @Test
    void unProfiloVedeLeUniversaliPiuLeSueDiStile() {
        Dialogo dialogo = new Dialogo("Salve.", List.of(
                universale("Dove si trovava?"),
                perStile("Domanda da osservatore", Attributo.OSSERVAZIONE),
                perStile("Domanda da logico", Attributo.LOGICA)));

        List<OpzioneDialogo> perLogico = dialogo.opzioniPer(Attributo.LOGICA);
        assertEquals(2, perLogico.size());
        assertTrue(perLogico.stream().anyMatch(o -> o.domanda().equals("Dove si trovava?")));
        assertTrue(perLogico.stream().anyMatch(o -> o.domanda().equals("Domanda da logico")));
    }

    @Test
    void profiliDiversiVedonoDomandeDiverseMaEntrambiLeUniversali() {
        Dialogo dialogo = new Dialogo("Salve.", List.of(
                universale("Comune"),
                perStile("Solo eloquenza", Attributo.ELOQUENZA),
                perStile("Solo intuito", Attributo.INTUITO)));

        assertEquals(List.of("Comune", "Solo eloquenza"),
                dialogo.opzioniPer(Attributo.ELOQUENZA).stream().map(OpzioneDialogo::domanda).toList());
        assertEquals(List.of("Comune", "Solo intuito"),
                dialogo.opzioniPer(Attributo.INTUITO).stream().map(OpzioneDialogo::domanda).toList());
    }

    @Test
    void leDomandeUniversaliSonoVisibiliAOgniStile() {
        Dialogo dialogo = new Dialogo("Salve.", List.of(universale("Sempre"), universale("Anche questa")));
        for (Attributo a : Attributo.values()) {
            assertEquals(2, dialogo.opzioniPer(a).size());
        }
    }
}
