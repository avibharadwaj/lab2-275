package com.example.demo.Passenger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, String> {
	public Passenger getById(String id);
	public void deleteById(String id);
	public Passenger getByPhone(String phone);
}
