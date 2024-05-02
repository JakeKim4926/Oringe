package com.ssafy.devway.domain.member.controller;

import com.ssafy.devway.domain.member.document.Member;
import com.ssafy.devway.domain.member.dto.request.MemberSignupReqDto;
import com.ssafy.devway.domain.member.service.MemberService;
import com.ssafy.devway.global.api.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oringe/api")
@Tag(name = "회원", description = "member API")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Member>  signup(@RequestBody MemberSignupReqDto dto){
       return ResponseEntity.ok(memberService.signup(dto));
    }

    @GetMapping("/signin")
    public ResponseEntity<Member> signin(String memberEmail){
        return ResponseEntity.ok(memberService.signin(memberEmail));
    }

    @GetMapping("/valid")
    public ApiResponse<Boolean> valid(@RequestParam String memberEmail){
        return ApiResponse.ok(memberService.validMember(memberEmail));
    }

}
