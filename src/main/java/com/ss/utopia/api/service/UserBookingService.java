package com.ss.utopia.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ss.utopia.api.dao.BookingRepository;
import com.ss.utopia.api.dao.PassengerRepository;
import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.Passenger;


@Service
public class UserBookingService {

	@Autowired
	BookingRepository booking_repository;
	
	@Autowired
	PassengerRepository passenger_repository;
	
	
	public Optional<List<Booking>> getBookingByUsernameQuery(String username) {
		try {
		return Optional.of(booking_repository.getBookingsByUser(username));
		} catch(Exception e) {
			return Optional.empty();
		}

	}

	
	
}
