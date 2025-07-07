package com.fistein.repository;

import com.fistein.entity.Group;
import com.fistein.entity.GroupMember;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByGroup(Group group);
    
    List<GroupMember> findByUser(User user);
    
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    
    boolean existsByGroupAndUser(Group group, User user);
    
    void deleteByGroupAndUser(Group group, User user);
}