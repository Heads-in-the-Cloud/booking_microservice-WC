package com.ss.utopia.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Transaction;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.utopia.api.controller.UserController;
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
import com.ss.utopia.api.service.FlightBookingService;
import com.ss.utopia.api.service.PassengerBookingService;
import com.ss.utopia.api.service.UserBookingService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BookingServiceTests {

	@Autowired
	UserController passenger_controller;

	@Autowired
	BookingService booking_service;

	@Autowired
	FlightBookingService flight_booking_service;

	@Autowired
	PassengerBookingService passenger_booking_service;

	@Autowired
	UserBookingService user_booking_service;

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

		Passenger p1 = new Passenger(null, null, "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male",
				"address1");

		Booking booking = booking_service.createSimpleBooking();
		p1.setBooking_id(booking.getId());

		p1 = passenger_booking_service.save(p1);
		System.out.println(p1);
		List<Passenger> passengers = passenger_repository.findAll();

		assertEquals(passengers.contains(p1), true);

		passenger_booking_service.deletePassengerById(p1.getId());

		passengers = passenger_repository.findAll();

		assertEquals(booking_repository.existsById(booking.getId()), false);

	}

	@Test
	public void testPassengerInit() {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Booking booking = booking_service.createSimpleBooking();

		Passenger p1 = new Passenger(null, booking.getId(), "passenger1", "passenger1", LocalDate.of(1997, 9, 20),
				"Male", "address1");

		passenger_booking_service.save(p1);

		tx.commit();
		session.close();

		updatePassenger(p1);

	}

	public void updatePassenger(Passenger p1) {
		Passenger p1_update = new Passenger(p1.getId(), null, "passenger1.1", "passenger1.1", LocalDate.of(1999, 9, 20),
				"female", "address1.1");
		p1 = passenger_booking_service.update(p1_update);

		assertEquals(p1.getGiven_name(), p1_update.getGiven_name());
		assertEquals(p1.getFamily_name(), p1_update.getFamily_name());
		assertEquals(p1.getGender(), p1_update.getGender());
		assertEquals(p1.getAddress(), p1_update.getAddress());
		assertEquals(p1.getDob(), p1_update.getDob());

		passenger_booking_service.deletePassengerById(p1.getId());

	}

	@Nested
	class testSave {
		private Booking booking;
		public Integer booking_id;

		private Integer flight_id = 14;

		private List<Passenger> passengers;

		private FlightBookings flight_bookings;

		private BookingAgent booking_agent;
		private BookingUser booking_user;
		private BookingGuest booking_guest;

		public void setup() {

			this.booking = new Booking();

			booking_agent = new BookingAgent();
			booking_agent.setAgent_id(1);

			flight_bookings = new FlightBookings();
			flight_bookings.setFlight_id(flight_id);

			booking.setBooking_agent(booking_agent);
			booking.setFlight_bookings(flight_bookings);

		}

		public void save() {

			this.booking = booking_service.save(booking); // save

			this.booking_id = booking.getId();

		}

		@Test
		public void testPassengerList() {

			setup();

			passengers = new ArrayList<>();
			Passenger p1 = new Passenger(null, null, "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male",
					"address1");
			Passenger p2 = new Passenger(null, null, "passenger2", "passenger2", LocalDate.of(1996, 8, 20), "Male",
					"address1");
			Passenger p3 = new Passenger(null, null, "passenger3", "passenger3", LocalDate.of(1995, 7, 20), "Male",
					"address1");
			passengers.add(p1);
			passengers.add(p2);
			passengers.add(p3);
			booking.setPassengers(passengers);

			save();

			List<Passenger> passenger_list = passenger_repository.findAll();

			for (Passenger p : passengers) {

				assertEquals(passenger_list.contains(p), true);

			}

			teardown();
		}

		@Test
		public void testBookingAgent() {

			setup();
			save();

			List<BookingAgent> saved_object = booking_agent_repository.findAll().stream()
					.filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
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

			List<BookingUser> saved_object = booking_user_repository.findAll().stream()
					.filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
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

			List<BookingGuest> saved_object = booking_guest_repository.findAll().stream()
					.filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
			assertEquals(saved_object.size(), 1);
			assertEquals(saved_object.get(0).getContact_email(), this.booking_guest.getContact_email());
			assertEquals(saved_object.get(0).getContact_phone(), this.booking_guest.getContact_phone());

			teardown();
		}

		@Test
		public void testFlightBookings() {

			setup();
			save();

			List<FlightBookings> saved_object = flight_bookings_repository.findAll().stream()
					.filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
			assertEquals(saved_object.size(), 1);
			assertEquals(saved_object.get(0).getFlight_id(), this.flight_bookings.getFlight_id());

			teardown();
		}

		@Test
		public void testBookingPayment() {

			setup();
			save();

			List<BookingPayment> saved_object = booking_payment_repository.findAll().stream()
					.filter(x -> x.getBooking_id().equals(booking.getId())).collect(Collectors.toList());
			assertEquals(saved_object.size(), 1);
			assertEquals(saved_object.get(0).getStripe_id(), booking.getBooking_payment().getStripe_id());

			teardown();
		}

		@Test
		public void testAddPassengerMissingField() {

			setup();

			passengers = new ArrayList<>();
			Passenger p1 = new Passenger(null, null, null, "passenger1", LocalDate.of(1997, 9, 20), "Male", "address1");
			passengers.add(p1);
			booking.setPassengers(passengers);

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

				save();

			});

		}

		@Test
		public void testBookingAgentInvalidFK() {

			setup();

			booking.getBooking_agent().setAgent_id(0);

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

				save();

			});
		}

		@Test
		public void testMissingBooking() {

			setup();
			booking.setBooking_agent(null);
			assertEquals(booking_service.save(booking), null);
		}

		@Test
		public void testMissingFlight() {

			setup();
			booking.setFlight_bookings(null);
			assertEquals(booking_service.save(booking), null);
		}

		@Test
		public void testUpdateBookingPassenger() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Passenger new_passenger = new Passenger(null, null, "passenger4", "passenger4", LocalDate.of(1997, 9, 20),
					"Male", "address1");
			passengers = new ArrayList<>();
			passengers.add(new_passenger);

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setPassengers(passengers);
			new_booking = booking_service.update(new_booking);

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

			new_booking = booking_service.update(new_booking);

			assertEquals(booking_agent_repository.findAll().contains(new_booking.getBooking_agent()), true);
			teardown();

		}

		@Test
		public void testUpdateBookingUser() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setBooking_user(new BookingUser(booking.getId(), user_repository.findAll().get(0).getId()));

			new_booking = booking_service.update(new_booking);

			assertEquals(booking_user_repository.findAll().contains(new_booking.getBooking_user()), true);
			teardown();

		}

		@Test
		public void testUpdateBookingGuest() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setBooking_guest(new BookingGuest(booking.getId(), "username@domain.com", "555 555 5555"));

			new_booking = booking_service.update(new_booking);

			assertEquals(booking_guest_repository.findAll().contains(new_booking.getBooking_guest()), true);
			teardown();

		}

		@Test
		public void testUpdateBookingMissingFields() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setBooking_agent(new BookingAgent(booking.getId(), null));

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
				Booking empty = booking_service.update(new_booking);

			});

			teardown();

		}

		@Test
		public void testUpdateInvalidFKUpdate() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setBooking_agent(new BookingAgent(booking.getId(), 0));

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
				Booking empty = booking_service.update(new_booking);

			});

			teardown();

		}

		@Test
		public void testUpdateInvalidDataUpdate() {
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			Passenger new_passenger = new Passenger(null, null, "passenger4", "passenger4", LocalDate.of(1997, 9, 20),
					"MaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMaleMale", "address1");
			passengers = new ArrayList<>();
			passengers.add(new_passenger);

			Booking new_booking = new Booking();
			new_booking.setId(booking.getId());
			new_booking.setPassengers(passengers);

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

				booking_service.update(new_booking);

			});
			teardown();

		}

		@Test
		public void testDeleteBookingAgent() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();
			save();

			tx.commit();
			session.close();

			user_booking_service.deleteBookingAgent(booking_id);

			assertEquals(booking_agent_repository.findAll().contains(booking.getBooking_agent()), false);
			teardown();
		}

		@Test
		public void testDeleteBookingUser() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();

			booking.setBooking_user(new BookingUser(booking.getId(), booking.getBooking_agent().getAgent_id()));

			save();

			tx.commit();
			session.close();

			user_booking_service.deleteBookingUser(booking_id);

			assertEquals(booking_user_repository.findAll().contains(booking.getBooking_user()), false);
			teardown();
		}

		@Test
		public void testDeleteBookingGuest() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			setup();

			booking.setBooking_guest(new BookingGuest(booking.getId(), "username@domain.com", "555 555 5555"));

			save();

			tx.commit();
			session.close();

			user_booking_service.deleteBookingGuest(booking_id);

			assertEquals(booking_guest_repository.findAll().contains(booking.getBooking_guest()), false);
			teardown();
		}

		@Test
		public void testAddEntireBookingByAgent() {

			List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
			FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
			BookingAgent booking_agent = new BookingAgent();
			booking_agent.setAgent_id(user_repository.findAll().stream()
					.filter(x -> x.getUser_role().getName().equals("ROLE_AGENT")).findFirst().get().getId());

			passengers.forEach(x -> x.setId(null));
			;

			Booking booking = new Booking();
			booking.setPassengers(passengers);
			booking.setFlight_bookings(flight_bookings);
			booking.setBooking_agent(booking_agent);

			Booking inserted_booking = booking_service.save(booking);

			this.booking_id = inserted_booking.getId();

			booking = booking_repository.findById(booking_id).get();

			assertEquals(booking.getId(), inserted_booking.getId());

			assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());

			assertEquals(booking.getIs_active(), inserted_booking.getIs_active());

			assertEquals(booking.getBooking_agent().getAgent_id(), inserted_booking.getBooking_agent().getAgent_id());

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())),
					true);

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getFamily_name())
							.collect(Collectors.toList())),
					true);

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())),
					true);

			assertEquals(
					collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
							inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())),
					true);

			teardown();

		}

		@Test
		public void testAddEntireBookingByUser() {

			List<Passenger> passengers = passenger_repository.findAll().stream().limit(3).collect(Collectors.toList());
			FlightBookings flight_bookings = flight_bookings_repository.findAll().get(0);
			BookingUser booking_user = new BookingUser();
			booking_user.setUser_id(user_repository.findAll().stream()
					.filter(x -> x.getUser_role().getName().equals("ROLE_TRAVELER")).findFirst().get().getId());

			passengers.forEach(x -> x.setId(null));
			;

			Booking booking = new Booking();
			booking.setPassengers(passengers);
			booking.setFlight_bookings(flight_bookings);
			booking.setBooking_user(booking_user);

			Booking inserted_booking = booking_service.save(booking);

			this.booking_id = inserted_booking.getId();

			booking = booking_repository.findById(booking_id).get();

			assertEquals(booking.getId(), inserted_booking.getId());

			assertEquals(booking.getConfirmation_code(), inserted_booking.getConfirmation_code());

			assertEquals(booking.getIs_active(), inserted_booking.getIs_active());

			assertEquals(booking.getBooking_user().getUser_id(), inserted_booking.getBooking_user().getUser_id());

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getGiven_name()).collect(Collectors.toList())),
					true);

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getFamily_name()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getFamily_name())
							.collect(Collectors.toList())),
					true);

			assertEquals(collectionsMatch(
					booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList()),
					inserted_booking.getPassengers().stream().map(x -> x.getAddress()).collect(Collectors.toList())),
					true);

			assertEquals(
					collectionsMatch(booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList()),
							inserted_booking.getPassengers().stream().map(x -> x.getId()).collect(Collectors.toList())),
					true);

			teardown();

		}

		public void teardown() {
			booking_service.deleteBookingById(booking_id);
		}

	}

	@Nested
	class testQueries {

		String good_id = "user1";
		List<Passenger> passengers_nested;
		List<Flight> flights;
		List<Booking> bookings;

		public void getBookings() {
			this.bookings = user_booking_service.getBookingByUsernameQuery(good_id);
		}

		public void getFlights() {
			this.flights = passenger_controller.getFlightByUsername(good_id).getBody();
		}

		public void getPassengers() {

			this.passengers_nested = passenger_controller.getPassengerByUsername(good_id).getBody();

		}

		@Test
		@Transactional
		public void testFlights() {
			getFlights();
			List<Integer> flightIds = passenger_controller.getFlightByUsername(good_id).getBody().stream()
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
			List<Integer> passengerIdsCheck = passenger_controller.getPassengerByUsername(good_id).getBody().stream()
					.map(x -> x.getId()).collect(Collectors.toList());
			for (Integer id : passengerIdsCheck) {
				assertEquals(passengerIds.contains(id), true);
			}
			assertEquals(passengerIds.size(), passengerIdsCheck.size());
		}

	}

	@Nested
	class testSaveParams {

		private Booking booking;

		public Integer booking_id;
		private Integer flight_id = 1;
		private Integer agent_id = 32;
		private Integer user_id = 33;
		private Integer new_flight_id = 2;

		private String first_email = "email@domain.com";
		private String first_phone = "555 555 5555";

		private String second_email = "newemail@domain.com";
		private String second_phone = "444 444 4444";

		private List<Passenger> passengers;

		public void setup() {
			this.booking = new Booking();
		}

		public void save(Integer flight_id, Integer user_id) {

			booking = booking_service.saveParams(booking, flight_id, user_id);
			this.booking_id = booking.getId();
		}

		@Test
		public void testSavePassengers() {

			Session session = sessionFactory.openSession();

			setup();

			passengers = new ArrayList<>();
			Passenger p1 = new Passenger(null, null, "passenger1", "passenger1", LocalDate.of(1997, 9, 20), "Male",
					"address1");
			Passenger p2 = new Passenger(null, null, "passenger2", "passenger2", LocalDate.of(1996, 8, 20), "Male",
					"address1");
			Passenger p3 = new Passenger(null, null, "passenger3", "passenger3", LocalDate.of(1995, 7, 20), "Male",
					"address1");
			passengers.add(p1);
			passengers.add(p2);
			passengers.add(p3);

			this.booking.setPassengers(passengers);

			Transaction tx = session.beginTransaction();

			save(flight_id, agent_id);

			tx.commit();
			session.close();

			List<Passenger> passenger_list = passenger_repository.findAll();
			for (Passenger p : passengers) {
				assertEquals(passenger_list.contains(p), true);

			}
			teardown();

		}

		@Test
		public void testSaveAgent() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			setup();
			save(flight_id, agent_id);

			tx.commit();
			session.close();

			assertEquals(booking_agent_repository.findAll().contains(booking.getBooking_agent()), true);
			teardown();
		}

		@Test
		public void testSaveUser() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			setup();
			save(flight_id, user_id);

			tx.commit();
			session.close();

			assertEquals(booking_user_repository.findAll().contains(booking.getBooking_user()), true);
			teardown();
		}

		@Test
		public void testSaveFlightBookings() {
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			setup();
			save(flight_id, user_id);

			tx.commit();
			session.close();

			assertEquals(flight_bookings_repository.findAll().contains(booking.getFlight_bookings()), true);
			assertEquals(flight_bookings_repository.findById(booking_id).get().getFlight_id(), flight_id);
			teardown();
		}

		@Test
		public void testSaveBookingPayment() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			setup();
			save(flight_id, user_id);

			tx.commit();
			session.close();

			assertEquals(booking_payment_repository.findAll().contains(booking.getBooking_payment()), true);
			teardown();

		}

		@Test
		public void testBadParams() {

			setup();

			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

				booking_service.saveParams(booking, 9999, user_id);

			});

			Assertions.assertThrows(NoSuchElementException.class, () -> {

				booking_service.saveParams(booking, flight_id, 9999);

			});

		}

		@Test
		public void testInvalidData() {

			setup();

			Passenger p1 = new Passenger(null, null, null, "lastname", LocalDate.of(1990, 12, 10), "male", "address");
			Assertions.assertThrows(DataIntegrityViolationException.class, () -> {

				booking_service.saveParams(booking, 9999, user_id);

			});

		}

		@Test
		public void testUpdateAgentDirectly() {

			setup();
			save(flight_id, agent_id);

			BookingAgent agent_to_update = new BookingAgent(booking.getId(), user_repository.findAll().stream()
					.filter(x -> !x.getId().equals(agent_id)).findAny().get().getId());
			user_booking_service.update(agent_to_update);

			assertEquals(booking_repository.findById(booking_id).get().getBooking_agent().getAgent_id(),
					agent_to_update.getAgent_id());
			teardown();

		}

		@Test
		public void testUpdateUserDirectly() {

			setup();
			save(flight_id, user_id);

			BookingUser user_to_update = new BookingUser(booking.getId(),
					user_repository.findAll().stream().filter(x -> !x.getId().equals(user_id)).findAny().get().getId());
			user_booking_service.update(user_to_update);

			assertEquals(booking_repository.findById(booking_id).get().getBooking_user().getUser_id(),
					user_to_update.getUser_id());
			teardown();

		}

		@Test
		public void testUpdateGuestDirectly() {

			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			booking = booking_service.createSimpleBooking();
			booking_id = booking.getId();

			booking_guest_repository.save(new BookingGuest(booking.getId(), first_email, first_phone));

			tx.commit();
			session.close();

			BookingGuest update_booking_guest = new BookingGuest(booking.getId(), second_email, second_phone);
			BookingGuest updated_booking_guest = user_booking_service.update(update_booking_guest);

			assertEquals(updated_booking_guest.getContact_email(), second_email);
			assertEquals(updated_booking_guest.getContact_phone(), second_phone);

			teardown();

		}

		@Test
		public void testUpdateBookingPayment() {
			setup();
			save(flight_id, user_id);

			BookingPayment update_booking_payment = new BookingPayment(booking.getId(), null, Boolean.TRUE);
			BookingPayment updated_booking_payment = booking_service.update(update_booking_payment);

			assertEquals(updated_booking_payment.getRefunded(), Boolean.TRUE);
			teardown();

		}

		@Test
		public void testUpdateFlightBookings() {
			setup();
			save(flight_id, user_id);

			FlightBookings update_flight_bookings = new FlightBookings(booking.getId(), new_flight_id);
			FlightBookings updated_flight_bookings = flight_booking_service.update(update_flight_bookings);

			assertEquals(updated_flight_bookings.getFlight_id(), new_flight_id);
			teardown();

		}

		public void teardown() {

			booking_repository.deleteById(booking_id);

		}

	}

}
