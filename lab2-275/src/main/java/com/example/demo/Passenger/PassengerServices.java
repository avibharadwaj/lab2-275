package com.example.demo.Passenger;

import com.example.demo.Flight.Flight;
import com.example.demo.Plane.Plane;
import com.example.demo.Reservation.Reservation;
import com.example.demo.Reservation.ReservationRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerServices {
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private PassengerRepository passengerRepository;
	
	public ResponseEntity<?> getPassengers(String id, String responseType) throws JSONException {
		System.out.println("---Returning passenger by Id---");
		//Passenger passenger = passengerRepository.findById(Integer.parseInt(id)).get();
		Passenger passenger = passengerRepository.getById(id);
		if(passenger==null){
			return new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested passenger with id " 
			+ id +" does not exist"), HttpStatus.NOT_FOUND);
		}
		else{
			try {
			if(responseType.equals("json"))
				return  new ResponseEntity<>(passToJSON(passenger),HttpStatus.OK);
			else
				return  new ResponseEntity<>(XML.toString(new JSONObject(passToJSON(passenger))),HttpStatus.OK);
			} catch(Exception e) {
				return new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested passenger with id " 
						+ id +" does not exist"), HttpStatus.NOT_FOUND);				
			}
		}
	}
	
	public ResponseEntity<?> createPassenger(String firstname, String lastname, int birthyear, String gender, String phone, String responseType) throws JSONException{
		System.out.println("---Creating a new Passenger---");
		Passenger passenger = passengerRepository.getByPhone(phone);
		JSONObject pJSON = new JSONObject();
		JSONObject json = new JSONObject();
		JSONObject reservations = new JSONObject();
		JSONObject array[] = new JSONObject[0];
		if(passenger == null){
			System.out.println("HELLO");
			Passenger newPassenger = new Passenger(firstname, lastname, birthyear, gender, phone);
			passengerRepository.save(newPassenger);
			try {
				newPassenger.setId(""+newPassenger.getGenId());
				pJSON.put("passenger", json);
				System.out.print("HELLLLLLLO");
				json.put("id", newPassenger.getId());
				System.out.println("SDFSDF");
				json.put("firstname", firstname);
				json.put("lastname", lastname);
				json.put("gender", gender);
				json.put("phone", phone);
				reservations.put("reservation", array);
				json.put("reservations", reservations);
			} catch (Exception e) {
				e.printStackTrace();
			}
            if (responseType.equals("json"))
                return new ResponseEntity<>(pJSON.toString(), HttpStatus.OK);
            else
               return new ResponseEntity<>(XML.toString(new JSONObject(pJSON.toString())), HttpStatus.OK);
			
		}
		else{
			return new ResponseEntity<>(displayError("BadRequest", "400", "Another passenger with the same number already exists" ),HttpStatus.BAD_REQUEST);
		}
	}
	
	public ResponseEntity<?> updatePassenger(String id, String firstname, String lastname, int birthyear, String gender, String phone, String responseType) throws JSONException{
		System.out.println("---Updating Passenger---");
		Passenger passenger = passengerRepository.getById(id);
		JSONObject json = new JSONObject();

		if(passenger == null){
			return  new ResponseEntity<>(displayError("BadRequest", "404", 
					"No such Passenger exists" ),HttpStatus.BAD_REQUEST);
		}
		else {
		try{
			Passenger guest=passengerRepository.getByPhone(phone);
			if(guest != null && !guest.getId().equals(id))
				return  new ResponseEntity<>(displayError("BadRequest", "404", 
						"The passenger with phone number "+guest.getPhone()
						+" already exists in the DB!" ),HttpStatus.NOT_FOUND);
			

			passenger.setFirstName(firstname);
			passenger.setGender(gender);
			passenger.setLastName(lastname);
			passenger.setPhone(phone);
			passenger.setBirthYear(birthyear);
			passengerRepository.save(passenger);
			
			json.put("id", passenger.getId());
			json.put("firstname", firstname);
			json.put("lastname", lastname);
			json.put("birthyear", birthyear);
			json.put("gender", gender);
			json.put("phone", phone);
			
		} catch (Exception e) {
			return  new ResponseEntity<>(displayError("BadRequest", "404", "The requested Passenger was not updated" ),HttpStatus.NOT_FOUND);

		}
	}
		
//		return  new ResponseEntity<>(passToJSON(passenger), HttpStatus.OK);
        if (responseType.equals("json"))
            return new ResponseEntity<>(passToJSON(passenger), HttpStatus.OK);
        else
           return new ResponseEntity<>(XML.toString(json), HttpStatus.OK);

	}
	
	public ResponseEntity<?> deletePassenger(String id) throws JSONException {
		System.out.println("---Deleting a Passenger by Id---");
		Passenger passenger = passengerRepository.getById(id);
		if(passenger == null){
			return new ResponseEntity<>(displayError("BadRequest", "404", "Sorry, the requested passenger with id " + id +" does not exist" ), HttpStatus.NOT_FOUND);
		}
		else{
			try{
				List<Reservation> reservations = reservationRepository.findByPassenger(passenger);
				for(Reservation reservation : reservations){
					deleteReservations(reservation, passenger);
				}
				passengerRepository.delete(passenger);
			}
			catch(Exception e){
				e.printStackTrace();
				return new ResponseEntity<>(displayError("BadRequest", "404", "Could not delete a passenger. Please try again."), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		return new ResponseEntity<>(XML.toString(new JSONObject(displayError("Response", "200", "Passenger with id " + id + " is deleted successfully"))),HttpStatus.OK);
	}

	public void deleteReservations(Reservation reservation, Passenger passenger){
		try{
			for(Flight flight : reservation.getFlights()){
				changeSeatCount(flight);
				flight.getPassengers().remove(passenger);
			}
			
			passenger.getReservation().remove(reservation);
			reservationRepository.delete(reservation);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void changeSeatCount(Flight flight){
		
		try{
			flight.setSeatsLeft(flight.getSeatsLeft()+1);
		}
		catch(Exception e){}
	}	
	public String displayError(String header, String code, String message){
		JSONObject result = new JSONObject();
		JSONObject error = new JSONObject();
		
		try{
			result.put(header, error);
			error.put("code", code);
			error.put("msg", message);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}

	public String passToJSON(Passenger passenger){
		JSONObject result = new JSONObject();
		JSONObject fields = new JSONObject();
		JSONObject reservationsJSON = new JSONObject();
		JSONObject arr[] = null;
		
		try {
			result.put("passenger", fields);
			
			fields.put("id", ""+passenger.getId());
			fields.put("firstname", passenger.getFirstName());
			fields.put("lastname", passenger.getLastName());
			fields.put("age", ""+passenger.getBirthYear());
			fields.put("gender", passenger.getGender());
			fields.put("phone", passenger.getPhone());
			
			int i = 0;
			List<Reservation> reservations = passenger.getReservation();
			arr = new JSONObject[reservations.size()];

			for(Reservation reservation : reservations){
				arr[i++] = reservationToJSON(reservation);
			}
			reservationsJSON.put("reservation", arr);
			fields.put("reservations", reservationsJSON);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public JSONObject reservationToJSON(Reservation reservation){
		
		JSONObject result = new JSONObject();
		JSONObject flightsJSON = new JSONObject();
		JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
		int i = 0, price = 0;
		
		try {
			result.put("orderNumber", ""+reservation.getGenOrderNumber());
					
			for(Flight flight : reservation.getFlights()){
				arr[i++] =  flightToJSON(flight);
				price += flight.getPrice();
			}
			result.put("price", ""+price);
			flightsJSON.put("flight", arr);
			result.put("flights", flightsJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject flightToJSON(Flight flight){
		JSONObject json = new JSONObject();
		JSONObject flightJSON = new JSONObject();

		try {
			json.put("flight", flightJSON);
			flightJSON.put("number", flight.getFlightNumber());
			flightJSON.put("price", ""+flight.getPrice());
			flightJSON.put("from", flight.getOrigin());
			flightJSON.put("to", flight.getDestination());
			flightJSON.put("departureTime", flight.getDepartureTime());
			flightJSON.put("arrivalTime", flight.getArrivalTime());
			flightJSON.put("description", flight.getDescription());
			flightJSON.put("plane", planeToJSON(flight.getPlane()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public JSONObject planeToJSON(Plane plane){
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
	
}
