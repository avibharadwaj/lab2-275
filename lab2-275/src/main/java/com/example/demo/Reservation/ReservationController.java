package com.example.demo.Reservation;

import com.example.demo.Flight.Flight;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Transactional
    @GetMapping(value = "/{number}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getReservation(@PathVariable int number, @RequestParam(value = "xml", required = false) String xml) throws JSONException {
        String responseType = "json";
        if (xml != null && xml.equals("true")) {
            responseType = "xml";
        }
        return reservationService.getReservation(number, responseType);
    }
    
    @Transactional
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> makeReservation(
            @RequestParam String passengerId,
            @RequestParam("flightLists") String flightListStr,
            @RequestParam("departureDates") String departureDatesStr,
            @RequestParam(value = "xml", required = false) String xml
    ) throws JSONException {
        String responseType = "json";
        if (xml != null && xml.equals("true")) {
            responseType = "xml";
        }

        String[] flightsList = new String[0];
        String[] departureDates = new String[0];
        if (flightListStr != null) {
            flightsList = flightListStr.split(",");
        }
        if (departureDatesStr != null) {
            departureDates = departureDatesStr.split(",");
        }

        return reservationService.addReservation(passengerId, flightsList, responseType, departureDates);
    }

    @Transactional
    @DeleteMapping(value = "/{number}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> cancelReservation(@PathVariable int number, @RequestParam(value = "xml", required = false) String xml) throws JSONException {
        String responseType = "json";
        if (xml != null && xml.equals("true")) {
            responseType = "xml";
        }
        return reservationService.cancelReservation(number, responseType);
    }

    @Transactional
    @PutMapping(value = "/{number}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateReservation(
            @PathVariable int number,
            @RequestParam(value = "flightsAdded", required = false) String flightsAddedStr,
            @RequestParam(value = "flightsRemoved", required = false) String flightsRemovedStr,
            @RequestParam(value = "departureDatesAdded", required = false) String departureDatesAddedStr,
            @RequestParam(value = "departureDatesRemoved", required = false) String departureDatesRemovedStr,
            @RequestParam(value = "xml", required = false) String xml) throws JSONException {

        String responseType = "json";

        if (xml != null && xml.equals("true")) {
            responseType = "xml";
        }

        List<Flight> addedFlights = null;
        if (flightsAddedStr != null) {
            String[] flightsAddedList = flightsAddedStr.split(",");
            for (String s : flightsAddedList) {
                if (reservationService.iSFlightExist(s) != null) {
                    return reservationService.iSFlightExist(s);
                }
            }
            addedFlights = reservationService.getFlights(flightsAddedList);
        }
        if (addedFlights != null) {
            ResponseEntity<?> res = reservationService.removeFlightUpdate(number, addedFlights);
            if (res != null)
                return res;
        }

        List<Flight> removedFlights = null;
        if (flightsRemovedStr != null) {
            String[] flightsRemovedList = flightsRemovedStr.split(",");
            for (String s : flightsRemovedList) {
                if (reservationService.iSFlightExist(s) != null) {
                    return reservationService.iSFlightExist(s);
                }
                removedFlights = reservationService.getFlights(flightsRemovedList);
            }
        }
        if (removedFlights != null) {
            ResponseEntity<?> res = reservationService.addFlightUpdate(number, removedFlights);
            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                return res;
            }
        }
        return reservationService.getReservation(number, responseType);
    }

}
