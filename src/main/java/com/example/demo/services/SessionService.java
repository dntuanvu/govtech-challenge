package com.example.demo.services;

import com.example.demo.models.Restaurant;
import com.example.demo.models.Session;
import com.example.demo.models.SessionStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.RestaurantRepository;
import com.example.demo.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Session createSession(User initiator) {
        Session session = new Session();
        session.setInitiator(initiator);
        session.setStatus(SessionStatus.ACTIVE);
        session.setCreatedDate(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public void joinSession(Long sessionId, User user) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot join a session that has already ended.");
        }

        session.getUsersInSession().add(user);
        sessionRepository.save(session);
    }

    public void submitRestaurant(Session session, Restaurant restaurant) {
        if (session.getStatus() == SessionStatus.ACTIVE) {
            restaurant.setSession(session);
            restaurantRepository.save(restaurant);
        } else {
            throw new IllegalStateException("Cannot submit restaurant to an ended session.");
        }
    }

    public List<Restaurant> getRestaurants(Session session) {
        return restaurantRepository.findAllBySession(session);
    }

    public Session endSession(Long sessionId, User user) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));

        if (!session.getInitiator().getId().equals(user.getId())) {
            throw new IllegalStateException("Only the session initiator can end the session.");
        }

        if (session.getStatus() == SessionStatus.ENDED) {
            throw new IllegalStateException("Session already ended.");
        }

        List<Restaurant> restaurants = restaurantRepository.findAllBySession(session);
        if (!restaurants.isEmpty()) {
            Random random = new Random();
            Restaurant pickedRestaurant = restaurants.get(random.nextInt(restaurants.size()));
            session.setPickedRestaurant(pickedRestaurant);
        }

        session.setStatus(SessionStatus.ENDED);
        return sessionRepository.save(session);
    }

    public List<Session> getSessionsForUser(User user) {
        return sessionRepository.findByUsersInSession(user);
    }

    public Session getSessionById(Long sessionId) {
        if (sessionRepository.findById(sessionId).isEmpty()) {
            throw new NoSuchElementException("session not found");
        }

        return sessionRepository.findById(sessionId).get();
    }
}

