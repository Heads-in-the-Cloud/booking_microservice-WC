package com.ss.utopia.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ss.utopia.api.pojo.Route;

public interface RouteRepository extends JpaRepository<Route, Integer> {

//	@Query(value="SELECT * FROM route WHERE origin_id = ?1", nativeQuery=true)
//	List<Route> findAllDestinationRoutes(String airport_code);
//	
//	@Query(value="SELECT * FROM route WHERE destination_id = ?1", nativeQuery=true)
//	List<Route> findAllOriginRoutes(String airport_code);
}
