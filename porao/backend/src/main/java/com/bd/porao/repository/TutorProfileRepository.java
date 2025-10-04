package com.bd.porao.repository;

import com.bd.porao.model.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long>
{
    @Query("SELECT t FROM TutorProfile t WHERE " +
            "(:q IS NULL OR LOWER(t.subject) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(t.user.name) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<TutorProfile> search(@Param("q") String q);

    @Query(value = """
        SELECT t.*, 
               (6371 * acos(
                 cos(radians(:lat)) * cos(radians(t.lat)) * 
                 cos(radians(t.lng) - radians(:lng)) +
                 sin(radians(:lat)) * sin(radians(t.lat))
               )) AS distance_km
        FROM tutor_profiles t
        WHERE t.lat IS NOT NULL AND t.lng IS NOT NULL
        HAVING distance_km <= :radiusKm
        ORDER BY distance_km ASC
        """, nativeQuery = true)
    List<Object[]> findNear(@Param("lat") double lat,
                            @Param("lng") double lng,
                            @Param("radiusKm") double radiusKm);
}
