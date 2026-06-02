package it.unicam.cs.mpgc.rpg125947.app;

import it.unicam.cs.mpgc.rpg125947.logic.MotorePartita;
import it.unicam.cs.mpgc.rpg125947.model.Caso;
import it.unicam.cs.mpgc.rpg125947.persistence.GameStateRepository;
import it.unicam.cs.mpgc.rpg125947.persistence.ScenarioLoader;
import it.unicam.cs.mpgc.rpg125947.persistence.xml.CaricatoreCasoXml;
import it.unicam.cs.mpgc.rpg125947.persistence.xml.XmlGameStateRepository;

/**
 * Contesto applicativo condiviso: raccoglie le collaborazioni di cui i
 * controller hanno bisogno (motore di gioco, caricamento scenario, persistenza,
 * navigazione tra schermate).
 *
 * <p>I controller dipendono da queste <strong>astrazioni</strong>
 * ({@link ScenarioLoader}, {@link GameStateRepository}) e non dalle loro
 * implementazioni XML: sostituirle (JSON, DB) non tocca la UI (Dependency
 * Inversion). E il punto di composizione (composition root) del front-end.</p>
 */
public final class AppContext {

    private final ScenarioLoader scenarioLoader = new CaricatoreCasoXml();
    private final GameStateRepository repository = new XmlGameStateRepository();
    private final MotorePartita motore = new MotorePartita();

    private Caso caso;
    private SceneManager sceneManager;

    /** Carica (una sola volta) e restituisce il caso dello scenario. */
    public Caso getCaso() {
        if (caso == null) {
            caso = scenarioLoader.carica();
        }
        return caso;
    }

    public MotorePartita getMotore() {
        return motore;
    }

    public GameStateRepository getRepository() {
        return repository;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
