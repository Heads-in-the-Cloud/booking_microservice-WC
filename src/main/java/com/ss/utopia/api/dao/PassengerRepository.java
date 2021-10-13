package com.ss.utopia.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

	
	@Query(value="SELECT p FROM Passenger p WHERE p.booking_id = :booking_id")
	public List<Passenger> getPassengerByBookingId(@Param("booking_id") Integer booking_id);
}
