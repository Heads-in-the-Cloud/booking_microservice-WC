package com.ss.utopia.api.service;

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

	public List<Passenger> findAllPassengers() {
		return passenger_repository.findAll();
	}

	public List<Passenger> getPassengerByBooking(List<Booking> bookings) {

		return bookings.stream().map(x -> passenger_repository.getPassengerByBookingId(x.getId())).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	public Optional<List<Booking>> getBookingByUsernameQuery(String username) {
		try {
			return Optional.of(booking_repository.getBookingsByUser(username));
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	public Passenger save(Passenger passenger) {
		return passenger_repository.save(passenger);
	}

	public List<Flight> getFlightByBookingId(List<Booking> bookings) {

		bookings.stream().forEach(x -> {

			System.out.println(flight_repository.getFlightByBooking(x.getId()));
		});

		return bookings.stream().map(x -> flight_repository.getFlightByBooking(x.getId())).collect(Collectors.toList());
	}

	public Optional<Booking> getBookingById(Integer booking_id) {

		try {
			return booking_repository.findById(booking_id);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

//	public Booking getBookingById(Integer booking_id) {
//		return booking_repository.getById(booking_id);
//	}

	public Optional<Passenger> getPassengerById(Integer passenger_id) {
		try {
			return Optional.of(passenger_repository.findById(passenger_id).get());
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Boolean deletePassengerById(Integer passenger_id) {

		System.out.println(passenger_id);

		// delete booking if the last passenger is deleted
		try {

			Passenger passenger = passenger_repository.findById(passenger_id).get();

			passenger_repository.deleteById(passenger_id);

			System.out.println(passenger.getBooking_id());

			if (booking_repository.findById(passenger.getBooking_id()).get().getPassengers().size() == 0) {
				booking_repository.deleteById(passenger.getBooking_id());

			}

			return Boolean.TRUE;

		} catch (Exception e) {

			return Boolean.FALSE;
		}
	}

	public Boolean deleteBookingById(Integer booking_id) {
		try {

			booking_repository.deleteById(booking_id);
			return Boolean.TRUE;

		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	public Optional<Booking> createSimpleBooking() {
		return Optional.of(booking_repository.save(new Booking(Boolean.TRUE, generateConfirmationCode())));
	}

	@Transactional // TODO use batch saves
	public Optional<Booking> save(Booking booking) {

		try {
			List<Passenger> passengers = booking.getPassengers();
			BookingAgent booking_agent = booking.getBooking_agent();
			BookingUser booking_user = booking.getBooking_user();
			BookingGuest booking_guest = booking.getBooking_guest();
			FlightBookings flight_bookings = booking.getFlight_bookings();

			if (flight_bookings == null || passengers == null
					|| (booking_agent == null && booking_user == null && booking_guest == null)) {
				return Optional.empty();
			}

			// Booking persist_booking = new Booking(Boolean.TRUE,
			// generateConfirmationCode());
			Booking persist_booking = createSimpleBooking().get();

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

			persist_booking.setPassengers(
					passengers.stream().peek(x -> x.setBooking_id(booking_id)).collect(Collectors.toList()));
			persist_booking.setBooking_agent(booking.getBooking_agent());
			persist_booking.setFlight_bookings(flight_bookings);
			persist_booking.setBooking_payment(booking_payment);

			return Optional.of(persist_booking);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	@Transactional // TODO use batch saves
	public Optional<Booking> saveParams(Booking booking, Integer flight_id, Integer user_id) {

		try {
			List<Passenger> passengers = booking.getPassengers();

			// Booking persist_booking = new Booking(Boolean.TRUE,
			// generateConfirmationCode());
			Booking persist_booking = createSimpleBooking().get();

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

			persist_booking.setPassengers(
					passengers.stream().peek(x -> x.setBooking_id(booking_id)).collect(Collectors.toList()));

			return Optional.of(persist_booking);
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public Booking saveBookingAgentBooking(Passenger passenger, Integer user_id, Integer flight_id) {

		Booking booking = new Booking();
		booking.setConfirmation_code(generateConfirmationCode());
		booking.setIs_active(Boolean.TRUE);

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		booking = booking_repository.save(booking);

		passenger.setBooking_id(booking.getId());

		BookingAgent booking_agent = new BookingAgent();
		booking_agent.setBooking_id(booking.getId());
		booking_agent.setAgent_id(user_id);

		BookingPayment booking_payment = new BookingPayment();
		booking_payment.setBooking_id(booking.getId());
		booking_payment.setRefunded(Boolean.FALSE);
		booking_payment.setStripe_id(generateStripeId());

		FlightBookings flight_bookings = new FlightBookings();
		flight_bookings.setBooking_id(booking.getId());
		flight_bookings.setFlight_id(flight_id);

		passenger_repository.save(passenger);
		booking_agent_repository.save(booking_agent);
		flight_bookings_repository.save(flight_bookings);
		booking_payment_repository.save(booking_payment);

		tx.commit();
		session.close();

		return booking;

	}

	public Booking saveBookingUserBooking(Passenger passenger, Integer user_id, Integer flight_id) {

		Booking booking = new Booking();
		booking.setConfirmation_code(generateConfirmationCode());
		booking.setIs_active(Boolean.TRUE);

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		booking = booking_repository.save(booking);

		passenger.setBooking_id(booking.getId());

		BookingUser booking_user = new BookingUser();
		booking_user.setBooking_id(booking.getId());
		booking_user.setUser_id(user_id);

		BookingPayment booking_payment = new BookingPayment();
		booking_payment.setBooking_id(booking.getId());
		booking_payment.setRefunded(Boolean.FALSE);
		booking_payment.setStripe_id(generateStripeId());

		FlightBookings flight_bookings = new FlightBookings();
		flight_bookings.setBooking_id(booking.getId());
		flight_bookings.setFlight_id(flight_id);

		passenger_repository.save(passenger);
		booking_user_repository.save(booking_user);
		flight_bookings_repository.save(flight_bookings);
		booking_payment_repository.save(booking_payment);

		tx.commit();
		session.close();

		return booking;

	}

	public Optional<User> findUserByBookingId(Integer booking_id) {
		return user_repository.findUserByBookingId(booking_id);

	}

	@Transactional
	public Optional<Passenger> update(Passenger passenger) {
		try {
			if (!passenger_repository.existsById(passenger.getId())) {
				return Optional.empty();
			}

			Passenger passenger_to_save = passenger_repository.findById(passenger.getId()).get();

			if (passenger.getGiven_name() != null) {
				passenger_to_save.setGiven_name(passenger.getGiven_name());
			}
			if (passenger.getFamily_name() != null) {
				passenger_to_save.setFamily_name(passenger.getFamily_name());
			}
			if (passenger.getAddress() != null) {
				passenger_to_save.setAddress(passenger.getAddress());
			}
			if (passenger.getGender() != null) {
				passenger_to_save.setGender(passenger.getGender());
			}
			if (passenger.getDob() != null) {
				passenger_to_save.setDob(passenger.getDob());
			}

			return Optional.of(passenger_to_save);
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	@Transactional
	public Optional<Booking> update(Booking booking) {

		try {

			Booking booking_to_update = booking_repository.findById(booking.getId()).get();

			if (booking.getPassengers() != null) {
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
					booking_to_update.getBooking_guest()
							.setContact_email(booking.getBooking_guest().getContact_email());
					booking_to_update.getBooking_guest()
							.setContact_phone(booking.getBooking_guest().getContact_phone());
				}

			}

			booking_to_update.setConfirmation_code(generateConfirmationCode());

			return Optional.of(booking_to_update);

		} catch (Exception e) {
			return Optional.empty();
		}

	}


	public Boolean deleteBookingAgent(Integer booking_id) {

		try {
			booking_agent_repository.deleteById(booking_id);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}

	public Boolean deleteBookingUser(Integer booking_id) {

		try {
			booking_user_repository.deleteById(booking_id);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}

	public Boolean deleteBookingGuest(Integer booking_id) {

		try {
			booking_guest_repository.deleteById(booking_id);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}
	
	
	

	@Transactional
	public Boolean cancelBooking(Integer booking_id) {
		Booking booking = booking_repository.findById(booking_id).get();
		booking.setIs_active(Boolean.FALSE);
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
