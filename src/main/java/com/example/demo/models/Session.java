package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User initiator;

    @Enumerated(EnumType.STRING)
    private SessionStatus status; // ACTIVE or ENDED

    @ManyToOne
    private Restaurant pickedRestaurant;

    private LocalDateTime createdDate;

    // Many-to-Many relationship to track users in the session
    @ManyToMany
    @JoinTable(
            name = "session_users",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private Set<User> usersInSession = new HashSet<>();

}
