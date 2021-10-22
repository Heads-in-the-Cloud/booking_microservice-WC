package com.ss.utopia.api.dao;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	
	@Query(value="SELECT b FROM User u, BookingAgent ba, BookingUser bu, Booking b WHERE u.username = :username AND u.id = ba.agent_id AND ba.booking_id = b.id OR u.username = :username AND u.id = bu.user_id AND bu.booking_id = b.id GROUP BY b.id")
	public List<Booking> getBookingsByUser(@Param("username") String username);
	
//	@Query(value="SELECT b from Booking b LEFT JOIN BookingUser bu ON b.id = bu.booking_id LEFT JOIN BookingAgent ba ON b.id = ba.booking_id WHERE ba.agent_id = :user_id OR bu_.user_id = :user_id")
//	public List<Booking> getBookingsByUserId(@Param("user_id") Integer user_id);
	
	
}
