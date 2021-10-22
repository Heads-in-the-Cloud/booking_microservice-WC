package com.ss.utopia.api.controller;

import java.net.URI;
import java.util.NoSuchElementException;

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
import com.ss.utopia.api.pojo.BookingGuest;
import com.ss.utopia.api.pojo.BookingPayment;
import com.ss.utopia.api.pojo.BookingUser;
import com.ss.utopia.api.pojo.FlightBookings;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;
import com.ss.utopia.api.service.FlightBookingService;
import com.ss.utopia.api.service.PassengerBookingService;
import com.ss.utopia.api.service.UserBookingService;

@RestController
@RequestMapping("booking/guest/")
public class GuestController {

	@Autowired
	BookingService booking_service;

	@Autowired
	FlightBookingService flight_booking_service;

	@Autowired
	PassengerBookingService passenger_booking_service;

	@Autowired
	UserBookingService user_booking_service;

	@PostMapping("/add")
	public ResponseEntity<Booking> addBooking(@RequestBody Booking booking) {

		return ResponseEntity.ok().body(booking_service.createSimpleBooking());

	}

	@PostMapping("/add/passenger")
	public ResponseEntity<Passenger> addPassenger(@RequestBody Passenger passenger) {

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/read/passenger=" + passenger.getId()).toUriString());
		return ResponseEntity.created(uri).body(passenger_booking_service.save(passenger));

	}

	@PostMapping("/add/booking_guest")
	public ResponseEntity<BookingGuest> addBookingGuest(@RequestBody BookingGuest booking_guest) {

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/read/id=" + booking_guest.getBooking_id()).toUriString());
		return ResponseEntity.created(uri).body(user_booking_service.save(booking_guest));

	}

	@GetMapping("/read/id={booking_id}")
	public ResponseEntity<Booking> findBookingById(@PathVariable Integer booking_id) {

		Booking booking = booking_service.getBookingById(booking_id);

		return ResponseEntity.ok().body(booking);

	}

	@GetMapping("/read/passenger/id={passenger_id}")
	public ResponseEntity<Passenger> getPassengerById(@PathVariable Integer passenger_id) {

		Passenger passenger = passenger_booking_service.getPassengerById(passenger_id);
		return ResponseEntity.ok().body(passenger);

	}

	@PutMapping("/update/passenger")
	public ResponseEntity<Passenger> updatePassengers(@RequestBody Passenger passenger) {

		Passenger new_passenger = passenger_booking_service.update(passenger);

		return ResponseEntity.ok().body(new_passenger);

	}

	@PutMapping("/update/flight_bookings")
	public ResponseEntity<FlightBookings> updateFlightBookings(@RequestBody FlightBookings flight_bookings) {

		FlightBookings new_booking = flight_booking_service.update(flight_bookings);
		return ResponseEntity.ok(new_booking);

	}

	@PutMapping("/update/booking_payment")
	public ResponseEntity<BookingPayment> updateBookingPayment(@RequestBody BookingPayment booking_payment) {

		BookingPayment new_booking = booking_service.update(booking_payment);
		return ResponseEntity.ok(new_booking);

	}

	@PutMapping("/update/booking_guest")
	public ResponseEntity<BookingGuest> updateBookingGuest(@RequestBody BookingGuest booking_guest) {

		BookingGuest new_booking = user_booking_service.update(booking_guest);
		return ResponseEntity.ok(new_booking);

	}

	@GetMapping("/cancel/id={booking_id}")
	public ResponseEntity<?> cancelBooking(@PathVariable Integer booking_id) {

		booking_service.cancelBooking(booking_id);
		return ResponseEntity.noContent().build();

	}

	@GetMapping("/refund/booking={booking_id}")
	public ResponseEntity<?> refundTicket(@PathVariable Integer booking_id) {

		booking_service.refundBooking(booking_id);
		return ResponseEntity.noContent().build();

	}

	@Transactional
	@DeleteMapping("/delete/booking_guest/id={booking_id}")
	public ResponseEntity<?> deleteBookingGuest(@PathVariable Integer booking_id) {

		user_booking_service.deleteBookingGuest(booking_id);
		return ResponseEntity.noContent().build();

	}

	@DeleteMapping("/delete/passenger/id={passenger_id}")
	public ResponseEntity<?> deletePassengerById(@PathVariable Integer passenger_id) {

		passenger_booking_service.deletePassengerById(passenger_id);

		return ResponseEntity.noContent().build();
	}

}
