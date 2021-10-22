package com.ss.utopia.api.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ss.utopia.api.dao.BookingRepository;
import com.ss.utopia.api.dao.FlightRepository;
import com.ss.utopia.api.dao.PassengerRepository;
import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.Passenger;

@Service
public class PassengerBookingService {

	@Autowired
	PassengerRepository passenger_repository;

	@Autowired
	BookingRepository booking_repository;

	@Autowired
	FlightRepository flight_repository;

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public Passenger save(Passenger passenger) {

		passenger.setId(null); // prevent unintentional update to existing passenger

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		flight_repository.updateReservedSeats(passenger.getBooking_id(), 1);

		Passenger new_passenger = passenger_repository.save(passenger);

		tx.commit();
		session.close();
		return new_passenger;
	}

	public List<Passenger> findAllPassengers() {
		return passenger_repository.findAll();
	}

	public Passenger getPassengerById(Integer passenger_id) {

		return passenger_repository.findById(passenger_id).get();

	}

	public List<Passenger> getPassengerByBooking(List<Booking> bookings) {

		return bookings.stream().map(x -> passenger_repository.getPassengerByBookingId(x.getId())).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	@Transactional
	public Passenger update(Passenger passenger) {

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

		return passenger_to_save;

	}

	@Transactional
	public void deletePassengerById(Integer passenger_id) {


		
		// delete booking if the last passenger is deleted
		Passenger passenger = passenger_repository.findById(passenger_id).get();

		
		Integer booking_id = passenger.getBooking_id();		


			passenger_repository.deleteById(passenger.getId());

	
		flight_repository.updateReservedSeats(booking_id, -1);


				
	}

}
