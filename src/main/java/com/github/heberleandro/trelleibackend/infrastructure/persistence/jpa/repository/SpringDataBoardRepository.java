package com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.repository;

import com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.entity.BoardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataBoardRepository extends JpaRepository<BoardJpaEntity, Integer> {

    List<BoardJpaEntity> findAllByOwnerId(Integer ownerId);
}
