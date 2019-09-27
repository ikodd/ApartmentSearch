package com.company.apartments.dao;

import com.company.apartments.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DistrictDAOEx extends AbstractDAO<Long, District> {
    public DistrictDAOEx(Connection conn, String table){
        super(conn,table);
    }

      public void init(){
        try(Statement st = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO " +
                    table + " (name) VALUES(?)")) {
            conn.setAutoCommit(false);
            st. execute("CREATE TABLE " + table +
                    " (id BIGINT SIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY," +
                    "name VARCHAR(100))");
            List<String> districts = Arrays.asList("North","South","East","West");
            for(String d : districts){
                ps.setString(1,d);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

