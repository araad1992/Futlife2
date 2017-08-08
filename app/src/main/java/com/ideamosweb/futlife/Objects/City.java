package com.ideamosweb.futlife.Objects;

/**
 * Creado por Deimer Villa on 26/04/17.
 * Función:
 */
public class City {

    private int id;
    private String name;
    private String country_name;
    private boolean active;

    public City() {}

    public City(int id, String name, String country_name, boolean active) {
        this.id = id;
        this.name = name;
        this.country_name = country_name;
        this.active = active;
    }

//region Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCountry_name() {
        return country_name;
    }
    public boolean isActive() {
        return active;
    }
//endregion

//region Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
//endregion

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country_name='" + country_name + '\'' +
                ", active=" + active +
                '}';
    }
}
