package com.ss.utopia.api.pojo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="booking_agent")
public class BookingAgent{
	
	@Id
	private Integer booking_id;
	
	private Integer agent_id;
	public Integer getBooking_id() {
		return booking_id;
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
	
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="booking_id", referencedColumnName="id")
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
