package com.ss.utopia.api.pojo;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="passenger")
public class Passenger {

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Passenger other = (Passenger) obj;
		return Objects.equals(id, other.id);
	}
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer booking_id;
	private String given_name;
	private String family_name;
	private LocalDate dob;
	private String gender;
	private String address;
	public Integer getId() {
		return id;
	}
	
	public Passenger() {
		
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Passenger(Integer id, Integer booking_id, String given_name, String family_name, LocalDate dob,
			String gender, String address) {
		super();
		this.id = id;
		this.booking_id = booking_id;
		this.given_name = given_name;
		this.family_name = family_name;
		this.dob = dob;
		this.gender = gender;
		this.address = address;
	}

	public String getGiven_name() {
		return given_name;
	}
	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}
	public String getFamily_name() {
		return family_name;
	}
	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Integer getBooking_id() {
		return booking_id;
	}
	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}
	@Override
	public String toString() {
		return "Passenger [id=" + id + ", booking_id=" + booking_id + ", given_name=" + given_name + ", family_name="
				+ family_name + ", dob=" + dob + ", gender=" + gender + ", address=" + address + "]";
	}
	
	
	
	
}
