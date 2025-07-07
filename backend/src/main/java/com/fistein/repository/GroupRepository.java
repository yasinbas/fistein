package com.fistein.repository;

import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    @Query("SELECT g FROM Group g " +
           "JOIN g.members m " +
           "WHERE m.user = :user AND m.isActive = true AND g.isActive = true")
    List<Group> findActiveGroupsByUser(@Param("user") User user);
    
    @Query("SELECT g FROM Group g " +
           "JOIN FETCH g.members m " +
           "WHERE g.id = :groupId AND g.isActive = true")
    Optional<Group> findByIdWithMembers(@Param("groupId") Long groupId);
    
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM GroupMember m " +
           "WHERE m.group.id = :groupId AND m.user.id = :userId AND m.isActive = true")
    boolean isUserMemberOfGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
}