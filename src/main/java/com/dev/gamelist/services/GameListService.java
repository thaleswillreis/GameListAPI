package com.dev.gamelist.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.gamelist.dto.GameListDTO;
import com.dev.gamelist.entities.GameList;
import com.dev.gamelist.exceptions.DatabaseException;
import com.dev.gamelist.exceptions.ResourceNotFoundException;
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

	    if (listId == null) {
	        throw new IllegalArgumentException("O ID da lista não pode ser nulo.");
	    }

	    try {
	        // Recebe a lista de jogos a ser reorganizada
	        List<GameMinProjection> list = gameRepository.searchByList(listId);

	        if (list.isEmpty()) {
	            throw new ResourceNotFoundException("Nenhum jogo encontrado para o ID de lista especificado: " + listId);
	        }

	        // Verifica se os índices são válidos
	        if (sourceIndex < 0 || sourceIndex >= list.size()) {
	            throw new IllegalArgumentException("O valor especificado para o índice inicial está fora dos limites: " + sourceIndex);
	        }
	        if (destinationIndex < 0 || destinationIndex >= list.size()) {
	            throw new IllegalArgumentException("O valor especificado para o índice de destino está fora dos limites: " + destinationIndex);
	        }

	        // Exclui e reinsere o jogo no intervalo da lista para reorganizar as posições
	        GameMinProjection obj = list.remove(sourceIndex);
	        list.add(destinationIndex, obj);

	        // Determina a posição mínima e máxima do jogo na lista
	        int min = Math.min(sourceIndex, destinationIndex);
	        int max = Math.max(sourceIndex, destinationIndex);

	        // Atualiza o BD com as novas posições da lista especificada
	        for (int i = min; i <= max; i++) {
	            gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
	        }

	    } catch (ResourceNotFoundException e) {
	        // Repropaga a exceção para ser tratada pelo ControllerAdvice
	        throw e;

	    } catch (IllegalArgumentException e) {
	        // Lança uma resposta clara para erros de argumentos inválidos
	        throw e;

	    } catch (Exception e) {
	        // Captura qualquer outro erro inesperado
	        throw new DatabaseException("Ocorreu um erro ao atualizar a lista de jogos.", e);
	    }
	}
}
