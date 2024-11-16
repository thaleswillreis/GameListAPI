package com.dev.gamelist.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.gamelist.dto.GameListDTO;
import com.dev.gamelist.entities.GameList;
import com.dev.gamelist.projections.GameMinProjection;
import com.dev.gamelist.repositories.GameListRepository;
import com.dev.gamelist.repositories.GameRepository;

@Service
public class GameListService {

	@Autowired
	private GameListRepository gameListRepository;

	@Autowired
	private GameRepository gameRepository;

	@Transactional(readOnly = true)
	public List<GameListDTO> findAll() {
		List<GameList> result = gameListRepository.findAll();
		return result.stream().map(x -> new GameListDTO(x)).toList();
	}

	@Transactional
	public void move(Long listId, int sourceIndex, int destinationIndex) {

		// Recebe a lista de jogos a ser reorganizada
		List<GameMinProjection> list = gameRepository.searchByList(listId);

		// Exclui e reinsere o jogo no intervalo da lista para reoganizar as posições
		GameMinProjection obj = list.remove(sourceIndex);
		list.add(destinationIndex, obj);

		// Determina a posição minima e a máxima do jogo na lista
		int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
		int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;

		// Atualiza o BD com as novas posições da lista especificada
		for (int i = min; i <= max; i++) {
			gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
		}
	}
}
