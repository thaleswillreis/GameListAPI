package com.dev.gamelist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.gamelist.entities.GameList;

public interface GameListRepository extends JpaRepository<GameList, Long>{

}
