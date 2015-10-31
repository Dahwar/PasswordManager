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
 * To manage all the screens of the application
 * @author Florent
 */
public class ScreenManager {
    
    private static HashMap<String, Scene> screens = new HashMap<>();
    private static Stage stage;
    private static String currentScreenName;
    
    /**
     * Constructor
     * @param stage the stage of the application
     */
    public ScreenManager(Stage stage) {
        if(ScreenManager.stage == null) {
            ScreenManager.stage = stage;
        }
    }
    
    /**
     * Dafault constructor
     */
    public ScreenManager() {
    }
    
    /**
     * Add a screen to the ScreenManager
     * @param name the name of the screen
     * @param resource the path of the screen
     */
    public void addScreen(String name, String resource) {
        Scene scene = createScene(loadScreen(getClass().getResource(resource)));
        screens.put(name, scene);
    }
    
    /**
     * Load the screen
     * @param url the url of the screen
     * @return the parent of the scene
     */
    private Parent loadScreen(URL url) {
        try {
            return FXMLLoader.load(url);
        } catch (IOException ex) {
            Logger.getLogger(ScreenManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Create the scene
     * @param root
     * @return the scene
     */
    private Scene createScene(Parent root) {
        if(root != null) {
            return new Scene(root);
        } else {
            return null;
        }
    }
    
    /**
     * Set the screen
     * @param name the name of the screen
     */
    public static void setScreen(String name) {
        if(screens.containsKey(name) && screens.get(name) != null) {
            stage.setScene(screens.get(name));
            currentScreenName = name;
        }
    }
    
    /**
     * Get the current screen name
     * @return the current screen name
     */
    public static String getCurrentScreenName() {
        return currentScreenName;
    }
}
