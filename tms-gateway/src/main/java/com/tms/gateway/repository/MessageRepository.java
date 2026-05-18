package com.tms.gateway.repository;

import com.tms.gateway.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId AND m.receiverId = :otherId) OR " +
           "(m.senderId = :otherId AND m.receiverId = :userId) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("otherId") Long otherId);

    @Query("SELECT m FROM Message m WHERE m.receiverId = :userId AND m.read = false")
    List<Message> findUnreadByReceiver(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.read = false")
    long countUnreadByReceiver(@Param("userId") Long userId);

    @Query("SELECT DISTINCT CASE WHEN m.senderId = :userId THEN m.receiverId ELSE m.senderId END " +
           "FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId")
    List<Long> findConversationPartners(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) AND " +
           "m.sentAt = (SELECT MAX(m2.sentAt) FROM Message m2 WHERE " +
           "(m2.senderId = :userId AND m2.receiverId = m.receiverId) OR " +
           "(m2.receiverId = :userId AND m2.senderId = m.senderId)) " +
           "ORDER BY m.sentAt DESC")
    List<Message> findLastMessages(@Param("userId") Long userId);
}
