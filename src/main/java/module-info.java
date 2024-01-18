module com.example.dvdwyposerver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.dvdwyposerver to javafx.fxml;
    exports com.example.dvdwyposerver;
}