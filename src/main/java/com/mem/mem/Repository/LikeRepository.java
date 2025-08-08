package com.mem.mem.Repository;



import com.mem.mem.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndMemoryId(Long userId, Long memoryId);
    boolean existsByUserIdAndMemoryId(Long userId, Long memoryId);
    void deleteByUserIdAndMemoryId(Long userId, Long memoryId);
}