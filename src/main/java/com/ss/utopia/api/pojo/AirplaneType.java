package com.ss.utopia.api.pojo;

import java.util.List;



import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;


@Entity
@Table(name = "airplane_type")
public class AirplaneType {


	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private Integer max_capacity;

	@Override
	public String toString() {
		return "AirplaneType [id=" + id + ", max_capacity=" + max_capacity + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMax_capacity() {
		return max_capacity;
	}

	public void setMax_capacity(Integer max_capacity) {
		this.max_capacity = max_capacity;
	}




	

}
