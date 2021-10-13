package com.ss.utopia.api.dao;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.User;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByUsername(String userName);

	@Query(value="SELECT u FROM User u LEFT JOIN BookingAgent ba ON ba.agent_id = u.id LEFT JOIN BookingUser bu ON bu.user_id = u.id WHERE bu.booking_id = :booking_id OR ba.booking_id = :booking_id")
	public Optional<User> findUserByBookingId(@Param("booking_id") Integer booking_id);
	
	@Query(value="SELECT u FROM User u WHERE u.id = :user_id")
	public Optional<User> findById(@Param("user_id") Integer user_id);
	
	
	@Query(value="SELECT u FROM User u WHERE u.email = :email")
	public Optional<User> findByEmail(@Param("email") String email);
	
	@Query(value="SELECT u FROM User u WHERE u.phone = :phone")
	public Optional<User> findByPhone(@Param("phone") String phone);

	void deleteById(Integer user_id);
	
	Boolean existsById(Integer user_id);

}
