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
import org.junit.After;
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
import com.ss.utopia.api.pojo.User;
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

	final Integer NUM_TO_ADD = 5;

	public Boolean collectionsMatch(List<?> collection1, List<?> collection2) {
		for (Object o : collection1) {
			if (!collection2.contains(o))
				return Boolean.FALSE;
		}
		return collection1.size() == collection2.size();

	}

	
	
	

	@Transactional
	@Test
	public void testCancel() {

		Booking booking = booking_repository.findAll().stream().filter(x -> x.getIs_active()).findFirst().get();
		booking_service.cancelBooking(booking.getId());

		assertEquals(booking_repository.findById(booking.getId()).get().getIs_active(), false);

		booking.setIs_active(false);
		booking_service.save(booking);

	}

	@Transactional
	@Test
	public void testRefunded() {

		BookingPayment booking_payment = booking_payment_repository.findAll().stream().filter(x -> !x.getRefunded())
				.findFirst().get();
		booking_service.refundBooking(booking_payment.getBooking_id());

		assertEquals(booking_payment_repository.findById(booking_payment.getBooking_id()).get().getRefunded(), true);

		booking_payment.setRefunded(false);
		booking_payment_repository.save(booking_payment);
	}
	
	@Transactional
	@Test
	public void testPassengerAddAndDelete() {
	
		Passenger p1 = new Passenger(null, null, "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male", "address1");

		Booking booking = booking_service.createSimpleBooking().get();
		p1.setBooking_id(booking.getId());
		
		p1 = booking_service.save(p1);
		List<Passenger> passengers = passenger_repository.findAll();
		
		assertEquals(passengers.contains(p1),true);
		
		booking_service.deletePassengerById(p1.getId());
		
		passengers = passenger_repository.findAll();
		
		assertEquals(passengers.contains(p1),false);

	}
	
	
	
	@Test
	public void testPassengerInit() {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		Booking booking = booking_service.createSimpleBooking().get();
		
		Passenger p1 = new Passenger(null, booking.getId(), "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male", "address1");
		
		booking_service.save(p1);
		
		tx.commit();
		session.close();
		
		updatePassenger(p1);
		
	}
	
	
	public void updatePassenger(Passenger p1) {
		Passenger p1_update = new Passenger(p1.getId(), null, "passenger1.1", "passenger1.1", LocalDate.of(1999, 9, 20), "female", "address1.1");
		p1 = booking_service.update(p1_update).get();
		
		assertEquals(p1.getGiven_name(), p1_update.getGiven_name());
		assertEquals(p1.getFamily_name(), p1_update.getFamily_name());
		assertEquals(p1.getGender(), p1_update.getGender());
		assertEquals(p1.getAddress(), p1_update.getAddress());
		assertEquals(p1.getDob(), p1_update.getDob());
		
		booking_service.deletePassengerById(p1.getId());
		
		
	}
	
	
	

	@Nested
	class testSave{
		private Booking booking;
		private String confirmation_code;;
		public Integer booking_id;
		private Boolean is_active;
		private String test_user = "user1";
		private Integer user_id;
		private Integer flight_id;

		private List<Passenger> passengers;

		private FlightBookings flight_bookings;

		private BookingPayment booking_payment;

		private BookingAgent booking_agent;
		private BookingUser booking_user;
		private BookingGuest booking_guest;
	
		
		
	public void setup() {

		this.booking = new Booking();
		passengers = new ArrayList<>();
		Passenger p1 = new Passenger(null, null, "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male", "address1");
		Passenger p2 = new Passenger(null, null, "passenger2", "passenger2", LocalDate.of(1996, 8, 20), "Male", "address1");
		Passenger p3 = new Passenger(null, null, "passenger3", "passenger3", LocalDate.of(1995, 7, 20), "Male", "address1");
		passengers.add(p1);
		passengers.add(p2);
		passengers.add(p3);
		
		booking_agent = new BookingAgent();
		booking_agent.setAgent_id(1);
		
		flight_bookings = new FlightBookings();
		flight_bookings.setFlight_id(14);
		
		
		
		booking.setPassengers(passengers);
		booking.setBooking_agent(booking_agent);
		booking.setFlight_bookings(flight_bookings);
		
	}
	
	public void save() {
	
		this.booking = booking_service.save(booking).get(); // save

		this.booking_id = booking.getId();
		
	}
	
	@Test
	public void testPassengerList() {

		setup();	
		save();		
	
		List<Passenger> passenger_list = passenger_repository.findAll();
		
		for(Passenger p : passengers) {
			
			assertEquals(passenger_list.contains(p), true);
			
		}
		
		
		teardown();
	}
	
	@Test
	public void testBookingAgent() {
		
		setup();
		save();
		
		List<BookingAgent> saved_object = booking_agent_repository.findAll().stream().filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
		assertEquals(saved_object.size(), 1);
		assertEquals(saved_object.get(0).getAgent_id(), this.booking_agent.getAgent_id());
		
		teardown();

	}
	
	@Test
	public void testBookingUser() {
		
		setup();
		
		booking.setBooking_agent(null);
		booking_user = new BookingUser();
		booking_user.setUser_id(33);
		booking.setBooking_user(booking_user);
		
		save();
		
		List<BookingUser> saved_object = booking_user_repository.findAll().stream().filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
		assertEquals(saved_object.size(), 1);
		assertEquals(saved_object.get(0).getUser_id(), this.booking_user.getUser_id());
		
		teardown();		
	}
	
	@Test
	public void testBookingGuest() {
		
		setup();
		
		booking.setBooking_agent(null);
		
		booking_guest = new BookingGuest();
		booking_guest.setContact_email("username@domain.com");
		booking_guest.setContact_phone("555-555-5555");
		booking.setBooking_guest(booking_guest);
		
		save();
		
		List<BookingGuest> saved_object = booking_guest_repository.findAll().stream().filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
		assertEquals(saved_object.size(), 1);
		assertEquals(saved_object.get(0).getContact_email(), this.booking_guest.getContact_email());
		assertEquals(saved_object.get(0).getContact_phone(), this.booking_guest.getContact_phone());

		
		teardown();		
	}
	
	
	@Test
	public void testFlightBookings() {
		
		setup();
		save();
		
		List<FlightBookings> saved_object = flight_bookings_repository.findAll().stream().filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
		assertEquals(saved_object.size(), 1);
		assertEquals(saved_object.get(0).getFlight_id(), this.flight_bookings.getFlight_id());
		
		teardown();		
	}
	
	@Test
	public void testBookingPayment() {
		
		setup();
		save();
		
		List<BookingPayment> saved_object = booking_payment_repository.findAll().stream().filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
		assertEquals(saved_object.size(), 1);
		assertEquals(saved_object.get(0).getStripe_id(), booking.getBooking_payment().getStripe_id());
		
		teardown();		
	}
	
	@Test
	public void testUpdateBookingPassenger() {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		setup();
		save();
		
		tx.commit();
		session.close();

		Passenger new_passenger = new Passenger(null, null, "passenger4", "passenger4", LocalDate.of(1997, 9, 20), "Male", "address1");
		passengers = new ArrayList<>();
		passengers.add(new_passenger);
		
		Booking new_booking = new Booking();
		new_booking.setId(booking.getId());
		new_booking.setPassengers(passengers);
		System.out.println(booking.getId());
		new_booking = booking_service.update(new_booking).get();

		
		assertEquals(new_booking.getPassengers().contains(new_passenger), true);
		teardown();
		
	}
	
	@Test
	public void testUpdateBookingAgent() {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		setup();
		save();
		
		tx.commit();
		session.close();

		
		
		Booking new_booking = new Booking();
		new_booking.setId(booking.getId());
		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(0).getId()));
		
		new_booking = booking_service.update(new_booking).get();

		
		assertEquals(booking_agent_repository.findAll().contains(new_booking.getBooking_agent()), true);
		teardown();
		
	}
	

	
//	@Test
//	public void testUpdateBookingAgentConflict() {
//		
//		Session session = sessionFactory.openSession();
//		Transaction tx = session.beginTransaction();
//		setup();
//		save();
//		
//		tx.commit();
//		session.close();
//
//		
//		
//		Booking new_booking = new Booking();
//		new_booking.setId(booking.getId());
//		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(0).getId()));
//		new_booking = booking_service.update(new_booking).get();
//		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(1).getId()));
//		new_booking = booking_service.update(new_booking).get();
//		
//		assertEquals(booking_agent_repository.findAll().contains(new_booking.getBooking_agent()), true);
//		teardown();
//		
//	}
	
	
	
//	@Test
//	public void testUpdateBookingUser() {
//		
//		Session session = sessionFactory.openSession();
//		Transaction tx = session.beginTransaction();
//		setup();
//		save();
//		
//		tx.commit();
//		session.close();
//
//		
//		
//		Booking new_booking = new Booking();
//		new_booking.setId(booking.getId());
//		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(1).getId()));
//		new_booking = booking_service.update(new_booking).get();
//
//		
//		assertEquals(booking_agent_repository.findAll().contains(new_booking.getBooking_agent()), true);
//		teardown();
//		
//	}
//	
//	@Test
//	public void testUpdateBookingUserConflict() {
//		
//		Session session = sessionFactory.openSession();
//		Transaction tx = session.beginTransaction();
//		setup();
//		save();
//		
//		tx.commit();
//		session.close();
//
//		
//		
//		Booking new_booking = new Booking();
//		new_booking.setId(booking.getId());
//		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(0).getId()));
//		new_booking = booking_service.update(new_booking).get();
//		new_booking.setBooking_agent(new BookingAgent(booking.getId(), user_repository.findAll().get(1).getId()));
//		new_booking = booking_service.update(new_booking).get();
//		
//		assertEquals(booking_agent_repository.findAll().contains(new_booking.getBooking_agent()), true);
//		teardown();
//		
//	}
	

	
	

//	@Test
//	public void testAddBookingAgent() {
//		Passenger passenger = new Passenger();
//		passenger.setId(null);
//		passenger.setBooking_id(null);
//		passenger.setFamily_name("lllllllll");
//		passenger.setGiven_name("lllllllll");
//		passenger.setAddress("lllllllll");
//		passenger.setDob(LocalDate.now());
//		passenger.setGender("lllllllll");
//
//		Integer flight_id = flight_repository.findAll().get(0).getId();
//		Integer user_id = booking_agent_repository.findAll().get(0).getAgent_id();
//
//		booking = booking_service.saveBookingAgentBooking(passenger, user_id, flight_id);
//
//		this.booking_id = booking.getId();
//		assertEquals(true, booking_agent_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		assertEquals("ROLE_AGENT", booking_service.findUserByBookingId(booking.getId()).get().getUser_role().getName());
//
//		assertEquals(true, booking_payment_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		assertEquals(true, flight_bookings_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		teardown();
//
//	}
//
//	@Test
//	public void testAddBookingUser() {
//		Passenger passenger = new Passenger();
//		passenger.setId(null);
//		passenger.setBooking_id(null);
//		passenger.setFamily_name("lllllllll");
//		passenger.setGiven_name("lllllllll");
//		passenger.setAddress("lllllllll");
//		passenger.setDob(LocalDate.now());
//		passenger.setGender("lllllllll");
//
//		Integer flight_id = flight_repository.findAll().get(0).getId();
//		Integer user_id = booking_user_repository.findAll().get(0).getUser_id();
//
//		booking = booking_service.saveBookingUserBooking(passenger, user_id, flight_id);
//		this.booking_id = booking.getId();
//
//		assertEquals(true, booking_user_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		assertEquals("ROLE_TRAVELER",
//				booking_service.findUserByBookingId(booking.getId()).get().getUser_role().getName());
//
//		assertEquals(true, booking_payment_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		assertEquals(true, flight_bookings_repository.findAll().stream().map(x -> x.getBooking_id())
//				.collect(Collectors.toList()).contains(booking.getId()));
//		teardown();
//
//	}
//
//	@Test
//	public void testAddEntireBookingByAgent() {
//
//		List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
//		FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
//		BookingAgent booking_agent = new BookingAgent();
//		booking_agent.setAgent_id(user_repository.findAll().stream()
//				.filter(x -> x.getUser_role().getName().equals("ROLE_AGENT")).findFirst().get().getId());
//
//		passengers.forEach(x -> x.setId(null));
//		;
//
//		Booking booking = new Booking();
//		booking.setPassengers(passengers);
//		booking.setFlight_bookings(flight_bookings);
//		booking.setBooking_agent(booking_agent);
//
//		Booking inserted_booking = booking_service.save(booking).get();
//
//		System.out.println(inserted_booking);
//
//		this.booking_id = inserted_booking.getId();
//
//		booking = booking_repository.findById(booking_id).get();
//
//		assertEquals(booking.getId(), inserted_booking.getId());
//
//		assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());
//
//		assertEquals(booking.getIs_active(), inserted_booking.getIs_active());
//
//		assertEquals(booking.getBooking_agent().getAgent_id(), inserted_booking.getBooking_agent().getAgent_id());
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())),
//				true);
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList())),
//				true);
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())), true);
//
//		assertEquals(
//				collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
//						inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())),
//				true);
//
//		teardown();
//
//	}
//
//	@Test
//	public void testAddEntireBookingByUser() {
//
//		List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
//		FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
//		BookingUser booking_user = new BookingUser();
//		booking_user.setUser_id(user_repository.findAll().stream()
//				.filter(x -> x.getUser_role().getName().equals("ROLE_TRAVELER")).findFirst().get().getId());
//
//		passengers.forEach(x -> x.setId(null));
//		;
//
//		Booking booking = new Booking();
//		booking.setPassengers(passengers);
//		booking.setFlight_bookings(flight_bookings);
//		booking.setBooking_user(booking_user);
//
//		Booking inserted_booking = booking_service.save(booking).get();
//
//		System.out.println(inserted_booking);
//
//		this.booking_id = inserted_booking.getId();
//
//		booking = booking_repository.findById(booking_id).get();
//
//		assertEquals(booking.getId(), inserted_booking.getId());
//
//		assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());
//
//		assertEquals(booking.getIs_active(), inserted_booking.getIs_active());
//
//		assertEquals(booking.getBooking_user().getUser_id(), inserted_booking.getBooking_user().getUser_id());
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())),
//				true);
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList())),
//				true);
//
//		assertEquals(collectionsMatch(
//				booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
//				inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())), true);
//
//		assertEquals(
//				collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
//						inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())),
//				true);
//
//		teardown();
//
//	}

	public void teardown() {
		booking_service.deleteBookingById(booking_id);

//		while (true) {
//			if (booking_repository.existsById(booking_id)) {
//				booking_service.deleteBookingById(booking_id);
//				break;
//			}
//		}

	}

//	}
//	@Nested
//	class testQueries {
//
//		String good_id = "user1";
//		List<Passenger> passengers_nested;
//		List<Flight> flights;
//		List<Booking> bookings;
//
//		public void getBookings() {
//			this.bookings = booking_controller.getBookingByUsername(good_id).getBody();
//		}
//
//		public void getFlights() {
//			this.flights = booking_controller.getFlightByUsername(good_id).getBody();
//		}
//
//		public void getPassengers() {
//
//			this.passengers_nested = booking_controller.getPassengerByUsername(good_id).getBody();
//
//		}
//
//
//
//		@Test
//		@Transactional
//		public void testFlights() {
//			getFlights();
//			List<Integer> flightIds = booking_controller.getFlightByUsername(good_id).getBody().stream()
//					.map(x -> x.getId()).collect(Collectors.toList());
//			List<Integer> flightIdsCheck = this.flights.stream().map(x -> x.getId()).collect(Collectors.toList());
//			for (Integer id : flightIdsCheck) {
//				assertEquals(flightIds.contains(id), true);
//			}
//			assertEquals(flightIdsCheck.size(), flightIds.size());
//
//		}
//
//		@Test
//		@Transactional
//		public void testPassengers() {
//			getBookings();
//			getPassengers();
//			List<Integer> passengerIds = this.passengers_nested.stream().map(x -> x.getId())
//					.collect(Collectors.toList());
//			List<Integer> passengerIdsCheck = booking_controller.getPassengerByUsername(good_id).getBody().stream()
//					.map(x -> x.getId()).collect(Collectors.toList());
//			for (Integer id : passengerIdsCheck) {
//				assertEquals(passengerIds.contains(id), true);
//			}
//			assertEquals(passengerIds.size(), passengerIdsCheck.size());
//		}
//
//	}
	}
}
