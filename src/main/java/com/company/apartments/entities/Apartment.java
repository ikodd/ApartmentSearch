package com.company.apartments.entities;

public class Apartment {
    @Id
    private Long id;
    private Long districtId; // District id as foreign key for db
    private String address;
    private Double area;
    private Integer numRooms;
    private Double price;

    public Apartment() {
    }

    public Apartment(Long districtId, String address, Double area, Integer numRooms, Double price) {
        this.districtId = districtId;
        this.address = address;
        this.area = area;
        this.numRooms = numRooms;
        this.price = price;
    }

    public Apartment(String address, Double area, Integer numRooms, Double price) {
        this.address = address;
        this.area = area;
        this.numRooms = numRooms;
        this.price = price;
    }

    public Apartment(Long id, Long districtId, String address, Double area, Integer numRooms, Double price) {
        this.id = id;
        this.districtId = districtId;
        this.address = address;
        this.area = area;
        this.numRooms = numRooms;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(Integer numRooms) {
        this.numRooms = numRooms;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", districtId='" + districtId + '\'' +
                ", address='" + address + '\'' +
                ", area=" + area +
                ", numRooms=" + numRooms +
                ", price=" + price +
                '}';
    }
}
