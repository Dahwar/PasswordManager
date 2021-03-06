/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

/**
 * FXML Home Controller class
 * @author Florent
 */
public class FXMLHomeController implements Initializable {
    
    private static final String ERROR = "Une erreur est survenue.";
    private static final String ERROR_PASSWORD = "Les mots de passe sont différents !";
    private static final String ERROR_FILE_ALREADY_EXIST = "Le fichier existe déjà, veuillez saisir un nom de fichier différent.";
    
    private static final String TITLE_CHOOSE_DIRECTORY = "Choisir un emplacement pour le fichier";
    private static final String TITLE_OPEN_FILE = "Ouvrir un fichier";
    
    private static final String FILE_TYPE = "Fichiers chiffrées";
    private static final String FILE_EXTENSION = "fpm";
    
    @FXML
    private Button createButton;
    @FXML
    private TextField newFileDirectoryTextField;
    @FXML
    private TextField newFileNameTextField;
    @FXML
    private PasswordField filePasswordPasswordField;
    @FXML
    private PasswordField filePasswordConfirmPasswordField;
    @FXML
    private Label newFileErrorLabel;
    @FXML
    private TextField openFileTextField;
    @FXML
    private PasswordField passwordOpenFilePasswordField;
    @FXML
    private Button openFileButton;
    @FXML
    private Label openFileErrorLabel;

    /**
     * Initialize the controller class.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.newFileErrorLabel.setText("");
        this.openFileErrorLabel.setText("");
        this.createButton.setDisable(true);
        this.openFileButton.setDisable(true);
    }    

    /**
     * Call by the menu fichier/quitter
     * @param event 
     */
    @FXML
    private void quitMenuItem(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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

    /**
     * Call by the button create new file
     * @param event 
     */
    @FXML
    private void createNewFileButton(ActionEvent event) {
        File newFile = new File(this.newFileDirectoryTextField.getText() + "\\" + this.newFileNameTextField.getText() + "." + FILE_EXTENSION);
        if(newFile.exists()) {
            this.newFileErrorLabel.setText(ERROR_FILE_ALREADY_EXIST);
        } else {
            try {
                newFile.createNewFile();
                FXMLPasswordManagerController.setPassword(this.filePasswordPasswordField.getText());
                FXMLPasswordManagerController.setFile(newFile);
                ScreenManager.setScreen("passwordManager");
                eraseField();
            } catch (IOException ex) {
                this.newFileErrorLabel.setText(ERROR);
                Logger.getLogger(FXMLHomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }

    /**
     * Open the directory chooser when click on the choose folder field
     * @param event 
     */
    @FXML
    private void openDirectoryChooser(MouseEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(TITLE_CHOOSE_DIRECTORY);
        File file = dc.showDialog(this.createButton.getScene().getWindow());
        if(file!=null) {
            this.newFileDirectoryTextField.setText(file.getPath());
        }
        checkForActivateCreateButton();
    }

    /**
     * Check if passwords are identical on key release
     * @param event 
     */
    @FXML
    private void checkPassword(KeyEvent event) {
        if(!this.filePasswordPasswordField.getText().equals(this.filePasswordConfirmPasswordField.getText())) {
            this.newFileErrorLabel.setText(ERROR_PASSWORD);
        } else {
            this.newFileErrorLabel.setText("");
        }
        checkForActivateCreateButton();
    }
    
    /**
     * Able or disable create new file button
     */
    private void checkForActivateCreateButton() {
        if(this.filePasswordPasswordField.getText().equals(this.filePasswordConfirmPasswordField.getText())
                && !this.filePasswordPasswordField.getText().equals("")
                && !this.newFileDirectoryTextField.getText().equals("")
                && !this.newFileNameTextField.getText().equals("")) {
            this.createButton.setDisable(false);
        } else {
            this.createButton.setDisable(true);
        }
    }
    
    /**
     * Able or disable open file button
     */
    private void checkForActivateOpenButton() {
        if(!this.openFileTextField.getText().equals("") 
                && !this.passwordOpenFilePasswordField.getText().equals("")) {
            this.openFileButton.setDisable(false);
        } else {
            this.openFileButton.setDisable(true);
        }
    }

    /**
     * Check if all field aren't empty when feed file name
     * @param event 
     */
    @FXML
    private void checkFileName(KeyEvent event) {
        this.checkForActivateCreateButton();
    }

    /**
     * Open the file chooser when click on the choose file field
     * @param event 
     */
    @FXML
    private void openFileChooser(MouseEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle(TITLE_OPEN_FILE);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(FILE_TYPE, "*." + FILE_EXTENSION)
            );
        File file = fc.showOpenDialog(this.createButton.getScene().getWindow());
        if(file!=null) {
            this.openFileTextField.setText(file.getPath());
        }
        this.checkForActivateOpenButton();
    }

    /**
     * Open file when click on open file button
     * @param event 
     */
    @FXML
    private void openCipheredFile(ActionEvent event) {
        ObjectInputStream ois = null;
        try {
            File file = new File(this.openFileTextField.getText());
            ois = new ObjectInputStream(new FileInputStream(file));
            ArrayList<MyCipheredInfo> al = (ArrayList<MyCipheredInfo>) ois.readObject();
            
            FXMLPasswordManagerController.setFile(file);
            FXMLPasswordManagerController.setPassword(this.passwordOpenFilePasswordField.getText());
            FXMLPasswordManagerController.setObsListMyInfos(FXCollections.observableArrayList(al));
            
            ScreenManager.setScreen("passwordManager");
            eraseField();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLHomeController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(FXMLHomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Check if all field aren't empty when feed file name
     * @param event 
     */
    @FXML
    private void checkOpenFileName(KeyEvent event) {
        this.checkForActivateOpenButton();
    }
    
    /**
     * Erase all fields
     */
    private void eraseField() {
        this.newFileDirectoryTextField.clear();
        this.newFileNameTextField.clear();
        this.filePasswordPasswordField.clear();
        this.filePasswordConfirmPasswordField.clear();
        
        this.openFileTextField.clear();
        this.passwordOpenFilePasswordField.clear();
        
        this.newFileErrorLabel.setText("");
        this.openFileErrorLabel.setText("");
        
        this.createButton.setDisable(true);
        this.openFileButton.setDisable(true);
    }
}
