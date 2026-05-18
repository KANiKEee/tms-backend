package com.tms.gateway.controller;

import com.tms.gateway.entity.*;
import com.tms.gateway.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final ChatGroupMemberRepository chatGroupMemberRepository;
    private final GroupMessageRepository groupMessageRepository;

    // ══════════════════════════════════════════════
    // ═══  AUTO-CREATE DEFAULT tms-group  ═══
    // ══════════════════════════════════════════════
    @PostConstruct
    @Transactional
    public void initDefaultGroup() {
        Optional<ChatGroup> existing = chatGroupRepository.findByNameAndIsDefault("tms-group", true);
        ChatGroup group;
        if (existing.isEmpty()) {
            group = ChatGroup.builder()
                    .name("tms-group")
                    .description("Groupe général — tous les utilisateurs")
                    .isDefault(true)
                    .build();
            group = chatGroupRepository.save(group);
        } else {
            group = existing.get();
        }

        // Add all existing users who aren't members yet
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (!chatGroupMemberRepository.existsByGroupIdAndUserId(group.getId(), u.getId())) {
                chatGroupMemberRepository.save(
                        ChatGroupMember.builder()
                                .groupId(group.getId())
                                .userId(u.getId())
                                .username(u.getUsername())
                                .build()
                );
            }
        }
    }

    // ══════════════════════════════════════════════
    // ═══  DIRECT MESSAGES (existing)  ═══
    // ══════════════════════════════════════════════

    // ── Send a message ──
    @PostMapping("/send")
    public ResponseEntity<Message> send(@AuthenticationPrincipal User sender,
                                        @RequestBody Map<String, Object> body) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = body.get("content").toString();

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        Message msg = Message.builder()
                .senderId(sender.getId())
                .senderUsername(sender.getUsername())
                .receiverId(receiver.getId())
                .receiverUsername(receiver.getUsername())
                .content(content)
                .build();

        return ResponseEntity.ok(messageRepository.save(msg));
    }

    // ── Get conversation with a specific user ──
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<Message>> getConversation(@AuthenticationPrincipal User me,
                                                          @PathVariable Long userId) {
        List<Message> messages = messageRepository.findConversation(me.getId(), userId);
        // Mark received messages as read
        messages.stream()
                .filter(m -> m.getReceiverId().equals(me.getId()) && !m.isRead())
                .forEach(m -> { m.setRead(true); messageRepository.save(m); });
        return ResponseEntity.ok(messages);
    }

    // ── Get all conversations (contact list with last message) ──
    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations(@AuthenticationPrincipal User me) {
        List<Long> partners = messageRepository.findConversationPartners(me.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long partnerId : partners) {
            User partner = userRepository.findById(partnerId).orElse(null);
            if (partner == null) continue;

            List<Message> conv = messageRepository.findConversation(me.getId(), partnerId);
            Message lastMsg = conv.isEmpty() ? null : conv.get(conv.size() - 1);
            long unread = conv.stream()
                    .filter(m -> m.getReceiverId().equals(me.getId()) && !m.isRead())
                    .count();

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("userId", partner.getId());
            entry.put("username", partner.getUsername());
            entry.put("role", partner.getRole().name());
            entry.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            entry.put("lastMessageAt", lastMsg != null ? lastMsg.getSentAt() : null);
            entry.put("unreadCount", unread);
            result.add(entry);
        }

        // Sort by last message time desc
        result.sort((a, b) -> {
            Object atA = a.get("lastMessageAt");
            Object atB = b.get("lastMessageAt");
            if (atA == null && atB == null) return 0;
            if (atA == null) return 1;
            if (atB == null) return -1;
            return atB.toString().compareTo(atA.toString());
        });

        return ResponseEntity.ok(result);
    }

    // ── Get all users (to start a new conversation) ──
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers(@AuthenticationPrincipal User me) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("userId", u.getId());
            m.put("username", u.getUsername());
            m.put("role", u.getRole().name());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ── Unread count ──
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal User me) {
        return ResponseEntity.ok(Map.of("count", messageRepository.countUnreadByReceiver(me.getId())));
    }

    // ══════════════════════════════════════════════
    // ═══  GROUP CHAT  ═══
    // ══════════════════════════════════════════════

    // ── Get my groups ──
    @GetMapping("/groups")
    public ResponseEntity<List<Map<String, Object>>> getMyGroups(@AuthenticationPrincipal User me) {
        List<Long> groupIds = chatGroupMemberRepository.findGroupIdsByUserId(me.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long gid : groupIds) {
            ChatGroup group = chatGroupRepository.findById(gid).orElse(null);
            if (group == null) continue;

            List<ChatGroupMember> members = chatGroupMemberRepository.findByGroupId(gid);
            GroupMessage lastMsg = groupMessageRepository.findTopByGroupIdOrderBySentAtDesc(gid);

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", group.getId());
            entry.put("name", group.getName());
            entry.put("description", group.getDescription());
            entry.put("isDefault", group.isDefault());
            entry.put("memberCount", members.size());
            entry.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            entry.put("lastMessageAt", lastMsg != null ? lastMsg.getSentAt() : null);
            entry.put("lastSenderUsername", lastMsg != null ? lastMsg.getSenderUsername() : null);
            result.add(entry);
        }

        // Sort: default group first, then by last message time
        result.sort((a, b) -> {
            boolean aDefault = (boolean) a.getOrDefault("isDefault", false);
            boolean bDefault = (boolean) b.getOrDefault("isDefault", false);
            if (aDefault && !bDefault) return -1;
            if (!aDefault && bDefault) return 1;
            Object atA = a.get("lastMessageAt");
            Object atB = b.get("lastMessageAt");
            if (atA == null && atB == null) return 0;
            if (atA == null) return 1;
            if (atB == null) return -1;
            return atB.toString().compareTo(atA.toString());
        });

        return ResponseEntity.ok(result);
    }

    // ── Create a new group ──
    @PostMapping("/groups")
    @Transactional
    public ResponseEntity<Map<String, Object>> createGroup(@AuthenticationPrincipal User me,
                                                            @RequestBody Map<String, Object> body) {
        String name = body.get("name").toString();
        String description = body.getOrDefault("description", "").toString();
        @SuppressWarnings("unchecked")
        List<Number> memberIds = (List<Number>) body.getOrDefault("memberIds", List.of());

        ChatGroup group = ChatGroup.builder()
                .name(name)
                .description(description)
                .createdBy(me.getId())
                .createdByUsername(me.getUsername())
                .build();
        group = chatGroupRepository.save(group);

        // Add creator as member
        chatGroupMemberRepository.save(
                ChatGroupMember.builder()
                        .groupId(group.getId())
                        .userId(me.getId())
                        .username(me.getUsername())
                        .build()
        );

        // Add other members
        for (Number mid : memberIds) {
            Long userId = mid.longValue();
            if (userId.equals(me.getId())) continue;
            User u = userRepository.findById(userId).orElse(null);
            if (u == null) continue;
            if (!chatGroupMemberRepository.existsByGroupIdAndUserId(group.getId(), userId)) {
                chatGroupMemberRepository.save(
                        ChatGroupMember.builder()
                                .groupId(group.getId())
                                .userId(userId)
                                .username(u.getUsername())
                                .build()
                );
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", group.getId());
        result.put("name", group.getName());
        result.put("description", group.getDescription());
        result.put("memberCount", chatGroupMemberRepository.findByGroupId(group.getId()).size());

        return ResponseEntity.ok(result);
    }

    // ── Get group details (members) ──
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroupDetails(@PathVariable Long groupId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        List<ChatGroupMember> members = chatGroupMemberRepository.findByGroupId(groupId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", group.getId());
        result.put("name", group.getName());
        result.put("description", group.getDescription());
        result.put("isDefault", group.isDefault());
        result.put("createdBy", group.getCreatedByUsername());
        result.put("createdAt", group.getCreatedAt());
        result.put("members", members.stream().map(m -> {
            Map<String, Object> mm = new LinkedHashMap<>();
            mm.put("userId", m.getUserId());
            mm.put("username", m.getUsername());
            mm.put("joinedAt", m.getJoinedAt());
            return mm;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(result);
    }

    // ── Get group messages ──
    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMessageRepository.findByGroupIdOrderBySentAtAsc(groupId));
    }

    // ── Send group message ──
    @PostMapping("/groups/{groupId}/messages")
    public ResponseEntity<GroupMessage> sendGroupMessage(@AuthenticationPrincipal User sender,
                                                          @PathVariable Long groupId,
                                                          @RequestBody Map<String, Object> body) {
        String content = body.get("content").toString();
        String messageType = body.getOrDefault("messageType", "TEXT").toString();

        // Ensure user is a member (or auto-add for default group)
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        if (!chatGroupMemberRepository.existsByGroupIdAndUserId(groupId, sender.getId())) {
            if (group.isDefault()) {
                chatGroupMemberRepository.save(
                        ChatGroupMember.builder()
                                .groupId(groupId)
                                .userId(sender.getId())
                                .username(sender.getUsername())
                                .build()
                );
            } else {
                throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
            }
        }

        GroupMessage msg = GroupMessage.builder()
                .groupId(groupId)
                .senderId(sender.getId())
                .senderUsername(sender.getUsername())
                .content(content)
                .messageType(messageType)
                .build();

        return ResponseEntity.ok(groupMessageRepository.save(msg));
    }

    // ── Add members to group ──
    @PostMapping("/groups/{groupId}/members")
    @Transactional
    public ResponseEntity<Map<String, Object>> addMembers(@AuthenticationPrincipal User me,
                                                           @PathVariable Long groupId,
                                                           @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Number> memberIds = (List<Number>) body.get("memberIds");

        int added = 0;
        for (Number mid : memberIds) {
            Long userId = mid.longValue();
            User u = userRepository.findById(userId).orElse(null);
            if (u == null) continue;
            if (!chatGroupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
                chatGroupMemberRepository.save(
                        ChatGroupMember.builder()
                                .groupId(groupId)
                                .userId(userId)
                                .username(u.getUsername())
                                .build()
                );
                added++;
            }
        }

        return ResponseEntity.ok(Map.of("added", added,
                "totalMembers", chatGroupMemberRepository.findByGroupId(groupId).size()));
    }

    // ── Start a call/video in the group (creates a system message) ──
    @PostMapping("/groups/{groupId}/call")
    public ResponseEntity<GroupMessage> startCall(@AuthenticationPrincipal User me,
                                                   @PathVariable Long groupId,
                                                   @RequestBody Map<String, Object> body) {
        String callType = body.getOrDefault("type", "CALL_STARTED").toString(); // CALL_STARTED or VIDEO_STARTED
        String content = callType.contains("VIDEO")
                ? me.getUsername() + " a lancé un appel vidéo 📹"
                : me.getUsername() + " a lancé un appel vocal 📞";

        GroupMessage msg = GroupMessage.builder()
                .groupId(groupId)
                .senderId(me.getId())
                .senderUsername(me.getUsername())
                .content(content)
                .messageType(callType)
                .build();

        return ResponseEntity.ok(groupMessageRepository.save(msg));
    }
}
