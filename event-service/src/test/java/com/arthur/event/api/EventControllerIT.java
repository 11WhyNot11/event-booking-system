package com.arthur.event.api;

import com.arthur.event.infrastructure.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Autowired
    EventRepository eventRepository;

    String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void clean() {
        eventRepository.deleteAll();
    }

    private Map<String, Object> validEventBody() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "TEST");
        m.put("description", "TEST description");
        m.put("location", "LVIV");
        m.put("startTime", "2025-09-10T10:00:00");
        m.put("endTime",   "2025-09-10T12:00:00");
        m.put("capacity", "100");
        m.put("price", "50");

        return m;
    }

    @Test
    void create_get_delete_happyPath(){
        // CREATE
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(validEventBody(), headers);

        ResponseEntity<Map> created = rest.postForEntity(url("/api/events"), req, Map.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        Object id = created.getBody().get("id");
        assertThat(id).isNotNull();

        // GET by id
        ResponseEntity<Map> got = rest.getForEntity(url("/api/events/" + id), Map.class);
        assertThat(got.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(got.getBody()).isNotNull();
        assertThat(got.getBody().get("name")).isEqualTo("TEST");

        //DELETE
        rest.delete(url("/api/events/" + id));

        ResponseEntity<Map> get404 = rest.getForEntity(url("/api/events/" + id), Map.class);
        assertThat(get404.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(get404.getBody()).isNotNull();
        assertThat(get404.getBody().get("errorCode")).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    void invalidTime_returns400_withProblemDetail(){
        Map<String, Object> bad = new HashMap<>();
        bad.put("name", "BadConf");
        bad.put("description", "Some desc");
        bad.put("location", "Kyiv");
        bad.put("startTime", "2025-09-10T12:00:00");
        bad.put("endTime",   "2025-09-10T10:00:00");
        bad.put("capacity",  100);
        bad.put("price",     50);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(bad, headers);

        ResponseEntity<Map> resp = rest.postForEntity(url("/api/events"), req, Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().get("type"))
                .isEqualTo("https://errors.event-booking/validation");
        assertThat(resp.getBody().get("errorCode"))
                .isEqualTo("BUSINESS_VALIDATION_FAILED");



    }

    @Test
    void duplicate_returns409_conflict() {
        Map<String,Object> body = new HashMap<>();
        body.put("name", "DupConf");
        body.put("description", "Some desc");
        body.put("location", "Lviv");
        body.put("startTime", "2025-09-11T10:00:00");
        body.put("endTime",   "2025-09-11T12:00:00");
        body.put("capacity",  100);
        body.put("price",     50);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<Map> first = rest.postForEntity(url("/api/events"), req, Map.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map> second = rest.postForEntity(url("/api/events"), req, Map.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody()).isNotNull();
        assertThat(second.getBody().get("type"))
                .isEqualTo("https://errors.event-booking/conflict");
        assertThat(second.getBody().get("errorCode"))
                .isEqualTo("DATA_CONFLICT");
    }

}
