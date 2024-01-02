package com.application.Interface;


import com.application.cipherAlgorithm.CaesarCipher;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Optional;


public class LoginController {

	@FXML
	private TextField username;
	@FXML
	private PasswordField password;

	private static final String serverAddress = "127.0.0.1";
	private static final int port = 9090;

	@FXML
	public void login(ActionEvent event) {
		Task<Void> loginTask = new Task<>() {

			@Override
			protected Void call() {
				try (Socket socket = new Socket(serverAddress, port);
					 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

					String usernameText = username.getText();
					String passwordText = encryptPassword(password.getText());
					writer.write("Login," + usernameText + "," + passwordText);
					writer.newLine();
					writer.flush();

					String response = reader.readLine();
					boolean loginSuccess = Boolean.parseBoolean(response);

					String receivedData = reader.readLine();
					if(receivedData.startsWith("theme,")){
						String[] credentials = receivedData.split(",");
						String theme = credentials[1];
						Platform.runLater(() -> {
							if (loginSuccess) {
								if(theme.contains("theme")){
									UserInterface.setTheme(theme);
								}else{
									UserInterface.setTheme("theme2");
								}
								UserInterface.setUser(usernameText);
								System.out.println("Login successful!");
								try {
									showInterface(event);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								System.out.println("Login failed. Please try again.");
								showAlert();
							}

						});
					}


				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};
		new Thread(loginTask).start();
	}

	public void showInterface(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoaderMain = new FXMLLoader(UserInterface.class.getResource("interface.fxml"));
		AnchorPane root = fxmlLoaderMain.load();
		setTheme(root);
		Scene scene = new Scene(root, 800, 600);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}
	public void setTheme(AnchorPane root){

		if(UserInterface.getTheme().equals("theme2")){
			System.out.println("User did not set a theme");
		}else{
			String oldCSS= root.getStylesheets().toString();
			String newCSS = oldCSS.substring(1, oldCSS.length() - 1);
			newCSS  = newCSS .replace("theme2", UserInterface.getTheme());
			root.getStylesheets().clear();
			root.getStylesheets().add(newCSS);
		}

	}
	public void showAlert(){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Invalid Login");
		alert.setHeaderText("Wrong password or username.");
		alert.setContentText("Please try again.");
		alert.showAndWait();
	}
	@FXML
	public void registerNewAccount(){
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Register New Account");
		dialog.setHeaderText(null);
		DialogPane dialogPane = dialog.getDialogPane();

		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		PasswordField passwordCheckField = new PasswordField();
		Button registerBtn = new Button("Register");
		Text hint = new Text();
		VBox content = new VBox(10, new Label("Set Username:"), usernameField,new Label("Set Password:"), passwordField,new Label("Confirm your Password:"),passwordCheckField, hint,registerBtn);
		dialogPane.setContent(content);
		dialogPane.setMinWidth(300);
		dialogPane.setMinHeight(200);

		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialogPane.getButtonTypes().addAll(cancelButton);
		registerBtn.setOnAction(event -> {
				String usernameR = usernameField.getText();
				String passwordR = passwordField.getText();
				String passwordCheck = passwordCheckField.getText();
				if(!passwordR.equals(passwordCheck)){
					hint.setText("Please input the same password.");
				}else {
					passwordR = encryptPassword(passwordR);

					String response = addUserToDB(usernameR, passwordR, hint);
					if (response.equals("UserRegistered")) {
						hint.setText("Registered successfully!");
						usernameField.clear();
						passwordField.clear();
						passwordCheckField.clear();
					} else if (response.equals("InvalidUserName")) {

						hint.setText("Invalid Username!");
					}

				}

		});


		dialog.showAndWait();

	}
	public String addUserToDB(String usernameR, String passwordR, Text hint){
		String response;
		try {
			Socket socket = new Socket(serverAddress, port);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String dataToSend = "Register," +usernameR + "," + passwordR;
			writer.write(dataToSend);
			writer.newLine();
			writer.flush();
			response =  reader.readLine();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	public String encryptPassword(String passwordText){
		CaesarCipher caesarCipher = new CaesarCipher();
		String encryptedPassword = caesarCipher.encrypt(7, passwordText);
		return encryptedPassword;
	}
}
