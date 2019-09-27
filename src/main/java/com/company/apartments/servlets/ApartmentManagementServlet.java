package com.company.apartments.servlets;

import com.company.apartments.dao.*;
import com.company.apartments.entities.*;
import com.company.apartments.connection.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.LocalTime.*;

@WebServlet(name = "ApartmentManagement")
public class ApartmentManagementServlet extends HttpServlet {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";
    static final String tableA = "apartments";
    static final String tableD = "districts";
    private static ApartmentDAOEx daoA;
    private static DistrictDAOEx daoD;
    private static Connection conn;

    @Override
    public void init() throws ServletException {
            super.init();
            conn =  (new ConnectionFactory(DB_CONNECTION,DB_USER,DB_PASSWORD)).getConnection();
            daoA = new ApartmentDAOEx(conn,tableA);
            daoD = new DistrictDAOEx(conn,tableD);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
String command = request.getParameter("command");
Long districtId = null;
        if(request.getParameter("districtId") != null &&
        !"".equals(request.getParameter("districtId")))
            districtId = Long.parseLong(request.getParameter("districtId"));
Boolean success = false;
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/search.jsp");

switch (command){
    case "find":
        Double areaLow = Double.parseDouble(request.getParameter("areaLow"));
        Double areaTop = Double.parseDouble(request.getParameter("areaTop"));
        Integer numRoomsLow = Integer.parseInt(request.getParameter("numRoomsLow"));
        Integer numRoomsTop = Integer.parseInt(request.getParameter("numRoomsTop"));
        Double priceLow = Double.parseDouble(request.getParameter("priceLow"));
        Double priceTop = Double.parseDouble(request.getParameter("priceTop"));

        List<Apartment> apartments;
        List<Apartment>apartmentRange = new ArrayList<>();
        List<District> districts = new ArrayList<>();
        District district = daoD.findById(District.class,Long.valueOf(districtId));
        if(district != null)districts.add(district);
        apartmentRange.add(new Apartment(null,areaLow,numRoomsLow,priceLow));
        apartmentRange.add(new Apartment(null,areaTop,numRoomsTop,priceTop));
        apartments = daoA.findByParams(districts,apartmentRange);
        districts = daoD.getAll(District.class);
        request.setAttribute("view","searchResult");
        request.setAttribute("apartments",apartments);
        request.setAttribute("districts",districts);
        rd.forward(request,response);
        break;
    case "add":
        String address = request.getParameter("address");
        Double area = request.getParameter("area") != null ?
                Double.parseDouble(request.getParameter("area")) : null;
        Integer numRooms = request.getParameter("numRooms") != null ?
                Integer.parseInt(request.getParameter("numRooms")) : null;
        Double price = request.getParameter("price") != null ?
                Double.parseDouble(request.getParameter("price")) : null;
        if(address == null || "".equals(address) || area == null || area <= 0 ||
        numRooms == null || numRooms <= 0 || price == null || price <= 0){
    success=false;
    String reason = "One or more fields are empty";
    }else{
            daoA.add(new Apartment(districtId,address,area,numRooms,price));
            apartments = daoA.getAll(Apartment.class);
            request.setAttribute("view","list");
            request.setAttribute("apartments",apartments);
            rd.forward(request,response);
        }
        break;
    case "delete":
        Enumeration enumeration = request.getParameterNames();
        List<Long> ids = new ArrayList<>();
        for(;enumeration.hasMoreElements();){
            String paramName = (String)enumeration.nextElement();
            Pattern pat = Pattern.compile("^([0-9]{1,20})$");
            Matcher mat = pat.matcher(paramName);
            for(;mat.find();){
                ids.add(Long.parseLong(mat.group(1)));
            }
        }
        apartments = new ArrayList<>();
        for(Long id : ids){
            Apartment a = null;
            if((a = daoA.findById(Apartment.class,id)) != null){
                if(daoA.delete(a))apartments.add(a);
            }
        }
        request.setAttribute("view","deleteResult");
        request.setAttribute("apartments",apartments);
        rd.forward(request,response);
        break;
    default:
        response.sendRedirect("/index.jsp");
        break;
}
    }

   /*Invoked from search.jsp:"Apartment list" or "Delete" links*/
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = (request.getParameter("command") == null) ? "" : request.getParameter("command");
        System.out.println(now() + " command:" +  command);
        Boolean isDelete = (request.getParameter("delete_form") != null);
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/search.jsp");
        switch (command){
            case "list":
                List<District> districts;
                districts = daoD.getAll(District.class);
                List<Apartment> apartments;
                apartments = daoA.getAll(Apartment.class);
                request.setAttribute("view","list");
                request.setAttribute("districts",districts);
                request.setAttribute("apartments",apartments);
                request.setAttribute("isDelete",isDelete); // Form with checkboxes appears, if true
                rd.forward(request,response);
                break;
            case "list_selection":
                apartments = daoA.getAll(Apartment.class);
                request.setAttribute("isDelete",true);
                request.setAttribute("apartments",apartments);
                rd.forward(request,response);
                break;
            case "search_form":
                apartments = daoA.getAll(Apartment.class);
                districts = daoD.getAll(District.class);
                Double areaLow = Double.MAX_VALUE;
                Double areaTop = Double.MIN_VALUE;
                Integer numRoomsLow = Integer.MAX_VALUE;
                Integer numRoomsTop = Integer.MIN_VALUE;
                Double priceLow = Double.MAX_VALUE;
                Double priceTop = Double.MIN_VALUE;
                for(Apartment apartment : apartments){
                    areaLow = (apartment.getArea() < areaLow) ? apartment.getArea() : areaLow;
                    areaTop = (apartment.getArea() > areaTop) ? apartment.getArea() : areaTop;
                    numRoomsLow = (apartment.getNumRooms() < numRoomsLow) ? apartment.getNumRooms() : numRoomsLow;
                    numRoomsTop = (apartment.getNumRooms() > numRoomsTop) ? apartment.getNumRooms() : numRoomsTop;
                    priceLow = (apartment.getPrice() < priceLow) ? apartment.getPrice() : priceLow;
                    priceTop = (apartment.getPrice() > priceTop) ? apartment.getPrice() : priceTop;
                }
                List<Apartment> apartmentRange = new ArrayList<>();
                apartmentRange.add(new Apartment(null,areaLow,numRoomsLow,priceLow));
                apartmentRange.add(new Apartment(null,areaTop,numRoomsTop,priceTop));
                request.setAttribute("view","searchForm");
                request.setAttribute("apartmentRange",apartmentRange);
                request.setAttribute("districts",districts);
                rd = getServletContext().getRequestDispatcher("/search.jsp");
                rd.forward(request,response);
                break;
            case "add_apartment_form":
                districts = daoD.getAll(District.class);
                apartmentRange = new ArrayList<>();
                apartmentRange.add(new Apartment(null,1.0,1,1.0));
                apartmentRange.add(new Apartment(null,1000000000.0,400,100000000.0));
                request.setAttribute("view","addApartForm");
                request.setAttribute("districts",districts);
                request.setAttribute("apartmentRange",apartmentRange);
                rd.forward(request,response);
                break;
            default:
                response.sendRedirect("/index.jsp");
                break;
        }
    }
}
