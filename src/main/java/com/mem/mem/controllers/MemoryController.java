package com.mem.mem.controllers;



import com.mem.mem.DTOs.*;

import com.mem.mem.services.MemoryService;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/memories")
@CrossOrigin(origins = "*")
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    @GetMapping
    public ResponseEntity<List<MemoryResponse>> getAllMemories() {
        List<MemoryResponse> memories = memoryService.getAllMemories();
        
        return ResponseEntity.ok(memories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoryResponse> getMemoryById(@PathVariable Long id) {
        try {
            MemoryResponse memory = memoryService.getMemoryById(id);
            return ResponseEntity.ok(memory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemoryResponse> createMemory( @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("images") List<MultipartFile> images) throws IOException {
        try {
            MemoryResponse memory = memoryService.createMemory(title,description,images);
            
            return ResponseEntity.ok(memory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemory(@PathVariable Long id) {
        try {
            memoryService.deleteMemory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long id) {
        // System.out.println(("like1"));
        try {
            memoryService.toggleLike(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}