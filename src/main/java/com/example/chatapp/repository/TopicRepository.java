package com.example.chatapp.repository;

import com.example.chatapp.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByNameContainingIgnoreCase(String name);

    Optional<Topic> findByName(String name);
}
