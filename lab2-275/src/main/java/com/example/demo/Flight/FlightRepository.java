package com.example.demo.Flight;

import org.springframework.data.repository.CrudRepository;


public interface FlightRepository extends CrudRepository<Flight, String>{
	
	public Flight findByflightNumber(String flightNumber); 
	

}
