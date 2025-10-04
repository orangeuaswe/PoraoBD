package com.bd.porao.repository;

import com.bd.porao.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long>
{

    @Query("SELECT a FROM AvailabilitySlot a WHERE a.tutor.id = :tutorId " +
            "AND a.startUtc >= :start AND a.endUtc <= :end")
    List<AvailabilitySlot> findByTutorAndBetween(@Param("tutorId") Long tutorId,
                                                 @Param("start") Instant start,
                                                 @Param("end") Instant end);
}
