package com.fistein.service;

import com.fistein.dto.*;
import com.fistein.entity.User;

import java.util.List;

public interface GroupService {
    
    GroupResponse createGroup(CreateGroupRequest request, User currentUser);
    
    GroupResponse getGroupById(Long groupId, User currentUser);
    
    List<GroupResponse> getUserGroups(User currentUser);
    
    GroupResponse addMemberToGroup(Long groupId, AddMemberRequest request, User currentUser);
    
    void removeMemberFromGroup(Long groupId, Long userId, User currentUser);
    
    GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, User currentUser);
    
    void deleteGroup(Long groupId, User currentUser);
    
    List<GroupMemberResponse> getGroupMembers(Long groupId, User currentUser);
    
    GroupBalanceResponse getGroupBalances(Long groupId, User currentUser);
}