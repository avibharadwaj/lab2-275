package com.example.demo.Reservation;

import com.example.demo.Flight.Flight;
import com.example.demo.Flight.FlightRepository;
import com.example.demo.Passenger.Passenger;
import com.example.demo.Passenger.PassengerRepository;
import com.example.demo.Plane.Plane;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    public ResponseEntity<?> getReservation(int number, String responseType) throws JSONException {
        Reservation reservation;
        try {
            reservation = reservationRepository.findById(number).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(displayError("BadRequest", "404", "Requested reservation with number " + number
                    + " does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (responseType.equals("json"))
            return new ResponseEntity<>(reservationToJSON(reservation), HttpStatus.OK);
        else
            return new ResponseEntity<>(XML.toString(new JSONObject(reservationToJSON(reservation))), HttpStatus.OK);
    }

    public ResponseEntity<?> addReservation(String passengerId, String[] flightListStr, String responseType, String[] departureDates) throws JSONException {
        Passenger passenger = passengerRepository.getById(passengerId);

        List<Flight> flightList = new ArrayList<>();
        for (String s : flightListStr) {
            Flight f = flightRepository.findByflightNumber(s);
            if (f == null) {
                return new ResponseEntity<>(displayError("BadRequest", "400", "Flight numbers not provided to make reservation"), HttpStatus.BAD_REQUEST);
            }
            flightList.add(f);
        }

        if (passenger != null) {
            if (flightList.size() == 0) {
                return new ResponseEntity<>(displayError("BadRequest", "400", "Flight numbers not provided to make reservation"), HttpStatus.BAD_REQUEST);
            }
            List<Flight> currentFlights = flightScheduleChecker(passengerId, flightList);
            List<Flight> passengerFlights = reservationChecker(passengerId, flightList);
            if (currentFlights != null) {
                return new ResponseEntity<>(displayError("BadRequest", "400", "Timings of flights: "
                        + currentFlights.get(0).getFlightNumber() + " and " + currentFlights.get(1).getFlightNumber() + " overlap"), HttpStatus.BAD_REQUEST);
            }
            if (passengerFlights != null) {
                return new ResponseEntity<>(displayError("BadRequest", "400", "Timings of flights: "
                        + passengerFlights.get(0).getFlightNumber() + " and " + passengerFlights.get(1).getFlightNumber() + " overlap"), HttpStatus.BAD_REQUEST);
            }
            Flight fullFlight = seatsChecker(flightList);
            if (fullFlight != null) {
                return new ResponseEntity<>(displayError("BadRequest", "400", "Requested flight with id "
                        + fullFlight.getSeatsLeft() + " is full"), HttpStatus.BAD_REQUEST);

            }
            updateSeats(flightList);

            int totalPrice = 0;
            for (Flight f : flightList) {
                totalPrice += f.getPrice();
            }

            Reservation reservation = new Reservation(flightList.get(0).getOrigin(), flightList.get(flightList.size() - 1).getDestination(), totalPrice, passenger, flightList);
            passenger.getReservation().add(reservation);

            for (Flight flight : flightList) {
                flight.getPassengers().add(passenger);
            }
            reservationRepository.save(reservation);
            if (responseType.equals("json"))
                return new ResponseEntity<>(reservationToJSON(reservation), HttpStatus.OK);
            else
                return new ResponseEntity<>(XML.toString(new JSONObject(reservationToJSON(reservation))), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(displayError("BadRequest", "404", "Requested passenger with id " +
                    passengerId + " does not exist"), HttpStatus.NOT_FOUND);
        }
    }

    private List<Flight> flightScheduleChecker(String passengerId, List<Flight> flightList) {
        for (int i = 0; i < flightList.size(); i++) {
            for (int j = i + 1; j < flightList.size(); j++) {
                Date dd = flightList.get(i).getDepartureTime();
                Date ad = flightList.get(i).getArrivalTime();
                Date mi = flightList.get(j).getDepartureTime();
                Date ma = flightList.get(j).getArrivalTime();
                if ((ad.compareTo(mi) >= 0 && ad.compareTo(ma) <= 0) || (dd.compareTo(mi) >= 0 && dd.compareTo(ma) <= 0)) {
                    List<Flight> l = new ArrayList<Flight>();
                    l.add(flightList.get(i));
                    l.add(flightList.get(j));
                    return l;
                }
            }
        }
        return null;
    }

    private List<Flight> reservationChecker(String passengerId, List<Flight> flightList) {
        List<Reservation> reservations = passengerRepository.getById(passengerId).getReservation();
        List<Flight> currentFlights = new ArrayList<>();
        for (Reservation reservation : reservations) {
            currentFlights.addAll(reservation.getFlights());
        }
        for (int i = 0; i < flightList.size(); i++) {
            for (int j = 0; j < currentFlights.size(); j++) {
                Date dd = flightList.get(i).getDepartureTime();
                Date ad = flightList.get(i).getArrivalTime();
                Date mi = currentFlights.get(j).getDepartureTime();
                Date ma = currentFlights.get(j).getArrivalTime();
                if ((ad.compareTo(mi) >= 0 && ad.compareTo(ma) <= 0) || (dd.compareTo(mi) >= 0 && dd.compareTo(ma) <= 0)) {
                    List<Flight> l = new ArrayList<>();
                    l.add(flightList.get(i));
                    l.add(currentFlights.get(j));
                    return l;
                }
            }
        }
        return null;
    }

    public void addPassengerToFlight(Passenger passenger, List<Flight> flightList) {
        for (Flight flight : flightList) {
            flight.getPassengers().add(passenger);
        }
    }

    public Flight seatsChecker(List<Flight> flightList) {
        for (Flight flight : flightList) {
            if (flight.getSeatsLeft() <= 0) 
                return flight;
        }
        return null;
    }

    public Flight updateSeats(List<Flight> flightList) {
        for (Flight flight : flightList) {
            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
        }
        return null;
    }

    public String displayError(String header, String code, String message) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject error = new JSONObject();
        result.put(header, error);
        error.put("code", code);
        error.put("msg", message);
        return result.toString();
    }

    public String reservationToJSON(Reservation reservation) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject container = new JSONObject();
        JSONObject passengerJSON = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
        int i = 0;
        int price = 0;
        Passenger passenger = reservation.getPassenger();
        result.put("reservation", container);
        container.put("orderNumber", "" + reservation.getGenOrderNumber());
        passengerJSON.put("id", "" + passenger.getId());
        passengerJSON.put("firstname", passenger.getFirstName());
        passengerJSON.put("lastname", passenger.getLastName());
        passengerJSON.put("gender", passenger.getGender());
        passengerJSON.put("phone", passenger.getPhone());
        container.put("passenger", passengerJSON);
        for (Flight flight : reservation.getFlights()) {
            arr[i++] = flightToJSON(flight);
            price += flight.getPrice();
            flight.getPassengers().add(passenger);
        }
        container.put("price", "" + price);
        flightsJSON.put("flight", arr);
        container.put("flights", flightsJSON);
        return result.toString();
    }

    public JSONObject flightToJSON(Flight flight) throws JSONException {
        JSONObject flightJSON = new JSONObject();
        flightJSON.put("number", flight.getFlightNumber());
        flightJSON.put("price", "" + flight.getPrice());
        flightJSON.put("origin", flight.getOrigin());
        System.out.println("inside flightToJSON() try 2");
        flightJSON.put("destination", flight.getDestination());
        flightJSON.put("departureTime", flight.getDepartureTime());
        flightJSON.put("arrivalTime", flight.getArrivalTime());
        flightJSON.put("description", flight.getDescription());
        flightJSON.put("seatsLeft", "" + flight.getSeatsLeft());
        flightJSON.put("plane", planeToJSON(flight.getPlane()));
        return flightJSON;
    }

    public JSONObject planeToJSON(Plane plane) throws JSONException {
        JSONObject planeJSON = new JSONObject();
        planeJSON.put("capacity", "" + plane.getCapacity());
        planeJSON.put("model", plane.getModel());
        planeJSON.put("manufacturer", plane.getManufacturer());
        planeJSON.put("yearOfManufacture", "" + plane.getYearOfManufacture());
        return planeJSON;
    }

    public ResponseEntity<?> cancelReservation(int number, String responseType) throws JSONException {
        Reservation reservation;
        try {
            reservation = reservationRepository.findById(number).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(displayError("BadRequest", "404", "Requested reservation with number " + number
                    + " does not exist"), HttpStatus.BAD_REQUEST);
        }
        Passenger passenger = reservation.getPassenger();
        passenger.getReservation().remove(reservation);
        for (Flight flight : reservation.getFlights()) {
            flight.setSeatsLeft(flight.getSeatsLeft() + 1);
            flight.getPassengers().remove(passenger);
        }
        reservationRepository.delete(reservation);
        if (responseType.equals("json"))
            return new ResponseEntity<>(displayError("Response", "200", "Reservation with number " + number + " is canceled successfully"), HttpStatus.OK);
        else
            return new ResponseEntity<>(XML.toString(new JSONObject(displayError("Response", "200", "Reservation with number " + number + " is canceled successfully"))), HttpStatus.OK);
    }

    public ResponseEntity<?> removeFlightUpdate(int number, List<Flight> removeFlights) throws JSONException {
        Reservation reservation;
        try {
            reservation = reservationRepository.findById(number).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(displayError("BadRequest", "404", "Requested reservation with number " + number
                    + " does not exist"), HttpStatus.BAD_REQUEST);
        }   
        for (Flight flight : removeFlights)
            reservation.getFlights().remove(flight);
        reservationRepository.save(reservation);
        return null;
    }

    public ResponseEntity<?> addFlightUpdate(int number, List<Flight> flightsAdded) throws JSONException {
        Reservation reservation = reservationRepository.findById(number).get();
        String passengerId = reservation.getPassenger().getId();
        if (flightScheduleChecker(passengerId, flightsAdded) == null &&
                reservationChecker(passengerId, flightsAdded) == null) {
            for (Flight flight : flightsAdded)
                reservation.getFlights().add(flight);
            reservationRepository.save(reservation);
            return new ResponseEntity<>("Added", HttpStatus.OK);
        } else {
            List<Flight> currentFlights = flightScheduleChecker(passengerId, flightsAdded);
            List<Flight> passengerFlights = reservationChecker(passengerId, flightsAdded);
            if (currentFlights != null) {
                return new ResponseEntity<>(XML.toString(new JSONObject(displayError("BadRequest", "404",
                        "Timings of flights: " + currentFlights.get(0).getFlightNumber()
                                + " and " + currentFlights.get(1).getFlightNumber() + "overlap"))), HttpStatus.NOT_FOUND);
            }
            if (passengerFlights != null) {
                return new ResponseEntity<>(XML.toString(new JSONObject(displayError("BadRequest", "404",
                        "Timings of flights: " + passengerFlights.get(0).getFlightNumber() + " and "
                                + passengerFlights.get(1).getFlightNumber() + "overlap"))), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(displayError("Response", "404", "Time Overlap Constraint Violated"), HttpStatus.NOT_FOUND);
        }
    }

    public List<Flight> getFlights(String[] flight) {
        List<Flight> flights = new ArrayList<Flight>();
        for (String currentFlightNumber : flight)
            flights.add(flightRepository.findByflightNumber(currentFlightNumber));
        return flights;
    }

    public ResponseEntity<?> iSFlightExist(String flightNumber) throws JSONException {
        if (flightRepository.findByflightNumber(flightNumber) == null) {
            return new ResponseEntity<>(displayError("Response", "404", "Flight with number " + flightNumber + " doesn't exist"), HttpStatus.NOT_FOUND);

        }
        return null;
    }
}
