package com.ss.utopia.api.exception;

import java.sql.SQLException;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

import org.hibernate.id.IdentifierGenerationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(value = { SQLException.class })
	public ResponseEntity<Object> handleSQLException(SQLException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = ex.getMessage();

		switch (ex.getErrorCode()) {

//		case 0:
//			message = "Entity with Id already exists"; //trying to save with existing id
//			break;
		case 1046:
			message = "Invalid field: data truncation";
			break;
		case 1048:
			message = "Missing one or more fields";
			break;
		case 1062:
			message = "Duplicate entry for a field that must be uniqe"; //ex. duplicate email
			break;
		case 1406:
			message = "Invalid field: data truncation"; //ex. data too long for column iata_id
			break;
		case 1452:
			message = "Missing foreign key value in one or more fields"; //ex. route to nonexisting airport
			break;
		case 1644:
			message = "Invalid field: data conflicts with another row"; //ex. route: LAX-LAX
			break;

		}

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}

	@ExceptionHandler(value = { NoSuchElementException.class })
	public ResponseEntity<Object> handleInvalidIdException(NoSuchElementException ex) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		String message = "No entity with this Id found";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}

	@ExceptionHandler(value = { InvalidFormatException.class })
	public ResponseEntity<Object> handleInvalidDataException(InvalidFormatException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Wrong type or format for field";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}
	
	@ExceptionHandler(value = { EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleInvalidDeleteId(EmptyResultDataAccessException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "No Entity to delete";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}
	
	@ExceptionHandler(value = { MissingPathVariableException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Missing path variable";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}
	
	
	@ExceptionHandler(value = { IllegalArgumentException.class})
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Given Id must not be null";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}
	
	@ExceptionHandler(value = { IdentifierGenerationException.class })
	public ResponseEntity<Object> handleInvalidMapping(IdentifierGenerationException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Missing one or more fields";

		ApiException apiException = new ApiException(message, status, ZonedDateTime.now(ZoneId.of("Z")));

		return new ResponseEntity<Object>(apiException, status);

	}
//	@ExceptionHandler(value = { DataIntegrityViolationException.class })
//	public ResponseEntity<Object> handleMissingDataException(DataIntegrityViolationException ex) {
//
//
//		HttpStatus status = HttpStatus.BAD_REQUEST;
//		String message = "Missing or incorrect fields";
//		
//		ApiException apiException = new ApiException(message, 
//				status,
//				ZonedDateTime.now(ZoneId.of("Z")));
//		
//		
//		return new ResponseEntity<Object>(apiException, status);
//
//	}

}
