package com.lumina_bank.userservice.controller;

import com.lumina_bank.userservice.dto.UserCreateDto;
import com.lumina_bank.userservice.dto.UserResponse;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.model.User;
import com.lumina_bank.userservice.repository.UserRepository;
import com.lumina_bank.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto userDto) {
        User user = userService.createUser(userDto);
        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser (@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto){

        User user = userService.updateUser(id, userDto);
        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok().body("User with id " + id + " was deleted");
    }
}
