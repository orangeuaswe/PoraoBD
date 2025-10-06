package com.bd.porao.repository;
import com.bd.porao.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long>
{
    @Query("SELECT c FROM Conversation c WHERE c.student.id = :uid OR c.tutor.id = :uid")
    List<Conversation> findForUser(@Param("uid") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.student.id = :sid AND c.tutor.id = :tid")
    Optional<Conversation> findBetween(@Param("sid") Long studentId, @Param("tid") Long tutorId);
}
