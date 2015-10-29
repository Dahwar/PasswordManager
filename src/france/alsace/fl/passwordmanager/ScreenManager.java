/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Florent
 */
public class ScreenManager {
    
    private static HashMap<String, Scene> screens = new HashMap<>();
    private static Stage stage;
    private static String currentScreenName;
    
    public ScreenManager(Stage stage) {
        if(this.stage == null) {
            this.stage = stage;
        }
    }
    
    public ScreenManager() {
    }
    
    public void addScreen(String name, String resource) {
        Scene scene = createScene(loadScreen(getClass().getResource(resource)));
        screens.put(name, scene);
    }
    
    private Parent loadScreen(URL url) {
        try {
            return FXMLLoader.load(url);
        } catch (IOException ex) {
            Logger.getLogger(ScreenManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private Scene createScene(Parent root) {
        if(root != null) {
            return new Scene(root);
        } else {
            return null;
        }
    }
    
    public static void setScreen(String name) {
        if(screens.containsKey(name) && screens.get(name) != null) {
            stage.setScene(screens.get(name));
            currentScreenName = name;
        }
    }
    
    public static String getCurrentScreenName() {
        return currentScreenName;
    }
}
