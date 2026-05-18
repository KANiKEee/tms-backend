package com.tms.gateway.repository;

import com.tms.gateway.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    Optional<ChatGroup> findByNameAndIsDefault(String name, boolean isDefault);

    List<ChatGroup> findByIsDefault(boolean isDefault);
}
