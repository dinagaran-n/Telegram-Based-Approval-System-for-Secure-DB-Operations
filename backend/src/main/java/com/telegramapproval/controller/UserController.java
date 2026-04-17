package com.telegramapproval.controller;

import com.telegramapproval.dto.UserRequestDto;
import com.telegramapproval.model.PendingRequest;
import com.telegramapproval.model.User;
import com.telegramapproval.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/requests")
    public ResponseEntity<List<PendingRequest>> getAllRequests() {
        return ResponseEntity.ok(userService.getAllRequests());
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequestDto request) {
        try {
            userService.submitAddUserRequest(request);
            return ResponseEntity.accepted()
                    .body(Map.of("message", "ADD request submitted for Telegram approval", "status", "PENDING"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto request) {
        try {
            userService.submitUpdateUserRequest(id, request);
            return ResponseEntity.accepted()
                    .body(Map.of("message", "UPDATE request submitted for Telegram approval", "status", "PENDING"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.submitDeleteUserRequest(id);
            return ResponseEntity.accepted()
                    .body(Map.of("message", "DELETE request submitted for Telegram approval", "status", "PENDING"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
