package com.atipera.githubapi;
import java.util.List;

record RepositoryResponse(String name, String ownerLogin, List<BranchResponse> branches) {}
record BranchResponse(String name, String lastCommitSha) {}
record GitHubRepo(String name, Owner owner, boolean fork) {}
record Owner(String login) {}
record GitHubBranch(String name, Commit commit) {}
record Commit(String sha) {}
record ErrorResponse(int status, String message) {}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}