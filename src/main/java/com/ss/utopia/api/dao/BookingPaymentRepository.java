package com.ss.utopia.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.utopia.api.pojo.BookingPayment;

public interface BookingPaymentRepository extends JpaRepository<BookingPayment, Integer> {

}
