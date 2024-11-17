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

@RestController
@RequestMapping(value = "/lists")
public class GameListController {

	@Autowired
	private GameListService gameListService;

	@Autowired
	private GameService gameService;

	@GetMapping
	public ResponseEntity<List<GameListDTO>> findAll() {
		List<GameListDTO> gameLists = gameListService.findAll();
		if (gameLists.isEmpty()) {
			return ResponseEntity.noContent().build(); // 204 No Content
		}
		return ResponseEntity.ok(gameLists); // 200 OK
	}

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
