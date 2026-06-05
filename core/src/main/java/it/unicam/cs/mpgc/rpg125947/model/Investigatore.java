package it.unicam.cs.mpgc.rpg125947.model;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Il protagonista controllato dal giocatore: una vera <strong>scheda
 * personaggio</strong> da gioco di ruolo.
 *
 * <p>Oltre al nome, incapsula le caratteristiche ({@link Attributo}), il
 * <em>livello</em>, i punti <em>esperienza</em> accumulati e i punti abilita
 * spendibili. Gli attributi determinano la riuscita delle prove; raccogliere
 * indizi fa guadagnare esperienza e, al salire di livello, punti da investire
 * sugli attributi (progressione del personaggio).</p>
 */
public final class Investigatore {

    /** Valore di partenza di ogni attributo prima della distribuzione dei punti. */
    public static final int VALORE_BASE = 1;
    /** Valore assegnato a ogni attributo dal profilo "standard" (costruttore rapido). */
    public static final int VALORE_STANDARD = 3;
    /** Punti caratteristica da distribuire in fase di creazione del personaggio. */
    public static final int PUNTI_CREAZIONE = 8;
    /** Tetto di un attributo raggiungibile distribuendo i punti in creazione. */
    public static final int MASSIMO_IN_CREAZIONE = 6;
    /** Punti abilita guadagnati a ogni passaggio di livello. */
    public static final int PUNTI_PER_LIVELLO = 2;

    private final String nome;
    private final EnumMap<Attributo, Integer> attributi = new EnumMap<>(Attributo.class);
    private int livello = 1;
    private int esperienza;        // esperienza accumulata nel livello corrente
    private int puntiAbilita;      // punti caratteristica non ancora spesi

    /**
     * Profilo "standard": ogni attributo al valore {@link #VALORE_STANDARD}.
     * Usato per partite rapide, dai test e come default di compatibilita.
     */
    public Investigatore(String nome) {
        this(nome, profiloUniforme());
    }

    /**
     * @param nome              nome dell'investigatore
     * @param attributiIniziali valore iniziale di ciascun attributo (tutti richiesti)
     */
    public Investigatore(String nome, Map<Attributo, Integer> attributiIniziali) {
        this.nome = Validazioni.nonVuota(nome, "nome investigatore");
        Validazioni.nonNullo(attributiIniziali, "attributi iniziali");
        for (Attributo a : Attributo.values()) {
            Integer valore = attributiIniziali.get(a);
            if (valore == null || valore < VALORE_BASE) {
                throw new IllegalArgumentException(
                        "Attributo " + a + " mancante o sotto il minimo (" + VALORE_BASE + ")");
            }
            attributi.put(a, valore);
        }
    }

    private static Map<Attributo, Integer> profiloUniforme() {
        EnumMap<Attributo, Integer> m = new EnumMap<>(Attributo.class);
        for (Attributo a : Attributo.values()) {
            m.put(a, VALORE_STANDARD);
        }
        return m;
    }

    /** @return il valore corrente dell'attributo richiesto */
    public int getAttributo(Attributo attributo) {
        return attributi.get(Validazioni.nonNullo(attributo, "attributo"));
    }

    /** @return la scheda degli attributi in sola lettura */
    public Map<Attributo, Integer> getAttributi() {
        return Collections.unmodifiableMap(attributi);
    }

    /**
     * L'attributo su cui l'investigatore e piu specializzato: definisce il suo
     * <strong>stile investigativo</strong> e, di conseguenza, le domande che puo
     * porre e le risposte che ottiene nei dialoghi (vedi
     * {@link it.unicam.cs.mpgc.rpg125947.model.dialogo.Dialogo#opzioniPer}).
     *
     * <p>A parita di valore vince l'attributo che compare prima nell'ordine di
     * {@link Attributo}: la scelta e cosi <em>deterministica</em> e stabile.</p>
     *
     * @return l'attributo con il valore piu alto (mai {@code null})
     */
    public Attributo attributoDominante() {
        Attributo dominante = Attributo.values()[0];
        for (Attributo a : Attributo.values()) {
            if (attributi.get(a) > attributi.get(dominante)) {
                dominante = a;
            }
        }
        return dominante;
    }

    public String getNome() {
        return nome;
    }

    public int getLivello() {
        return livello;
    }

    public int getEsperienza() {
        return esperienza;
    }

    public int getPuntiAbilita() {
        return puntiAbilita;
    }

    /**
     * Esperienza necessaria per passare dal livello corrente al successivo.
     * Cresce con il livello, cosi i primi avanzamenti sono piu rapidi.
     */
    public int esperienzaProssimoLivello() {
        return 30 * livello;
    }

    /**
     * Accredita esperienza, gestendo eventuali passaggi di livello (anche
     * multipli). A ogni livello si guadagnano {@link #PUNTI_PER_LIVELLO} punti
     * abilita da spendere con {@link #potenzia(Attributo)}.
     *
     * @param punti esperienza guadagnata (non negativa)
     * @return {@code true} se almeno un passaggio di livello e avvenuto
     */
    public boolean aggiungiEsperienza(int punti) {
        if (punti < 0) {
            throw new IllegalArgumentException("L'esperienza non puo essere negativa: " + punti);
        }
        esperienza += punti;
        boolean salito = false;
        while (esperienza >= esperienzaProssimoLivello()) {
            esperienza -= esperienzaProssimoLivello();
            livello++;
            puntiAbilita += PUNTI_PER_LIVELLO;
            salito = true;
        }
        return salito;
    }

    /**
     * Spende un punto abilita per aumentare di 1 un attributo.
     *
     * @throws IllegalStateException se non ci sono punti abilita disponibili
     */
    public void potenzia(Attributo attributo) {
        Validazioni.nonNullo(attributo, "attributo");
        if (puntiAbilita <= 0) {
            throw new IllegalStateException("Nessun punto abilita disponibile");
        }
        attributi.merge(attributo, 1, Integer::sum);
        puntiAbilita--;
    }

    /** Reimposta livello, esperienza e punti (usato dalla persistenza al caricamento). */
    public void ripristinaProgressione(int livello, int esperienza, int puntiAbilita) {
        if (livello < 1 || esperienza < 0 || puntiAbilita < 0) {
            throw new IllegalArgumentException("Valori di progressione non validi");
        }
        this.livello = livello;
        this.esperienza = esperienza;
        this.puntiAbilita = puntiAbilita;
    }

    @Override
    public String toString() {
        return "Investigatore " + nome + " (liv. " + livello + ")";
    }
}
