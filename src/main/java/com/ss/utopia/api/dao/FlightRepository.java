package com.ss.utopia.api.dao;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

	
	@Query(value="SELECT f FROM Flight f, FlightBookings fb WHERE f.id = fb.flight_id AND  fb.booking_id = :booking_id GROUP BY f.id")
	public Flight getFlightByBooking(@Param("booking_id") Integer booking_id);
	
}
