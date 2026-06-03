package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.model.Attributo;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.RisolutoreProva;
import it.unicam.cs.mpgc.rpg125947.model.prova.EsitoProva;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Random;

/**
 * Risoluzione delle prove con un classico <strong>tiro di dado a 20 facce</strong>
 * (d20) sommato al valore dell'attributo: la prova e superata se
 * {@code d20 + valoreAttributo >= difficolta}.
 *
 * <p>L'aleatorieta del dado e l'ingrediente che rende ogni partita diversa e da
 * la tensione tipica del gioco di ruolo. La sorgente casuale e iniettabile, cosi
 * il comportamento puo essere reso riproducibile (seme fisso) nei test.</p>
 */
public final class RisolutoreProvaDado implements RisolutoreProva {

    /** Numero di facce del dado: il d20 dei giochi di ruolo. */
    public static final int FACCE_DADO = 20;

    private final Random dado;

    /** Usa una sorgente casuale non deterministica (gioco reale). */
    public RisolutoreProvaDado() {
        this(new Random());
    }

    /** Permette di iniettare un {@link Random} con seme noto per i test. */
    public RisolutoreProvaDado(Random dado) {
        this.dado = Validazioni.nonNullo(dado, "dado");
    }

    @Override
    public EsitoProva tira(Attributo attributo, int valoreAttributo, int difficolta) {
        int tiro = dado.nextInt(FACCE_DADO) + 1; // 1..20
        boolean superata = tiro + valoreAttributo >= difficolta;
        return new EsitoProva(attributo, tiro, valoreAttributo, difficolta, superata);
    }
}
