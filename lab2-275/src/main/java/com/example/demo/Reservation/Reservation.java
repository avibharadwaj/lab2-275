package com.example.demo.Reservation;

import com.example.demo.Flight.Flight;
import com.example.demo.Passenger.Passenger;

import javax.persistence.*;
import java.util.List;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int genOrderNumber;

    private String reservationNumber;

    private String origin;

    private String destination;

    private int price;

    @OneToOne(targetEntity = Passenger.class, cascade = CascadeType.DETACH)
    private Passenger passenger;

    @ManyToMany(targetEntity = Flight.class)
    private List<Flight> flights;

    public Reservation(String origin, String destination, int price, Passenger passenger, List<Flight> flights) {
        this.origin = origin;
        this.destination = destination;
        this.price = price;
        this.passenger = passenger;
        this.flights = flights;
    }

    public Reservation() {

    }

    public int getGenOrderNumber() {
        return genOrderNumber;
    }

    public void setGenOrderNumber(int genOrderNumber) {
        this.genOrderNumber = genOrderNumber;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

}
