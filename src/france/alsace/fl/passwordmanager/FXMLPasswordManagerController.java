package france.alsace.fl.passwordmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Password Manager Controller class
 * 
 * @author Florent
 */
public class FXMLPasswordManagerController implements Initializable {
    @FXML
    private TextField lifetimePasswordTextField;
    @FXML
    private ListView<MyCipheredInfo> itemListView;
    @FXML
    private Label infoLabel;
    @FXML
    private TextField entityTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label entityLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label lifetimePasswordLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button displayPasswordButton;
    @FXML
    private TextField generatePasswordTextField;
    @FXML
    private TextField sizeGenerationTextField;
    @FXML
    private CheckBox minCheckBox;
    @FXML
    private CheckBox majCheckBox;
    @FXML
    private CheckBox numericCheckBox;
    @FXML
    private CheckBox specialCheckBox;
    @FXML
    private Label generatePasswordLabel;
    
    private static ObservableList<MyCipheredInfo> obsListMyInfos = FXCollections.observableArrayList();
    private static String password;
    private static File file;
    
    private static final String ERROR_MISSING_ENTITY = "Veuillez saisir une entité !";
    private static final String ERROR_MISSING_ID = "Veuillez saisir un identifiant !";
    private static final String ERROR_MISSING_PASSWORD = "Veuillez saisir un mot de passe !";
    
    private static final String ALPHA_MIN = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALPHA_MAJ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL = "/*-+=.?!,;:&'\"()[]{}$%#@<>";
    
    private static final int SIZE_LIMIT_PASSWORD = 1024;
    
    private Encryptor enc = new Encryptor(65536,256);
    private Decryptor dec = new Decryptor(65536,256);
    
    private static ObjectOutputStream oos = null;
    
    private Dialog<String> dialog = new Dialog<>();
    
    //if the item list is modif
    private static boolean modif = false;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Initialize the controller class
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        itemListView.setItems(obsListMyInfos);
        itemListView.setOnMouseClicked((event) -> {
            MyCipheredInfo mci = itemListView.getSelectionModel().getSelectedItem();
            if(mci != null) {
                entityLabel.setText(mci.getWebSite());
                idLabel.setText(mci.getId());
                passwordLabel.setText("");
                lifetimePasswordLabel.setText(dateFormat.format(mci.getLastChange()));
                displayPasswordButton.setVisible(true);
                displayPasswordButton.setOnAction((eventBis) -> {
                    passwordLabel.setText(dec.decipher(mci.getPassword(), FXMLPasswordManagerController.password));
                    displayPasswordButton.setVisible(false);
                });
            }
        });
        
        //configure the dialog
        dialog.setTitle("Changer le mot de passe de l'entité : " + "");
        dialog.setHeaderText("Veuillez entrer votre nouveau mot de passe : ");
        dialog.initStyle(StageStyle.UTILITY);
        
        ImageView secureView = new ImageView(new Image("/img/secure.png"));
        dialog.setGraphic(secureView);
        
        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField password1 = new PasswordField();
        password1.setPromptText("Mot de passe");
        PasswordField password2 = new PasswordField();
        password2.setPromptText("Confirmation");

        grid.add(new Label("Mot de passe : "), 0, 0);
        grid.add(password1, 1, 0);
        grid.add(new Label("Confirmer : "), 0, 1);
        grid.add(password2, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        
        password1.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!isSameNewPassword(password1.getText(), password2.getText()));
        });
        
        password2.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!isSameNewPassword(password1.getText(), password2.getText()));
        });
        
        dialog.setResultConverter(dialogButton -> {
            String pwd = password1.getText();
            password1.clear();
            password2.clear();
            if (dialogButton == okButtonType) {
                return pwd;
            }
            return null;
        });
        
        //configure the refresh of listView
        lifetimePasswordTextField.setOnKeyReleased((event) -> {
            itemListView.refresh();
        });
        
        //set the cell factory of listView
        itemListView.setCellFactory((list) -> { 
            return new ListCell<MyCipheredInfo>() {
                @Override
                 public void updateItem(final MyCipheredInfo item, boolean empty) {
                    super.updateItem(item, empty);    
                    if (item != null) {
                        Label label = new Label(item.toString());
                        
                        long l = (lifetimePasswordTextField.getText().length() <= 0)?0:Long.parseLong(lifetimePasswordTextField.getText());
                        
                        if(((new Date().getTime())-(item.getLastChange().getTime())) > 
                                (l*1000*60*60*24)) {
                            label.setTextFill(Color.web("#FF0000"));
                            label.setStyle("-fx-font-weight: bold;");
                        } else {
                            label.setTextFill(Color.web("#000000"));
                        }

                        setGraphic(label);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
    }

    /**
     * Check if the two string are the same and not empty
     * @param s1 first string
     * @param s2 second string
     * @return true if the string are the same and not empty, false otherwise
     */
    private boolean isSameNewPassword(String s1, String s2) {
        return (s1.equals(s2) && !s1.isEmpty() && !s2.isEmpty());
    }

    /**
     * Save the file when click on menu fichier/enregistrers
     * @param event 
     */
    @FXML
    private void saveFile(ActionEvent event) {
        saveListInFile();
    }

    /**
     * Quit the application when click on menu fichier/quitter (with a confirmation dialog)
     * @param event 
     */
    @FXML
    private void quit(ActionEvent event) {
        if(isModif()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Enregistrer");
            alert.setHeaderText("Enregistrer avant de quitter");
            alert.setContentText("Voulez-vous enregistrer avant de quitter ?");

            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non");
            ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonTypeYes){
                saveListInFile();
                ScreenManager.setScreen("home");
                eraseField();
            } else if (result.get() == buttonTypeNo) {
                ScreenManager.setScreen("home");
                eraseField();
            } else {
                event.consume();
            }
        } else {
            ScreenManager.setScreen("home");
            eraseField();
        }
    }

    /**
     * Update the password
     * @param event 
     */
    @FXML
    private void updatePassword(ActionEvent event) {
        if(itemListView.getSelectionModel().getSelectedItem()!=null) {
            Optional<String> result = dialog.showAndWait();
                
            result.ifPresent(newPassword -> {
                itemListView.getSelectionModel().getSelectedItem().setPassword(enc.cipher(newPassword, FXMLPasswordManagerController.password));
                itemListView.getSelectionModel().getSelectedItem().setLastChange(new Date());
                itemListView.refresh();
                displayPasswordButton.setVisible(true);
                lifetimePasswordLabel.setText(dateFormat.format(itemListView.getSelectionModel().getSelectedItem().getLastChange()));
                passwordLabel.setText("");
                modif = true;
            });
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Erreur");
            alert.setHeaderText("Pas d'entité sélectionnée");
            alert.setContentText("Veuillez sélectionner une entité à mettre à jour !");

            alert.showAndWait();
        }
    }

    /**
     * Delete a item of the list (with a confirmation dialog)
     * @param event 
     */
    @FXML
    private void deleteItem(ActionEvent event) {        
        if(itemListView.getSelectionModel().getSelectedItem()!=null) {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.initStyle(StageStyle.UTILITY);
            confirm.setTitle("Suppression de l'entité : ");
            confirm.setHeaderText("Supprimer une entité");
            confirm.setContentText("Êtes-vous sur de vouloir supprimer cette entité ?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            
            if (result.get() == ButtonType.OK){
                obsListMyInfos.remove(itemListView.getSelectionModel().getSelectedItem());
                itemListView.getSelectionModel().clearSelection();

                entityLabel.setText("");
                idLabel.setText("");
                lifetimePasswordLabel.setText("");
                passwordLabel.setText("");
                displayPasswordButton.setVisible(false);
                modif = true;
            }            
            
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Erreur");
            alert.setHeaderText("Pas d'entité sélectionnée");
            alert.setContentText("Veuillez sélectionner une entité à supprimer !");

            alert.showAndWait();
        }
    }

    /**
     * Add a entity
     * @param event 
     */
    @FXML
    private void addEntity(ActionEvent event) {
        if(!entityTextField.getText().equals("") 
                && !idTextField.getText().equals("") 
                && !passwordTextField.getText().equals("")) {
            obsListMyInfos.add(new MyCipheredInfo(entityTextField.getText(), idTextField.getText(), enc.cipher(passwordTextField.getText(), FXMLPasswordManagerController.password), new Date()));
            infoLabel.setText("Entité ajoutée");
            
            Thread t = new Thread()  {
                @Override
                public void run() {
                    FadeTransition fadeTransition = 
                        new FadeTransition(Duration.millis(2000), infoLabel);
                    fadeTransition.setFromValue(1.0f);
                    fadeTransition.setToValue(0.0f);
                    fadeTransition.setCycleCount(1);
                    fadeTransition.setAutoReverse(true);

                    fadeTransition.play();
                    fadeTransition.setOnFinished((event) -> {
                        infoLabel.setText("");
                    });
                }
            };
            t.start();
                    
            entityTextField.clear();
            idTextField.clear();
            passwordTextField.clear();
            modif = true;
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Erreur");
            alert.setHeaderText("Entité incomplète");

            if(entityTextField.getText().equals("")) {
                alert.setContentText(ERROR_MISSING_ENTITY);
            } else if(idTextField.getText().equals("")) {
                alert.setContentText(ERROR_MISSING_ID);
            } else if(passwordTextField.getText().equals("")) {
                alert.setContentText(ERROR_MISSING_PASSWORD);
            }
            
            alert.showAndWait();
        }
    }

    /**
     * Save the list in a file
     */
    public static void saveListInFile() {
        List<MyCipheredInfo> list = new ArrayList<>();
        for(MyCipheredInfo mci : FXMLPasswordManagerController.obsListMyInfos) {
            list.add(mci);
        }
        
        try {
            getObjectOutputStream().writeObject(list);
        } catch (IOException ex) {
            Logger.getLogger(FXMLPasswordManagerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        modif = false;
    }
    
    /**
     * To pass the password from the Home view to the Password Manager view
     * @param password the password
     */
    public static void setPassword(String password) {
        FXMLPasswordManagerController.password = password;
    }
    
    /**
     * To pass the file from the Home view to the Password Manager view
     * @param file the file who contains the passwords and other infos
     */
    public static void setFile(File file) {
        FXMLPasswordManagerController.file = file;
    }
    
    /**
     * To pass the list from the Home view to the Password Manager view
     * @param obs the list of informations
     */
    public static void setObsListMyInfos (ObservableList<MyCipheredInfo> obs) {
        obsListMyInfos.addAll(obs);
    }
    
    /**
     * Get a object output stream on the opened file
     * @return the object output stream on the opened file
     */
    private static ObjectOutputStream getObjectOutputStream() {
        if(oos == null) {
            try {
                oos =  new ObjectOutputStream(new FileOutputStream(file));
            } catch (IOException ex) {
                Logger.getLogger(FXMLPasswordManagerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return oos;
    }
    
    /**
     * Erase all the fields
     */
    private void eraseField() {
        file = null;
        password = null;
        obsListMyInfos.clear();
        oos = null;
        
        entityLabel.setText("");
        idLabel.setText("");
        lifetimePasswordLabel.setText("");
        passwordLabel.setText("");
        displayPasswordButton.setVisible(false);
        
        infoLabel.setText("");
        entityTextField.clear();
        idTextField.clear();
        passwordTextField.clear();
        
        modif = false;
    }
    
    /**
     * Check if the list is modif
     * @return true if list modified, false otherwise
     */
    public static boolean isModif() {
        return modif;
    }

    /**
     * Generate a password
     * @param event 
     */
    @FXML
    private void generatePassword(ActionEvent event) {
        StringBuilder characterList = new StringBuilder();

        if(minCheckBox.isSelected()) {
            characterList.append(ALPHA_MIN);
        }
        
        if(majCheckBox.isSelected()) {
            characterList.append(ALPHA_MAJ);
        }
        
        if(numericCheckBox.isSelected()) {
            characterList.append(NUMERIC);
        }
        
        if(specialCheckBox.isSelected()) {
            characterList.append(SPECIAL);
        }
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Erreur");
        
        if(!sizeGenerationTextField.getText().isEmpty() && characterList.toString().length()>0) {
            StringBuilder sb = new StringBuilder();
            
            try {
                int counter = 0;
                
                for(int i=0; i<Integer.parseInt(sizeGenerationTextField.getText()); i++) {
                    if(counter>=SIZE_LIMIT_PASSWORD) {
                        alert.setHeaderText("Longueur dépassée");
                        alert.setContentText("Limite taille mot de passe atteinte : " + SIZE_LIMIT_PASSWORD);
                        alert.showAndWait();
                        break;
                    }
                    double idx = Math.random() * characterList.toString().length();
                    sb.append(characterList.toString().charAt((int)idx));
                    counter++;
                }
                generatePasswordTextField.setText(sb.toString());
                generatePasswordTextField.requestFocus();
                generatePasswordTextField.selectAll();
                
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(generatePasswordTextField.getText());
                clipboard.setContent(content);
                
                generatePasswordLabel.setText("Mot de passe copié dans le presse-papier");
            
                Thread t = new Thread()  {
                    @Override
                    public void run() {
                        FadeTransition fadeTransition = 
                            new FadeTransition(Duration.millis(2000), generatePasswordLabel);
                        fadeTransition.setFromValue(1.0f);
                        fadeTransition.setToValue(0.0f);
                        fadeTransition.setCycleCount(1);
                        fadeTransition.setAutoReverse(true);

                        fadeTransition.play();
                        fadeTransition.setOnFinished((event) -> {
                            generatePasswordLabel.setText("");
                        });
                    }
                };
                t.start();
                
            } catch (Exception ex) {
                Logger.getLogger(FXMLPasswordManagerController.class.getName()).log(Level.SEVERE, null, ex);
                
                alert.setHeaderText("Longueur incorrecte");
                alert.setContentText("Veuillez indiquer un entier comme longueur.");
                alert.showAndWait();
            }
        } else {
            if(characterList.toString().length()<=0) {
                alert.setHeaderText("Pas de caractère seléctionné");
                alert.setContentText("Veuillez cocher au moins un type de caractère.");
            } else {
                alert.setHeaderText("Longueur incorrecte");
                alert.setContentText("Veuillez indiquer la longueur du mot de passe désiré.");
            }
            alert.showAndWait();
        }
    }
}
