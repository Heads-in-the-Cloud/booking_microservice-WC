package com.ss.utopia.api.service;

import java.sql.SQLException;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class BookingService {

	@Autowired
	BookingPaymentRepository booking_payment_repository;

	@Autowired
	FlightBookingsRepository flight_bookings_repository;

	@Autowired
	BookingAgentRepository booking_agent_repository;

	@Autowired
	BookingUserRepository booking_user_repository;

	@Autowired
	BookingGuestRepository booking_guest_repository;

	@Autowired
	BookingRepository booking_repository;

	@Autowired
	PassengerRepository passenger_repository;

	@Autowired
	FlightRepository flight_repository;

	@Autowired
	UserRepository user_repository;

	@Autowired
	SessionFactory sessionFactory;

	final Integer ADMIN = 1;
	final Integer AGENT = 2;
	final Integer TRAVELER = 3;

	public List<Booking> findAllBookings() {

		return booking_repository.findAll();
	}

	public Booking getBookingById(Integer booking_id) {

		return booking_repository.findById(booking_id).get();

	}

	public Booking createSimpleBooking() {
		return booking_repository.save(new Booking(Boolean.TRUE, generateConfirmationCode()));
	}

	@Transactional // TODO use batch saves
	public Booking save(Booking booking) throws SQLException {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		List<Passenger> passengers = booking.getPassengers();
		BookingAgent booking_agent = booking.getBooking_agent();
		BookingUser booking_user = booking.getBooking_user();
		BookingGuest booking_guest = booking.getBooking_guest();
		FlightBookings flight_bookings = booking.getFlight_bookings();

		if (flight_bookings == null || (booking_agent == null && booking_user == null && booking_guest == null)) {
			throw new SQLException("Booking type required");
		}

		Booking persist_booking = new Booking(Boolean.TRUE, generateConfirmationCode());

		persist_booking = booking_repository.save(persist_booking);
		Integer booking_id = persist_booking.getId();

		if (booking_agent != null) {
			booking_agent.setBooking_id(booking_id);
		}
		if (booking_user != null) {
			booking_user.setBooking_id(booking_id);
		}
		if (booking_guest != null) {
			booking_guest.setBooking_id(booking_id);
		}

		flight_bookings.setBooking_id(booking_id);

		BookingPayment booking_payment = new BookingPayment();
		booking_payment.setBooking_id(booking_id);
		booking_payment.setRefunded(Boolean.FALSE);
		booking_payment.setStripe_id(generateStripeId());

		
		
		persist_booking.setBooking_user(booking_user);
		persist_booking.setBooking_guest(booking_guest);

		persist_booking.setBooking_agent(booking.getBooking_agent());
		persist_booking.setFlight_bookings(flight_bookings);
		persist_booking.setBooking_payment(booking_payment);

		if (passengers != null) {
			flight_repository.updateReservedSeats(booking_id, passengers.size());

			persist_booking.setPassengers(
					passengers.stream().peek(x -> x.setBooking_id(booking_id)).collect(Collectors.toList()));
		}
		
		tx.commit();
		session.close();
		
		
		return persist_booking;

	}

	@Transactional // TODO use batch saves
	public Booking saveParams(Booking booking, Integer flight_id, Integer user_id) {

		
		List<Passenger> passengers = booking.getPassengers();
				
		Booking persist_booking = new Booking(Boolean.TRUE, generateConfirmationCode());

		persist_booking = booking_repository.save(persist_booking);

		Integer booking_id = persist_booking.getId();

		User user = user_repository.findById(user_id).get();
		Integer role_id = user.getUser_role().getId();

		if (role_id.equals(ADMIN) || role_id.equals(AGENT)) {
			BookingAgent booking_agent = new BookingAgent(booking_id, user_id);
			persist_booking.setBooking_agent(booking_agent);
		} else {
			BookingUser booking_user = new BookingUser(booking_id, user_id);
			persist_booking.setBooking_user(booking_user);
		}

		persist_booking.setFlight_bookings(new FlightBookings(booking_id, flight_id));

		persist_booking.setBooking_payment(new BookingPayment(booking_id, generateStripeId(), Boolean.FALSE));

		if (passengers != null) {

			flight_repository.updateReservedSeats(booking_id, passengers.size());

			persist_booking.setPassengers(
					passengers.stream().peek(x -> x.setBooking_id(booking_id)).collect(Collectors.toList()));
		}

		return persist_booking;

	}

	@Transactional
	public Booking update(Booking booking) {

		Booking booking_to_update = booking_repository.findById(booking.getId()).get();

		if (booking.getPassengers() != null) {

			flight_repository.updateReservedSeats(booking.getId(), booking.getPassengers().size());

			booking.getPassengers().forEach(x -> x.setBooking_id(booking_to_update.getId()));
			booking.getPassengers().addAll(booking_to_update.getPassengers());
			booking_to_update.setPassengers(booking.getPassengers());

		}

		if (booking.getIs_active() != null) {
			booking_to_update.setIs_active(booking.getIs_active());
		}

		if (booking.getFlight_bookings() != null) {

			booking_to_update.getFlight_bookings().setFlight_id(booking.getFlight_bookings().getFlight_id());
		}

		if (booking.getBooking_payment() != null) {

			booking_to_update.getBooking_payment().setRefunded(booking.getBooking_payment().getRefunded());
		}

		if (booking.getBooking_agent() != null) {

			if (booking_to_update.getBooking_agent() == null) {

				booking.getBooking_agent().setBooking_id(booking_to_update.getId());
				booking_to_update.setBooking_agent(booking.getBooking_agent());
			} else {

				booking_to_update.getBooking_agent().setAgent_id(booking.getBooking_agent().getAgent_id());
			}
		}

		if (booking.getBooking_user() != null) {

			if (booking_to_update.getBooking_user() == null) {

				booking.getBooking_user().setBooking_id(booking_to_update.getId());
				booking_to_update.setBooking_user(booking.getBooking_user());
			} else {
				booking_to_update.getBooking_user().setUser_id(booking.getBooking_user().getUser_id());

			}
		}

		if (booking.getBooking_guest() != null) {

			if (booking_to_update.getBooking_guest() == null) {

				booking.getBooking_guest().setBooking_id(booking_to_update.getId());
				booking_to_update.setBooking_guest(booking.getBooking_guest());

			}

			else {
				booking_to_update.getBooking_guest().setContact_email(booking.getBooking_guest().getContact_email());
				booking_to_update.getBooking_guest().setContact_phone(booking.getBooking_guest().getContact_phone());
			}

		}

		booking_to_update.setConfirmation_code(generateConfirmationCode());

		return booking_to_update;

	}
	
	
	

	public BookingPayment update(BookingPayment booking_payment) {

		booking_payment.setStripe_id(generateStripeId());
		return booking_payment_repository.save(booking_payment);

	}

	@Transactional
	public Boolean cancelBooking(Integer booking_id) {
		Booking booking = booking_repository.findById(booking_id).get();
		if (booking.getIs_active() && booking.getPassengers() != null) {
			flight_repository.updateReservedSeats(booking_id, -(booking.getPassengers().size()));
		}
		booking.setIs_active(Boolean.FALSE);
		return Boolean.TRUE;
	}

	@Transactional
	public Boolean overrideTripCancellation(Integer booking_id) {

		Booking booking = booking_repository.findById(booking_id).get();
		if (!booking.getIs_active() && booking.getPassengers() != null) {
			flight_repository.updateReservedSeats(booking_id, booking.getPassengers().size());
		}
		booking.setIs_active(Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Transactional
	public Boolean refundBooking(Integer booking_id) {
		BookingPayment booking_payment = booking_payment_repository.findById(booking_id).get();
		booking_payment.setRefunded(Boolean.TRUE);
		return Boolean.TRUE;
	}

	public List<Booking> getCancelledBookings() {
		return booking_repository.findAll().stream().filter(x -> !x.getIs_active()).collect(Collectors.toList());
	}

	public List<BookingPayment> getRefundedBookings() {
		return booking_payment_repository.findAll().stream().filter(x -> x.getRefunded()).collect(Collectors.toList());
	}

	@Transactional
	public void deleteBookingById(Integer booking_id) {

		Booking booking_to_delete = booking_repository.findById(booking_id).get();
		if (booking_to_delete.getPassengers() != null) {
			flight_repository.updateReservedSeats(booking_id, -(booking_to_delete.getPassengers().size()));

		}

		booking_repository.deleteById(booking_id);

	}

	/* Generation of random characters */

	public String generateConfirmationCode() {
		String s = "";
		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			if (Math.random() > 0.5) {
				s += Math.random() > 0.5 ? Character.toUpperCase((char) (random.nextInt(26) + 97))
						: (char) (random.nextInt(26) + 97);
			} else {
				s += random.nextInt(10);
			}
		}

		return s;
	}

	public String generateStripeId() {
		String s = "";
		Random random = new Random();
		for (int i = 0; i < 25; i++) {
			if (Math.random() > 0.5) {
				s += Math.random() > 0.5 ? Character.toUpperCase((char) (random.nextInt(26) + 97))
						: (char) (random.nextInt(26) + 97);
			} else {
				s += random.nextInt(10);
			}
		}

		return s;
	}

}
