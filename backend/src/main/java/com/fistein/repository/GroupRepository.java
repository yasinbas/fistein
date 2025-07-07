package com.fistein.repository;

import com.fistein.entity.Group;
import com.fistein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Find groups created by a specific user
    List<Group> findByCreatedBy(User user);

    // Find groups where a user is a member
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    List<Group> findGroupsByMemberId(@Param("userId") Long userId);

    // Find groups by name containing (case insensitive)
    List<Group> findByNameContainingIgnoreCase(String name);

    // Find group by id with members loaded
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.members WHERE g.id = :groupId")
    Optional<Group> findByIdWithMembers(@Param("groupId") Long groupId);

    // Find group by id with expenses loaded
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.expenses WHERE g.id = :groupId")
    Optional<Group> findByIdWithExpenses(@Param("groupId") Long groupId);

    // Count groups created by user
    long countByCreatedBy(User user);

    // Check if user is member of group
    @Query("SELECT COUNT(g) > 0 FROM Group g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    boolean isUserMemberOfGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
}