package com.example.demo.Flight;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.example.demo.Passenger.Passenger;
import com.example.demo.Plane.Plane;

@Entity
public class Flight {
	@Id
	private String flightNumber; // part of the primary key

    /*  Date format: yy-mm-dd, do not include hours, minutes, or seconds.
    **The system only needs to support PST. You can ignore other time zones.  
    */
    private Date departureDate; //  serve as the primary key together with flightNumber    

    /*  Date format: yy-mm-dd-hh, do not include minutes or seconds.
    ** Example: 2017-03-22-19
    */
    private Date departureTime; // Must be within the same calendar day as departureDate.   
    private Date arrivalTime;
    private int price;    // Full form only
    private String origin;
    private String destination;  
    private int seatsLeft; 
    private String description;   // Full form only
    
   // @OneToOne(targetEntity=Plane.class, cascade=CascadeType.ALL)
    @Embedded
    private Plane plane;  // Embedded,    Full form only
    
    @ManyToMany(targetEntity=Passenger.class)
    @Column(name = "passlist")
    private List<Passenger> passengers;    // Full form only
	
    public Flight(String flightNumber, int price, String from, String to, Date departureDate, Date departure, Date arrival,
			String description, ArrayList<Passenger> passengers, Plane plane) {
    	this.flightNumber = flightNumber;
		this.price = price;
		this.origin = from;
		this.destination = to;
		this.departureDate = departureDate;
		this.departureTime = departure;
		this.arrivalTime = arrival;
		this.description = description;
		this.plane = plane;
		this.seatsLeft = plane.getCapacity();
		this.passengers = passengers;
	}

	public Flight() {

	}

	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public Date getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}
	public Date getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getSeatsLeft() {
		return seatsLeft;
	}
	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Plane getPlane() {
		return plane;
	}
	public void setPlane(Plane plane) {
		this.plane = plane;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	

}

