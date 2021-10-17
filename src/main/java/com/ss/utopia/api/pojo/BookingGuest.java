package com.ss.utopia.api.pojo;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="booking_guest")
public class BookingGuest{
	
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
		BookingGuest other = (BookingGuest) obj;
		return Objects.equals(booking_id, other.booking_id);
	}

	@Id
	private Integer booking_id;
	private String contact_email;
	private String contact_phone;
	

	

	@Override
	public String toString() {
		return "BookingGuest [booking_id=" + booking_id + ", contact_email=" + contact_email + ", contact_phone="
				+ contact_phone + "]";
	}

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}

	public Integer getBooking_id() {
		return booking_id;
	}

	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}

	




	
}
