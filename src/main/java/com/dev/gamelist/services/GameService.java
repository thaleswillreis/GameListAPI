package com.dev.gamelist.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.gamelist.dto.GameDTO;
import com.dev.gamelist.dto.GameMinDTO;
import com.dev.gamelist.entities.Game;
import com.dev.gamelist.exceptions.DatabaseException;
import com.dev.gamelist.exceptions.ResourceNotFoundException;
import com.dev.gamelist.projections.GameMinProjection;
import com.dev.gamelist.repositories.GameRepository;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;

	// retorna um game a partir de um ID
	@Transactional(readOnly = true)
	public GameDTO findById(Long id) {
		Game result = gameRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Título de jogo não encontrado para o ID: " + id));
		return new GameDTO(result);
	}

	// retorna todos os games do catálogo
	@Transactional(readOnly = true)
	public List<GameMinDTO> findAll() {
		List<Game> result = gameRepository.findAll();
		return result.stream().map(x -> new GameMinDTO(x)).toList();
	}

	@Transactional(readOnly = true)
	public List<GameMinDTO> findByList(Long listId) {
		if (listId == null) {
			throw new IllegalArgumentException("O ID da lista não pode ser nulo");
		}

		try {
			List<GameMinProjection> result = gameRepository.searchByList(listId);

			if (result.isEmpty()) {
				throw new ResourceNotFoundException("Nenhum jogo encontrado para a lista de ID: " + listId);
			}

			return result.stream().map(x -> new GameMinDTO(x)).toList();

		} catch (Exception ex) {
			throw new DatabaseException("Ocorreu um erro ao buscar jogos para o ID da lista: " + listId, ex);
		}
	}
}
