package com.example.chatapp.service;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.room.CreateRoomRequest;
import com.example.chatapp.model.dto.room.CreateRoomResponse;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldCreateRoom() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");

//        User user = new User(1L, "testUser", );
//        when(userRepository.findByUsername("testUser"))
//                .thenReturn(Optional.of(user));

        Room saved = Room.builder().id(10L).name("TestRoom").build();
        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setName("TestRoom");
        request.setMemberLimit(50L);

        CreateRoomResponse response = roomService.createRoom(request, auth);

        assertEquals("TestRoom", response.getName());
        verify(roomRepository).save(any(Room.class));
    }
}