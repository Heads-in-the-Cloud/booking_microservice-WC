package com.ss.utopia.api.pojo;

import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.sun.istack.NotNull;

@Entity
@Table(name = "booking")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Boolean is_active;
	private String confirmation_code;

	public Booking(Boolean is_active, String confirmation_code) {
		super();
		this.is_active = is_active;
		this.confirmation_code = confirmation_code;
	}

	public Booking() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public String getConfirmation_code() {
		return confirmation_code;
	}

	public void setConfirmation_code(String confirmation_code) {
		this.confirmation_code = confirmation_code;
	}

	@Override
	public String toString() {
		return "Booking [id=" + id + ", is_active=" + is_active + ", confirmation_code=" + confirmation_code + "]";
	}

	@OneToMany(targetEntity = Passenger.class, cascade = CascadeType.ALL, mappedBy = "booking_id", fetch = FetchType.EAGER)
	List<Passenger> passengers;

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	public BookingAgent getBooking_agent() {
		return booking_agent;
	}

	public void setBooking_agent(BookingAgent booking_agent) {
		this.booking_agent = booking_agent;
	}

	public BookingUser getBooking_user() {
		return booking_user;
	}

	public void setBooking_user(BookingUser booking_user) {
		this.booking_user = booking_user;
	}

	public BookingPayment getBooking_payment() {
		return booking_payment;
	}

	public BookingGuest getBooking_guest() {
		return booking_guest;
	}

	public void setBooking_guest(BookingGuest booking_guest) {
		this.booking_guest = booking_guest;
	}

	public void setBooking_payment(BookingPayment booking_payment) {
		this.booking_payment = booking_payment;
	}

	public FlightBookings getFlight_bookings() {
		return flight_bookings;
	}

	public void setFlight_bookings(FlightBookings flight_bookings) {
		this.flight_bookings = flight_bookings;
	}

	@OneToOne(targetEntity = BookingPayment.class, cascade=CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "booking_id")
	BookingPayment booking_payment;

	@OneToOne(targetEntity = FlightBookings.class, cascade=CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "booking_id")
	FlightBookings flight_bookings;

	@OneToOne(targetEntity = BookingGuest.class, cascade=CascadeType.PERSIST,  fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "booking_id", nullable = true)
	BookingGuest booking_guest;

	@OneToOne(targetEntity = BookingAgent.class, cascade=CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "booking_id", nullable = true)
	BookingAgent booking_agent;

	@OneToOne(targetEntity = BookingUser.class, cascade=CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "booking_id", nullable = true)
	BookingUser booking_user;

}
