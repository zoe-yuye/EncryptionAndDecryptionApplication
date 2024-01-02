module com.application.encryptionanddecryption {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.application.Interface to javafx.fxml;
    exports com.application.Interface;
}