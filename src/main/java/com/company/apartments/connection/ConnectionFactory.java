package com.company.apartments.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private String url;
    private String login;
    private String password;

    public ConnectionFactory(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        try{
            return DriverManager.getConnection(url,login,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
