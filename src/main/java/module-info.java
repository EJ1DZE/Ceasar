module com.example.ceasar {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.ceasar to javafx.fxml;
    exports com.example.ceasar;
}