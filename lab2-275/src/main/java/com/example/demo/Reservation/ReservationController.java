package com.example.demo.Reservation;

import com.example.demo.Flight.Flight;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value="/reservation/{number}", method= RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReservation(@PathVariable int number, @RequestParam(value = "xml", required=false) String xml) throws JSONException {
        String responseType="json";
        if(xml != null && xml.equals("true")){ // ?xml=true
            responseType="xml";
        }
        return reservationService.getReservation(number, responseType);
    }

    @RequestMapping(value="/reservation", method=RequestMethod.POST, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})// Chaged it form Applicatiion_JSON
    public ResponseEntity<?> addReservation(
            @RequestParam String passengerId,
            @RequestParam("flightLists") List<Flight> flightLists,
            @RequestParam("departureDates") List<Date> departureDates,
            @RequestParam(value = "xml", required=false) String xml
    ) {
        String responseType="json";
        if(xml != null && xml.equals("true")){ // ?xml=true
            responseType="xml";
        }
        return reservationService.addReservation(passengerId, flightLists, responseType);
    }

    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteReservation(@PathVariable int number, @RequestParam(value = "xml", required=false) String xml) {
        String responseType="json";
        if(xml != null && xml.equals("true")){ // ?xml=true
            responseType="xml";
        }
        return reservationService.deleteReservation(number, responseType);
    }

    @RequestMapping(value="/reservation/{number}", method=RequestMethod.POST, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateReservaton(
            @PathVariable int number,
            @RequestParam(value="flightsAdded", required=false) String flightsAdded,
            @RequestParam(value="flightsRemoved", required=false) String flightsRemoved,
            @RequestParam(value = "xml", required=false) String xml) throws JSONException {

        String responseType="json";

        if(xml != null && xml.equals("true")){ // ?xml=true
            responseType="xml";
        }

        List<Flight> flightAddedObects=null;
        List<Flight> flightRemovedObects=null;

        if(flightsAdded!=null){
            String[] flightsAddedList=flightsAdded.split(",");
            for(String s: flightsAddedList){
                if(reservationService.checkFlightExistance(s)!=null){
                    return reservationService.checkFlightExistance(s);
                }
            }
            flightAddedObects=reservationService.getFlights(flightsAddedList);

        }

        if(flightsRemoved!=null){
            String[] flightsRemovedList=flightsRemoved.split(",");

            for(String s: flightsRemovedList){
                if(reservationService.checkFlightExistance(s)!=null){
                    return reservationService.checkFlightExistance(s);
                }
                flightRemovedObects=reservationService.getFlights(flightsRemovedList);
            }
        }

        if(flightRemovedObects != null){
            ResponseEntity<?> obj = reservationService.updateReservatonRemoveFlights(number, flightRemovedObects);
            if(obj != null) return obj;
        }

        if(flightAddedObects!=null){
            ResponseEntity<?> response = reservationService.updateReservationAddFlights(number, flightAddedObects);
            if(response.getStatusCode() == HttpStatus.NOT_FOUND){
                System.out.print("inside if of updateReservation() ");
                return response;
            }
        }
        return reservationService.getReservation(number, responseType);
    }

}
