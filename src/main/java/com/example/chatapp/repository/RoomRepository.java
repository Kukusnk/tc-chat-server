package com.example.chatapp.repository;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    int countByOwner(User owner);

    @Modifying
    @Query("UPDATE Room r SET r.owner = :user, r.deleteAfter = null WHERE r.id = :roomId AND r.owner IS NULL")
    int claimOwnership(@Param("roomId") Long roomId, @Param("user") User user);

    @Query("select case when count(r) > 0 then true else false end " +
            "from Room r join r.members m " +
            "where r.id = :roomId and m.username = :username")
    boolean isMember(@Param("roomId") Long roomId, @Param("username") String username);

    @Query("select r from Room r join fetch r.owner where r.id = :id")
    Optional<Room> findByIdWithOwner(@Param("id") Long id);

    @Query("""
            SELECT r FROM Room r
            WHERE size(r.members) < r.memberLimit
            """)
    Page<Room> findAvailableRooms(Pageable pageable);

    List<Room> findAllByDeleteAfterBefore(LocalDateTime now);
}
