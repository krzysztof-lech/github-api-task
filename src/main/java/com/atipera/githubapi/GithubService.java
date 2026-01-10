package com.atipera.githubapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Arrays;
import java.util.List;

@Service
class GithubService {
    private final RestClient restClient;

    public GithubService(RestClient.Builder builder, @Value("${github.api.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl)
                .defaultHeader("User-Agent", "GithubApiApp")
                .build();
    }

    public List<RepositoryResponse> getRepositories(String username) {
        try {
            GitHubRepo[] repos = restClient.get()
                    .uri("/users/{user}/repos", username)
                    .retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> { throw new UserNotFoundException("User not found"); })
                    .body(GitHubRepo[].class);

            return Arrays.stream(repos)
                    .filter(repo -> !repo.fork())
                    .map(repo -> new RepositoryResponse(repo.name(), repo.owner().login(), getBranches(username, repo.name())))
                    .toList();
        } catch (Exception e) {
            if (e instanceof UserNotFoundException) throw e;
            e.printStackTrace();
            throw new RuntimeException("GitHub API error: " + e.getMessage());
        }
    }

    private List<BranchResponse> getBranches(String username, String repoName) {
        GitHubBranch[] branches = restClient.get()
                .uri("/repos/{user}/{repo}/branches", username, repoName)
                .retrieve()
                .body(GitHubBranch[].class);

        return Arrays.stream(branches)
                .map(b -> new BranchResponse(b.name(), b.commit().sha()))
                .toList();
    }
}