package com.example.chatapp.repository;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    int countByOwner(User owner);

    boolean existsByName(String name);
}
