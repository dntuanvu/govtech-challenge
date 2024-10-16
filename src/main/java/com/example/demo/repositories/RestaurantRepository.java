package com.example.demo.repositories;

import com.example.demo.models.Restaurant;
import com.example.demo.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findAllBySession(Session session);

}
