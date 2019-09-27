package com.company.apartments.dao;

import com.company.apartments.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Bios on 29.11.2017.
 */
public class ApartmentDAOEx extends AbstractDAO<Long, Apartment> {
    public ApartmentDAOEx(Connection conn, String table) {
        super(conn, table);
    }
    static final double INIT_PRICE = 10000;
    static final double PRICE_STEP = 30000;
    static final double MAX_AREA  = 100;
    static final int MAX_ROOMS = 4;
    static final int DISTRICT_NUM = 4;  // "North", "South", "East" and "West"
    static final int NUM_RAND_REC = 20;
    static final int NUM_RAND_BUILD = 100;
    static final int NUM_RAND_STREET = 4;

    public void init(){
        try(Statement st = conn.createStatement();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO " + table +
                " (districtId, address,area,numRooms,price) " +
                " VALUES(?,?,?,?,?)")) {
            conn.setAutoCommit(false);
            st.execute("DROP TABLE IF EXISTS " + table);
            st.execute("CREATE TABLE " + table +
                    " (id BIGINT SIGNED NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY," +
                    "districtId BIGINT SIGNED," +
                    "address VARCHAR(150), area DOUBLE(10,2)," +
                    "numRooms INT(10), price DOUBLE (20,2)," +
                    "FOREIGN KEY (districtId) REFERENCES districts(id)" +
                    "ON DELETE SET NULL)" );
            List<String> streetNs = Arrays.asList("Abrikosovaia","Vinogradnaia", "Tenistaia");
            StringBuilder sb = new StringBuilder();
            Random rand = new Random();

            for(int i = 0; i < NUM_RAND_REC; i++){
                int randDistr = rand.nextInt(DISTRICT_NUM) + 1;
                int randBuild = rand.nextInt(NUM_RAND_BUILD) + 1;
                int randStreetN = rand.nextInt(NUM_RAND_STREET) + 1;
                sb.append(randBuild).append(" ")
                        .append(streetNs.get(rand.nextInt(streetNs.size() - 1)))
                        .append(randStreetN);
                double randArea = Math.random()*MAX_AREA + 1;
                double randPrice = 0;
                int numRooms = 0;
                for(int j = MAX_ROOMS; j > 0; j--){
                    if(randArea <= MAX_AREA / j){
                        numRooms = MAX_ROOMS - j + 1;
                        randPrice = Math.random()*(PRICE_STEP - INIT_PRICE) + INIT_PRICE;
                        break;
                    }
                }
                ps.setInt(1,randDistr);
                ps.setString(2,sb.toString()); // Random address
                randArea = Math.floor(randArea);
                ps.setDouble(3,randArea);
                ps.setInt(4,numRooms);
                randPrice = Math.floor(randPrice)*numRooms;
                ps.setDouble(5,randPrice);
                ps.addBatch();
                sb.delete(0,sb.length());
            }
            ps.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Apartment> findByParams(List<District> districtRange, List<Apartment> apartmentRange){
        Class<?> cls = Apartment.class;
        List<Apartment> apartments = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try(Statement st = conn.createStatement()) {
            if(districtRange.size() > 0){
                for(District district : districtRange){
                    sb.append("districtId=\"")
                            .append(district.getId())
                            .append("\"")
                            .append(" AND ");
                }

            }
            if(apartmentRange.size() > 0){
                Double areaLow = apartmentRange.get(0).getArea();
                Double areaTop = apartmentRange.get(1).getArea();
                Integer numRoomsLow = apartmentRange.get(0).getNumRooms();
                Integer numRoomsTop = apartmentRange.get(1).getNumRooms();
                Double priceLow = apartmentRange.get(0).getPrice();
                Double priceTop = apartmentRange.get(1).getPrice();
                if(areaLow !=null){
                    sb.append("area>=\"")
                            .append(areaLow).append("\"")
                            .append(" AND ");
                }
                if(areaTop !=null){
                    sb.append("area<=\"")
                            .append(areaTop).append("\"")
                            .append(" AND ");
                }
                if(numRoomsLow !=null){
                    sb.append("numRooms>=\"")
                            .append(numRoomsLow).append("\"")
                            .append(" AND ");
                }
                if(numRoomsTop !=null){
                    sb.append("numRooms<=\"")
                            .append(numRoomsTop).append("\"")
                            .append(" AND ");
                }
                if(priceLow !=null){
                    sb.append("price>=\"")
                            .append(priceLow).append("\"")
                            .append(" AND ");
                }
                if(priceTop !=null){
                    sb.append("price<=\"")
                            .append(priceTop).append("\"")
                            .append(" AND ");
                }
                sb.delete(sb.length() - 5,sb.length());
                ResultSet rs = st.executeQuery("SELECT * FROM "
                        + table + " WHERE "
                + sb.toString());
                ResultSetMetaData md = rs.getMetaData();
                for(;rs.next();){
                    Long id = 0L;
                    Long districtId = 0L;
                    String address = "";
                    Double area = 0.0;
                    Integer numRooms = 0;
                    Double price = 0.0;
                    for(int i = 1; i <= md.getColumnCount(); i++){
                        String columnN = md.getColumnName(i);
                        switch (columnN.toLowerCase()){
                            case "id":
                                id = (Long)rs.getObject(i);
                                break;
                            case "districtid":
                                districtId = (Long)rs.getObject(i);
                                break;
                            case "address":
                                address =(String)rs.getObject(i);
                                break;
                            case "area":
                                area =(Double)rs.getObject(i);
                                break;
                            case "numrooms":
                                numRooms = (Integer)rs.getObject(i);
                                break;
                            case "price":
                                price = (Double)rs.getObject(i);
                                break;
                            default:
                                break;
                        }
                    }
                    apartments.add(new Apartment(id,districtId,address,area,numRooms,price));
                }
            }
            return apartments;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
