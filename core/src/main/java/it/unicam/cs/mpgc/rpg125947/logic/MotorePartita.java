package it.unicam.cs.mpgc.rpg125947.logic;

import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseEsplorazione;
import it.unicam.cs.mpgc.rpg125947.logic.fase.FaseFinale;
import it.unicam.cs.mpgc.rpg125947.model.Attributo;
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
import it.unicam.cs.mpgc.rpg125947.model.dialogo.OpzioneDialogo;
import it.unicam.cs.mpgc.rpg125947.model.dialogo.Testimonianza;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.FaseDiGioco;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.RisolutoreProva;
import it.unicam.cs.mpgc.rpg125947.model.interfaces.ValutatoreAccusa;
import it.unicam.cs.mpgc.rpg125947.model.personaggio.Sospettato;
import it.unicam.cs.mpgc.rpg125947.model.prova.EsitoProva;
import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Optional;
import java.util.Set;

/**
 * Orchestratore della logica di gioco: espone le operazioni applicative di alto
 * livello (creare/riprendere una partita, muoversi, interrogare, ispezionare,
 * accusare) al di sopra dell'aggregato {@link Partita}.
 *
 * <p>Separare il motore dallo stato (SRP) mantiene {@link Partita} un semplice
 * contenitore consistente, mentre qui vivono le regole d'uso. Sia la regola di
 * vittoria ({@link ValutatoreAccusa}) sia la risoluzione delle prove di abilita
 * ({@link RisolutoreProva}) sono iniettate come Strategy: il motore non conosce
 * <em>come</em> si valuta un'accusa ne <em>come</em> si decide l'esito di una
 * prova, e cio lo rende collaudabile in modo deterministico.</p>
 */
public final class MotorePartita {

    /** Esperienza accreditata per ogni nuovo indizio aggiunto al taccuino. */
    public static final int XP_PER_INDIZIO = 10;

    private final ValutatoreAccusa valutatore;
    private final RisolutoreProva risolutore;
    private Partita partita;

    /** Crea il motore con le strategie standard (valutazione + dado d20). */
    public MotorePartita() {
        this(new ValutatoreAccusaStandard(), new RisolutoreProvaDado());
    }

    public MotorePartita(ValutatoreAccusa valutatore) {
        this(valutatore, new RisolutoreProvaDado());
    }

    public MotorePartita(ValutatoreAccusa valutatore, RisolutoreProva risolutore) {
        this.valutatore = Validazioni.nonNullo(valutatore, "valutatore");
        this.risolutore = Validazioni.nonNullo(risolutore, "risolutore");
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
     * Interroga il personaggio scegliendo un'opzione di dialogo. Se l'opzione e
     * una prova di abilita, la testimonianza (ed eventuale indizio) si ottiene
     * solo superando il controllo; in caso di fallimento si puo ritentare.
     *
     * @return l'esito ricco dell'interazione (prova, indizio, esperienza)
     */
    public RisultatoInterazione interroga(OpzioneDialogo opzione) {
        Validazioni.nonNullo(opzione, "opzione");
        if (opzione.richiedeProva()) {
            EsitoProva esito = tira(opzione.attributoRichiesto().orElseThrow(), opzione.difficolta());
            if (!esito.superata()) {
                return new RisultatoInterazione(esito, null, 0, false);
            }
            return concludi(esito, registraTestimonianza(opzione.risposta()));
        }
        return concludi(null, registraTestimonianza(opzione.risposta()));
    }

    /**
     * Registra una testimonianza nel taccuino e, se essa rivela un indizio, lo
     * risolve dal caso e lo annota anch'esso. Operazione di basso livello senza
     * prove ne esperienza (usata internamente e per testimonianze incondizionate).
     *
     * @return l'eventuale indizio rivelato dalla testimonianza
     */
    public Optional<Indizio> raccogli(Testimonianza testimonianza) {
        Optional<Indizio> rivelato = Optional.ofNullable(registraTestimonianza(testimonianza));
        rivelato.ifPresent(partita.getTaccuino()::registra);
        return rivelato;
    }

    /** Annota un sospettato (con il suo movente) nel taccuino. */
    public void annota(Sospettato sospettato) {
        partita.getTaccuino().notaSospettato(sospettato);
    }

    // --- Ispezione ---------------------------------------------------------

    /**
     * Ispeziona un hotspot. Se la scoperta dell'indizio richiede una prova di
     * abilita, l'indizio si rivela solo superandola.
     *
     * @return l'esito ricco dell'interazione (prova, indizio, esperienza)
     */
    public RisultatoInterazione ispeziona(Hotspot hotspot) {
        Validazioni.nonNullo(hotspot, "hotspot");
        if (hotspot.richiedeProva()) {
            EsitoProva esito = tira(hotspot.getAttributoRichiesto().orElseThrow(), hotspot.getDifficolta());
            if (!esito.superata()) {
                return new RisultatoInterazione(esito, null, 0, false);
            }
            return concludi(esito, hotspot.ispeziona().orElse(null));
        }
        return concludi(null, hotspot.ispeziona().orElse(null));
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

    // --- Interni -----------------------------------------------------------

    /** Esegue una prova leggendo il valore dell'attributo dall'investigatore. */
    private EsitoProva tira(Attributo attributo, int difficolta) {
        int valore = partita.getInvestigatore().getAttributo(attributo);
        return risolutore.tira(attributo, valore, difficolta);
    }

    /** Registra la testimonianza e risolve l'eventuale indizio rivelato (senza annotarlo). */
    private Indizio registraTestimonianza(Testimonianza testimonianza) {
        partita.getTaccuino().registra(testimonianza);
        return testimonianza.indizioRivelato()
                .flatMap(id -> partita.getCaso().getIndizio(id))
                .orElse(null);
    }

    /**
     * Annota l'indizio scoperto (se nuovo) e accredita l'esperienza, gestendo il
     * passaggio di livello. Confeziona il risultato dell'interazione.
     */
    private RisultatoInterazione concludi(EsitoProva prova, Indizio indizio) {
        if (indizio == null) {
            return new RisultatoInterazione(prova, null, 0, false);
        }
        Taccuino taccuino = partita.getTaccuino();
        boolean nuovo = taccuino.registra(indizio);
        int xp = nuovo ? XP_PER_INDIZIO : 0;
        boolean salito = nuovo && partita.getInvestigatore().aggiungiEsperienza(xp);
        return new RisultatoInterazione(prova, indizio, xp, salito);
    }
}
