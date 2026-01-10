package com.atipera.githubapi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "github.api.url=http://localhost:8081")
@WireMockTest(httpPort = 8081)
public class GithubApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnUserNotFoundWhenGithubReturns404() {
        stubFor(get(urlEqualTo("/users/nonexistent/repos"))
                .willReturn(aResponse()
                        .withStatus(404)));

        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity("/api/repositories/nonexistent", ErrorResponse.class);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody().message());
    }

    @Test
    void shouldReturnRepositoriesWithBranchesAndExcludeForks() {
        stubFor(get(urlEqualTo("/users/karol/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        [
                            {"name": "my-project", "owner": {"login": "karol"}, "fork": false},
                            {"name": "some-fork", "owner": {"login": "karol"}, "fork": true}
                        ]
                        """)));

        stubFor(get(urlEqualTo("/repos/karol/my-project/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        [
                            {"name": "main", "commit": {"sha": "12345"}}
                        ]
                        """)));
        ResponseEntity<RepositoryResponse[]> response = restTemplate.getForEntity("/api/repositories/karol", RepositoryResponse[].class);


        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().length);
        assertEquals("my-project", response.getBody()[0].name());
        assertEquals("main", response.getBody()[0].branches().get(0).name());
    }
}