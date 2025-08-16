package com.example.chatapp.repository;

import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

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

    /**
     * Optional: find user by email (ignoring case)
     *
     * @param email user's email
     * @return Optional<User>
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Optional: find user by username (ignoring case)
     *
     * @param username username
     * @return Optional<User>
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Optional: check existence by email (ignoring case)
     *
     * @param email user email
     * @return boolean
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Optional: check existence by username (ignoring case)
     *
     * @param username username
     * @return boolean
     */
    boolean existsByUsernameIgnoreCase(String username);
}
