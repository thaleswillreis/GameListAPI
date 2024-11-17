package com.dev.gamelist.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.gamelist.dto.GameListDTO;
import com.dev.gamelist.dto.GameMinDTO;
import com.dev.gamelist.dto.ReplacementDTO;
import com.dev.gamelist.exceptions.ResourceNotFoundException;
import com.dev.gamelist.services.GameListService;
import com.dev.gamelist.services.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/lists")
@Tag(name = "Game List", description = "Controlador para buscar e manipular listas de jogos do catálogo.")
public class GameListController {

	@Autowired
	private GameListService gameListService;

	@Autowired
	private GameService gameService;

	@Operation(summary = "Busca todas as listas de jogos", description = "Busca e retorna todas as listas de jogos disponíveis no catálogo.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Listas de jogos recuperadas com sucesso.", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameListDTO.class))),
			@ApiResponse(responseCode = "204", description = "Nenhuma lista encontrada.") })
	@GetMapping
	public ResponseEntity<List<GameListDTO>> findAll() {
		List<GameListDTO> gameLists = gameListService.findAll();
		if (gameLists.isEmpty()) {
			return ResponseEntity.noContent().build(); // 204 No Content
		}
		return ResponseEntity.ok(gameLists); // 200 OK
	}

	@Operation(summary = "Busca jogos de uma lista específica", description = "Retorna todos os jogos associados a uma lista específica identificada pelo ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Jogos recuperados com sucesso.", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameMinDTO.class))),
			@ApiResponse(responseCode = "400", description = "ID inválido fornecido."),
			@ApiResponse(responseCode = "404", description = "Lista não encontrada.") })
	@GetMapping(value = "/{listId}/games")
	public ResponseEntity<List<GameMinDTO>> searchByList(@PathVariable Long listId) {
		if (listId == null || listId <= 0) {
			return ResponseEntity.badRequest().body(null); // 400 Bad Request
		}
		try {
			List<GameMinDTO> games = gameService.findByList(listId);
			if (games.isEmpty()) {
				return ResponseEntity.noContent().build(); // 204 No Content
			}
			return ResponseEntity.ok(games); // 200 OK
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
		}
	}

	@Operation(summary = "Reposiciona jogos em uma lista", description = "Reposiciona dinamicamente jogos de uma lista com base nos índices fornecidos.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "204", description = "Reorganização concluída com sucesso."),
			@ApiResponse(responseCode = "400", description = "ID inválido ou body malformado."),
			@ApiResponse(responseCode = "500", description = "Erro interno durante a reorganização.") })
	@PostMapping(value = "/{listId}/replacement")
	public ResponseEntity<Void> move(@PathVariable Long listId, @RequestBody ReplacementDTO body) {
		if (listId == null || listId <= 0) {
			return ResponseEntity.badRequest().build(); // 400 Bad Request
		}
		if (body == null || body.getSourceIndex() < 0 || body.getDestinationIndex() < 0) {
			return ResponseEntity.badRequest().build(); // 400 Bad Request
		}
		try {
			gameListService.move(listId, body.getSourceIndex(), body.getDestinationIndex());
			return ResponseEntity.noContent().build(); // 204 No Content
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build(); // 400 Bad Request
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
		}
	}
}
