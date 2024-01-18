package com.example.dvdwyposerver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;

import java.sql.SQLException;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) throws IOException {
        try{
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            closeEvrything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendDataToClient(ResultSet resultSet) throws IOException, SQLException {
        try {
            while (resultSet.next()) {
                bufferedWriter.write(Integer.toString(resultSet.getInt("ID_DVD")));
                bufferedWriter.write(",");
                bufferedWriter.write(resultSet.getString("Tytul"));
                bufferedWriter.write(",");
                bufferedWriter.write(resultSet.getString("Rezyser"));
                bufferedWriter.write(",");
                bufferedWriter.write(resultSet.getString("Aktor"));
                bufferedWriter.write(",");
                bufferedWriter.write(resultSet.getString("Typ"));
                bufferedWriter.write(",");
                bufferedWriter.write(Integer.toString(resultSet.getInt("Ilosc")));
                // Add a newline to separate records
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            // Flush the BufferedWriter to ensure data is sent immediately

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeEvrything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
