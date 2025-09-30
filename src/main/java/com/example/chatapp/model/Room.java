package com.example.chatapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(unique = true)
    @Size(min = 5, max = 128)
    private String name;
    private String description;
    @ManyToMany
    private List<Topic> topics;

    @Enumerated(EnumType.STRING)
    private RoomType type; //SYSTEM, DEFAULT

    @OneToMany(mappedBy = "room")
    private List<Message> messages;

    private Long ownerId;
    @NotNull
    private Long memberLimit;
    private List<Long> membersId;
}
