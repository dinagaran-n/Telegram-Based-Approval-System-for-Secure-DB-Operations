package com.telegramapproval.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegramapproval.dto.UserRequestDto;
import com.telegramapproval.model.ActionType;
import com.telegramapproval.model.PendingRequest;
import com.telegramapproval.model.RequestStatus;
import com.telegramapproval.model.User;
import com.telegramapproval.repository.PendingRequestRepository;
import com.telegramapproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PendingRequestRepository requestRepository;
    private final TelegramService telegramService;
    private final ObjectMapper objectMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<PendingRequest> getAllRequests() {
         return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void submitAddUserRequest(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        createAndSendRequest(ActionType.ADD, dto);
    }

    @Transactional
    public void submitUpdateUserRequest(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // If email changed, check if it's already taken by another user
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
             throw new IllegalArgumentException("Email already exists");
        }
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", id);
        payload.put("name", dto.getName());
        payload.put("email", dto.getEmail());
        
        createAndSendRequest(ActionType.UPDATE, payload);
    }

    @Transactional
    public void submitDeleteUserRequest(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", id);
        payload.put("name", user.getName());
        payload.put("email", user.getEmail());
        
        createAndSendRequest(ActionType.DELETE, payload);
    }

    private void createAndSendRequest(ActionType actionType, Object payloadObject) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payloadObject);
            
            PendingRequest request = PendingRequest.builder()
                    .actionType(actionType)
                    .payload(payloadJson)
                    .status(RequestStatus.PENDING)
                    .build();
                    
            PendingRequest savedRequest = requestRepository.save(request);
            telegramService.sendApprovalMessage(savedRequest);
            
            log.info("Created pending request ID {} for action {}", savedRequest.getId(), actionType);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload to JSON", e);
            throw new RuntimeException("Failed to process request data", e);
        }
    }

    @Transactional
    public void processTelegramApproval(Long requestId, boolean isApproved) {
        PendingRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Request {} is already in status {}", requestId, request.getStatus());
            return;
        }

        try {
            if (isApproved) {
                applyAction(request);
                request.setStatus(RequestStatus.APPROVED);
            } else {
                request.setStatus(RequestStatus.REJECTED);
            }

            requestRepository.save(request);
            
            // Update the message in Telegram so buttons are removed
            telegramService.updateMessageStatus(
                    request.getTelegramMessageId(), 
                    request.getId(), 
                    request.getStatus(), 
                    request.getActionType().toString()
            );
            
        } catch (Exception e) {
            log.error("Failed to process approval for request {}", requestId, e);
            throw new RuntimeException("Error processing approval", e);
        }
    }

    private void applyAction(PendingRequest request) throws JsonProcessingException {
        Map<String, Object> payload = objectMapper.readValue(request.getPayload(), Map.class);
        
        log.info("Applying action {} ...", request.getActionType());

        switch (request.getActionType()) {
            case ADD -> {
                User user = User.builder()
                        .name((String) payload.get("name"))
                        .email((String) payload.get("email"))
                        .build();
                userRepository.save(user);
            }
            case UPDATE -> {
                Long userId = ((Number) payload.get("userId")).longValue();
                userRepository.findById(userId).ifPresent(user -> {
                    user.setName((String) payload.get("name"));
                    user.setEmail((String) payload.get("email"));
                    userRepository.save(user);
                });
            }
            case DELETE -> {
                Long userId = ((Number) payload.get("userId")).longValue();
                userRepository.deleteById(userId);
            }
        }
    }
}
