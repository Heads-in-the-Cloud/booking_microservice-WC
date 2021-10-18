package com.ss.utopia.api.controller;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.Flight;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;
import com.ss.utopia.api.service.FlightBookingService;
import com.ss.utopia.api.service.PassengerBookingService;
import com.ss.utopia.api.service.UserBookingService;

@RestController
@RequestMapping("/booking/user")
public class UserController {

	@Autowired
	UserBookingService user_booking_service;

	@Autowired
	BookingService booking_service;
	
	@Autowired
	FlightBookingService flight_booking_service;
	
	@Autowired
	PassengerBookingService passenger_booking_service;

	@GetMapping(path = "/read/user={username}")
	public ResponseEntity<List<Booking>> getBookingByUsername(@PathVariable String username) {

		try {

			List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
			return ResponseEntity.ok().body(bookings);

		} catch (NoSuchElementException e) {
			
			return ResponseEntity.notFound().build();
		}
	}


	@GetMapping(path = "/read/passengers/id={username}")
	public ResponseEntity<List<Passenger>> getPassengerByUsername(@PathVariable String username) {

		try {

			List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
			return ResponseEntity.ok().body(passenger_booking_service.getPassengerByBooking(bookings));

		} catch (NoSuchElementException e) {
			
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping(path = "/read/flights/id={username}")
	public ResponseEntity<List<Flight>> getFlightByUsername(@PathVariable String username) {

		try {

			List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
			return ResponseEntity.ok().body(flight_booking_service.getFlightByBookingId(bookings).stream()
					.filter(distinctByKey(Flight::getId)).collect(Collectors.toList()));

		} catch (NoSuchElementException e) {

			return ResponseEntity.notFound().build();
		}

	}

	
	@PostMapping("/add/flight={flight_id}/user={user_id}")
	public ResponseEntity<Booking> addBookingArgs(@RequestBody Booking booking, @PathVariable Integer flight_id,
			@PathVariable Integer user_id) {

		try {
			Booking new_booking = booking_service.saveParams(booking, flight_id, user_id);

			URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/read/id=" + booking.getId()).toUriString());
			return ResponseEntity.created(uri).body(new_booking);

		} catch (NoSuchElementException | DataIntegrityViolationException e) {
			
			return ResponseEntity.badRequest().build();
		}

	}
	

	
	
	/*
	 * Check for uniques by
	 * attribute @https://stackoverflow.com/questions/23699371/java-8-distinct-by-
	 * property
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

}
