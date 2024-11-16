package com.dev.gamelist.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.gamelist.dto.GameListDTO;
import com.dev.gamelist.dto.GameMinDTO;
import com.dev.gamelist.dto.ReplacementDTO;
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
	public List<GameListDTO> findAll() {
		return gameListService.findAll();
	}

	@GetMapping(value = "/{listId}/games")
	public List<GameMinDTO> searchByList(@PathVariable Long listId) {
		return gameService.findByList(listId);
	}
	
	@PostMapping(value = "/{listId}/replacement")
	public void move(@PathVariable Long listId, @RequestBody ReplacementDTO body) {
	gameListService.move(listId, body.getSourceIndex(), body.getDestinationIndex());
	}
}
