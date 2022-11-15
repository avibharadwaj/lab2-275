package com.example.demo.Flight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Passenger.Passenger;
import com.example.demo.Passenger.PassengerRepository;
import com.example.demo.Plane.Plane;
import com.example.demo.Reservation.Reservation;
import com.example.demo.Reservation.ReservationRepository;

//import edu.sjsu.cmpe275.passenger.Passenger;
//import edu.sjsu.cmpe275.passenger.PassengerRepository;
//import edu.sjsu.cmpe275.plane.Plane;
//import edu.sjsu.cmpe275.reservation.ReservationRepository;



@Service
public class FlightService {
	
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private PassengerRepository passengerRepository;
	
	

	
	public ResponseEntity<?> getFlight(String flightNumber, String responseType) {
		
		
		
		Flight flight = flightRepository.findById(flightNumber).orElse(null);
		System.out.println(" get the Flight inside");
		
		if(flight != null){
						
			
			if(responseType.equals("json"))
				return  new ResponseEntity<>(flightToJSONString(flight).toString(),HttpStatus.OK);
			else
				try {
					return new ResponseEntity<>(XML.toString((flightToJSONString(flight))),HttpStatus.OK);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested flight with number " 
							+ flightNumber +" does not exist"), HttpStatus.NOT_FOUND);
				}
		}
		else{
			System.out.println("else of get the Flight");
			
			if(responseType.equals("json"))
				return  new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested flight with number " + flightNumber +" does not exist") ,HttpStatus.NOT_FOUND);
			else
				return new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested flight with number " 
			+ flightNumber +" does not exist"), HttpStatus.NOT_FOUND);
		}
	}
	
	public ResponseEntity<?> addFlight(String flightNumber, int price, String from, String to, String departureDate, String departureTime,
			String arrivalTime, String description, int capacity, String model, int yearOfManufacture,
			String manufacturer,String responseType) {
		
		Date departure = null, arrival = null, dateD =null;
		try {
			
			if(flightRepository.findByflightNumber(flightNumber)!=null)
				return updateFlight(flightNumber, price, from, to, departureDate, departureTime, arrivalTime, 
						description, capacity, model, yearOfManufacture, manufacturer);
			
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println("add Flight fn");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
			
			System.out.println("inside add");
			
				departure = dateFormat.parse(departureTime);
				arrival = dateFormat.parse(arrivalTime);
				dateD = dateFormat1.parse(departureDate);
				System.out.println("inside add2");
				
				if((departure).compareTo(arrival)>0){
					return  new ResponseEntity<>(displayError("BadRequest", "400", 
							"Sorry, the departure time cannot be greater than the arrival time"), 
							HttpStatus.BAD_REQUEST);
				}
				System.out.println("datetimne"+departureTime.substring(0, departureTime.length()-3));
				System.out.println("date"+departureDate);
				System.out.println("equals"+(departureTime.substring(0, departureTime.length()-3)).equals(departureDate));
				if(((departureTime.substring(0, departureTime.length()-3)).equals(departureDate)) != true){
					System.out.println("EXCEPT");
					return  new ResponseEntity<>(displayError("BadRequest", "400", 
							"Sorry, the departure Time and departure Date are not on the same day"), 
							HttpStatus.BAD_REQUEST);
				}
				
		

			
			Plane plane = new Plane(model, capacity, manufacturer, yearOfManufacture);
			
			Flight flight = new Flight(flightNumber, price, from, to, dateD, departure, arrival, description, new ArrayList<Passenger>(), plane);
			
			flightRepository.save(flight);
		
		} catch (Exception e) {
			
			System.out.println(e);
			return  new ResponseEntity<>(displayError("BadRequest", "400", 
					"Sorry, there was some problem."), 
					HttpStatus.BAD_REQUEST);
			

		}
		if(responseType.equals("json"))
			return getFlight(flightNumber, "json");
		
		return getFlight(flightNumber, "xml");
	}

	
	public String deleteFlight(String flightNumber,String responseType) {
		
		Flight flight = flightRepository.findById(flightNumber).orElse(null);
		System.out.println("delete the Flight inside");
		
		if(flight != null){
			try{
				
				flightRepository.delete(flight);
				
			}
			catch(Exception e){
			
				List<Reservation> reservations=(List<Reservation>) reservationRepository.findAll();
				System.out.println("catch delete the Flight");
				for(Reservation reservation: reservations){
					if(reservation.getFlights().contains(flight)){
						
						return displayError("BadRequest", "400", "Flight "+ flight.getFlightNumber()+" cannot be deleted as it is being used in reservation "+ reservation.getReservationNumber());
					}
				}
				return displayError("BadRequest", "400", "Flight cannot be romved, their are some passenger(s) who have booked this flight");
			}
			if(responseType.equals("json"))
				return displayError("Response", "200", "Flight with number " + flightNumber + " is deleted successfully");
			else 
				return "<Response>\n"
						+ "           <code> 200 </code>\n"
						+ "           <msg> Flight with number " + flightNumber + " is deleted successfully  </msg>\n"
						+ "</Response>\n"
						+ "";
			
		}
		return displayError("BadRequest", "404", "Flight with number " + flightNumber + " not found");
	}
	
	public ResponseEntity<?> updateFlight(String flightNumber, int price, String from, String to, String departureDate, String departureTime,
			String arrivalTime, String description, int capacity, String model, int yearOfManufacture,
			String manufacturer) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		
		Flight flight = flightRepository.findById(flightNumber).orElse(null);

		System.out.println("update the Flight"); 
		Date departure = null, arrival = null ,dateD =null;
		try {
			departure = dateFormat.parse(departureTime);
			arrival = dateFormat.parse(arrivalTime);
			dateD = dateFormat1.parse(departureDate);
			
			if(departure.compareTo(arrival)>=0)
				return  new ResponseEntity<>(displayError("BadRequest", "404", "arrival time cannot be lesser than the departure time") ,HttpStatus.NOT_FOUND);
			if(((departureTime.substring(0, departureTime.length()-3)).equals(departureDate)) != true){
				return  new ResponseEntity<>(displayError("BadRequest", "400", 
						"departure Time and departure Date are not on the same day"), 
						HttpStatus.BAD_REQUEST);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(flight != null){
			if(departure.compareTo(flight.getDepartureTime())!=0 || arrival.compareTo(flight.getArrivalTime())!=0){
				List<Passenger> passengers = (List<Passenger>) passengerRepository.findAll();
				for(Passenger passenger : passengers){
					List<Reservation> reservations=passenger.getReservation();
					List<Flight> currentPassengerFlights=new ArrayList<Flight>();
					for(Reservation reservation:reservations){
						for(Flight currentPassegnerFlight:reservation.getFlights()){
							currentPassengerFlights.add(currentPassegnerFlight);
						}
					}
					for(Flight currentFlight: currentPassengerFlights){
						if(currentFlight.getFlightNumber().equals(flightNumber)){
							
							flight.setDepartureTime(departure);
							flight.setArrivalTime(arrival);
							Flight returnedFlight=checkFlightUpdateForPassengers(currentPassengerFlights,flight);
							if(returnedFlight!=null){
								return  new ResponseEntity<>(displayError("BadRequest", "404", "Flight number " + flightNumber + " can't be updated since it is overlapping with the flight "+returnedFlight.getFlightNumber()),HttpStatus.NOT_FOUND);
							}
						}
					}
				}
			}
			System.out.println("conflicts not there");
			try{
				flight.setDestination(to);
				flight.setPrice(price);
				flight.setDepartureDate(dateD);
				flight.setOrigin(from);
				flight.setArrivalTime(arrival);
				flight.setDescription(description);
				flight.setDepartureTime(departure);
				flight.getPlane().setModel(model);
				flight.getPlane().setYearOfManufacture(yearOfManufacture);
				flight.getPlane().setCapacity(capacity);
				flight.getPlane().setManufacturer(manufacturer);
				
				flightRepository.save(flight);

			}
			catch(Exception e){
				System.out.println("catch delete the Flight");
				return  new ResponseEntity<>(displayError("BadRequest", "400", "deleting flight error" ),HttpStatus.NOT_FOUND);
			}
		}
		return getFlight(flightNumber, "xml");
	}

	
	public Flight checkFlightUpdateForPassengers(List<Flight> passengerFlights, Flight updatedFlight){
		for(Flight flight: passengerFlights){
			
			if(flight.getFlightNumber().equals(updatedFlight.getFlightNumber()))
				continue;
			Date currentFlightDepartureDate=flight.getDepartureTime();
			Date currentFlightArrivalDate=flight.getArrivalTime();
			Date min=updatedFlight.getDepartureTime();
			Date max=updatedFlight.getArrivalTime();
			
			if((currentFlightArrivalDate.compareTo(min)>=0 && currentFlightArrivalDate.compareTo(max)<=0) || (currentFlightDepartureDate.compareTo(min)>=0 && currentFlightDepartureDate.compareTo(max)<=0)){
				

				return flight;
			}
		}
		return null;
	}
	
	

	public String displayError(String header, String code, String message){
		JSONObject error = new JSONObject();
		JSONObject result = new JSONObject();
		
		try{
			result.put(header, error);
			error.put("code", code);
			error.put("msg", message);
			
		}catch(Exception e){
			System.out.println("catch generate err message");
		}
		
		return result.toString();
	}
	
	public JSONObject flightToJSONString(Flight flight){
		JSONObject json = new JSONObject();
		JSONObject flightJSON = new JSONObject();
		JSONObject passenger = new JSONObject();
		JSONObject arr[] = new JSONObject[flight.getPassengers().size()];
		int i = 0;
		
		try {
			json.put("flight", flightJSON);
			flightJSON.put("flightNumber", flight.getFlightNumber());
			flightJSON.put("price", ""+flight.getPrice());
			flightJSON.put("from", flight.getOrigin());
			flightJSON.put("to", flight.getDestination());
			flightJSON.put("departureDate", flight.getDepartureDate());
			flightJSON.put("departureTime", flight.getDepartureTime());
			flightJSON.put("arrivalTime", flight.getArrivalTime());
			flightJSON.put("description", flight.getDescription());
			flightJSON.put("seatsLeft", ""+flight.getSeatsLeft());
			flightJSON.put("plane", planeToJSON(flight.getPlane()));
			flightJSON.put("passengers", passenger);
			
			
			for(Passenger pass : flight.getPassengers()){
				
				arr[i++] = passToJSON(pass);
			}
			passenger.put("passenger", arr);
			
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return json;
	}
	
	private JSONObject passToJSON(Passenger passenger) {
		JSONObject json = new JSONObject();
		System.out.println("inside passToJSON");

		try {
			
			json.put("id", ""+passenger.getId());
			json.put("firstname", ""+passenger.getFirstName());
			json.put("lastname", ""+passenger.getLastName());
			json.put("birthyear", ""+passenger.getBirthYear());
			json.put("gender", ""+passenger.getGender());
			json.put("phone", ""+passenger.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("catch passenger To JSON strings ");
			
		}
		return json;
	}

	public JSONObject planeToJSON(Plane plane){
		JSONObject planeJSON = new JSONObject();

		try {
			planeJSON.put("model", plane.getModel());
			planeJSON.put("capacity", ""+plane.getCapacity());
			planeJSON.put("yearOfManufacture", ""+plane.getYearOfManufacture());
			planeJSON.put("manufacturer", plane.getManufacturer());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return planeJSON;
	}

}
