package com.example.dvdwyposerver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private String url = "jdbc:mysql://localhost:3306/DVDwypo";
    private String username = "root";
    private String password = "";
    public Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("DB Connected");
        } catch ( SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
