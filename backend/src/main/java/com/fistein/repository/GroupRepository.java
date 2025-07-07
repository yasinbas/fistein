package com.fistein.repository;

import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    List<Group> findByCreatedBy(User user);
    
    @Query("SELECT g FROM Group g JOIN g.members gm WHERE gm.user.id = :userId")
    List<Group> findByMemberId(@Param("userId") Long userId);
    
    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :userId OR g.id IN " +
           "(SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId)")
    List<Group> findAllUserGroups(@Param("userId") Long userId);
}