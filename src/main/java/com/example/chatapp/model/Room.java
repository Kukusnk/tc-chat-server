package com.example.chatapp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "room", indexes = {
        @Index(name = "idx_room_name", columnList = "name"),
        @Index(name = "idx_room_description", columnList = "description")
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 5, max = 128)
    @Schema(description = "Room name")
    private String name;
    @Size(max = 1000)
    private String description;
    @ManyToMany
    @NotNull
    private List<Topic> topics;

    @Enumerated(EnumType.STRING)
    private RoomType type; //SYSTEM, DEFAULT

    @OneToMany(mappedBy = "room")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @ManyToOne
    private User owner;
    @NotNull
    @Max(100)
    private Long memberLimit;
    @ManyToMany
    private List<User> members;
    private LocalDateTime createdAt;
    private LocalDateTime deleteAfter;

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }
}
