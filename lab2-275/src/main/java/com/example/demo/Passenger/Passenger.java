package com.example.demo.Passenger;
import java.util.*;
import javax.persistence.*;
import com.example.demo.Reservation.*;
import com.example.demo.Flight.*;
@Entity
public class Passenger {
	@Id
	private int genId;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;   // primary key
    private String firstname;
    private String lastname;
    private int birthyear;  // Full form only (see definition below)
    private String gender;  // Full form only
    private String phone; // Phone numbers must be unique.   Full form only
    @OneToMany(targetEntity=Reservation.class,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;   // Full form only
	@ManyToMany(targetEntity=Flight.class,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Flight> flights;
    
    public Passenger() {}
    
    public Passenger(String firstname, String lastname, int birthyear, String gender, String phone) {
    	this.firstname = firstname;
    	this.lastname = lastname;
    	this.birthyear = birthyear;
    	this.gender = gender;
    	this.phone = phone;
    }

    public String getId() { 
    	return id; 
    }
    
    public void setId(String id) {
    	this.id = id;
    }
    
    public String getFirstName() {
    	return firstname;
    }
    
    public void setFirstName(String firstname) {
    	this.firstname = firstname;
    }
    
    public String getLastName() {
    	return lastname;
    }
    
    public void setLastName(String lastname) {
    	this.lastname=lastname;
    }
    
    public int getBirthYear() {
    	return birthyear;
    }
    
    public void setBirthYear(int birthyear) {
    	this.birthyear = birthyear;
    }
    
    public String getGender() {
    	return gender;
    }
    
    public void setGender(String gender) {
    	this.gender = gender;
    }
    
    public String getPhone() {
    	return phone;
    }
    
    public void setPhone(String phone) {
    	this.phone = phone;
    }
	public List<Reservation> getReservation() {
		return reservations;
	}

	public void setReservation(List<Reservation> reservation) {
		this.reservations = reservation;
	}

	public List<Flight> getFlight() {
		return flights;
	}

	public void setFlight(List<Flight> flight) {
		this.flights = flight;
	}
    
}
