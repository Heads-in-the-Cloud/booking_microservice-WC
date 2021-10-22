package com.ss.utopia.api.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.BookingAgent;
import com.ss.utopia.api.pojo.BookingUser;
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

		List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
		return ResponseEntity.ok().body(bookings);

	}

	@GetMapping(path = "/read/passengers/id={username}")
	public ResponseEntity<List<Passenger>> getPassengerByUsername(@PathVariable String username) {

		List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
		return ResponseEntity.ok().body(passenger_booking_service.getPassengerByBooking(bookings));

	}

	@GetMapping(path = "/read/flights/id={username}")
	public ResponseEntity<List<Flight>> getFlightByUsername(@PathVariable String username) {

		List<Booking> bookings = user_booking_service.getBookingByUsernameQuery(username);
		
		return ResponseEntity.ok().body(flight_booking_service.getFlightByBookingId(bookings).stream()
				.filter(distinctByKey(Flight::getId)).collect(Collectors.toList()));

	}

	@PostMapping("/add/flight={flight_id}/user={user_id}")
	public ResponseEntity<Booking> addBookingArgs(@RequestBody Booking booking, @PathVariable Integer flight_id,
			@PathVariable Integer user_id) {

		Booking new_booking = booking_service.saveParams(booking, flight_id, user_id);

		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/read/id=" + booking.getId()).toUriString());
		return ResponseEntity.created(uri).body(new_booking);

	}
	

	
	@PostMapping("/add/booking_user")
	public ResponseEntity<BookingUser> addBookingUser(@RequestBody BookingUser booking_user) throws SQLException {

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/read/id=" + booking_user.getBooking_id()).toUriString());
		return ResponseEntity.created(uri).body(user_booking_service.save(booking_user));

	}
	
	@PutMapping("/update/booking_user")
	public ResponseEntity<BookingUser> updateBookingUser(@RequestBody BookingUser booking_user) {

		BookingUser new_booking = user_booking_service.update(booking_user);
		return ResponseEntity.ok(new_booking);

	}
	
	@Transactional
	@DeleteMapping("/delete/booking_user/id={booking_id}")
	public ResponseEntity<?> deleteBookingUserById(@PathVariable Integer booking_id) {

		user_booking_service.deleteBookingUser(booking_id);
		return ResponseEntity.noContent().build();

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
