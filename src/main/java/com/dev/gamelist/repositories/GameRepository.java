package com.dev.gamelist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.gamelist.entities.Game;

public interface GameRepository extends JpaRepository<Game, Long>{

}
