package com.teamjjang;

import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ApiIntegrationTest {
    @LocalServerPort int port;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());
    }
    private JsonNode create(String path, String body) throws Exception {
        var response = request("POST", path, body);
        assertThat(response.statusCode()).as(response.body()).isEqualTo(201);
        return json.readTree(response.body());
    }
    private long project() throws Exception {
        return create("/api/projects", "{\"name\":\"팀장봇\",\"description\":\"MVP\",\"deadline\":\""
                + LocalDate.now().plusDays(30) + "\"}").get("id").asLong();
    }
    private String taskBody(Long member, int optimistic, int likely, int pessimistic) {
        return "{\"title\":\"로그인 API\",\"assigneeId\":" + member + ",\"optimisticHours\":" + optimistic
                + ",\"likelyHours\":" + likely + ",\"pessimisticHours\":" + pessimistic + "}";
    }
    @Test void projectMemberTaskProgressRoundTrip() throws Exception {
        long project = project();
        String base = "/api/projects/" + project;
        long member = create(base + "/members", "{\"name\":\"예령\",\"role\":\"백엔드\"}").get("id").asLong();
        long task = create(base + "/tasks", taskBody(member, 4, 8, 16)).get("id").asLong();
        assertThat(json.readTree(request("GET", base, null).body()).get("id").asLong()).isEqualTo(project);
        assertThat(json.readTree(request("GET", base + "/members", null).body()).size()).isEqualTo(1);
        var tasks = json.readTree(request("GET", base + "/tasks", null).body());
        assertThat(tasks.get(0).get("assigneeId").asLong()).isEqualTo(member);
        String progress = base + "/tasks/" + task + "/progress";
        assertThat(json.readTree(request("GET", progress, null).body()).size()).isZero();
        create(progress, "{\"percent\":100,\"note\":\"완료\"}");
        create(progress, "{\"percent\":40,\"note\":\"재작업\"}");
        var history = json.readTree(request("GET", progress, null).body());
        assertThat(history.size()).isEqualTo(2);
        assertThat(history.get(0).get("percent").asInt()).isEqualTo(40);
        assertThat(history.get(1).get("percent").asInt()).isEqualTo(100);
        assertThat(history.get(0).get("recordedAt").asText()).isNotBlank();
        assertThat(request("GET", "/api/projects", null).statusCode()).isEqualTo(200);
    }
    @Test void rejectsCrossProjectAssignmentAndProgressAccess() throws Exception {
        long first = project(), second = project();
        long member = create("/api/projects/" + first + "/members", "{\"name\":\"A\",\"role\":\"개발\"}").get("id").asLong();
        assertThat(request("POST", "/api/projects/" + second + "/tasks", taskBody(member, 1, 2, 3)).statusCode()).isEqualTo(400);
        long task = create("/api/projects/" + first + "/tasks", taskBody(null, 1, 2, 3)).get("id").asLong();
        String wrong = "/api/projects/" + second + "/tasks/" + task + "/progress";
        assertThat(request("GET", wrong, null).statusCode()).isEqualTo(404);
        assertThat(request("POST", wrong, "{\"percent\":10,\"note\":\"\"}").statusCode()).isEqualTo(404);
    }
    @Test void validatesEstimatesProgressAndMalformedRequests() throws Exception {
        String base = "/api/projects/" + project();
        for (String body : new String[] {taskBody(null, 3, 2, 1), taskBody(null, 0, 1, 2),
                "{\"title\":\"missing estimates\"}"}) {
            assertThat(request("POST", base + "/tasks", body).statusCode()).isEqualTo(400);
        }
        long task = create(base + "/tasks", taskBody(null, 2, 2, 2)).get("id").asLong();
        for (String body : new String[] {"{\"percent\":101,\"note\":\"\"}", "{\"percent\":-1,\"note\":\"\"}", "{\"note\":\"missing\"}", "{\"percent\":12.5,\"note\":\"\"}"}) {
            assertThat(request("POST", base + "/tasks/" + task + "/progress", body).statusCode()).isEqualTo(400);
        }
        var invalid = request("POST", "/api/projects", "{\"name\":\" \",\"description\":\"\",\"deadline\":\"2000-01-01\"}");
        assertThat(invalid.statusCode()).isEqualTo(400);
        assertThat(json.readTree(invalid.body()).get("errors").has("name")).isTrue();
        assertThat(request("POST", "/api/projects", "{broken").statusCode()).isEqualTo(400);
        assertThat(request("GET", "/api/projects/not-a-number", null).statusCode()).isEqualTo(400);
        assertThat(request("GET", "/api/projects/999999/members", null).statusCode()).isEqualTo(404);
    }
    @Test void healthAndCors() throws Exception {
        assertThat(json.readTree(request("GET", "/actuator/health", null).body()).get("status").asText()).isEqualTo("UP");
        for (String origin : new String[] {"http://localhost:5173", "https://untrusted.example"}) {
            var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/projects"))
                    .header("Origin", origin).header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "content-type")
                    .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
            if (origin.contains("localhost")) {
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).contains(origin);
            } else {
                assertThat(response.statusCode()).isEqualTo(403);
                assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).isEmpty();
            }
        }
    }

    @Test void updatesProjectAndPersistsWithoutChangingDeadline() throws Exception {
        String path = "/api/projects/" + project();
        var before = json.readTree(request("GET", path, null).body());
        var response = request("PATCH", path, "{\"name\":\"  수정한 팀장봇  \",\"description\":\"새 설명\"}");
        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        var updated = json.readTree(response.body());
        assertThat(updated.get("name").asText()).isEqualTo("수정한 팀장봇");
        assertThat(updated.get("description").asText()).isEqualTo("새 설명");
        assertThat(updated.get("id")).isEqualTo(before.get("id"));
        assertThat(updated.get("deadline")).isEqualTo(before.get("deadline"));
        var fetched = request("GET", path, null);
        assertThat(fetched.statusCode()).isEqualTo(200);
        assertThat(json.readTree(fetched.body())).isEqualTo(updated);
    }

    @Test void acceptsEmptyDescriptionAndMaximumLengthsOnUpdate() throws Exception {
        String path = "/api/projects/" + project();
        for (String description : new String[] {"", "가".repeat(2000)}) {
            String body = json.writeValueAsString(java.util.Map.of("name", "나".repeat(100), "description", description));
            var response = request("PATCH", path, body);
            assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
            var fetched = json.readTree(request("GET", path, null).body());
            assertThat(fetched.get("name").asText()).isEqualTo("나".repeat(100));
            assertThat(fetched.get("description").asText()).isEqualTo(description);
        }
    }

    @Test void rejectsInvalidProjectUpdatesWithoutChangingStoredData() throws Exception {
        String path = "/api/projects/" + project();
        var before = json.readTree(request("GET", path, null).body());
        for (String body : new String[] {
                "{\"description\":\"변경\"}",
                "{\"name\":null,\"description\":\"변경\"}",
                "{\"name\":\"\",\"description\":\"변경\"}",
                "{\"name\":\"   \",\"description\":\"변경\"}",
                "{\"name\":\"변경\"}",
                "{\"name\":\"변경\",\"description\":null}",
                json.writeValueAsString(java.util.Map.of("name", "가".repeat(101), "description", "변경")),
                json.writeValueAsString(java.util.Map.of("name", "변경", "description", "가".repeat(2001)))}) {
            var response = request("PATCH", path, body);
            assertThat(response.statusCode()).as(response.body()).isEqualTo(400);
            assertThat(json.readTree(request("GET", path, null).body())).isEqualTo(before);
        }
    }

    @Test void returnsNotFoundWhenUpdatingMissingProject() throws Exception {
        var response = request("PATCH", "/api/projects/999999999", "{\"name\":\"변경\",\"description\":\"설명\"}");
        assertThat(response.statusCode()).as(response.body()).isEqualTo(404);
    }

    @Test void allowsPatchPreflightFromReact() throws Exception {
        var response = client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/projects/1"))
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "PATCH")
                .header("Access-Control-Request-Headers", "content-type")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).contains("http://localhost:5173");
        assertThat(response.headers().firstValue("Access-Control-Allow-Methods").orElse("")).contains("PATCH");
    }

    private long memberFor(long projectId) throws Exception {
        return create("/api/projects/" + projectId + "/members",
                "{\"name\":\"테스트 팀원\",\"role\":\"개발\"}").get("id").asLong();
    }

    private JsonNode storedTask(long projectId, long taskId) throws Exception {
        var response = request("GET", "/api/projects/" + projectId + "/tasks", null);
        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        for (JsonNode task : json.readTree(response.body())) {
            if (task.get("id").asLong() == taskId) {
                return task;
            }
        }
        throw new AssertionError("저장된 작업을 찾지 못했습니다: " + taskId);
    }

    @Test void changesRepeatsClearsAndReassignsTaskAssignee() throws Exception {
        long projectId = project();
        long first = memberFor(projectId);
        long second = memberFor(projectId);
        String base = "/api/projects/" + projectId + "/tasks";
        JsonNode original = create(base, taskBody(first, 2, 4, 8));
        long taskId = original.get("id").asLong();

        for (Long next : new Long[] {second, second, null, null, first}) {
            var response = request("PATCH", base + "/" + taskId + "/assignee",
                    "{\"assigneeId\":" + next + "}");
            assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
            JsonNode updated = json.readTree(response.body());
            if (next == null) {
                assertThat(updated.get("assigneeId").isNull()).isTrue();
            } else {
                assertThat(updated.get("assigneeId").asLong()).isEqualTo(next);
            }
            for (String field : new String[] {"id", "projectId", "title", "optimisticHours", "likelyHours", "pessimisticHours"}) {
                assertThat(updated.get(field)).as(field).isEqualTo(original.get(field));
            }
            assertThat(storedTask(projectId, taskId)).isEqualTo(updated);
        }
    }

    @Test void rejectsInvalidAssigneesWithoutChangingTask() throws Exception {
        long projectId = project();
        long current = memberFor(projectId);
        long outsider = memberFor(project());
        String base = "/api/projects/" + projectId + "/tasks";
        JsonNode original = create(base, taskBody(current, 1, 2, 3));
        long taskId = original.get("id").asLong();
        for (long invalidId : new long[] {outsider, Long.MAX_VALUE, 0, -1}) {
            var response = request("PATCH", base + "/" + taskId + "/assignee",
                    "{\"assigneeId\":" + invalidId + "}");
            assertThat(response.statusCode()).as(response.body()).isEqualTo(400);
            assertThat(storedTask(projectId, taskId)).isEqualTo(original);
        }
    }

    @Test void rejectsMissingAndOtherProjectTasksBeforeChangingAssignee() throws Exception {
        long owner = project();
        long other = project();
        long current = memberFor(owner);
        long otherMember = memberFor(other);
        JsonNode original = create("/api/projects/" + owner + "/tasks", taskBody(current, 1, 2, 3));
        long taskId = original.get("id").asLong();
        String[] paths = {
                "/api/projects/" + other + "/tasks/" + taskId + "/assignee",
                "/api/projects/" + owner + "/tasks/" + Long.MAX_VALUE + "/assignee",
                "/api/projects/" + Long.MAX_VALUE + "/tasks/" + taskId + "/assignee"
        };
        for (String path : paths) {
            for (Long next : new Long[] {otherMember, null}) {
                var response = request("PATCH", path, "{\"assigneeId\":" + next + "}");
                assertThat(response.statusCode()).as(response.body()).isEqualTo(404);
                assertThat(storedTask(owner, taskId)).isEqualTo(original);
            }
        }
    }
}
