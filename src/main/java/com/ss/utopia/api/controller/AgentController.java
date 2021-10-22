package com.ss.utopia.api.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Modifying;
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
import com.ss.utopia.api.pojo.BookingGuest;
import com.ss.utopia.api.pojo.BookingPayment;
import com.ss.utopia.api.pojo.BookingUser;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;
import com.ss.utopia.api.service.PassengerBookingService;
import com.ss.utopia.api.service.UserBookingService;

@RestController
@RequestMapping("booking/agent")
public class AgentController {

	@Autowired
	BookingService booking_service;

	@Autowired
	PassengerBookingService passenger_booking_service;

	@Autowired
	UserBookingService user_booking_service;

	@GetMapping(path = "/read")
	public ResponseEntity<List<Booking>> findAllBookings() {
		return ResponseEntity.ok().body(booking_service.findAllBookings());

	}

	@GetMapping(path = "/read/passengers")
	public ResponseEntity<List<Passenger>> findAllPassengers() {

		return ResponseEntity.ok().body(passenger_booking_service.findAllPassengers());

	}

	@GetMapping(path = "/read/cancelled")
	public ResponseEntity<List<Booking>> getCancelledBookings() {
		return ResponseEntity.ok().body(booking_service.getCancelledBookings());
	}

	@GetMapping(path = "/read/refunded")
	public ResponseEntity<List<BookingPayment>> getRefundedBookings() {
		return ResponseEntity.ok().body(booking_service.getRefundedBookings());
	}

	@PostMapping("/add")
	public ResponseEntity<Booking> addBooking(@RequestBody Booking booking) throws SQLException {

		Booking new_booking = booking_service.save(booking);

		if (new_booking == null) {
			throw new SQLException("Must specify flight Id and a booking method"); // Flight or Booking method missing

		}

		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/read/id=" + booking.getId()).toUriString());
		return ResponseEntity.created(uri).body(new_booking);

	}
	
	@PostMapping("/add/booking_agent")
	public ResponseEntity<BookingAgent> addBookingAgent(@RequestBody BookingAgent booking_agent) throws SQLException {

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/read/id=" + booking_agent.getBooking_id()).toUriString());
		return ResponseEntity.created(uri).body(user_booking_service.save(booking_agent));

	}

	@PutMapping("/update")
	public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {

		Booking new_booking = booking_service.update(booking);
		return ResponseEntity.ok(new_booking);

	}

	@GetMapping("/overrideTripCancellation/id={booking_id}")
	public ResponseEntity<Void> updateBooking(@PathVariable Integer booking_id) {

		booking_service.overrideTripCancellation(booking_id);
		return ResponseEntity.ok().build();

	}
	

	@PutMapping("/update/booking_agent")
	public ResponseEntity<BookingAgent> updateBookingAgent(@RequestBody BookingAgent booking_agent) {

		BookingAgent new_booking = user_booking_service.update(booking_agent);
		return ResponseEntity.ok(new_booking);
	}

	@Transactional
	@DeleteMapping("/delete/booking={booking_id}")
	public ResponseEntity<?> deleteBookingById(@PathVariable Integer booking_id) {

		booking_service.deleteBookingById(booking_id);
		return ResponseEntity.noContent().build();

	}

	@Transactional
	@DeleteMapping("/delete/booking_agent/id={booking_id}")
	public ResponseEntity<?> deleteBookingAgentById(@PathVariable Integer booking_id) {

		user_booking_service.deleteBookingAgent(booking_id);
		return ResponseEntity.noContent().build();

	}

}
