module com.example.musicui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens musicUI to javafx.fxml;
    opens musicUI.model to javafx.fxml;

    exports musicUI;
    exports musicUI.model;
}