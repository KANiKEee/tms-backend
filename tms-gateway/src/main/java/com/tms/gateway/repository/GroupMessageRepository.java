package com.tms.gateway.repository;

import com.tms.gateway.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findByGroupIdOrderBySentAtAsc(Long groupId);

    GroupMessage findTopByGroupIdOrderBySentAtDesc(Long groupId);
}
