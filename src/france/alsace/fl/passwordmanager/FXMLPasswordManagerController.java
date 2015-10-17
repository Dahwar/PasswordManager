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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Florent
 */
public class FXMLPasswordManagerController implements Initializable {
    @FXML
    private TextField lifetimePasswordTextField;
    @FXML
    private ListView<MyCipheredInfo> itemListView;
    @FXML
    private Label errorLabel;
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
    
    private static ObservableList<MyCipheredInfo> obsListMyInfos = FXCollections.observableArrayList();
    private static String password;
    private static File file;
    
    private static final String ERROR_MISSING_ENTITY = "Veuillez saisir une entité";
    private static final String ERROR_MISSING_ID = "Veuillez saisir un identifiant";
    private static final String ERROR_MISSING_PASSWORD = "Veuillez saisir un mot de passe";
    
    private Encryptor enc = new Encryptor(65536,256);
    private Decryptor dec = new Decryptor(65536,256);
    
    private ObjectOutputStream oos = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {          
        itemListView.setItems(obsListMyInfos);
        itemListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                MyCipheredInfo mci = itemListView.getSelectionModel().getSelectedItem();
                if(mci != null) {
                    entityLabel.setText(mci.getWebSite());
                    idLabel.setText(mci.getId());
                    passwordLabel.setText("");
                    lifetimePasswordLabel.setText(dateFormat.format(mci.getLastChange()));
                    displayPasswordButton.setVisible(true);
                    displayPasswordButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            passwordLabel.setText(dec.decipher(mci.getPassword(), FXMLPasswordManagerController.password));
                            displayPasswordButton.setVisible(false);
                        }  
                    });
                }
            }
        });
    }

    @FXML
    private void newFile(ActionEvent event) {
    }

    @FXML
    private void openFile(ActionEvent event) {
    }

    @FXML
    private void saveFile(ActionEvent event) {
        this.saveListInFile();
    }

    @FXML
    private void saveAsFile(ActionEvent event) {
    }

    @FXML
    private void quit(ActionEvent event) {
    }

    @FXML
    private void updatePassword(ActionEvent event) {
        ScreenManager.setScreen("home");
    }

    @FXML
    private void deleteItem(ActionEvent event) {
    }

    @FXML
    private void addEntity(ActionEvent event) {
        if(!entityTextField.getText().equals("") 
                && !idTextField.getText().equals("") 
                && !passwordTextField.getText().equals("")) {
            obsListMyInfos.add(new MyCipheredInfo(entityTextField.getText(), idTextField.getText(), enc.cipher(passwordTextField.getText(), FXMLPasswordManagerController.password), new Date()));
            entityTextField.clear();
            idTextField.clear();
            passwordTextField.clear();
            errorLabel.setText("");
        } else if(entityTextField.getText().equals("")) {
            errorLabel.setText(ERROR_MISSING_ENTITY);
        } else if(idTextField.getText().equals("")) {
            errorLabel.setText(ERROR_MISSING_ID);
        } else if(passwordTextField.getText().equals("")) {
            errorLabel.setText(ERROR_MISSING_PASSWORD);
        }
    }

    @FXML
    private void displayPassword(ActionEvent event) {
        
    }
    
    private void saveListInFile() {
        List<MyCipheredInfo> list = new ArrayList<>();
        for(MyCipheredInfo mci : FXMLPasswordManagerController.obsListMyInfos) {
            list.add(mci);
        }
        
        try {
            this.getObjectOutputStream().writeObject(list);
        } catch (IOException ex) {
            Logger.getLogger(FXMLPasswordManagerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setPassword(String password) {
        FXMLPasswordManagerController.password = password;
    }
    
    public static void setFile(File file) {
        FXMLPasswordManagerController.file = file;
    }
    
    public static void setObsListMyInfos (ObservableList<MyCipheredInfo> obs) {
        FXMLPasswordManagerController.obsListMyInfos = obs;
    }
    
    private ObjectOutputStream getObjectOutputStream() {
        if(this.oos == null) {
            try {
                this.oos =  new ObjectOutputStream(new FileOutputStream(FXMLPasswordManagerController.file));
            } catch (IOException ex) {
                Logger.getLogger(FXMLPasswordManagerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.oos;
    }
}
