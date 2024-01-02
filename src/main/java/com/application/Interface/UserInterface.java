package com.application.Interface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class UserInterface extends Application {
    protected final static String serverAddress = "127.0.0.1";
    private static String user = "";
    public static String getUser() {
        return user;
    }
    public static void setUser(String user) {
        UserInterface.user = user;
    }

    private static String theme = "";

    public static String getTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        UserInterface.theme = theme;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoaderLogin = new FXMLLoader(UserInterface.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoaderLogin.load(), 800, 600);
        stage.setTitle("Encryption and Decryption Application");
        stage.setWidth(800);
        stage.setHeight(650);
        stage.setResizable(false);
        stage.getIcons().add(new Image("icon.png"));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}