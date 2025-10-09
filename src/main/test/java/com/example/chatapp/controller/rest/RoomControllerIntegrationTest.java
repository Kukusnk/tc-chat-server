package com.example.chatapp.controller.rest;


import com.example.chatapp.handler.exception.RoleNotFoundException;
import com.example.chatapp.model.*;
import com.example.chatapp.model.dto.room.CreateRoomRequest;
import com.example.chatapp.model.dto.room.CreateRoomResponse;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.TopicRepository;
import com.example.chatapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
//@Sql(scripts = "/data/cleanUp.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RoomControllerIntegrationTest {

    private final static String CHARACTER_A = "A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User anotherUser;
    private List<Topic> randomTopics;

    @BeforeEach
    void setUp() {
        initializeRoles();

        cleanTopics();
        initializeTopics();

        //roomRepository.deleteAll();
        //userRepository.deleteAll();

        randomTopics = getTopics();
        testUser = createUser("testuser", "test@example.com");
        anotherUser = createUser("anotheruser", "another@example.com");
    }

    private void initializeRoles() {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }
    }

    @Transactional
    public void initializeTopics() {
        List<String> topicNames = List.of(
                "Arts & Culture",
                "Technology & Gadgets",
                "Gaming",
                "Travel",
                "Food",
                "Sports",
                "Hobbies & DIY",
                "Lifestyle & Wellbeing",
                "Science",
                "Education & Careers",
                "Finance"
        );

        Set<String> existing = topicRepository.findAll()
                .stream()
                .map(Topic::getName)
                .collect(Collectors.toSet());

        List<Topic> newTopics = topicNames.stream()
                .filter(name -> !existing.contains(name))
                .map(name -> Topic.builder().name(name).build())
                .toList();

        if (!newTopics.isEmpty()) {
            topicRepository.saveAll(newTopics);
        }
    }

    @Transactional
    public void cleanTopics() {
        topicRepository.deleteAll();
    }

    private User createUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("password"))
                .createdAt(LocalDate.now())
                .isEmailVerified(true)
                .roles(Set.of(roleRepository.findByName("USER")
                        .orElseThrow(() -> new RoleNotFoundException("Role USER not found"))))
                .build();
        return userRepository.save(user);
    }

    private List<Topic> getTopics() {
        List<Topic> allTopics = topicRepository.findAll();
        Collections.shuffle(allTopics);
        return allTopics.subList(0, ThreadLocalRandom.current().nextInt(1, allTopics.size()));
    }

    // ==================== CREATE ROOM TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should successfully create a room with valid data")
    void shouldCreateRoomSuccessfully() throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .name("Test Room")
                .description("Test Description")
                .memberLimit(50L)
                .topics(randomTopics)
                .build();

        MvcResult result = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andReturn();

        CreateRoomResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CreateRoomResponse.class
        );

        assertThat(response.getName()).isEqualTo("Test Room");
        assertThat(roomRepository.findById(response.getId())).isPresent();
    }

    @ParameterizedTest
    @MethodSource("invalidRoomNameProvider")
    @WithMockUser(username = "testuser")
    @DisplayName("Should return 400 for invalid room names")
    void shouldReturnBadRequestForInvalidRoomNames(String name, String description) throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .name(name)
                .description(description)
                .memberLimit(50L)
                .topics(randomTopics)
                .build();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidRoomNameProvider() {
        return Stream.of(
                Arguments.of("Test", "Name too short (less than 5 characters)"),
                Arguments.of("A".repeat(129), "Name too long (more than 128 characters)"),
                Arguments.of("", "Empty name"),
                Arguments.of(null, "Null name")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptionProvider")
    @WithMockUser(username = "testuser")
    @DisplayName("Should return 400 for invalid description")
    void shouldReturnBadRequestForInvalidDescription(String description) throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .name("Valid Room Name")
                .description(description)
                .memberLimit(50L)
                .topics(randomTopics)
                .build();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> invalidDescriptionProvider() {
        return Stream.of(
                Arguments.of("A".repeat(1001))
        );
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return 403 when user has 5 or more rooms")
    void shouldReturnForbiddenWhenUserHasMaxRooms() throws Exception {
        for (int i = 1; i <= 5; i++) {
            Room room = new Room();
            room.setName("Room " + i);
            room.setDescription("Description " + i);
            room.setOwner(testUser);
            room.setTopics(randomTopics);
            room.setCreatedAt(LocalDateTime.now());
            room.setType(RoomType.DEFAULT_ROOM);
            room.setDeleteAfter(null);
            room.setMemberLimit(100L);
            roomRepository.save(room);
        }

        CreateRoomRequest request = CreateRoomRequest.builder()
                .name("Room 6")
                .description("Should fail")
                .memberLimit(50L)
                .topics(List.of(randomTopics.get(0)))
                .build();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource({
            "0, Should fail for 0 rooms owned",
            "1, Should succeed for 1 room owned",
            "2, Should succeed for 2 rooms owned",
            "3, Should succeed for 3 rooms owned",
            "4, Should succeed for 4 rooms owned"
    })
    @WithMockUser(username = "testuser")
    @DisplayName("Should validate room ownership limit correctly")
    void shouldValidateRoomOwnershipLimit(int existingRooms, String description) throws Exception {
        for (int i = 1; i <= existingRooms; i++) {
            Room room = new Room();
            room.setName("Existing Room " + i);
            room.setDescription("Description " + i);
            room.setOwner(testUser);
            room.setTopics(randomTopics);
            room.setCreatedAt(LocalDateTime.now());
            room.setType(RoomType.DEFAULT_ROOM);
            room.setDeleteAfter(null);
            room.setMemberLimit(100L);
            roomRepository.save(room);
        }

        CreateRoomRequest request = CreateRoomRequest.builder()
                .name("New Room")
                .description(description)
                .memberLimit(50L)
                .topics(randomTopics)
                .build();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 when user is not authenticated")
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .name("Test Room")
                .description("Description")
                .memberLimit(50L)
                .topics(randomTopics)
                .build();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET ALL ROOMS TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return paginated list of rooms")
    void shouldReturnPaginatedRoomsList() throws Exception {
        for (int i = 1; i <= 15; i++) {
            Room room = new Room();
            room.setName("Room " + i);
            room.setDescription("Description " + i);
            room.setOwner(testUser);
            room.setTopics(randomTopics);
            room.setCreatedAt(LocalDateTime.now());
            room.setType(RoomType.DEFAULT_ROOM);
            room.setDeleteAfter(null);
            room.setMembers(List.of(testUser));
            room.setMemberLimit(100L);
            roomRepository.save(room);
        }

        mockMvc.perform(get("/api/rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return empty page when no rooms exist")
    void shouldReturnEmptyPageWhenNoRooms() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== GET ROOM BY ID TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return room details by ID")
    void shouldReturnRoomDetailsById() throws Exception {
        Room room = new Room();
        room.setName("Test Room");
        room.setDescription("Test Description");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(List.of(testUser));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/" + room.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.memberCount").value("1"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return 404 when room not found")
    void shouldReturnNotFoundWhenRoomDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/rooms/99999"))
                .andExpect(status().isNotFound());
    }

    // ==================== SEARCH ROOMS TESTS ====================

    @ParameterizedTest
    @CsvSource({
            "Java, 3",
            "Python, 1",
            "Nonexistent, 0"
    })
    @WithMockUser(username = "testuser")
    @DisplayName("Should search rooms by keyword")
    void shouldSearchRoomsByKeyword(String keyword, int expectedCount) throws Exception {
        Room room1 = new Room();
        room1.setName("Java Programming");
        room1.setDescription("Learn Java");
        room1.setOwner(testUser);
        room1.setTopics(randomTopics);
        room1.setCreatedAt(LocalDateTime.now());
        room1.setType(RoomType.DEFAULT_ROOM);
        room1.setDeleteAfter(null);
        room1.setMembers(List.of(testUser));
        room1.setMemberLimit(100L);
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setName("Advanced Java");
        room2.setDescription("Advanced topics");
        room2.setOwner(testUser);
        room2.setTopics(randomTopics);
        room2.setCreatedAt(LocalDateTime.now());
        room2.setType(RoomType.DEFAULT_ROOM);
        room2.setDeleteAfter(null);
        room2.setMembers(List.of(testUser));
        room2.setMemberLimit(100L);
        roomRepository.save(room2);

        Topic javaTopic = Topic.builder().name("Java").build();
        topicRepository.save(javaTopic);

        Room room3 = new Room();
        room3.setName("Python Programming");
        room3.setDescription("Learn Python");
        room3.setOwner(testUser);
        room3.setTopics(List.of(javaTopic));
        room3.setCreatedAt(LocalDateTime.now());
        room3.setType(RoomType.DEFAULT_ROOM);
        room3.setDeleteAfter(null);
        room3.setMembers(List.of(testUser));
        room3.setMemberLimit(100L);
        roomRepository.save(room3);

        mockMvc.perform(get("/api/rooms/search")
                        .param("search", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(expectedCount));
    }

    // ==================== DELETE ROOM TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should delete room by owner")
    void shouldDeleteRoomByOwner() throws Exception {
        Room room = new Room();
        room.setName("Room to Delete");
        room.setDescription("Will be deleted");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(List.of(testUser));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(delete("/api/rooms/" + room.getId()))
                .andExpect(status().isNoContent());

        assertThat(roomRepository.findById(room.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should return 403 when non-owner tries to delete room")
    void shouldReturnForbiddenWhenNonOwnerTriesToDelete() throws Exception {
        Room room = new Room();
        room.setName("Protected Room");
        room.setDescription("Cannot be deleted by non-owner");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(List.of(testUser));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(delete("/api/rooms/" + room.getId()))
                .andExpect(status().isForbidden());
    }

    // ==================== JOIN ROOM TESTS ====================

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should join room successfully")
    void shouldJoinRoomSuccessfully() throws Exception {
        Room room = new Room();
        room.setName("Join Test Room");
        room.setDescription("Join here");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(post("/api/rooms/" + room.getId() + "/join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Join Test Room"));
    }

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should return 400 when room is full")
    void shouldReturnBadRequestWhenRoomIsFull() throws Exception {
        User seconduser = createUser("seconduser", "second@gmail.com");

        Room room = new Room();
        room.setName("Full Room");
        room.setDescription("No space");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser, seconduser)));
        room.setMemberLimit(2L);
        room = roomRepository.save(room);

        mockMvc.perform(post("/api/rooms/" + room.getId() + "/join"))
                .andExpect(status().isConflict());
    }

    // ==================== LEAVE ROOM TESTS ====================

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should leave room successfully")
    void shouldLeaveRoomSuccessfully() throws Exception {
        Room room = new Room();
        room.setName("Leave Test Room");
        room.setDescription("Leave here");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        // Сначала присоединяемся
        mockMvc.perform(post("/api/rooms/" + room.getId() + "/join"))
                .andExpect(status().isOk());

        // Затем выходим
        mockMvc.perform(post("/api/rooms/" + room.getId() + "/leave"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should return 400 when user not in room")
    void shouldReturnBadRequestWhenUserNotInRoom() throws Exception {
        Room room = new Room();
        room.setName("Not Joined Room");
        room.setDescription("Cannot leave");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(post("/api/rooms/" + room.getId() + "/leave"))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET MEMBERS TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Should return list of room members")
    void shouldReturnRoomMembers() throws Exception {
        Room room = new Room();
        room.setName("Members Test Room");
        room.setDescription("Has members");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/" + room.getId() + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should return 403 when non-member tries to get members")
    void shouldReturnForbiddenWhenNonMemberTriesToGetMembers() throws Exception {
        Room room = new Room();
        room.setName("Private Room");
        room.setDescription("Members only");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/" + room.getId() + "/members"))
                .andExpect(status().isForbidden());
    }

    // ==================== BECOME OWNER TESTS ====================

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should become owner successfully")
    void shouldBecomeOwnerSuccessfully() throws Exception {
        Room room = new Room();
        room.setName("Ownership Test Room");
        room.setDescription("Ownership transfer");
        room.setOwner(null); // Без владельца
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        // Присоединяемся к комнате
        mockMvc.perform(post("/api/rooms/" + room.getId() + "/join"))
                .andExpect(status().isOk());

        // Становимся владельцем
        mockMvc.perform(post("/api/rooms/" + room.getId() + "/become-owner"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "anotheruser")
    @DisplayName("Should return 403 when non-member tries to become owner")
    void shouldReturnForbiddenWhenNonMemberTriesToBecomeOwner() throws Exception {
        Room room = new Room();
        room.setName("Protected Ownership Room");
        room.setDescription("Cannot claim");
        room.setOwner(testUser);
        room.setTopics(randomTopics);
        room.setCreatedAt(LocalDateTime.now());
        room.setType(RoomType.DEFAULT_ROOM);
        room.setDeleteAfter(null);
        room.setMembers(new ArrayList<>(List.of(testUser)));
        room.setMemberLimit(100L);
        room = roomRepository.save(room);

        mockMvc.perform(post("/api/rooms/" + room.getId() + "/become-owner"))
                .andExpect(status().isForbidden());
    }
}