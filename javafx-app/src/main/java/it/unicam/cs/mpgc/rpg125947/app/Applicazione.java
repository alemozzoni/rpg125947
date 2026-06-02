package it.unicam.cs.mpgc.rpg125947.app;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Classe {@link Application} JavaFX: configura lo {@link Stage} principale e
 * mostra la prima schermata (creazione del personaggio).
 */
public class Applicazione extends Application {

    @Override
    public void start(Stage stage) {
        AppContext context = new AppContext();
        SceneManager sceneManager = new SceneManager(stage, context);
        context.setSceneManager(sceneManager);

        stage.setTitle("Delitto al Museo della Computazione");
        stage.setMinWidth(960);
        stage.setMinHeight(600);

        try {
            sceneManager.mostraCreazione();
            stage.show();
        } catch (RuntimeException e) {
            mostraErroreFatale(e);
        }
    }

    private void mostraErroreFatale(Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di avvio");
        alert.setHeaderText("Impossibile avviare il gioco");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
