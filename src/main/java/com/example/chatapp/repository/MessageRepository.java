package com.example.chatapp.repository;

import com.example.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoom_Id(Long roomId);

    List<Message> findTop10ByRoomIdOrderByTimestampDesc(Long roomId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.room.id IN :roomIds")
    void deleteByRoomIdIn(@Param("roomIds") List<Long> roomIds);

    void deleteByRoomId(Long id);
    //todo
}
