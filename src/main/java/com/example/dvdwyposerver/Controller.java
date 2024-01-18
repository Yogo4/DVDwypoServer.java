package com.example.dvdwyposerver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class Controller implements Initializable {

    @FXML
    private TableView<DataModel> dvdTable;
    @FXML
    private Button issueButton;
    @FXML
    private TableColumn<DataModel, String> tytul;
    @FXML
    private TableColumn<DataModel, String> rezyser;
    @FXML
    private TableColumn<DataModel, Integer> id_zamowienia;
    @FXML
    private TableColumn<DataModel, String> imie;
    @FXML
    private TableColumn<DataModel, String> nazwisko;
    @FXML
    private TableColumn<DataModel, String> stan;

    private Server server;
    private Database database;
    private ObservableList<DataModel> dataModel;
    private boolean newDataFlag;
    private ResultSet lastClientData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try{
            server = new Server(new ServerSocket(1234));
            database = new Database();
            dataModel = FXCollections.observableArrayList();
            startDataUpdateThread();
            System.out.println("Server ON");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error creating server");
        }

        tytul.setCellValueFactory(new PropertyValueFactory<>("tytul"));
        rezyser.setCellValueFactory(new PropertyValueFactory<>("rezyser"));
        id_zamowienia.setCellValueFactory(new PropertyValueFactory<>("id_zamowienia"));
        imie.setCellValueFactory(new PropertyValueFactory<>("imie"));
        nazwisko.setCellValueFactory(new PropertyValueFactory<>("nazwisko"));
        stan.setCellValueFactory(new PropertyValueFactory<>("stan"));

    }


    private void updateTableView() throws SQLException {
        String query = "SELECT DVD.Tytul AS Tytul, DVD.Rezyser AS Rezyser, " +
                "Wypozyczenia.ID_Zamowienia AS ID_Zamowienia, " +
                "Wypozyczenia.Imie AS Imie, Wypozyczenia.Nazwisko AS Nazwisko, " +
                "Wypozyczenia.Stan AS Stan FROM DVD " +
                "JOIN Wypozyczenia ON DVD.ID_DVD = Wypozyczenia.ID_DVD";

             PreparedStatement preparedStatement = database.connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery();

            dataModel.clear();

            while (resultSet.next()) {
                DataModel data = new DataModel(
                        resultSet.getString("Tytul"),
                        resultSet.getString("Rezyser"),
                        resultSet.getInt("ID_Zamowienia"),
                        resultSet.getString("Imie"),
                        resultSet.getString("Nazwisko"),
                        resultSet.getString("Stan")
                );

                dataModel.add(data);
            }

            Platform.runLater(() -> dvdTable.setItems(dataModel));
        }
    private ResultSet getDataForClient() throws SQLException {
        String query = "SELECT * FROM `dvd`";
        PreparedStatement preparedStatement = database.connection.prepareStatement(query);

        return preparedStatement.executeQuery();
    }

    private boolean SetNewData(ResultSet newData) {
        lastClientData = newData;
        return true;
    }

    private boolean checkForNewData() throws SQLException {
        ResultSet newData = getDataForClient();

        if(lastClientData == null) {
            lastClientData = newData;
            return true;
        }
        while(newData.next() && lastClientData.next())
        {
            if(!(newData.getInt("ID_DVD") == (lastClientData.getInt("ID_DVD"))))
                return SetNewData(newData);
            if(!(newData.getString("Tytul").equals(lastClientData.getString("Tytul"))))
                return SetNewData(newData);
            if(!(newData.getString("Rezyser").equals(lastClientData.getString("Rezyser"))))
                return SetNewData(newData);
            if(!(newData.getString("Aktor").equals(lastClientData.getString("Aktor"))))
                return SetNewData(newData);
            if(!(newData.getString("Typ").equals(lastClientData.getString("Typ"))))
                return SetNewData(newData);
            if(!(newData.getInt("Ilosc") == (lastClientData.getInt("Ilosc"))))
                return SetNewData(newData);
        }
        return false;
    }

    private void startDataUpdateThread() {
        Thread dataUpdateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                    newDataFlag = checkForNewData();
                    if(newDataFlag) server.sendDataToClient(lastClientData);
                    updateTableView();
                } catch (InterruptedException | SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        dataUpdateThread.setDaemon(true);
        dataUpdateThread.start();
    }

    @FXML
    public void handleIssueDVD(ActionEvent event) throws SQLException {
        updateTableView();
    }

    @FXML
    public void handleReturnDVD(ActionEvent event) throws SQLException {
        updateTableView();
    }

}


