package com.ss.utopia.api.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import com.ss.utopia.api.pojo.BookingPayment;
import com.ss.utopia.api.pojo.Flight;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;

@RestController
@RequestMapping(path = "/booking")
public class BookingController {

	@Autowired
	BookingService booking_service;

	@GetMapping(path = "/read")
	public ResponseEntity<List<Booking>> findAllBookings() {
		return ResponseEntity.ok().body(booking_service.findAllBookings());

	}
	
	@GetMapping("/read/id={booking_id}")
	public ResponseEntity<Booking> findBookingById(@PathVariable Integer booking_id) {
		return ResponseEntity.ok().body(booking_service.findBookingById(booking_id));
	}

	@GetMapping(path = "/read/user={username}")
	public ResponseEntity<List<Booking>> getBookingByUsername(@PathVariable String username) {
		return ResponseEntity.ok().body(booking_service.getBookingByUsernameQuery(username));
	}
	
	@GetMapping(path = "/read/passengers")
	public ResponseEntity<List<Passenger>> findAllPassengers() {
		return ResponseEntity.ok().body(booking_service.findAllPassengers());

	}
	
	@GetMapping("/read/passenger/id={passenger_id}")
	public ResponseEntity<Passenger> findPassengerById(@PathVariable Integer passenger_id) {
		return ResponseEntity.ok().body(booking_service.findPassengerById(passenger_id));
	}
	
	@GetMapping(path = "/read/passengers/user={username}")
	public ResponseEntity<List<Passenger>> getPassengerByUsername(@PathVariable String username) {
		return ResponseEntity.ok()
				.body(booking_service.getPassengerByBooking(booking_service.getBookingByUsernameQuery(username)));

	}
	
	

	@GetMapping(path = "/read/flights/user={username}")
	public ResponseEntity<List<Flight>> getFlightByUsername(@PathVariable String username) {

		return ResponseEntity.ok()
				.body(booking_service.getFlightByBookingId(booking_service.getBookingByUsernameQuery(username)).stream()
						.filter(distinctByKey(Flight::getId)).collect(Collectors.toList()));
	}
	@GetMapping(path = "/read/bookings/cancelled")
	public ResponseEntity<List<Booking>> getCancelledBookings() {
		return ResponseEntity.ok().body(booking_service.getCancelledBookings());
	}
	@GetMapping(path = "/read/bookings/refunded")
	public ResponseEntity<List<BookingPayment>> getRefundedBookings() {
		return ResponseEntity.ok().body(booking_service.getRefundedBookings());
	}

	
	@PostMapping("/add/passenger")
	public ResponseEntity<Passenger> addPassenger(@RequestBody Passenger passenger) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/read/passenger=" + passenger.getId())
				.toUriString());
		return ResponseEntity.created(uri).body(booking_service.save(passenger));
	}

	@PostMapping("/add/agent={agent_id}/flight={flight_id}")
	public Booking addBookingByAgent(@RequestBody Passenger passenger, @PathVariable Integer agent_id,
			@PathVariable Integer flight_id) {
		return booking_service.saveBookingAgentBooking(passenger, agent_id, flight_id);
	}


	@PostMapping("/add/user={user_id}/flight={flight_id}")
	public ResponseEntity<Booking> addBookingByUser(@RequestBody Passenger passenger, @PathVariable Integer user_id,
			@PathVariable Integer flight_id) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/read/id=" + passenger.getBooking_id())
				.toUriString());

		return ResponseEntity.created(uri).body(booking_service.saveBookingUserBooking(passenger, user_id, flight_id));
	}
	
	@PostMapping("/add")
	public ResponseEntity<Booking> addBooking(@RequestBody Booking booking){
		Optional<Booking> new_booking = booking_service.save(booking);
		
		if(new_booking.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/read/id=" + booking.getId())
				.toUriString());
		return ResponseEntity.created(uri).body(new_booking.get());
	}



	@DeleteMapping("/delete/passenger={passenger_id}")
	public ResponseEntity<?> deletePassengerById(@PathVariable Integer passenger_id) {
		booking_service.deletePassengerById(passenger_id);

		return ResponseEntity.noContent().build();
	}

	@Transactional
	@DeleteMapping("/delete/booking={booking_id}")
	public ResponseEntity<?> deleteBookingById(@PathVariable Integer booking_id) {
		booking_service.deleteBookingById(booking_id);

		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/update/passenger")
	public  ResponseEntity<Passenger> updatePassengers(@RequestBody Passenger passenger) {
		
		Optional<Passenger> new_passenger = booking_service.update(passenger);
		if(new_passenger.isPresent()) {
			return ResponseEntity.ok().body(new_passenger.get());

		}
		return ResponseEntity.noContent().build();
	}

	
	@GetMapping("/cancel/booking={booking_id}")
	public ResponseEntity<?> cancelBooking(@PathVariable Integer booking_id){
		booking_service.cancelBooking(booking_id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/refund/booking={booking_id}")
	public ResponseEntity<?> refundTicket(@PathVariable Integer booking_id){
		booking_service.refundBooking(booking_id);
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

//////////////////Just use get User ->//////////////////////////////////////////////////
////////////////// user.getBooking_Methods().getBookings()//////////////////////////////
////////////////// These queries provide ALL information of the users.//////////////////
////////////////// /////////////////////////////////////////////////////////////////////

//	@GetMapping(path = "/user/{username}/bookings")
//	public ResponseEntity<List<Booking>> getUserBooking(@PathVariable String username) {
//		return ResponseEntity.ok().body(booking_service.getBookingByUsername(username));
//	}
//
//	@GetMapping(path = "/user/{username}/bookings/passengers")
//	public ResponseEntity<List<Passenger>> getPassengersFromUsername(@PathVariable String username) {
//		List<Booking> bookings = booking_service.getBookingByUsername(username);
//		List<Passenger> passengers = new ArrayList<>();
//		for (Booking b : bookings) {
//			passengers.addAll(b.getPassengers());
//		}
//		return ResponseEntity.ok().body(passengers);
//	}
/////////////////////////////////////////////////////////////////////////////////////////

}
