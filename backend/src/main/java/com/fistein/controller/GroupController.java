package com.fistein.controller;

import com.fistein.dto.*;
import com.fistein.security.CustomUserDetailsService;
import com.fistein.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        GroupResponse response = groupService.createGroup(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(
            @PathVariable Long groupId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        GroupResponse response = groupService.getGroupById(groupId, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getUserGroups(Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        List<GroupResponse> groups = groupService.getUserGroups(currentUser);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupResponse> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        GroupResponse response = groupService.addMemberToGroup(groupId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        groupService.removeMemberFromGroup(groupId, userId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId, currentUser);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long groupId,
            @RequestBody UpdateGroupRequest request,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        GroupResponse response = groupService.updateGroup(groupId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        groupService.deleteGroup(groupId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/balances")
    public ResponseEntity<GroupBalanceResponse> getGroupBalances(
            @PathVariable Long groupId,
            Authentication authentication) {
        var currentUser = userDetailsService.loadUserEntityByEmail(authentication.getName());
        GroupBalanceResponse balances = groupService.getGroupBalances(groupId, currentUser);
        return ResponseEntity.ok(balances);
    }
}