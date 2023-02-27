package ru.practicum.shareIt.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareIt.client.BaseClient;
import ru.practicum.shareIt.user.dto.CreateUserRequestDto;
import ru.practicum.shareIt.user.dto.UpdateUserRequestDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findAll() {
        return get("");
    }

    public ResponseEntity<Object> findOne(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> create(CreateUserRequestDto createUserRequestDto) {
        return post("", createUserRequestDto);
    }

    public ResponseEntity<Object> update(long userId, UpdateUserRequestDto updateUserRequestDto) {
        return patch("/" + userId, updateUserRequestDto);
    }

    public ResponseEntity<Object> removeById(long userId) {
        return delete("/" + userId);
    }
}
