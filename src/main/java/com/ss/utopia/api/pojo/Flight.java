package com.ss.utopia.api.pojo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="flight")
public class Flight {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Integer id;
	Integer route_id;
	Integer airplane_id;
	LocalDateTime departure_time;
	Integer reserved_seats;
	Float seat_price;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRoute_id() {
		return route_id;
	}
	public void setRoute_id(Integer route_id) {
		this.route_id = route_id;
	}
	public Integer getAirplane_id() {
		return airplane_id;
	}
	public void setAirplane_id(Integer airplane_id) {
		this.airplane_id = airplane_id;
	}
	public LocalDateTime getDeparture_time() {
		return departure_time;
	}
	public void setDeparture_time(LocalDateTime departure_time) {
		this.departure_time = departure_time;
	}
	public Integer getReserved_seats() {
		return reserved_seats;
	}
	public void setReserved_seats(Integer reserved_seats) {
		this.reserved_seats = reserved_seats;
	}
	public Float getSeat_price() {
		return seat_price;
	}
	public void setSeat_price(Float seat_price) {
		this.seat_price = seat_price;
	}
	@Override
	public String toString() {
		return "Flight [id=" + id + ", route_id=" + route_id + ", airplane_id=" + airplane_id + ", departure_time="
				+ departure_time + ", reserved_seats=" + reserved_seats + ", seat_price=" + seat_price + "]";
	}
	


}
