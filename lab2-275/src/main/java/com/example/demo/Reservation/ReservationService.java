package com.example.demo.Reservation;

import com.example.demo.Flight.Flight;
import com.example.demo.Flight.FlightRepository;
import com.example.demo.Passenger.Passenger;
import com.example.demo.Passenger.PassengerRepository;
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

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    public ResponseEntity<?> addReservation(String passengerId, List<Flight> flightList, String responseType) {
        System.out.println("inside addReservation()");
        Passenger passenger = passengerRepository.getById(passengerId);


        if(passenger != null){
            System.out.println("inside addReservation() if");

            List<Flight> currentReservationFlights=checkCurrentreservationFlightsTimings(passengerId, flightList);
            List<Flight> passengerFlights=checkWithExistingPassengerReservations(passengerId, flightList);
            if(currentReservationFlights!=null){
                return new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, the timings of flights: "
                        +currentReservationFlights.get(0).getNumber() +" and "+ currentReservationFlights.get(1).getNumber()+" overlap" ), HttpStatus.NOT_FOUND);
            }
            if(passengerFlights!=null){
                return new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, the timings of flights: "
                        +passengerFlights.get(0).getNumber() +" and "+ passengerFlights.get(1).getNumber()+" overlap" ), HttpStatus.NOT_FOUND);
            }
            Flight fullFlight = checkSeats(flightList);
            if(fullFlight != null){
                return new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, the requested flight with id "
                        + fullFlight.getSeatsLeft() +" is full" ), HttpStatus.NOT_FOUND);

            }
            decreaseFlightSeats(flightList);
            Reservation reservation = new Reservation(0, passenger, flightList);
            passenger.getReservation().add(reservation);

            for(Flight flight : flightList){
                flight.getPassenger().add(passenger);
            }
            System.out.println("inside addReservation() if 1");
            reservationRepository.save(reservation);
            System.out.println("inside addReservation() if 2");
            if(responseType.equals("json"))
                return  new ResponseEntity<>(reservationToJSONString(reservation),HttpStatus.OK);
            else
                return  new ResponseEntity<>(XML.toString(new JSONObject(reservationToJSONString(reservation))),HttpStatus.OK);
        }
        else{
            System.out.println("inside addReservation() else");
            return new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, the requested passenger with id " +
                    passengerId +" does not exist" ), HttpStatus.NOT_FOUND);

        }
    }

    private List<Flight> checkCurrentreservationFlightsTimings(String passengerId, List<Flight> flightList) {
        for(int i=0;i<flightList.size();i++){
            for(int j=i+1;j<flightList.size();j++){
                Date currentFlightDepartureDate=flightList.get(i).getDepartureTime();
                Date currentFlightArrivalDate=flightList.get(i).getArrivalTime();
                Date min=flightList.get(j).getDepartureTime();
                Date max=flightList.get(j).getArrivalTime();
                if((currentFlightArrivalDate.compareTo(min)>=0 && currentFlightArrivalDate.compareTo(max)<=0) || (currentFlightDepartureDate.compareTo(min)>=0 && currentFlightDepartureDate.compareTo(max)<=0)){
                    System.out.println("I am failing here checkCurrentreservationFlightsTimings");
                    List<Flight> list= new ArrayList<Flight>();
                    list.add(flightList.get(i));
                    list.add(flightList.get(j));
                    return list;
                }
            }
        }
        return null;

    }

    private List<Flight> checkWithExistingPassengerReservations(String passengerId, List<Flight> flightList){
        System.out.println("");
        List<Reservation> reservations=passengerRepository.getById(passengerId).getReservation();
        List<Flight> currentPassengerFlights=new ArrayList<Flight>();
        for(Reservation reservation:reservations){
            currentPassengerFlights.addAll(reservation.getFlights());
        }
        for(int i=0;i<flightList.size();i++){
            for(int j=0;j<currentPassengerFlights.size();j++){
                try{
                    Flight flight = flightList.get(i);
                }
                catch(Exception e){
                    //return new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, one of the flights does not exist" ), HttpStatus.NOT_FOUND);
                }
                Date currentFlightDepartureDate=flightList.get(i).getDepartureTime();
                Date currentFlightArrivalDate=flightList.get(i).getArrivalTime();
                Date min=currentPassengerFlights.get(j).getDepartureTime();
                Date max=currentPassengerFlights.get(j).getArrivalTime();
                if((currentFlightArrivalDate.compareTo(min)>=0 && currentFlightArrivalDate.compareTo(max)<=0) || (currentFlightDepartureDate.compareTo(min)>=0 && currentFlightDepartureDate.compareTo(max)<=0)){
                    System.out.println("I am failing here checkCurrentreservationFlightsTimings");
                    //return false;
                    List<Flight> list= new ArrayList<Flight>();
                    list.add(flightList.get(i));
                    list.add(currentPassengerFlights.get(j));
                    return list;
                }
            }
        }
        return null;
    }

    public ResponseEntity<?> getReservation(int number, String responseType) throws JSONException {
        // TODO Auto-generated method stub

        System.out.println("inside getReservation()");
        Reservation reservation = reservationRepository.findOne(number);
        if(reservation == null){
            System.out.println("inside getReservation() if");

            //return generateErrorMessage("BadRequest", "404", "Sorry, the requested reservation with number " + number +" does not exist");
            return  new ResponseEntity<>(generateErrorMessage("BadRequest", "404", "Sorry, the requested reservation with number " + number
                    +" does not exist"),HttpStatus.NOT_FOUND);

        }
        else{
            System.out.println("inside getReservation() else");
            System.out.println("getReservation() flight size "+reservation.getFlights().size());
            //		return reservationToJSONString(reservation);

            if(responseType.equals("json"))
                return  new ResponseEntity<>(reservationToJSONString(reservation),HttpStatus.OK);
            else
                return  new ResponseEntity<>(XML.toString(new JSONObject(reservationToJSONString(reservation))),HttpStatus.OK);

//            return  new ResponseEntity<>(reservationToJSONString(reservation),HttpStatus.OK);
        }
    }

    public void addPassengerToFlight(Passenger passenger, List<Flight> flightList){
        for(Flight flight : flightList){
            flight.getPassenger().add(passenger);
        }
    }

    public Flight checkSeats(List<Flight> flightList){
        for(Flight flight : flightList){
            if(flight.getSeatsLeft() <= 0) return flight;
        }
        return null;
    }

    public Flight decreaseFlightSeats(List<Flight> flightList){
        for(Flight flight : flightList){
            flight.setSeatsLeft(flight.getSeatsLeft()-1);
        }
        return null;
    }

    public String generateErrorMessage(String header, String code, String message){
        JSONObject result = new JSONObject();
        JSONObject error = new JSONObject();

        try{
            result.put(header, error);
            error.put("code", code);
            error.put("msg", message);
        }catch(Exception e){
            System.out.println("generateErrorMessage() catch");
        }

        return result.toString();
    }

    public String reservationToJSONString(Reservation reservation){

        JSONObject result = new JSONObject();
        JSONObject container = new JSONObject();
        JSONObject passengerJSON = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
        int i = 0, price = 0;
        Passenger passenger = reservation.getPassenger();

        System.out.println("inside reservationToJSONString()");
        System.out.println("getReservation() flight size "+reservation.getFlights().size());

        try {
            result.put("reservation", container);

            container.put("orderNumber", ""+reservation.getGenOrderNumber());

            passengerJSON.put("id", ""+passenger.getId());
            passengerJSON.put("firstname", passenger.getFirstName());
            passengerJSON.put("lastname", passenger.getLastName());
            passengerJSON.put("gender", passenger.getGender());
            passengerJSON.put("phone", passenger.getPhone());
            container.put("passenger", passengerJSON);

            for(Flight flight : reservation.getFlights()){
                arr[i++] =  flightToJSONString(flight);
                price += flight.getPrice();
                flight.getPassenger().add(passenger);
            }
            container.put("price", ""+price);
            flightsJSON.put("flight", arr);
            container.put("flights", flightsJSON);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.toString();
    }

    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightJSON = new JSONObject();
        System.out.println("inside flightToJSONString()");

        try {
            System.out.println("inside flightToJSONString() try 1");
            flightJSON.put("number", flight.getNumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getFromSource());
            System.out.println("inside flightToJSONString() try 2");
            flightJSON.put("to", flight.getFromSource());
            flightJSON.put("departureTime", flight.getDepartureTime());
            flightJSON.put("arrivalTime", flight.getArrivalTime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", ""+flight.getSeatsLeft());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            System.out.println("inside flightToJSONString() catch");
            e.printStackTrace();
        }
        System.out.println("inside flightToJSONString() retruning");
        return flightJSON;
    }

    public JSONObject planeToJSONString(Plane plane){
        JSONObject planeJSON = new JSONObject();

        try {
            planeJSON.put("capacity", ""+plane.getCapacity());
            planeJSON.put("model", plane.getModel());
            planeJSON.put("manufacturer", plane.getManufacturer());
            planeJSON.put("yearOfManufacture", ""+plane.getYearOfManufacture());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planeJSON;
    }

    public ResponseEntity<?> deleteReservation(int number, String responseType) {
        System.out.println("inside deleteReservation()");

        Reservation reservation = reservationRepository.findOne(number);
        if(reservation == null){
            System.out.println("inside deleteReservation() if");

            return  new ResponseEntity<>(generateErrorMessage("BadRequest", "404",
                    "Sorry, the requested reservation with number " + number
                            +" does not exist"),HttpStatus.NOT_FOUND);
        }
        else{
            System.out.println("inside deleteReservation() else");
            System.out.println("getReservation() flight size "+reservation.getFlights().size());
            Passenger passenger = reservation.getPassenger();
            passenger.getReservation().remove(reservation);
            for(Flight flight : reservation.getFlights()){
                System.out.println("deleteReservation() else for");
                flight.setSeatsLeft(flight.getSeatsLeft()+1);
                flight.getPassenger().remove(passenger);
            }

            reservationRepository.delete(reservation);

            if(responseType.equals("json"))
                return  new ResponseEntity<>(generateErrorMessage("Response", "200", "Reservation with number " + number + " is canceled successfully"),HttpStatus.OK);
            else
                return  new ResponseEntity<>(XML.toString(new JSONObject(generateErrorMessage("Response", "200", "Reservation with number " + number + " is canceled successfully"))),HttpStatus.OK);

//            return  new ResponseEntity<>(XML.toString(new JSONObject(generateErrorMessage("Response", "200", "Reservation with number " + number + " is canceled successfully"))),HttpStatus.OK);
            //return generateErrorMessage("Response", "200", "Reservation with number " + number + " is canceled successfully");
        }
    }

    public ResponseEntity<?> updateReservatonRemoveFlights(int number, List<Flight> removeFlights) {
        // TODO Auto-generated method stub
        Reservation reservation = reservationRepository.findOne(number);

        if(reservation == null){
            return  new ResponseEntity<>(generateErrorMessage("BadRequest", "404",
                    "Sorry, the requested reservation with number "
                            + number + " does not exist"), HttpStatus.NOT_FOUND);
        }

        for(Flight flight: removeFlights)
            reservation.getFlights().remove(flight);
        reservationRepository.save(reservation);

        return null;
    }

    public ResponseEntity<?> updateReservationAddFlights(int number, List<Flight> flightsAdded) {
        // TODO Auto-generated method stub
        Reservation reservation = reservationRepository.findOne(number);
        String passengerId=reservation.getPassenger().getId();
        if(checkCurrentreservationFlightsTimings(passengerId, flightsAdded)==null &&
                checkWithExistingPassengerReservations(passengerId, flightsAdded)==null){
            for(Flight flight:flightsAdded)
                reservation.getFlights().add(flight);
            reservationRepository.save(reservation);
//			throw new RuntimeException("Testing transections");

            return new ResponseEntity<>("Success",HttpStatus.OK);
        }
        else{
            List<Flight> currentReservationFlights=checkCurrentreservationFlightsTimings(passengerId, flightsAdded);
            List<Flight> passengerFlights=checkWithExistingPassengerReservations(passengerId, flightsAdded);
            if(currentReservationFlights!=null){
                return new ResponseEntity<>(XML.toString(new JSONObject(generateErrorMessage("BadRequest", "404",
                        "Sorry, the timings of flights: "+currentReservationFlights.get(0).getNumber()
                                +" and "+ currentReservationFlights.get(1).getNumber()+"overlap" ))), HttpStatus.NOT_FOUND);
            }
            if(passengerFlights!=null){
                return new ResponseEntity<>(XML.toString(new JSONObject(generateErrorMessage("BadRequest", "404",
                        "Sorry, the timings of flights: "+passengerFlights.get(0).getNumber() +" and "
                                + passengerFlights.get(1).getNumber() + "overlap" ))), HttpStatus.NOT_FOUND);
            }
            return  new ResponseEntity<>(generateErrorMessage("Response", "404", "Time Overlap Constraint Violated"),HttpStatus.NOT_FOUND);

        }
    }

    public ResponseEntity<?> checkFlights(List<Flight> flights){
        List<Flight> flightList=(List<Flight>) flightRepository.findAll();
        for(Flight flight:flights){
            boolean flag=false;
            for(Flight currentFlight:flightList){
                if(currentFlight.getNumber().equals(flight.getNumber())){
                    flag=true;
                }
            }
            if(!flag)
                return new ResponseEntity<>((generateErrorMessage("BadRequest", "404",
                        "Sorry, the requested flight with number "
                                + flight.getNumber() + " does not exist")),HttpStatus.NOT_FOUND);
        }
        return null;
    }

    public List<Flight> getFlights(String[] flight){
        List<Flight> flights=new ArrayList<Flight>();
        for(String currentFlightNumber:flight)
            flights.add(flightRepository.findOne(currentFlightNumber));
        return flights;
    }

    public ResponseEntity<?> checkFlightExistance(String flightNumber){
        if( flightRepository.findOne(flightNumber)==null){
            return  new ResponseEntity<>(generateErrorMessage("Response", "404", "Flight with number "+flightNumber+" doesn't exist"),HttpStatus.NOT_FOUND);

        }
        return null;
    }

    private ResponseEntity<?> getReservation(List<Reservation> reservationList, String responseType) {

        if(reservationList == null || reservationList.size() == 0){
            return new ResponseEntity<>(generateErrorMessage("BadRequest", "200",
                    "Sorry, the requested search criteria doesn't return any reservations." ), HttpStatus.OK);
        }
        String outer = "";
        for(Reservation reservation : reservationList){
            String temp = reservationToJSONString(reservation);

            outer = outer + temp;
        }

        if(responseType.equals("json"))
            return  new ResponseEntity<>(outer,HttpStatus.OK);
        else
            return  new ResponseEntity<>(XML.toString(new JSONObject(outer),HttpStatus.OK);

    }

    public void helper(List<Reservation> reservations, List<Flight> numList, List<Flight> flight, String source){

        for(int i=0;i<flight.size();i++){
            if(flight.get(i).getFromSource().equals(source))
                numList.add(flight.get(i));
        }
        reservations=(List<Reservation>) reservationRepository.findAll();
        for(int i=0;i<reservations.size();i++){
            List<Flight> flightList=new ArrayList<>();
            flightList=reservations.get(i).getFlights();
            List<Flight> latest = new ArrayList<>();
            for(int j=0;j<flightList.size();j++){
                Flight flightD=flightList.get(j);
                if(numList.contains(flightD))
                    continue;
                else
                    latest.add(flightD);
            }
            for(int k=0;k<latest.size();k++){
                flightList.remove(latest.get(k));
            }
            if(flightList.size()==0)
                reservations.remove(reservations.get(i));
        }
    }

    public void helper2(List<Reservation> reservations, List<Flight> numList, List<Flight> flight, String source){

        for(int i=0;i<flight.size();i++){
            if(flight.get(i).getNumber().equals(source))
                numList.add(flight.get(i));
        }
        reservations=(List<Reservation>) reservationRepository.findAll();
        for(int i=0;i<reservations.size();i++){
            List<Flight> flightList=new ArrayList<>();
            flightList=reservations.get(i).getFlights();
            List<Flight> latest = new ArrayList<>();
            for(int j=0;j<flightList.size();j++){
                Flight flightD=flightList.get(j);
                if(numList.contains(flightD))
                    continue;
                else
                    latest.add(flightD);
            }
            for(int k=0;k<latest.size();k++){
                flightList.remove(latest.get(k));
            }
            if(flightList.size()==0)
                reservations.remove(reservations.get(i));
        }
    }
}
