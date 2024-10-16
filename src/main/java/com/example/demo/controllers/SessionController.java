package com.example.demo.controllers;

import com.example.demo.models.Restaurant;
import com.example.demo.models.Session;
import com.example.demo.models.User;
import com.example.demo.services.SessionService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Session createSession(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return sessionService.createSession(user);
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<String> joinSession(@PathVariable Long sessionId, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        try {
            sessionService.joinSession(sessionId, user);
            return ResponseEntity.ok("User successfully joined the session.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{sessionId}/submit")
    public void submitRestaurant(@PathVariable Long sessionId, @RequestParam String restaurantName, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        Session session = sessionService.getSessionById(sessionId);
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantName);
        restaurant.setSubmittedBy(user);
        sessionService.submitRestaurant(session, restaurant);
    }

    @GetMapping("/{sessionId}/restaurants")
    public List<Restaurant> getRestaurants(@PathVariable Long sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        return sessionService.getRestaurants(session);
    }

    @PostMapping("/{sessionId}/end")
    public Session endSession(@PathVariable Long sessionId, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        return sessionService.endSession(sessionId, user);
    }

    @GetMapping("/user/{userId}/sessions")
    public List<Session> getSessionsForUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return sessionService.getSessionsForUser(user);
    }
}

