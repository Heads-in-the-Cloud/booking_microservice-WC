package com.ss.utopia.api.pojo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="booking_user")
public class BookingUser{

	@Id
	private Integer booking_id;
	
	private Integer user_id;
	
	@Override
	public String toString() {
		return "BookingUser [booking_id=" + booking_id + ", user_id=" + user_id + "]";
	}
	public Integer getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="booking_id", referencedColumnName = "id")
//	Booking booking;
//
//	@Override
//	public Booking getBooking() {
//		return booking;
//	}
//	@Override
//	public void setBooking(Booking booking) {
//		this.booking = booking;
//	}
	
}
