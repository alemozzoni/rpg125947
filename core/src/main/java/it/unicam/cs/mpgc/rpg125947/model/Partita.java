package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;

/**
 * Aggregato radice dello <strong>stato di gioco</strong>: chi sta giocando, su
 * quale caso, in quale stanza, in quale fase, con quale taccuino ed eventuale
 * esito.
 *
 * <p>Responsabilita unica (SRP): custodire e far evolvere in modo consistente
 * lo stato della partita. Le operazioni applicative di alto livello (raccolta
 * indizi, valutazione dell'accusa) sono orchestrate dal
 * {@link it.unicam.cs.mpgc.rpg125947.logic.MotorePartita}, che mantiene questa
 * classe libera da dipendenze verso le strategie e la persistenza.</p>
 */
public final class Partita {

    private final Investigatore investigatore;
    private final Caso caso;
    private final Taccuino taccuino = new Taccuino();
    private Stanza stanzaCorrente;
    private FaseDiGioco fase;
    private EsitoAccusa esito; // valorizzato solo a partita conclusa

    public Partita(Investigatore investigatore, Caso caso) {
        this.investigatore = Validazioni.nonNullo(investigatore, "investigatore");
        this.caso = Validazioni.nonNullo(caso, "caso");
        this.stanzaCorrente = caso.getStanzaIniziale();
    }

    /** Sposta l'investigatore in una nuova stanza. */
    public void muoviVerso(Stanza destinazione) {
        this.stanzaCorrente = Validazioni.nonNullo(destinazione, "destinazione");
    }

    /**
     * Cambia la fase di gioco corrente (pattern State) ed esegue l'eventuale
     * hook di ingresso della nuova fase.
     */
    public void cambiaFase(FaseDiGioco nuovaFase) {
        this.fase = Validazioni.nonNullo(nuovaFase, "fase");
        this.fase.onIngresso(this);
    }

    /** Registra l'esito finale dell'accusa. */
    public void registraEsito(EsitoAccusa esito) {
        this.esito = Validazioni.nonNullo(esito, "esito");
    }

    public boolean isConclusa() {
        return esito != null;
    }

    public Investigatore getInvestigatore() {
        return investigatore;
    }

    public Caso getCaso() {
        return caso;
    }

    public Taccuino getTaccuino() {
        return taccuino;
    }

    public Stanza getStanzaCorrente() {
        return stanzaCorrente;
    }

    public FaseDiGioco getFase() {
        return fase;
    }

    public Optional<EsitoAccusa> getEsito() {
        return Optional.ofNullable(esito);
    }
}
