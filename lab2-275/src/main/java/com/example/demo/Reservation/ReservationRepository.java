package com.example.demo.Reservation;

import com.example.demo.Passenger.Passenger;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    Reservation findByGenOrderNumber(int number);

    List<Reservation> findByPassenger(Passenger passenger);

}
