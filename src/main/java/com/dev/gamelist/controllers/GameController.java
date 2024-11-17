package com.dev.gamelist.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.gamelist.dto.GameDTO;
import com.dev.gamelist.dto.GameMinDTO;
import com.dev.gamelist.exceptions.ResourceNotFoundException;
import com.dev.gamelist.services.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/games")
@Tag(name = "Games", description = "Controlador para buscar jogos do catálogo.")
public class GameController {

	@Autowired
	private GameService gameService;

	@Operation(summary = "Busca um jogo", description = "Busca um jogo do catálogo pelo ID do jogo.", parameters = {
			@Parameter(name = "id", description = "ID do jogo", required = true, example = "1") })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "A solicitação foi bem-sucedida.", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameDTO.class))),
			@ApiResponse(responseCode = "400", description = "ID inválido fornecido."),
			@ApiResponse(responseCode = "404", description = "Recurso não encontrado.") })
	@GetMapping(value = "/{id}")
	public ResponseEntity<GameDTO> findById(@PathVariable Long id) {
		if (id == null || id <= 0) {
			return ResponseEntity.badRequest().body(null); // Retorna 400 se o ID for inválido
		}
		try {
			GameDTO game = gameService.findById(id);
			return ResponseEntity.ok(game); // Retorna 200 e o recurso
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna 404 se não for encontrado
		}
	}

	@Operation(summary = "Busca todos os jogos", description = "Retorna todos os jogos de todas as listas do catálogo.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "A solicitação foi bem-sucedida.", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameMinDTO.class))),
			@ApiResponse(responseCode = "204", description = "Nenhum conteúdo a ser exibido.") })
	@GetMapping
	public ResponseEntity<List<GameMinDTO>> findAll() {
		List<GameMinDTO> games = gameService.findAll();
		if (games.isEmpty()) {
			return ResponseEntity.noContent().build(); // Retorna 204 se não houver dados
		}
		return ResponseEntity.ok(games); // Retorna 200 com a lista
	}
}
