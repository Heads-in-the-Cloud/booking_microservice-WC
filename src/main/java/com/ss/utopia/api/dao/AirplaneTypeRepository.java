package com.ss.utopia.api.dao;

import java.util.Optional;




import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ss.utopia.api.pojo.AirplaneType;


public interface AirplaneTypeRepository extends JpaRepository<AirplaneType, Integer>{

	//Optional<AirplaneType> findById(Integer airplane_type_id);

	@Modifying
	@Query(value="DELETE FROM airplane_type WHERE id=?1", nativeQuery=true)
	public void testdelete(Integer type);
	
}
