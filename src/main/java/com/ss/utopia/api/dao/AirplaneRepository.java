package com.ss.utopia.api.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ss.utopia.api.pojo.Airplane;


public interface AirplaneRepository extends JpaRepository<Airplane, Integer>{

	@Query(value ="SELECT * FROM airplane a WHERE a.type_id = ?1", nativeQuery = true)	
	List<Airplane> findByType(Integer type_id);
		
		Optional<Airplane> findById(Integer airplane_id);
		
		Airplane save(Airplane airplane);

		boolean existsById(Integer airplane_id);
}
