package com.company.apartments.utils;

import com.company.apartments.entities.*;
import com.company.apartments.connection.*;
import com.company.apartments.dao.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";
    static final String TABLE_APARTMENTS = "apartments";
    static final String TABLE_DISTRICTS = "districts";
    private static ApartmentDAOEx daoA;
    private static DistrictDAOEx daoD;
    private static Connection conn;

    public static void main(String[] args){
        conn =  (new ConnectionFactory(DB_CONNECTION,DB_USER,DB_PASSWORD)).getConnection();
        daoA = new ApartmentDAOEx(conn,TABLE_APARTMENTS);
//        daoD = new DistrictDAOEx(conn,TABLE_DISTRICTS); // The lines should be uncommented, ...
//        daoD.init();                                     // if you run snippet first time
        daoA.init();

    }
}
