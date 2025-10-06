package com.bd.porao.repository;

import com.bd.porao.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>
{

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :cid ORDER BY m.sentAt ASC")
    List<Message> findByConversation(@Param("cid") Long conversationId);

}
