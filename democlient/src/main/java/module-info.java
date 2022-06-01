module com.example.democlient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.democlient to javafx.fxml;
    exports com.example.democlient;
}