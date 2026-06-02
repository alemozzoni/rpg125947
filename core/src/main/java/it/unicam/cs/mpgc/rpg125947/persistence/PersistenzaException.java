package it.unicam.cs.mpgc.rpg125947.persistence;

/**
 * Eccezione non controllata sollevata quando un'operazione di persistenza
 * (caricamento scenario, salvataggio/caricamento partita) fallisce.
 *
 * <p>Avvolge le eccezioni tecniche di basso livello (I/O, parsing XML,
 * validazione XSD) in un tipo di dominio, cosi i livelli superiori non sono
 * costretti a conoscere i dettagli di JAXP.</p>
 */
public class PersistenzaException extends RuntimeException {

    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }
}
