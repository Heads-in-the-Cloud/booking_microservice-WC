package com.ss.utopia.api.dao;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

	
	@Query(value="SELECT f FROM Flight f, FlightBookings fb WHERE f.id = fb.flight_id AND  fb.booking_id = :booking_id GROUP BY f.id")
	public Flight getFlightByBooking(@Param("booking_id") Integer booking_id);
	
	@Modifying
	@Query(value="UPDATE flight f SET f.reserved_seats= 1 WHERE f.id=?", nativeQuery = true)
	public void test(Integer flight_id);
	
	@Modifying
	@Query(value="UPDATE flight f LEFT JOIN flight_bookings fb ON fb.flight_id = f.id SET f.reserved_seats = f.reserved_seats + :num WHERE fb.booking_id = :booking_id", nativeQuery = true)
	public void updateReservedSeats(@Param("booking_id") Integer booking_id, @Param("num") Integer num);
	
}
