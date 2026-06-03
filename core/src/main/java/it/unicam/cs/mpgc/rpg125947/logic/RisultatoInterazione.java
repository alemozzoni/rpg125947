package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.prova.EsitoProva;

import java.util.Optional;

/**
 * Esito di un'interazione del giocatore (ispezione di un hotspot o interrogatorio)
 * arricchito di tutto cio che serve alla UI per dare riscontro <em>da gioco di
 * ruolo</em>: l'eventuale prova di abilita tentata, l'eventuale indizio scoperto,
 * l'esperienza guadagnata e l'eventuale passaggio di livello.
 *
 * @param prova            esito della prova di abilita, se l'azione ne richiedeva una (altrimenti {@code null})
 * @param indizio          indizio scoperto, se l'azione ha avuto successo (altrimenti {@code null})
 * @param xpGuadagnati     punti esperienza accreditati con questa interazione
 * @param salitoDiLivello  {@code true} se l'esperienza ha fatto salire di livello
 */
public record RisultatoInterazione(EsitoProva prova, Indizio indizio,
                                   int xpGuadagnati, boolean salitoDiLivello) {

    /** Interazione senza effetti (nessuna prova, nessun indizio, nessuna esperienza). */
    public static RisultatoInterazione nessuno() {
        return new RisultatoInterazione(null, null, 0, false);
    }

    /** @return la prova tentata, se l'interazione era una sfida di abilita */
    public Optional<EsitoProva> provaTentata() {
        return Optional.ofNullable(prova);
    }

    /** @return l'indizio scoperto, se l'interazione ha avuto successo */
    public Optional<Indizio> indizioScoperto() {
        return Optional.ofNullable(indizio);
    }

    /** @return {@code true} se e stata tentata una prova ed e fallita */
    public boolean provaFallita() {
        return prova != null && !prova.superata();
    }
}
