package com.chat.toktalk.controller.api;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.UploadFile;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.UploadFileService;
import com.chat.toktalk.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    MessageService messageService;

    @RequestMapping(value = "/fileUpload/post") //ajax에서 호출하는 부분
    @ResponseBody
    @Transactional
    public String upload(MultipartHttpServletRequest multipartRequest) { //Multipart로 받는다.
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<String> itr =  multipartRequest.getFileNames();

        String filePath = "/Users/osejin/fileEx"; //설정파일로 뺀다. 윈도우는 다른 패스로...

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        String nickname = "";
        Long userId=0L;
        Long channelId = Long.parseLong(multipartRequest.getParameter("channelId"));

        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
            LoginUserInfo loginUserInfo = (LoginUserInfo)authentication.getPrincipal();
            nickname = loginUserInfo.getNickname();
            userId = loginUserInfo.getId();
        }

        List<UploadFile> uploadFiles = new ArrayList<>();

        while (itr.hasNext()) { //받은 파일들을 모두 돌린다.

            /* 기존 주석처리
            MultipartFile mpf = multipartRequest.getFile(itr.next());
            String originFileName = mpf.getOriginalFilename();
            System.out.println("FILE_INFO: "+originFileName); //받은 파일 리스트 출력'
            */

            MultipartFile mpf = multipartRequest.getFile(itr.next());

            String originalFilename = mpf.getOriginalFilename(); //파일명
            String fileFullPath = filePath+"/"+originalFilename; //파일 전체 경로
            String fileType = mpf.getContentType();
            Long fileLen = mpf.getSize();

            try {
                //임시파일 저장
                mpf.transferTo(new File(fileFullPath)); //파일저장 실제로는 service에서 처리.

                System.out.println("originalFilename => "+originalFilename);
                System.out.println("fileFullPath => "+fileFullPath);

                UploadFile uploadFile = new UploadFile();
                uploadFile.setFileName(originalFilename);
                uploadFile.setContentType(fileType);
                uploadFile.setLength(fileLen);
                uploadFile.setSaveFileName(filePath);

                uploadFiles.add(uploadFile);

                // 나중에  mysql로 넣어야함... 회의 한 후에
                uploadFileService.addUploadFile(uploadFile);
                System.out.println("파일 저장 성공!");

                Message message = new Message();
                message.setChannelId(channelId);
                message.setUserId(userId);
                message.setNickname(nickname);
                message.setType("file");
                message.setUploadFile(uploadFile);
                message.setRegdate(LocalDateTime.now());
                messageService.addMessage(message);

                SocketMessage socketMessage = new SocketMessage(channelId,nickname,uploadFiles);
                messageSender.sendMessage(socketMessage);
                // json test
                String strJson = objectMapper.writeValueAsString(uploadFile);

                // 다운로드해보기
//                List<String> mapstream = Collections.emptyList();
//                try (Stream<UploadFile> stream = uploadFileService.getUploadFileByFileName(originalFilename)) {
//                    mapstream = stream.map(uploadFile1 -> uploadFile1.toString()).collect(Collectors.toList());
//                }
//
//                System.out.println("download : " + mapstream.toString());
                // 다운로드 2

            } catch (Exception e) {
                System.out.println("postTempFile_ERROR======>"+fileFullPath);
                e.printStackTrace();
            }
        }
        return "success";
    }
}