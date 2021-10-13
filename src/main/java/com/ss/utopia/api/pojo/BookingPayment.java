package com.ss.utopia.api.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="booking_payment")
public class BookingPayment {
	
	@Id
	Integer booking_id;
	String stripe_id;
	Boolean refunded;
	public Integer getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}
	public String getStripe_id() {
		return stripe_id;
	}
	public void setStripe_id(String stripe_id) {
		this.stripe_id = stripe_id;
	}
	public Boolean getRefunded() {
		return refunded;
	}
	public void setRefunded(Boolean refunded) {
		this.refunded = refunded;
	}
	@Override
	public String toString() {
		return "BookingPayment [booking_id=" + booking_id + ", stripe_id=" + stripe_id + ", refunded=" + refunded + "]";
	}
	
	
	

}
