package com.ss.utopia.api.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ss.utopia.api.dao.BookingRepository;
import com.ss.utopia.api.dao.PassengerRepository;
import com.ss.utopia.api.pojo.Booking;
import com.ss.utopia.api.pojo.Passenger;

@Service
public class PassengerBookingService {

	@Autowired
	PassengerRepository passenger_repository;

	@Autowired
	BookingRepository booking_repository;

	public Passenger save(Passenger passenger) {
		return passenger_repository.save(passenger);
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
	

	public void deletePassengerById(Integer passenger_id) {

		// delete booking if the last passenger is deleted
		Passenger passenger = passenger_repository.findById(passenger_id).get();

		passenger_repository.deleteById(passenger_id);

		List<Passenger> passenger_list =  booking_repository.findById(passenger.getBooking_id()).get().getPassengers();
		
		if ( passenger_list == null || passenger_list.size() == 0) {
			
			booking_repository.deleteById(passenger.getBooking_id());

		}
	}

}
