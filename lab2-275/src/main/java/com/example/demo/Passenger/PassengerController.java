package com.example.demo.Passenger;
//import java.awt.PageAttributes.MediaType;
import java.util.*;
import com.example.demo.Passenger.*;
import com.example.demo.Flight.*;
import com.example.demo.Reservation.*;
import org.springframework.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RestController
public class PassengerController {
	
	@Autowired
	private PassengerServices passengerServices;
	
	//Get Passenger
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getPassenger(@PathVariable String id, 
			@RequestParam(value = "xml", required=false) String xml){
		
		String responseType="json";
		
		if(xml != null && xml.equals("true")){ // ?xml=true
			responseType="xml";
		}
		
		return passengerServices.getPassengers(id, responseType);
	}
	
	//Create a Passenger
	@Transactional
	@RequestMapping(value="/passenger", method=RequestMethod.POST, produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> createPassenger(
			@RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname,
			@RequestParam("gender") String gender,
			@RequestParam("phone") String phone,
			@RequestParam("birthyear") int birthyear) {
		
				ResponseEntity<?> res= passengerServices.createPassenger(firstname, lastname, birthyear, gender, phone);
				
				return res;
		
	}
	
	//Update a Passenger
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updatePassenger(
			@PathVariable String id, 
			@RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname,
			@RequestParam("birthyear") int birthyear,
			@RequestParam("gender") String gender,
			@RequestParam("phone") String phone
			) {
		
		 return passengerServices.updatePassenger(id, firstname, 
				lastname, birthyear, gender, phone);
	}
	//Delete a Passenger
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deletePassenger(@PathVariable String id) {
		return passengerServices.deletePassenger(id);
	}
	
}
