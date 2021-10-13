package com.ss.utopia.api;

import static org.junit.jupiter.api.Assertions.assertEquals;


import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.utopia.api.controller.BookingController;
import com.ss.utopia.api.dao.BookingAgentRepository;
import com.ss.utopia.api.dao.BookingGuestRepository;
import com.ss.utopia.api.dao.BookingPaymentRepository;
import com.ss.utopia.api.dao.BookingRepository;
import com.ss.utopia.api.dao.BookingUserRepository;
import com.ss.utopia.api.dao.FlightBookingsRepository;
import com.ss.utopia.api.dao.FlightRepository;
import com.ss.utopia.api.dao.PassengerRepository;
import com.ss.utopia.api.dao.UserRepository;
import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.BookingAgent;
import com.ss.utopia.api.pojo.BookingGuest;
import com.ss.utopia.api.pojo.BookingPayment;
import com.ss.utopia.api.pojo.BookingUser;
import com.ss.utopia.api.pojo.Flight;
import com.ss.utopia.api.pojo.FlightBookings;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.service.BookingService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BookingServiceTests {

	@Autowired
	BookingController booking_controller;

	@Autowired
	BookingService booking_service;

	@Autowired
	BookingRepository booking_repository;
	
	@Autowired
	FlightRepository flight_repository;
	
	@Autowired
	BookingAgentRepository booking_agent_repository;
	
	@Autowired
	BookingUserRepository booking_user_repository;
	
	@Autowired
	BookingGuestRepository booking_guest_repository;
	
	@Autowired
	FlightBookingsRepository flight_bookings_repository;
	
	@Autowired
	BookingPaymentRepository booking_payment_repository;
	
	@Autowired
	UserRepository user_repository;
	
	@Autowired
	PassengerRepository passenger_repository;

	@Autowired
	SessionFactory sessionFactory;

	
	public Boolean collectionsMatch(List<?> collection1, List<?> collection2) {
		for(Object o: collection1) {
			if(!collection2.contains(o)) return Boolean.FALSE;
		}
		return collection1.size() == collection2.size();
		
	}
	
	
	
	
	
	@Test
	public void testCancel() {
		
		Booking booking = booking_repository.findAll().get(0);
		booking_service.cancelBooking(booking.getId());
		
		assertEquals(booking_repository.findById(booking.getId()).get().getIs_active(), false);
		
		booking_service.save(booking);
		
	}
	
	@Test
	public void testRefunded() {
		
		BookingPayment booking_payment = booking_payment_repository.findAll().get(0);
		booking_service.refundBooking(booking_payment.getBooking_id());
		
		assertEquals(booking_payment_repository.findById(booking_payment.getBooking_id()).get().getRefunded(),true);
		
	}
	
	
	
	
	
	@Nested
	class testSave{
		private Booking booking;
		private String confirmation_code;;
		public Integer booking_id;
		private Boolean is_active;
		private String test_user = "user1";

		private List<Passenger> passengers;

		private FlightBookings flight_bookings;

		private BookingPayment booking_payment;

		private BookingAgent booking_agent;
		private BookingUser booking_user;
		private BookingGuest booking_guest;
	
		
		
	public void setup() {

		this.booking = new Booking();
		this.is_active = true;
		this.confirmation_code = "NOT_A_REAL_CONFIRMATION_CODE";
		booking.setConfirmation_code(confirmation_code);
		booking.setIs_active(Boolean.FALSE);
		List<Passenger> copy_passengers = new ArrayList<>();
		this.passengers = new ArrayList<>();
		copy_passengers = booking_service.getPassengerByBooking(booking_service.getBookingByUsernameQuery(test_user))
				.stream().collect(Collectors.toList());

		for (Passenger p : copy_passengers) {
			this.passengers.add(p);
		}
		booking = booking_repository.save(booking); // save

		this.passengers.stream().forEach(x -> {
			x.setBooking_id(booking.getId());
			x.setId(null);
		});
		
		

		booking.setPassengers(passengers);
		this.booking_id = booking.getId();
		booking = booking_repository.save(booking);

	}

	@Test
	public void testAddPassenger() {

		Session s = sessionFactory.openSession();
		Transaction tx = s.beginTransaction();
		
		this.booking = new Booking();
		this.is_active = true;
		this.confirmation_code = booking_service.generateConfirmationCode();
		booking.setConfirmation_code(confirmation_code);
		booking.setIs_active(Boolean.FALSE);
		List<Passenger> copy_passengers = new ArrayList<>();
		this.passengers = new ArrayList<>();
		copy_passengers = booking_service.getPassengerByBooking(booking_service.getBookingByUsernameQuery(test_user))
				.stream().collect(Collectors.toList());

		for (Passenger p : copy_passengers) {
			this.passengers.add(p);
		}
		booking = booking_repository.save(booking); // save
		this.booking_id = booking.getId();
		this.passengers.stream().forEach(x -> {
			x.setBooking_id(booking.getId());
			x.setId(null);
		});
		

		booking.setPassengers(passengers);
		booking = booking_repository.save(booking);
	
		 s.flush();
	        s.clear();
		
		List<Passenger> real_passengers = booking_service
				.getPassengerByBooking(booking_service.getBookingByUsernameQuery(test_user));
		assertEquals(real_passengers.size(), passengers.size());
		try {
			for (int i = 0; i < real_passengers.size(); i++) {
				assertEquals(real_passengers.get(i).getAddress(), this.passengers.get(i).getAddress());
				assertEquals(real_passengers.get(i).getGiven_name(), this.passengers.get(i).getGiven_name());
				assertEquals(real_passengers.get(i).getFamily_name(), this.passengers.get(i).getFamily_name());
				assertEquals(real_passengers.get(i).getDob(), this.passengers.get(i).getDob());
				assertEquals(real_passengers.get(i).getGender(), this.passengers.get(i).getGender());
			}
		} catch (Exception e) {

		}
		 tx.rollback();
		  s.close();
		teardown();

	}
	
	
	@Test
	public void testAddBookingAgent(){
		Passenger passenger = new Passenger();
		passenger.setId(null);
		passenger.setBooking_id(null);
		passenger.setFamily_name("lllllllll");
		passenger.setGiven_name("lllllllll");
		passenger.setAddress("lllllllll");
		passenger.setDob(LocalDate.now());
		passenger.setGender("lllllllll");
		
		Integer flight_id = flight_repository.findAll().get(0).getId();
		Integer user_id = booking_agent_repository.findAll().get(0).getAgent_id();
		
		booking = booking_service.saveBookingAgentBooking(passenger, user_id, flight_id);
		

		this.booking_id = booking.getId();
		assertEquals(true, booking_agent_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		assertEquals("ROLE_AGENT", booking_service.findUserByBookingId(booking.getId()).get().getUser_role().getName());
		
		
		assertEquals(true, booking_payment_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		assertEquals(true, flight_bookings_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		teardown();

	
	}
	
	
	@Test
	public void testAddBookingUser(){
		Passenger passenger = new Passenger();
		passenger.setId(null);
		passenger.setBooking_id(null);
		passenger.setFamily_name("lllllllll");
		passenger.setGiven_name("lllllllll");
		passenger.setAddress("lllllllll");
		passenger.setDob(LocalDate.now());
		passenger.setGender("lllllllll");
		
		Integer flight_id = flight_repository.findAll().get(0).getId();
		Integer user_id = booking_user_repository.findAll().get(0).getUser_id();
		
		booking = booking_service.saveBookingUserBooking(passenger, user_id, flight_id);
		this.booking_id = booking.getId();

	
		assertEquals(true, booking_user_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		assertEquals("ROLE_TRAVELER", booking_service.findUserByBookingId(booking.getId()).get().getUser_role().getName());
		
		assertEquals(true, booking_payment_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		assertEquals(true, flight_bookings_repository.findAll().stream().map(x -> x.getBooking_id()).collect(Collectors.toList()).contains(booking.getId()));
		teardown();
	
	}
	
	@Test
	public void testAddEntireBookingByAgent() {
		
		List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
		FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
		BookingAgent booking_agent = new BookingAgent();
		booking_agent.setAgent_id(user_repository.findAll().stream().filter(x -> x.getUser_role().getName().equals("ROLE_AGENT")).findFirst().get().getId());
		

		passengers.forEach(x -> x.setId(null));;		
		
		Booking booking = new Booking();
		booking.setPassengers(passengers);
		booking.setFlight_bookings(flight_bookings);
		booking.setBooking_agent(booking_agent);
		
		Booking inserted_booking = booking_service.save(booking).get();
		
		System.out.println(inserted_booking);
		
		
		this.booking_id = inserted_booking.getId();
		
		booking = booking_repository.findById(booking_id).get();
		
		assertEquals(booking.getId(), inserted_booking.getId());
		
		assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());
		
		assertEquals(booking.getIs_active(), inserted_booking.getIs_active());
		
		assertEquals(booking.getBooking_agent().getAgent_id(), inserted_booking.getBooking_agent().getAgent_id());
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())), true);
		
		teardown();
		
	}
	
	
	
	@Test
	public void testAddEntireBookingByUser() {
		
		List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
		FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
		BookingUser booking_user = new BookingUser();
		booking_user.setUser_id(user_repository.findAll().stream().filter(x -> x.getUser_role().getName().equals("ROLE_TRAVELER")).findFirst().get().getId());
		

		passengers.forEach(x -> x.setId(null));;		
		
		Booking booking = new Booking();
		booking.setPassengers(passengers);
		booking.setFlight_bookings(flight_bookings);
		booking.setBooking_user(booking_user);
		
		Booking inserted_booking = booking_service.save(booking).get();
		
		System.out.println(inserted_booking);
		
		
		this.booking_id = inserted_booking.getId();
		
		booking = booking_repository.findById(booking_id).get();
		
		assertEquals(booking.getId(), inserted_booking.getId());
		
		assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());
		
		assertEquals(booking.getIs_active(), inserted_booking.getIs_active());
		
		assertEquals(booking.getBooking_user().getUser_id(), inserted_booking.getBooking_user().getUser_id());
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())), true);
		
		assertEquals(collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
				inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())), true);
		
		teardown();
		
	}
	

	

	

	public void teardown() {
		while(true) {
		if (booking_repository.existsById(booking_id)) {
			booking_service.deleteBookingById(booking_id);
			break;
		}
		}

	}

	}
	@Nested
	class testQueries {

		String good_id = "user1";
		List<Passenger> passengers_nested;
		List<Flight> flights;
		List<Booking> bookings;

		public void getBookings() {
			this.bookings = booking_controller.getBookingByUsername(good_id).getBody();
		}

		public void getFlights() {
			this.flights = booking_controller.getFlightByUsername(good_id).getBody();
		}

		public void getPassengers() {

			this.passengers_nested = booking_controller.getPassengerByUsername(good_id).getBody();

		}



		@Test
		@Transactional
		public void testFlights() {
			getFlights();
			List<Integer> flightIds = booking_controller.getFlightByUsername(good_id).getBody().stream()
					.map(x -> x.getId()).collect(Collectors.toList());
			List<Integer> flightIdsCheck = this.flights.stream().map(x -> x.getId()).collect(Collectors.toList());
			for (Integer id : flightIdsCheck) {
				assertEquals(flightIds.contains(id), true);
			}
			assertEquals(flightIdsCheck.size(), flightIds.size());

		}

		@Test
		@Transactional
		public void testPassengers() {
			getBookings();
			getPassengers();
			List<Integer> passengerIds = this.passengers_nested.stream().map(x -> x.getId())
					.collect(Collectors.toList());
			List<Integer> passengerIdsCheck = booking_controller.getPassengerByUsername(good_id).getBody().stream()
					.map(x -> x.getId()).collect(Collectors.toList());
			for (Integer id : passengerIdsCheck) {
				assertEquals(passengerIds.contains(id), true);
			}
			assertEquals(passengerIds.size(), passengerIdsCheck.size());
		}

	}

}
