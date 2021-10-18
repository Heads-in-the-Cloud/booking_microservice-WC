package com.ss.utopia.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ss.utopia.api.dao.BookingAgentRepository;
import com.ss.utopia.api.dao.BookingGuestRepository;
import com.ss.utopia.api.dao.BookingRepository;
import com.ss.utopia.api.dao.BookingUserRepository;
import com.ss.utopia.api.dao.PassengerRepository;
import com.ss.utopia.api.dao.UserRepository;
import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.BookingAgent;
import com.ss.utopia.api.pojo.BookingGuest;
import com.ss.utopia.api.pojo.BookingUser;
import com.ss.utopia.api.pojo.Passenger;
import com.ss.utopia.api.pojo.User;

@Service
public class UserBookingService {

	@Autowired
	UserRepository user_repository;

	@Autowired
	BookingRepository booking_repository;

	@Autowired
	PassengerRepository passenger_repository;

	@Autowired
	BookingAgentRepository booking_agent_repository;

	@Autowired
	BookingUserRepository booking_user_repository;

	@Autowired
	BookingGuestRepository booking_guest_repository;

	
	public Optional<User> findUserByBookingId(Integer booking_id) {

		return user_repository.findUserByBookingId(booking_id);

	}

	public List<Booking> getBookingByUsernameQuery(String username) {

		return booking_repository.getBookingsByUser(username);

	}
	

	@Transactional
	public BookingAgent update(BookingAgent booking_agent) {

		BookingAgent booking_agent_to_update = booking_agent_repository.findById(booking_agent.getBooking_id()).get();
		booking_agent_to_update.setAgent_id(booking_agent.getAgent_id());
		return booking_agent_to_update;

	}

	@Transactional
	public BookingUser update(BookingUser booking_user) {

		BookingUser booking_user_to_update = booking_user_repository.findById(booking_user.getBooking_id()).get();
		booking_user_to_update.setUser_id(booking_user.getUser_id());
		return booking_user_to_update;

	}

	@Transactional
	public BookingGuest update(BookingGuest booking_guest) {

		BookingGuest booking_guest_to_update = booking_guest_repository.findById(booking_guest.getBooking_id()).get();
		booking_guest_to_update.setContact_email(booking_guest.getContact_email());
		booking_guest_to_update.setContact_phone(booking_guest.getContact_phone());
		return booking_guest_to_update;

	}

	public void deleteBookingAgent(Integer booking_id) {

		booking_agent_repository.deleteById(booking_id);

	}

	public void deleteBookingUser(Integer booking_id) {

		booking_user_repository.deleteById(booking_id);

	}

	public void deleteBookingGuest(Integer booking_id) {

		booking_guest_repository.deleteById(booking_id);

	}

}
