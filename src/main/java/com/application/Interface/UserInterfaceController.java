package com.application.Interface;

import com.application.cipherAlgorithm.AesCipher;
import com.application.cipherAlgorithm.CaesarCipher;
import com.application.cipherAlgorithm.DesCipher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class UserInterfaceController {
    @FXML
    private AnchorPane root;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea inputKey;
    @FXML
    private TextArea outputText;
    @FXML
    private TextArea outputKey;
    @FXML
    private VBox outPutKeyBox;
    @FXML
    private VBox inPutKeyBox;
    @FXML
    private ChoiceBox<String> choiceBox1;
    @FXML
    private ChoiceBox<String> choiceBox2;
    @FXML
    private ChoiceBox<String> choiceBox3;
    @FXML
    private Button encryptButton;
    @FXML
    private Button decryptButton;
    @FXML
    private VBox main;
    @FXML
    private Text user;
    @FXML
    private Text keyHint;
    @FXML
    private Text inputHint;
    @FXML
    private HBox loadFromDB;
    @FXML
    private HBox saveToDB;
    @FXML
    private TextField setName;
    @FXML
    private HBox radioBox1;
    @FXML
    private HBox radioBox2;

    private ToggleGroup toggleGroup1;
    private ToggleGroup toggleGroup2;
    private static final String serverAddress = "127.0.0.1";
    private static final int port = 9090;
    public void initialize() {
        user.setText(UserInterface.getUser() + ", loged in.");
        main.setVisible(false);
        decryptButton.setVisible(false);
        encryptButton.setVisible(false);
        outputText.setEditable(false);
        outputKey.setEditable(false);
        setName.setPromptText("Set a unique name");
        initRadioButton1();
        initRadioButton2();
        initChoiceBox12();

    }

    public void initRadioButton1(){
        toggleGroup1 = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("Encrypted Text");
        radioButton1.setToggleGroup(toggleGroup1);

        RadioButton radioButton2 = new RadioButton("Key");
        radioButton2.setToggleGroup(toggleGroup1);

        RadioButton radioButton3 = new RadioButton("Both");
        radioButton3.setToggleGroup(toggleGroup1);
        toggleGroup1.selectToggle(radioButton3);

        radioBox1.getChildren().addAll(radioButton1, radioButton2, radioButton3);

    }
    public String encryptKey(String key){
        CaesarCipher caesarCipher = new CaesarCipher();
        String encryptedKey = caesarCipher.encrypt(9, key);
        return encryptedKey;
    }
    public String decryptKey(String encryptedKey){
        CaesarCipher caesarCipher = new CaesarCipher();
        String key = caesarCipher.decrypt(9, encryptedKey);
        return key;
    }
    public boolean checkKeyName(String KeyName) throws IOException {
        boolean validKeyName = true;
        try {
            Socket socket = new Socket(serverAddress, port);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String dataToSend = "CheckKeyName," + KeyName + "," + UserInterface.getUser();
            writer.write(dataToSend);
            writer.newLine();
            writer.flush();
            String response =  reader.readLine();
            if(response.equals("invalidKeyName")){
                showSavedAlert("Key name must be valid. Please enter a valid key name.");
                validKeyName = false;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return validKeyName;
    }
    @FXML
    public void saveToDB() throws IOException {

        RadioButton selectedRadio = (RadioButton) toggleGroup2.getSelectedToggle();
        String radioValue = selectedRadio.getText();
        String keyName = setName.getText();
        String encryptedText = outputText.getText();
        String key = outputKey.getText();
        String encryptedKey = encryptKey(key);
        if(encryptedText.isEmpty() || key.isEmpty()){
            showSavedAlert("Please encrypted your data first.");
            return;
        }
        if(keyName.isEmpty()){
            showSavedAlert("Must set a name for your encrypted data.");
            return;
        }else if(!checkKeyName(keyName)){
            return;
        }

        String algorithm = choiceBox1.getValue();
        String dataToSend = "Savekey," + UserInterface.getUser() + "," + keyName + "," + radioValue + ","+ algorithm + ",";
        switch (radioValue) {
            case "Encrypted Text":
                dataToSend += encryptedText;
                break;
            case "Key":
                dataToSend += encryptedKey;

                break;
            case "Both":
                dataToSend += encryptedText + "," + encryptedKey;
                break;
        }

        try {
            Socket socket = new Socket(serverAddress, port);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write(dataToSend);
            writer.newLine();
            writer.flush();
            String response =  reader.readLine();
            if(response.equals("DatabaseUpdated")){
                showSavedAlert("Saved to database successfully.");
                setName.clear();
                initChoiceBox3();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void showSavedAlert(String notification){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(notification);
        alert.showAndWait();
    }

    @FXML
    public void loadFromDB(){
        RadioButton selectedRadio = (RadioButton) toggleGroup1.getSelectedToggle();
        String radioValue = selectedRadio.getText();
        String keyName = choiceBox3.getValue();
        if(keyName.isEmpty()){
            showSavedAlert("Please select a name first.");
            return;
        }

        String dataToSend = "Loadkey," + UserInterface.getUser() + "," + keyName;

        try {
            Socket socket = new Socket(serverAddress, port);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write(dataToSend);
            writer.newLine();
            writer.flush();
            String response =  reader.readLine();
            if(response.startsWith("LoadKeyToUser")){
                String[] receivedData = response.split(",");
                String text = receivedData[1];
                String encryptedKey = receivedData[2];
                String key = decryptKey(encryptedKey);
                switch (radioValue) {
                    case "Encrypted Text":
                        inputText.setText(text);
                        break;
                    case "Key":
                        inputKey.setText(key);
                        break;
                    case "Both":
                        inputText.setText(text);
                        inputKey.setText(key);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initRadioButton2(){
        toggleGroup2 = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("Encrypted Text");
        radioButton1.setToggleGroup(toggleGroup2);

        RadioButton radioButton2 = new RadioButton("Key");
        radioButton2.setToggleGroup(toggleGroup2);

        RadioButton radioButton3 = new RadioButton("Both");
        radioButton3.setToggleGroup(toggleGroup2);
        toggleGroup2.selectToggle(radioButton3);

        radioBox2.getChildren().addAll(radioButton1, radioButton2, radioButton3);

    }
    public void initChoiceBox3(){
        choiceBox3.getItems().clear();
        try {
            Socket socket = new Socket(serverAddress, port);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String algorithm = choiceBox1.getValue();
            String dataToSend = "Getkeys," + UserInterface.getUser() + "," + algorithm;

            writer.write(dataToSend);
            writer.newLine();
            writer.flush();

            String receivedData = reader.readLine();

            if(receivedData.startsWith("keyName,")){
                String[] keyNames = receivedData.split(",");

                for(int i = 1; i < keyNames.length;i++ ){
                    choiceBox3.getItems().add(keyNames[i]);
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void initChoiceBox12(){
        choiceBox1.getItems().addAll("Caesar Cipher", "DES Cipher", "AES Cipher");
        choiceBox1.setValue("");
        choiceBox2.getItems().addAll("Encrypt", "Decrypt");
        choiceBox2.setValue("");
        choiceBox1.setOnAction(event -> {
            inputHint.setText("");
            keyHint.setText("");
            String selectedOption1 = choiceBox1.getValue();
            String selectedOption2 = choiceBox2.getValue();
            initChoiceBox3();
            outputKey.clear();
            outputText.clear();
            if(selectedOption2 == "Encrypt"){
                decryptButton.setVisible(false);
                encryptButton.setVisible(true);
                if(selectedOption1 == "Caesar Cipher"){
                    outPutKeyBox.setVisible(false);
                    inPutKeyBox.setVisible(true);
                }else{
                    outPutKeyBox.setVisible(true);
                    inPutKeyBox.setVisible(false);
                }
            }else if(selectedOption2 == "Decrypt"){
                decryptButton.setVisible(true);
                encryptButton.setVisible(false);
                outPutKeyBox.setVisible(false);
                inPutKeyBox.setVisible(true);
            }
        });
        choiceBox2.setOnAction(event -> {
            inputHint.setText("");
            keyHint.setText("");
            outputKey.clear();
            outputText.clear();
            main.setVisible(true);
            String selectedOption1 = choiceBox1.getValue();
            String selectedOption2 = choiceBox2.getValue();
            if(selectedOption2 == "Encrypt"){
                loadFromDB.setVisible(false);
                saveToDB.setVisible(true);
                decryptButton.setVisible(false);
                encryptButton.setVisible(true);
                if(selectedOption1 == "Caesar Cipher"){
                    outPutKeyBox.setVisible(false);
                    inPutKeyBox.setVisible(true);
                }else{
                    outPutKeyBox.setVisible(true);
                    inPutKeyBox.setVisible(false);
                }
            }else if(selectedOption2 == "Decrypt"){
                loadFromDB.setVisible(true);
                saveToDB.setVisible(false);
                decryptButton.setVisible(true);
                encryptButton.setVisible(false);
                outPutKeyBox.setVisible(false);
                inPutKeyBox.setVisible(true);
            }
        });
    }
    @FXML
    protected void onEncryptButtonClick() throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String selectedOption = choiceBox1.getValue();
        String text = inputText.getText();
        String key = inputKey.getText();
        String encryptedText;
        if(text.isEmpty()){
            inputHint.setText("Text cannot be empty.");
            return;
        }
        switch (selectedOption) {
            case "Caesar Cipher":
                CaesarCipher caesarCipher = new CaesarCipher();

                try {
                    int keyValue = Integer.parseInt(key);
                    encryptedText = caesarCipher.encrypt(keyValue, text);
                    outputText.setText(encryptedText);
                } catch (NumberFormatException e) {
                    keyHint.setText("Invalid key. Please enter a valid integer key.");
                }
                break;

            case "DES Cipher":
                DesCipher desCipher = new DesCipher();
                String desKey = desCipher.generateKey();
                encryptedText = desCipher.encrypt(text);
                outputText.setText(encryptedText);
                outputKey.setText(desKey);
                break;
            case "AES Cipher":
                AesCipher aesCipher = new AesCipher();
                String aesKey = aesCipher.generateKey();
                encryptedText = aesCipher.encrypt(text);
                outputText.setText(encryptedText);
                outputKey.setText(aesKey);
                break;
        }
    }

    @FXML
    protected void onDecryptButtonClick() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        String selectedOption = choiceBox1.getValue();
        String text = inputText.getText();
        String key = inputKey.getText();
        String decryptedText = null;
        if(text.isEmpty() || key.isEmpty()){
            inputHint.setText("Text and key cannot be empty.");
            return;
        }else {
            inputHint.setText("");
        }
        switch (selectedOption) {
            case "Caesar Cipher":
                CaesarCipher caesarCipher = new CaesarCipher();
                try {
                    int keyValue = Integer.parseInt(key);
                    decryptedText = caesarCipher.decrypt(keyValue, text);
                    outputText.setText(decryptedText);
                    keyHint.setText("");
                } catch (NumberFormatException e) {
                    keyHint.setText("Invalid key. Please enter a valid integer key.");
                }
                break;
            case "DES Cipher":
                DesCipher desCipher = new DesCipher();
                if(key.length() != 12){
                    keyHint.setText("Invalid key. Please enter a valid key.");
                }else{
                    keyHint.setText("");
                    decryptedText = desCipher.decrypt(text, key);
                    outputText.setText(decryptedText);
                }

                break;
            case "AES Cipher":
                AesCipher aesCipher = new AesCipher();
                if(key.length() != 12){
                    keyHint.setText("Invalid key. Please enter a valid key.");
                }else {
                    decryptedText = aesCipher.decrypt(text, key);
                    outputText.setText(decryptedText);
                    keyHint.setText("");
                    break;
                }
        }
        if(decryptedText == null){
            showSavedAlert("Key does not match the text.");
        }
    }
    @FXML
    protected void userSetting(){
        showSettingsDialog();
    }
    private void showSettingsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Configure your settings");

        ChoiceBox<String> themeChoiceBox = new ChoiceBox<>();
        themeChoiceBox.getItems().addAll("theme1", "theme2", "theme3", "theme4", "theme5");

        themeChoiceBox.setValue(UserInterface.getTheme());
        VBox themeBox = new VBox(10, new Label("Select Theme:"), themeChoiceBox);

        PasswordField oldPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        VBox passwordBox = new VBox(10, new Label("Change Password:"), new Label("New Password:"), newPasswordField);

        VBox content = new VBox(10, themeBox, passwordBox);
        DialogPane dialogPane = dialog.getDialogPane();
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image("settingIcon.png"));
        dialogPane.setStyle("-fx-background-color: #EEF5FF;");
        dialogPane.setMinWidth(300);
        dialogPane.setMinHeight(200);
        dialogPane.setContent(content);

        ButtonType okButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(okButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                String newTheme = themeChoiceBox.getValue();

                String oldTheme = UserInterface.getTheme();
                setTheme(root, oldTheme, newTheme);
                UserInterface.setTheme(newTheme);

                CaesarCipher caesarCipher = new CaesarCipher();
                String newPasswordText = newPasswordField.getText();

                String newPassword = caesarCipher.encrypt(7, newPasswordText);

                updateDatabase(newTheme, newPassword);

                showSavedAlert("New configuration saved.");
            }
            return null;
        });

        dialog.showAndWait();
    }

    public void updateDatabase(String newTheme, String newPassword){
        try {

            Socket socket = new Socket(serverAddress, port);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String dataToSend = "Setting," + newTheme + "," + newPassword;

            writer.write(dataToSend);
            writer.newLine();
            writer.flush();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setTheme(AnchorPane root,String oldTheme, String newTheme){
        String oldCSS= root.getStylesheets().toString();
        String newCSS = oldCSS.substring(1, oldCSS.length() - 1);
        newCSS  = newCSS .replace(oldTheme, newTheme);
        root.getStylesheets().clear();
        root.getStylesheets().add(newCSS);

    }

    @FXML
    protected void logout() throws IOException {
        FXMLLoader fxmlLoaderMain = new FXMLLoader(UserInterface.class.getResource("login.fxml"));
        AnchorPane root = fxmlLoaderMain.load();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) main.getScene().getWindow();
        stage.setScene(scene);
    }
    @FXML
    protected void loadTextFromFile(){
        inputText.setText(loadFile());
    }
    @FXML
    protected void loadKeyFromFile(){
        inputKey.setText(loadFile());
    }
    @FXML
    protected String loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File");
        Stage stage = (Stage) main.getScene().getWindow();
        try {
            File selectedFile = fileChooser.showOpenDialog(stage);
            FileReader fileReader = new FileReader(selectedFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String text = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text += line;
            }

            bufferedReader.close();
            fileReader.close();
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void saveTextToFile() {
        String text = outputText.getText();
        saveFile(text);
    }
    @FXML
    protected void saveKeyToFile() {
        String key = outputKey.getText();
        saveFile(key);
    }
    public void saveFile(String text){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a Folder");
        Stage stage = (Stage) main.getScene().getWindow();
        File selectedFolder = directoryChooser.showDialog(stage);

        if (selectedFolder != null) {
            TextInputDialog dialog = new TextInputDialog("filename");
            dialog.setTitle("Set Filename");
            dialog.setHeaderText("Input your file name.");
            Optional<String> action = dialog.showAndWait();
            action.ifPresent(fileName->{

                try {
                    File file = new File(selectedFolder, fileName + ".txt");
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(text);
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }
    }
}