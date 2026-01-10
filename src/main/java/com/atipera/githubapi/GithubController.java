package com.atipera.githubapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
class GithubController {
    private final GithubService service;

    public GithubController(GithubService service) { this.service = service; }

    @GetMapping(value = "/{username}", produces = "application/json")
    public List<RepositoryResponse> getRepos(@PathVariable String username) {
        return service.getRepositories(username);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(UserNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }
}