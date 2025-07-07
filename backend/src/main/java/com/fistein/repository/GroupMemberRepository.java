package com.fistein.repository;

import com.fistein.entity.Group;
import com.fistein.entity.GroupMember;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    
    List<GroupMember> findByGroupAndIsActiveTrue(Group group);
    
    List<GroupMember> findByUserAndIsActiveTrue(User user);
    
    boolean existsByGroupAndUserAndIsActiveTrue(Group group, User user);
}