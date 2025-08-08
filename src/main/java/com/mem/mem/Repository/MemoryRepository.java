package com.mem.mem.Repository;



import com.mem.mem.models.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT m FROM Memory m LEFT JOIN FETCH m.likes ORDER BY m.createdAt DESC")
    List<Memory> findAllWithLikes();
    
    @Query("SELECT m FROM Memory m WHERE m.user.id = :userId")
    List<Memory> findByUserId(@Param("userId") Long userId);
}