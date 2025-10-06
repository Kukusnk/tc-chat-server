package com.example.chatapp.repository;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    int countByOwner(User owner);

    @Query("select r from Room r join fetch r.owner where r.id = :id")
    Optional<Room> findByIdWithOwner(@Param("id") Long id);


    boolean existsByName(String name);
}
