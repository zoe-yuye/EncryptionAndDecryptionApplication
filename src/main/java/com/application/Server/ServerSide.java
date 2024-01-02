package com.application.Server;

import com.application.cipherAlgorithm.CaesarCipher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;


public class ServerSide {
    private static final String JDBC_URL= "jdbc:mysql://127.0.0.1/cipher";
    private static final String USERNAME = "root";
    private static final String PASSWORD="";
    private static final int port = 9090;

    private Connection connection;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        new ServerSide().authenticate();

    }

    public ServerSide() throws ClassNotFoundException, SQLException {
        Class. forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Connecting to database...");
        connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public void authenticate() throws  ClassNotFoundException {
        boolean loginSuccess = false;
        String username = "";
        String password = "";
        System.out.println("Server started on 9090");
        try{
            serverSocket = new ServerSocket(port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                    String receivedData = reader.readLine();
                    if(receivedData.startsWith("Login,")){
                        String[] data = receivedData.split(",");
                        username = data[1];
                        password = encryptPassword(data[2]);
                        loginSuccess = authenticateUser(username, password);
                        writer.write(String.valueOf(loginSuccess));
                        writer.newLine();
                        writer.flush();

                        String theme = getCof(username);
                        writer.write("theme," + theme);
                        writer.newLine();
                        writer.flush();
                    }else if(receivedData.startsWith("Setting,")){
                        updateData(receivedData, password, username);
                    }else if(receivedData.startsWith("Getkeys")){
                        String[] data = receivedData.split(",");
                        String queryUser = data[1];
                        String algorithm = data[2];
                        writer.write(queryKeys(queryUser, algorithm));
                        writer.newLine();
                        writer.flush();
                    }else if(receivedData.startsWith("Savekey")){
                        saveAKey(receivedData);
                        writer.write("DatabaseUpdated");
                        writer.newLine();
                        writer.flush();
                        System.out.println("Saved to DB");
                    }else if(receivedData.startsWith("Register")){
                        String response = addUser(receivedData);
                        writer.write(response);
                        writer.newLine();
                        writer.flush();
                    }else if(receivedData.startsWith("Loadkey,")){
                        String response = loadKey(receivedData);
                        writer.write(response);
                        writer.newLine();
                        writer.flush();
                    }else if(receivedData.startsWith("CheckKeyName,")){
                        String response = checkKeyName(receivedData);
                        writer.write(response);
                        writer.newLine();
                        writer.flush();
                    }
                }

            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public String checkKeyName(String receivedData){
        String validKeyName = "valid";
        String[] data = receivedData.split(",");
        String keyName = data[1];
        String userName = data[2];
        String sql = "SELECT `keys`.`name` FROM `keys` JOIN `users` ON `keys`.`userId` = `users`.`id` WHERE `keys`.`name` = ? AND `users`.`username` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, keyName);
            preparedStatement.setString(2, userName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                validKeyName = "invalidKeyName";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return validKeyName;
    }

    public String loadKey(String receivedData){
        String[] data = receivedData.split(",");
        String userName = data[1];
        String keyName = data[2];
        String returnData = "";
        String sql = "SELECT `keys`.`key`, `keys`.`text` FROM `keys` JOIN `users` ON `keys`.`userId` = `users`.`id` WHERE `keys`.`name` = ? AND `users`.`username` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, keyName);
            preparedStatement.setString(2, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String text = resultSet.getString("text");
                String key = resultSet.getString("key");

                returnData = "LoadKeyToUser," + text + "," + key;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnData;
    }
    public String addUser(String receivedData){
        int id = getMaxId() + 1;

        String[] data = receivedData.split(",");
        String userName = data[1];
        String userPassword = encryptPassword(data[2]);
        if (!checkUserName(userName)) {
            String sql ="INSERT INTO `users`(`id`, `username`, `password`, `theme`) VALUES (?, ?, ?,'theme2')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, userName);
                preparedStatement.setString(3, userPassword);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User Registered");
                    return "UserRegistered";
                } else {
                    System.out.println("Update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("Username already exists. Choose another username.");
            return "InvalidUserName";
        }

        return "Update failed.";
    }

    private boolean checkUserName(String username) {
        String sql = "SELECT COUNT(*) FROM `users` WHERE `username` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private int getMaxId() {
        String sql = "SELECT MAX(`id`) FROM `users`";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveAKey(String receivedData){
        String[] data = receivedData.split(",");
        String queryUser = data[1];
        String keyName = data[2];
        String type = data[3];
        String algorithm = data[4];
        String sql = "";
        String text;
        String key;
        switch (type){
            case "Encrypted Text":
                text = data[5];
                sql ="INSERT INTO `keys` (`name`, `text`, `key`, `userId`, `algorithm`) VALUES (?, ?, '', (SELECT `id` FROM `users` WHERE `username` = ?), ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, keyName);
                    preparedStatement.setString(2, text);
                    preparedStatement.setString(3, queryUser);
                    preparedStatement.setString(4, algorithm);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Update successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case "Key":
                key = data[5];
                sql ="INSERT INTO `keys` (`name`, `text`, `key`, `userId`, `algorithm`) VALUES (?, '', ?, (SELECT `id` FROM `users` WHERE `username` = ?), ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, keyName);
                    preparedStatement.setString(2, key);
                    preparedStatement.setString(3, queryUser);
                    preparedStatement.setString(4, algorithm);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Update successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "Both":
                text = data[5];
                key = data[6];
                sql ="INSERT INTO `keys` (`name`, `text`, `key`, `userId`, `algorithm`) VALUES (?, ?, ?, (SELECT `id` FROM `users` WHERE `username` = ?), ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, keyName);
                    preparedStatement.setString(2, text);
                    preparedStatement.setString(3, key);
                    preparedStatement.setString(4, queryUser);
                    preparedStatement.setString(5, algorithm);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Update successfully.");
                    } else {
                        System.out.println("Update failed.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

    }
    public String queryKeys(String queryUser, String algorithm){
        String result ="keyName,";
        String sql = "SELECT `keys`.`name` FROM `keys` LEFT JOIN `users` ON `keys`.`userId` = `users`.`id` WHERE `users`.`username` = ? AND `algorithm` = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, queryUser);
            preparedStatement.setString(2, algorithm);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    result += name + ",";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void updateData(String receivedData,String password, String username) throws IOException {

        String[] data = receivedData.split(",");
        String newTheme = data[1];

        if(data.length > 2){
            String newPassword = encryptPassword(data[2]);
            String sql = "UPDATE users SET password = ?, theme = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, newTheme);
                preparedStatement.setString(3, username);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Update successfully.");
                } else {
                    System.out.println("Update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            String sql = "UPDATE users SET theme = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newTheme);
                preparedStatement.setString(2, username);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Update successfully.");
                } else {
                    System.out.println("Update failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public String encryptPassword(String passwordText){
        CaesarCipher caesarCipher = new CaesarCipher();
        String encryptedPassword = caesarCipher.encrypt(17, passwordText);
        return encryptedPassword;
    }

    public boolean authenticateUser(String username, String password) throws IOException, SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successfully.");
                return true;
            } else {
                System.out.println("Login failed.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getCof(String username) throws SQLException {

        String sql = "SELECT theme FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String theme = resultSet.getString("theme");
                return theme;
            } else {
                System.out.println("User not found.");
                resultSet.close();
                return null;

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
