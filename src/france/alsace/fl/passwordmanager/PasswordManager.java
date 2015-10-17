package france.alsace.fl.passwordmanager;

import javafx.application.Application;
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
        stage.setTitle("Test");
        
        ScreenManager sm = new ScreenManager(stage);
        sm.addScreen("home", "FXMLHome.fxml");
        sm.addScreen("passwordManager", "FXMLPasswordManager.fxml");
        
        ScreenManager.setScreen("home");
        
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                Alert alert = new Alert(AlertType.CONFIRMATION);
//                alert.setTitle("Confirmation Dialog");
//                alert.setHeaderText("Look, a Confirmation Dialog");
//                alert.setContentText("Are you ok with this?");
//
//                Optional<ButtonType> result = alert.showAndWait();
//                if (result.get() == ButtonType.OK){
//                    System.out.println("ok");
//                    Platform.exit();
//                } else {
//                    System.out.println("nok");
//                    event.consume();
//                }
//            }
//        });
        
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
        System.out.println("Stage is closing");
    }
    
}
