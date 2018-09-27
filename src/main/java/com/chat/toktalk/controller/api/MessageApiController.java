package com.chat.toktalk.controller.api;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.UploadFile;
import com.chat.toktalk.dto.SendType;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.dto.UnreadMessageInfo;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.UploadFileService;
import com.chat.toktalk.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "/api/messages", produces = "application/json; charset=utf8")
public class MessageApiController {

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelUserService channelUserService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> messages(Long channelId, LoginUserInfo loginUserInfo) {
        if (loginUserInfo != null) {
            ChannelUser channelUser = channelUserService.getChannelUser(channelId, loginUserInfo.getId());
            if (channelUser != null) {
                List<Message> messages = messageService.getMessagesByChannelUser(channelId, channelUser.getFirstReadId());
                return new ResponseEntity<>(messages, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/unread")
    public ResponseEntity<UnreadMessageInfo> unread(Long channelId, LoginUserInfo loginUserInfo) {
        ChannelUser channelUser = channelUserService.getChannelUser(channelId, loginUserInfo.getId());
        if (channelUser != null) {
            Long unread = messageService.countUnreadMessageByChannelUser(channelUser);
            if (unread > 0) {
                return new ResponseEntity<>(new UnreadMessageInfo(channelId, unread), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/file")
    @ResponseBody
    @Transactional
    public String upload(MultipartHttpServletRequest multipartRequest, LoginUserInfo loginUserInfo) { //Multipart로 받는다.
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<String> itr =  multipartRequest.getFileNames();

        String filePath = "/Users/osejin/fileEx"; //설정파일로 뺀다. 윈도우는 다른 패스로...

        String nickname = "";
        Long userId=0L;
        Long channelId = Long.parseLong(multipartRequest.getParameter("channelId"));

        if(loginUserInfo != null){
            nickname = loginUserInfo.getNickname();
            userId = loginUserInfo.getId();
        }

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

                SocketMessage socketMessage = new SocketMessage(SendType.UPLOAD_FILE);
                socketMessage.setChannelId(channelId);
                socketMessage.setNickname(nickname);
                socketMessage.setUploadFile(uploadFile);
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

            } catch (Exception e) {
                System.out.println("postTempFile_ERROR======>"+fileFullPath);
                e.printStackTrace();
            }
        }
        return "success";
    }
}