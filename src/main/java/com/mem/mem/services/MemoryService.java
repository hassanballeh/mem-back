package com.mem.mem.services;



import com.mem.mem.DTOs.*;

import com.mem.mem.models.*;

import com.mem.mem.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class MemoryService {

    @Autowired
    private MemoryRepository memoryRepository;

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    public List<MemoryResponse> getAllMemories() {
        System.out.println("nullhas5");
        List<Memory> memories = memoryRepository.findAllWithLikes();
        Long currentUserId = getCurrentUserId();
        
        return memories.stream()
                .map(memory -> convertToResponse(memory, currentUserId))
                .collect(Collectors.toList());
    }

    public MemoryResponse getMemoryById(Long id) {
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memory not found with id: " + id));
        Long currentUserId = getCurrentUserId();
        return convertToResponse(memory, currentUserId);
    }

    public MemoryResponse createMemory( String title,
         String description,
        List<MultipartFile> images) throws IOException {
        if(title.equals("")){
            throw new RuntimeException("Title is required!");
        }
        if(images.size()==0){
            throw new RuntimeException("Images is required!");
        }
        if(images.size()>4){
            throw new RuntimeException("Images should be less than 4");
        }
        User currentUser = getCurrentUser();
         List<String> imageBase64List = new ArrayList<>();
        for (MultipartFile file : images) {
            String base64 = encodeToBase64(file);
            imageBase64List.add(base64);
        }
        Memory memory = new Memory();
        memory.setTitle(title);
        memory.setDescription(description);
        memory.setImages(imageBase64List);
        memory.setUser(currentUser);

        Memory savedMemory = memoryRepository.save(memory);
        return convertToResponse(savedMemory, currentUser.getId());
    }
    private String encodeToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String contentType = file.getContentType();
        return "data:" + contentType + ";base64," + base64;
    }
    public void deleteMemory(Long id) {
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memory not found with id: " + id));
                User currentUser = getCurrentUser();
                if (!memory.getUser().getId().equals(currentUser.getId())) {
                    throw new RuntimeException("You can only delete your own memories");
                }
                

        memoryRepository.delete(memory);
    }

    @Transactional
    public void toggleLike(Long memoryId) {
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new RuntimeException("Memory not found with id: " + memoryId));
        // System.out.println("sada"+memory.getLikeCount());
        User currentUser = getCurrentUser();
        if (likeRepository.existsByUserIdAndMemoryId(currentUser.getId(), memoryId)) {
            likeRepository.deleteByUserIdAndMemoryId(currentUser.getId(), memoryId);
        } else {
            Like like = new Like(currentUser, memory);
            likeRepository.save(like);
        }
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (User) authentication.getPrincipal();
        } catch (Exception e) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (User) authentication.getPrincipal();
            
        }
    }

    private Long getCurrentUserId() {
        try {
            return getCurrentUser().getId();
        } catch (Exception e) {
            return null;
        }
    }

    private MemoryResponse convertToResponse(Memory memory, Long currentUserId) {
        UserResponse userResponse = new UserResponse(
                memory.getUser().getId(),
                memory.getUser().getFirstName(),
                memory.getUser().getLastName(),
                memory.getUser().getEmail()
        );

        boolean isLikedByCurrentUser = currentUserId != null && 
                likeRepository.existsByUserIdAndMemoryId(currentUserId, memory.getId());

        return new MemoryResponse(
                memory.getId(),
                memory.getTitle(),
                memory.getDescription(),
                memory.getImages(),
                userResponse,
                memory.getLikeCount(),
                isLikedByCurrentUser,
                memory.getCreatedAt(),
                memory.getUpdatedAt()
        );
    }
}