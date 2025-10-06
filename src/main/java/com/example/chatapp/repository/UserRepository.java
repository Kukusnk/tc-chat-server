package com.example.chatapp.repository;

import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM Room r JOIN r.members u WHERE r.id = :roomId")
    List<User> findMembersByRoomId(@Param("roomId") Long roomId);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.username = :username")
    void updateEmailByUsername(@Param("username") String username, @Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.username = :new_username WHERE u.username = :username")
    void updateUsernameByUsername(@Param("username") String username, @Param("new_username") String new_username);

    /**
     * Find a user by email     *
     *
     * @param email user email
     * @return Optional<User> - user or empty Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by username
     *
     * @param username username
     * @return Optional<User> - user or empty Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Check user existence by email
     *
     * @param email user's email
     * @return true if user exists, false if not
     */
    boolean existsByEmail(String email);

    /**
     * Check user existence by username
     *
     * @param username username
     * @return true if user exists, false if not
     */
    boolean existsByUsername(String username);


}
