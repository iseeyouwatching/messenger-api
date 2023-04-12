package ru.hits.messengerapi.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.user.service.implementation.IntegrationUserService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/integration/users")
@RequiredArgsConstructor
public class IntegrationUserController {

    private final IntegrationUserService integrationUserService;

    @GetMapping("/checkUserByIdAndFullName")
    public ResponseEntity<String> checkUserByIdAndFullName(@RequestParam MultiValueMap<String, String> queryParams) {
        Collection<List<String>> values = queryParams.values();
        for (List<String> value : values) {
            value.replaceAll(s -> URLDecoder.decode(s, StandardCharsets.UTF_8));
        }
        List<String> parameters = new ArrayList<>();
        for (List<String> valuesList : values) {
            String value = valuesList.get(1);
            parameters.add(value);
        }
        return new ResponseEntity<>(integrationUserService.checkUserByIdAndFullName(
                parameters.get(0),
                parameters.get(1)),
                HttpStatus.OK
        );
    }
}
