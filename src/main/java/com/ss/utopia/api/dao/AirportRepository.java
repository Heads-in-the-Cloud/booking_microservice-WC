package com.ss.utopia.api.dao;

import java.util.List;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.utopia.api.pojo.Airport;



public interface AirportRepository extends JpaRepository<Airport, String> {

	
	@Query(value="SELECT p FROM Airport p where iata_id = :airport_code")
	public Optional<Airport> getAirportById(@Param("airport_code") String airport_code);
//	@Query(value="SELECT * FROM airport a JOIN route r WHERE ?1 = r.origin_id", nativeQuery=true)
//	public List<Airport> findAllRoutes(String airportCode);
	
	@Transactional
	@Modifying
	@Query(value="DELETE FROM Airport where iata_id = :airport_code")
	public void deleteAirportById(@Param("airport_code") String airport_code);
	
	
}
