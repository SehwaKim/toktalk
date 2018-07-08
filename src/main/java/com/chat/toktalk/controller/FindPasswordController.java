package com.chat.toktalk.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/identity/password")
public class PasswordController {

    @GetMapping("/reset")
    public String displayForgotPasswordPage(){

        //이메일 입력폼 보여주기.
            //


        return null;
    }

    @PostMapping("/reset")
    public String processForgotPassword(){
        //입력된 이메일로 사용자 조회
            //조회가 되지 않는다면, 이메일에 해당하는 사용자없음 뿌리기
        //토큰 발행 UUID
            //UUID 토큰 레퍼지토리에 저장
        //이메일로 패스워드 변경 URL보냄 - 파라미터로 토큰같이 보냄

        //메일 확인요청 패스워드로 리다이랙트 시킴


        return null;
    }

    @GetMapping
    public String displayResetPasswordPage(){

        //이메일에서 링크를 누르면 여기서 처리됨
        //비밀번호변경 페이지 보여줌
            //전달받은 토큰만료시,
            //토큰이 null일때,
            //또는 토큰이 같지 않을때
            //오류처리
        return null;
    }

    @PostMapping
    public String handlePasswordReset(){
        //변경된 password저장
        //DB에서 토큰삭제
        //login  page로 이동
        //파라미터 true ----- > 비밀번호 변경이 성공적으로 됬으니까 로그인 해봐 메시지 보여주
        return null;
    }
}
