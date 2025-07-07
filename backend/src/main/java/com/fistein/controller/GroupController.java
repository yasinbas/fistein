package com.fistein.controller;

import com.fistein.entitiy.Group;
import com.fistein.entitiy.User;
import com.fistein.repository.GroupRepository;
import com.fistein.repository.UserRepository;
import com.fistein.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroupController {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public List<Group> getUserGroups(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return groupRepository.findByMembersContaining(userDetails.getId());
    }
    
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        group.setCreatedBy(currentUser);
        group.getMembers().add(currentUser);
        
        Group savedGroup = groupRepository.save(group);
        return ResponseEntity.ok(savedGroup);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        return groupRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}