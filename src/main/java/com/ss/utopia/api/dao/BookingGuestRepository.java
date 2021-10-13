package com.ss.utopia.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.utopia.api.pojo.BookingGuest;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, Integer> {

}
