package it.unicam.cs.mpgc.rpg125947.model.accusa;

import it.unicam.cs.mpgc.rpg125947.util.Validazioni;

/**
 * Risultato della valutazione di un'{@link Accusa}: esito (successo/fallimento)
 * e narrazione conclusiva da mostrare al giocatore.
 *
 * @param successo   {@code true} se l'accusa risolve correttamente il caso
 * @param narrazione testo dell'epilogo
 */
public record EsitoAccusa(boolean successo, String narrazione) {

    public EsitoAccusa {
        Validazioni.nonVuota(narrazione, "narrazione esito");
    }

    public static EsitoAccusa successo(String narrazione) {
        return new EsitoAccusa(true, narrazione);
    }

    public static EsitoAccusa fallimento(String narrazione) {
        return new EsitoAccusa(false, narrazione);
    }
}
