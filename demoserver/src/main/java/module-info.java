module com.example.demoserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demoserver to javafx.fxml;
    exports com.example.demoserver;
}