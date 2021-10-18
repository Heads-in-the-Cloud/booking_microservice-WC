package com.ss.utopia.api.controller;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.BookingPayment;
import com.ss.utopia.api.pojo.FlightBookings;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;
import com.ss.utopia.api.service.FlightBookingService;
import com.ss.utopia.api.service.PassengerBookingService;

@RestController
public class GuestController {

	@Autowired
	BookingService booking_service;
	
	@Autowired
	FlightBookingService flight_booking_service;
	
	@Autowired
	PassengerBookingService passenger_booking_service;
	
	
	
	@PostMapping("/add")
	public ResponseEntity<Booking> addBooking(@RequestBody Booking booking){
		
		return ResponseEntity.ok().body(booking_service.createSimpleBooking());
		
	}
	
	@PostMapping("/add/passenger")
	public ResponseEntity<Passenger> addPassenger(@RequestBody Passenger passenger) {

		try {
			
			URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/read/passenger=" + passenger.getId()).toUriString());
			return ResponseEntity.created(uri).body(passenger_booking_service.save(passenger));
			
		} catch (DataIntegrityViolationException e) {

			return ResponseEntity.badRequest().build();
		}
	}
	
	
	@GetMapping("/read/id={booking_id}")
	public ResponseEntity<Booking> findBookingById(@PathVariable Integer booking_id) {

		try {
			Booking booking = booking_service.getBookingById(booking_id);

			return ResponseEntity.ok().body(booking);

		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();

		}
	}
	
	@GetMapping("/read/passenger/id={passenger_id}")
	public ResponseEntity<Passenger> getPassengerById(@PathVariable Integer passenger_id) {
		try {

			Passenger passenger = passenger_booking_service.getPassengerById(passenger_id);
			return ResponseEntity.ok().body(passenger);

		} catch (NoSuchElementException e) {
			
			return ResponseEntity.notFound().build();
		}

	}
	
	@PutMapping("/update/passenger")
	public ResponseEntity<Passenger> updatePassengers(@RequestBody Passenger passenger) {

		try {
			Passenger new_passenger = passenger_booking_service.update(passenger);

			return ResponseEntity.ok().body(new_passenger);

		} catch (NoSuchElementException | DataIntegrityViolationException e) {
			return ResponseEntity.badRequest().build();
		}
	}


	@PutMapping("/update/flight_bookings")
	public ResponseEntity<FlightBookings> updateFlightBookings(@RequestBody FlightBookings flight_bookings) {

		try {
			FlightBookings new_booking = flight_booking_service.update(flight_bookings);
			return ResponseEntity.ok(new_booking);

		} catch (NoSuchElementException e) {
			return ResponseEntity.badRequest().body(flight_bookings);

		}
	}
	
	@PutMapping("/update/booking_payment")
	public ResponseEntity<BookingPayment> updateBookingPayment(@RequestBody BookingPayment booking_payment) {

		try {

			BookingPayment new_booking = booking_service.update(booking_payment);
			return ResponseEntity.ok(new_booking);

		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.badRequest().body(booking_payment);

		}
	}

	@GetMapping("/cancel/booking={booking_id}")
	public ResponseEntity<?> cancelBooking(@PathVariable Integer booking_id) {
		try {

			booking_service.cancelBooking(booking_id);
			return ResponseEntity.noContent().build();

		} catch (NoSuchElementException e) {

			return ResponseEntity.badRequest().build();
		}

	}

	@GetMapping("/refund/booking={booking_id}")
	public ResponseEntity<?> refundTicket(@PathVariable Integer booking_id) {
		
		try {
			
			booking_service.refundBooking(booking_id);
			return ResponseEntity.noContent().build();

		} catch (NoSuchElementException e) {

			return ResponseEntity.badRequest().build();
		}

	}
	
	@DeleteMapping("/delete/passenger/id={passenger_id}")
	public ResponseEntity<?> deletePassengerById(@PathVariable Integer passenger_id) {

		passenger_booking_service.deletePassengerById(passenger_id);

		return ResponseEntity.noContent().build();
	}

}
