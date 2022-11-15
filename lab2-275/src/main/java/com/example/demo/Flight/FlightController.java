package com.example.demo.Flight;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class FlightController {
	
	@Autowired
	private FlightService flightService;
	@RequestMapping(value = "/flight/{flightNumber}/{departureDate}", method = RequestMethod.POST, produces=MediaType.APPLICATION_XML_VALUE)
	@Transactional
	public ResponseEntity<?> addFlight(@PathVariable String flightNumber,
			@PathVariable String departureDate,
			@RequestParam("price") int price,
			@RequestParam("to") String to,
			@RequestParam("arrivalTime") String arrivalTime,
			@RequestParam("from") String from,
			@RequestParam("capacity") int capacity,
			@RequestParam("departureTime") String departureTime,
			@RequestParam("description") String description,
			@RequestParam("yearOfManufacture") int yearOfManufacture,
			@RequestParam("model") String model,
			@RequestParam("manufacturer") String manufacturer,
			@RequestParam(value="xml", required=false) String xml){
		
		String responseType = "json";
		if(xml != null && xml.equals("true")){
			responseType = "xml";
		}
		return flightService.addFlight(flightNumber, price, from, to, departureDate,
				departureTime, arrivalTime, description, capacity,
				model, yearOfManufacture, manufacturer,responseType);
	}
	
	@RequestMapping(value = "/flight/{flightNumber}/{departureDate}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<?> getFlight(
			@PathVariable String flightNumber, @PathVariable String departureDate,
			@RequestParam(value="xml", required=false) String xml){
		
		String responseType = "json";
		if(xml != null && xml.equals("true")){
			responseType = "xml";
		}
		return flightService.getFlight(flightNumber, responseType);
	}
	
	@RequestMapping(value = "/flight/{flightNumber}/{departureDate}", method = RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<?> updateFlight(@PathVariable String flightNumber,
			@PathVariable String departureDate,
			@RequestParam("price") int price,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("departureTime") String departureTime,
			@RequestParam("arrivalTime") String arrivalTime,
			@RequestParam("description") String description,
			@RequestParam("capacity") int capacity,
			@RequestParam("model") String model,
			@RequestParam("yearOfManufacture") int yearOfManufacture,
			@RequestParam("manufacturer") String manufacturer	){
		
		return flightService.updateFlight(flightNumber, price, from, to, departureDate,
				departureTime, arrivalTime, description, capacity,
				model, yearOfManufacture, manufacturer);
	}
	
	@RequestMapping(value = "/flight/{flightNumber}/{departureDate}", method = RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public String deleteFlight(@PathVariable String flightNumber,@PathVariable String departureDate, @RequestParam(value="xml", required=false) String xml){
		
		String responseType = "json";
		if(xml != null && xml.equals("true")){
			responseType = "xml";
		}
		
		return flightService.deleteFlight(flightNumber,responseType);
	}

	

}
