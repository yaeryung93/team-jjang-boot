package com.teamjjang.member;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class MemberController {
    private final MemberService service;
    public MemberController(MemberService service) { this.service = service; }
    @GetMapping
    public List<MemberDtos.View> list(@PathVariable Long projectId) { return service.list(projectId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MemberDtos.View create(@PathVariable Long projectId, @Valid @RequestBody MemberDtos.Create input) {
        return service.create(projectId, input);
    }
}
