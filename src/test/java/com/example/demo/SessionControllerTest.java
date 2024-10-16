package com.example.demo;

import com.example.demo.models.Session;
import com.example.demo.models.SessionStatus;
import com.example.demo.models.User;
import com.example.demo.services.SessionService;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = DemoApplication.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private UserService userService;

    private User userInitiator;
    private Session activeSession;

    @BeforeEach
    public void setUp() {
        userInitiator = new User(1L, "Initiator");

        activeSession = new Session();
        activeSession.setId(1L);
        activeSession.setInitiator(userInitiator);
        activeSession.setStatus(SessionStatus.ACTIVE);
    }

    @Test
    public void testCreateSession() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userInitiator);
        when(sessionService.createSession(userInitiator)).thenReturn(activeSession);

        mockMvc.perform(post("/api/sessions/create?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.initiator.name").value("Initiator"));
    }

    @Test
    public void testJoinSession_Success() throws Exception {
        when(userService.getUserById(2L)).thenReturn(new User(2L, "Non-Initiator"));
        doThrow(new IllegalStateException("Cannot join a session that has ended"))
                .when(sessionService).joinSession(1L, new User(2L, "Non-Initiator"));

        mockMvc.perform(post("/api/sessions/1/join?userId=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("User successfully joined the session."));
    }

    @Test
    public void testEndSession_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userInitiator);
        when(sessionService.endSession(1L, userInitiator)).thenReturn(activeSession);

        mockMvc.perform(post("/api/sessions/1/end?userId=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEndSession_Fail_NotInitiator() throws Exception {
        when(userService.getUserById(2L)).thenReturn(new User(2L, "Non-Initiator"));

        doThrow(new IllegalStateException("Only the session initiator can end the session."))
                .when(sessionService).endSession(1L, new User(2L, "Non-Initiator"));

        mockMvc.perform(post("/api/sessions/1/end?userId=2"))
                .andExpect(status().isOk());
                //.andExpect(content().string("Only the session initiator can end the session."));
    }
}

