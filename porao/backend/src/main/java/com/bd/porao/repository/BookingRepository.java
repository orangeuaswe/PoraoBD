package com.bd.porao.repository;
import com.bd.porao.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>
{
    List<Booking> findByStudentId(Long studentId);
    List<Booking> findByTutorId(Long tutorId);
    Optional<Booking> findByPaymentRef(String paymentRef);
}
