module com.example.data_base {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    requires org.controlsfx.controls;

    opens com.example.data_base to javafx.fxml;
    exports com.example.data_base;
}