package com.ss.utopia.api.pojo;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "booking_agent")
public class BookingAgent {

	@Id
	private Integer booking_id;

	private Integer agent_id;

	public Integer getBooking_id() {
		return booking_id;
	}

	public BookingAgent(Integer booking_id, Integer agent_id) {
		super();
		this.booking_id = booking_id;
		this.agent_id = agent_id;
	}

	public BookingAgent() {

	}


	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}

	public Integer getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Integer agent_id) {
		this.agent_id = agent_id;
	}

	@Override
	public String toString() {
		return "BookingAgent [booking_id=" + booking_id + ", agent_id=" + agent_id + "]";
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
		BookingAgent other = (BookingAgent) obj;
		return Objects.equals(booking_id, other.booking_id);
	}


}
