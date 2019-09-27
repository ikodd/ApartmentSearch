package com.company.apartments.dao;

import com.company.apartments.entities.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAO<K, T> {
    protected final Connection conn;
    protected final String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void add(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (Field f : fields) {
                if(!f.isAnnotationPresent(Id.class)){
                    f.setAccessible(true);

                    names.append(f.getName()).append(',');
                    values.append('"').append(f.get(t)).append("\",");
                }
            }
            names.deleteCharAt(names.length() - 1); // last ','
            values.deleteCharAt(values.length() - 1); // last ','

            String sql = "INSERT INTO " + table + "(" + names.toString() +
                    ") VALUES(" + values.toString() + ")";

            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean delete(T t) {
        boolean success = false;
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = null;

            for (Field f : fields) {
                if (f.isAnnotationPresent(Id.class)) {
                    id = f;
                    id.setAccessible(true);
                    break;
                }
            }
            if (id == null)
                throw new RuntimeException("No Id field");

            String sql = "DELETE FROM " + table + " WHERE " + id.getName() +
                    " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
                success = true;
                return success;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> res = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T client = (T) cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);

                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(client, rs.getObject(columnName));
                        }

                        res.add(client);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public List<List<Object>> getAll(Class<T> cls, String ... parameters){
        List<List<Object>> recList = new ArrayList<>(); // For all records
        Field[] fields = cls.getDeclaredFields();
        List<Field> fieldsSel = new ArrayList<>();
        for(Field f : fields){
            for(int i = 0; i < parameters.length; i++){
                if(f.getName().equals(parameters[i])){
                    fieldsSel.add(f);
                    break;
                }
            }
        }
        if(fieldsSel.size() == 0){
            System.out.println("No valid field names entered");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(Field f : fieldsSel){
            sb.append(f.getName()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); //last character ","
        try(Statement st = conn.createStatement()){
           ResultSet rs = st.executeQuery("SELECT " + sb.toString() + " FROM " + table);
           ResultSetMetaData md = rs.getMetaData();
           for(;rs.next();){
               List<Object> objList = new ArrayList<>(); // For one record
               for(int i = 1; i <= md.getColumnCount(); i++){
                   String colunmName = md.getColumnName(i);
                   objList.add(rs.getObject(colunmName));
               }
               recList.add(objList);
           }
           return recList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> getAll(List<K> keys, Class<T> cls) {
        List<T> res = new ArrayList<>();
        Field[] fields = cls.getDeclaredFields();
        Field id = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                id = field;
                id.setAccessible(true);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + table + " WHERE ").append(id.getName()).append(" IN (");
        for (K key : keys) {
            if (key == null) continue;
            sb.append('"').append(key).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery(sb.toString())) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T client = (T) cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);

                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(client, rs.getObject(columnName));
                        }

                        res.add(client);
                    }
                }
            }

            return res;
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return  res;
    }

    public void update(T t) throws IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = null;
StringBuilder sb = new StringBuilder();
        for(Field field : fields){
            if(field.isAnnotationPresent(Id.class)){
                id = field;
                id.setAccessible(true);
            }else{
                field.setAccessible(true);
                sb.append(field.getName()).append("=").append('"');
                sb.append(field.get(t) + "\",");
            }
        }
        if (id == null)
            throw new RuntimeException("No Id field");

        sb.deleteCharAt(sb.length() - 1);

        String sql = "UPDATE " + table + " SET " + sb.toString() + " WHERE " + id.getName() + "=\"" + id.get(t) + "\"";
        try(Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {e.printStackTrace();
        }
    }

    public T findById(Class<T> cls, K k){
        Field[] fields = cls.getDeclaredFields();
        Field id = null;
        for(Field f : fields){
            if(f.isAnnotationPresent(Id.class)){
                f.setAccessible(true);
                id = f;
            }
        }
      try(Statement st = conn.createStatement()){
          String sqlExpr = (k == null) ? "" :
                  " WHERE "+ id.getName() + "=\"" + k +"\"";
          ResultSet rs = st.executeQuery("SELECT * FROM " + table + sqlExpr);
          System.out.println(rs);
          ResultSetMetaData md = rs.getMetaData();
          try {
              T t = cls.newInstance();
              rs.next();
              for(int i = 1; i <= md.getColumnCount(); i++){
                  String columnName = md.getColumnName(i);
                  Field f = cls.getDeclaredField(columnName);
                  f.setAccessible(true);
                  Object obj = rs.getObject(columnName);
                  f.set(t, rs.getObject(columnName));
              }
              return t;
          } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
              e.printStackTrace();
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return null;
    }
}
