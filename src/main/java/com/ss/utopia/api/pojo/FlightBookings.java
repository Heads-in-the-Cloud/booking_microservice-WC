package com.ss.utopia.api.pojo;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="flight_bookings")
public class FlightBookings {
	
	@Override
	public int hashCode() {
		return Objects.hash(booking_id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlightBookings other = (FlightBookings) obj;
		return Objects.equals(booking_id, other.booking_id);
	}
	
	public FlightBookings(Integer booking_id, Integer flight_id) {
		super();
		this.booking_id = booking_id;
		this.flight_id = flight_id;
	}
	public FlightBookings() {
		
	}

	@Id
	private Integer booking_id;
	private Integer flight_id;
	public Integer getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}
	public Integer getFlight_id() {
		return flight_id;
	}
	public void setFlight_id(Integer flight_id) {
		this.flight_id = flight_id;
	}
	@Override
	public String toString() {
		return "FlightBookings [booking_id=" + booking_id + ", flight_id=" + flight_id + "]";
	}
	

}
