package com.dev.gamelist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.dev.gamelist.entities.GameList;

public interface GameListRepository extends JpaRepository<GameList, Long> {

	// atualiza o número da posição de um game específico de uma lista específica
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE tb_belonging SET position = :newPosition WHERE list_id = :listId AND game_id=:gameId")
	void updateBelongingPosition(Long listId, Long gameId, Integer newPosition);
}
