package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseEsplorazione;
import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseFinale;
import it.unicam.cs.mpgc.rpg125947.model.AzioneGiocatore;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.model.Hotspot;
import it.unicam.cs.mpgc.rpg125947.model.Indizio;
import it.unicam.cs.mpgc.rpg125947.model.Investigatore;
import it.unicam.cs.mpgc.rpg125947.model.Partita;
import it.unicam.cs.mpgc.rpg125947.model.Stanza;
import it.unicam.cs.mpgc.rpg125947.model.Taccuino;
import it.unicam.cs.mpgc.rpg125947.model.accusa.Accusa;
import it.unicam.cs.mpgc.rpg125947.model.accusa.EsitoAccusa;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.ValutatoreAccusa;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;
import java.util.Set;

/**
 * Orchestratore della logica di gioco: espone le operazioni applicative di alto
 * livello (creare/riprendere una partita, muoversi, raccogliere testimonianze e
 * indizi, ispezionare, accusare) al di sopra dell'aggregato {@link Partita}.
 *
 * <p>Separare il motore dallo stato (SRP) mantiene {@link Partita} un semplice
 * contenitore consistente, mentre qui vivono le regole d'uso. La regola di
 * vittoria e iniettata come {@link ValutatoreAccusa} (Strategy + Dependency
 * Inversion): il motore non conosce <em>come</em> si valuta un'accusa.</p>
 */
public final class MotorePartita {

    private final ValutatoreAccusa valutatore;
    private Partita partita;

    /** Crea il motore con la strategia di valutazione standard. */
    public MotorePartita() {
        this(new ValutatoreAccusaStandard());
    }

    public MotorePartita(ValutatoreAccusa valutatore) {
        this.valutatore = Validazioni.nonNullo(valutatore, "valutatore");
    }

    /** Avvia una nuova partita nella stanza iniziale, in fase di esplorazione. */
    public Partita nuovaPartita(Investigatore investigatore, Caso caso) {
        partita = new Partita(investigatore, caso);
        partita.cambiaFase(FaseEsplorazione.INSTANCE);
        return partita;
    }

    /** Riprende una partita caricata, ripristinando la fase di esplorazione. */
    public void riprendi(Partita partita) {
        this.partita = Validazioni.nonNullo(partita, "partita");
        if (partita.getFase() == null) {
            partita.cambiaFase(FaseEsplorazione.INSTANCE);
        }
    }

    public Partita getPartita() {
        return partita;
    }

    // --- Transizioni di fase (pattern State) -------------------------------

    /**
     * Inoltra un'azione alla fase corrente e applica l'eventuale transizione.
     *
     * @return la fase risultante
     */
    public FaseDiGioco eseguiAzione(AzioneGiocatore azione) {
        FaseDiGioco prossima = partita.getFase().gestisci(azione, partita);
        if (prossima != partita.getFase()) {
            partita.cambiaFase(prossima);
        }
        return partita.getFase();
    }

    // --- Navigazione -------------------------------------------------------

    /**
     * @return {@code true} se la stanza e accessibile (non chiusa a chiave, o il
     *         relativo indizio-chiave e gia nel taccuino)
     */
    public boolean puoEntrare(Stanza stanza) {
        if (!stanza.isChiusaAChiave()) {
            return true;
        }
        return stanza.getIdChiaveRichiesta()
                .map(idChiave -> partita.getTaccuino().getIndizi().stream()
                        .anyMatch(indizio -> indizio.getId().equals(idChiave)))
                .orElse(true);
    }

    /**
     * Tenta di muovere l'investigatore nella stanza indicata.
     *
     * @return {@code true} se lo spostamento e avvenuto, {@code false} se la
     *         stanza e chiusa a chiave e non ancora accessibile
     */
    public boolean muovi(Stanza destinazione) {
        if (!puoEntrare(destinazione)) {
            return false;
        }
        partita.muoviVerso(destinazione);
        return true;
    }

    // --- Dialogo -----------------------------------------------------------

    /**
     * Registra una testimonianza nel taccuino e, se essa rivela un indizio, lo
     * risolve dal caso e lo annota anch'esso.
     *
     * @return l'eventuale indizio rivelato dalla testimonianza
     */
    public Optional<Indizio> raccogli(Testimonianza testimonianza) {
        Taccuino taccuino = partita.getTaccuino();
        taccuino.registra(testimonianza);
        Optional<Indizio> rivelato = testimonianza.indizioRivelato()
                .flatMap(id -> partita.getCaso().getIndizio(id));
        rivelato.ifPresent(taccuino::registra);
        return rivelato;
    }

    /** Annota un sospettato (con il suo movente) nel taccuino. */
    public void annota(Sospettato sospettato) {
        partita.getTaccuino().notaSospettato(sospettato);
    }

    // --- Ispezione ---------------------------------------------------------

    /**
     * Ispeziona un hotspot e registra l'eventuale indizio rivelato.
     *
     * @return l'eventuale indizio trovato
     */
    public Optional<Indizio> ispeziona(Hotspot hotspot) {
        Optional<Indizio> trovato = hotspot.ispeziona();
        trovato.ifPresent(partita.getTaccuino()::registra);
        return trovato;
    }

    // --- Accusa ------------------------------------------------------------

    /**
     * Formula l'accusa, la valuta con la Strategy, ne registra l'esito e porta
     * la partita alla fase finale.
     */
    public EsitoAccusa accusa(Sospettato sospettato, Set<Indizio> prove) {
        Accusa accusa = new Accusa(sospettato, prove);
        EsitoAccusa esito = valutatore.valuta(accusa, partita.getCaso());
        partita.registraEsito(esito);
        partita.cambiaFase(FaseFinale.INSTANCE);
        return esito;
    }
}
