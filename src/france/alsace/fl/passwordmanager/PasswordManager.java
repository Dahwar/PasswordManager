package france.alsace.fl.passwordmanager;

import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Florent
 */
public class PasswordManager extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Password Manager");
        
        ScreenManager sm = new ScreenManager(stage);
        sm.addScreen("home", "FXMLHome.fxml");
        sm.addScreen("passwordManager", "FXMLPasswordManager.fxml");
        
        ScreenManager.setScreen("home");
        
        stage.setOnCloseRequest((event) -> {
            if(ScreenManager.getCurrentScreenName().equals("home")) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Quitter");
                alert.setHeaderText("Quitter l'application");
                alert.setContentText("Voulez-vous quitter l'application ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Platform.exit();
                } else {
                    event.consume();
                }
            } else if(ScreenManager.getCurrentScreenName().equals("passwordManager")) {
                if(FXMLPasswordManagerController.isModif()) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle("Enregistrer");
                    alert.setHeaderText("Enregistrer avant de quitter");
                    alert.setContentText("Voulez-vous enregistrer avant de quitter l'application ?");
                    
                    ButtonType buttonTypeYes = new ButtonType("Oui");
                    ButtonType buttonTypeNo = new ButtonType("Non");
                    ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
                    
                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    
                    if (result.get() == buttonTypeYes){
                        FXMLPasswordManagerController.saveListInFile();
                        Platform.exit();
                    } else if (result.get() == buttonTypeNo) {
                        Platform.exit();
                    } else {
                        event.consume();
                    }
                } else {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle("Quitter");
                    alert.setHeaderText("Quitter l'application");
                    alert.setContentText("Voulez-vous quitter l'application ?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        Platform.exit();
                    } else {
                        event.consume();
                    }
                }
            }
        });
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
    }
    
}
