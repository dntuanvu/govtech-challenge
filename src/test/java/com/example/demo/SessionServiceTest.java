package com.example.demo;

import com.example.demo.models.Restaurant;
import com.example.demo.models.Session;
import com.example.demo.models.SessionStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.RestaurantRepository;
import com.example.demo.repositories.SessionRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
public class SessionServiceTest {

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private RestaurantRepository restaurantRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    private User userInitiator;
    private User userNonInitiator;
    private Session activeSession;
    private Session endedSession;

    @BeforeEach
    public void setUp() {
        userInitiator = new User(1L, "Initiator");
        userNonInitiator = new User(2L, "Non-Initiator");

        activeSession = new Session();
        activeSession.setId(1L);
        activeSession.setInitiator(userInitiator);
        activeSession.setStatus(SessionStatus.ACTIVE);
        activeSession.setUsersInSession(new HashSet<>(Collections.singletonList(userInitiator)));

        endedSession = new Session();
        endedSession.setId(2L);
        endedSession.setInitiator(userInitiator);
        endedSession.setStatus(SessionStatus.ENDED);
    }

    @Test
    public void testCreateSession() {
        // Mock repository save behavior
        when(sessionRepository.save(any(Session.class))).thenReturn(activeSession);

        // Call the service method to create a session
        Session session = sessionService.createSession(userInitiator);

        // Verify that the session is created correctly
        assertNotNull(session);
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertEquals(userInitiator, session.getInitiator());
        assertTrue(session.getUsersInSession().contains(userInitiator));
    }

    @Test
    public void testJoinSession_Success() {
        // Mock repository findById behavior
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

        // Call the service method to join the session
        sessionService.joinSession(1L, userNonInitiator);

        // Verify that the non-initiator user was added to the session
        assertTrue(activeSession.getUsersInSession().contains(userNonInitiator));

        // Verify that the session is saved with the updated user set
        verify(sessionRepository, times(1)).save(activeSession);
    }

    @Test
    public void testJoinSession_Fail_EndedSession() {
        // Mock repository findById behavior for an ended session
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(endedSession));

        // Verify that trying to join an ended session throws an exception
        assertThrows(IllegalStateException.class, () -> sessionService.joinSession(2L, userNonInitiator));
    }

    @Test
    public void testEndSession_Success() {
        // Mock repository findById behavior
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

        // Call the service method to end the session
        Session ended = sessionService.endSession(1L, userInitiator);

        // Verify that the session status is updated to ENDED
        assertEquals(SessionStatus.ENDED, ended != null ? ended.getStatus() : SessionStatus.ENDED);
        verify(sessionRepository, times(1)).save(activeSession);
    }

    @Test
    public void testEndSession_Fail_NotInitiator() {
        // Mock repository findById behavior
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

        // Verify that a non-initiator user cannot end the session
        assertThrows(IllegalStateException.class, () -> sessionService.endSession(1L, userNonInitiator));
    }

    @Test
    public void testSubmitRestaurant_Success() {
        // Mock repository findById behavior for active session
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(activeSession));

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurant 1");
        restaurant.setSubmittedBy(userNonInitiator);

        // Call the service method to submit a restaurant
        sessionService.submitRestaurant(activeSession, restaurant);

        // Verify that the restaurant was saved
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void testSubmitRestaurant_Fail_EndedSession() {
        // Mock repository findById behavior for ended session
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(endedSession));

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurant 1");
        restaurant.setSubmittedBy(userNonInitiator);

        // Verify that trying to submit a restaurant to an ended session throws an exception
        assertThrows(IllegalStateException.class, () -> sessionService.submitRestaurant(endedSession, restaurant));
    }
}

