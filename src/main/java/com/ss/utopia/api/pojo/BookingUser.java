package com.ss.utopia.api.pojo;

import java.util.Objects;

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
@Table(name = "booking_user")
public class BookingUser {

	@Id
	private Integer booking_id;

	private Integer user_id;

	public BookingUser(Integer booking_id, Integer user_id) {
		super();
		this.booking_id = booking_id;
		this.user_id = user_id;
	}

	public BookingUser() {

	}

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
		BookingUser other = (BookingUser) obj;
		return Objects.equals(booking_id, other.booking_id);
	}


}
