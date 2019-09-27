package com.company.apartments.entities;

public class District {
    @Id
    private Long id;
    private String name;

    public District() {
    }

    public District(String name) {
        this.name = name;
    }

    public District(Long id, String distrName) {
        this.id = id;
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "District{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
