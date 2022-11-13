package com.example.demo.Plane;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Plane {

	@Id
	private String id;
	

	private String model;
    private int capacity;
    private String manufacturer;
    private int yearOfManufacture;

    

    public Plane() {
    }

    public Plane(String model, int capacity, String manufacturer, int yearOfManufacture) {
        this.model = model;
        this.capacity = capacity;
        this.manufacturer = manufacturer;
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

        
}
