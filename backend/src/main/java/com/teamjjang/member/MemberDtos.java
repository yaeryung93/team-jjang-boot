package com.teamjjang.member;

import jakarta.validation.constraints.*;

public final class MemberDtos {
    private MemberDtos() {}
    public record Create(@NotBlank @Size(max = 50) String name,
                         @NotBlank @Size(max = 50) String role) {}
    public record View(Long id, Long projectId, String name, String role) {
        public static View from(Member member) {
            return new View(member.getId(), member.getProjectId(), member.getName(), member.getRole());
        }
    }
}
