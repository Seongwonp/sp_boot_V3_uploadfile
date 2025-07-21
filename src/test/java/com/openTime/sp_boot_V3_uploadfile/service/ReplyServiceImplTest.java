package com.openTime.sp_boot_V3_uploadfile.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class ReplyServiceImplTest {

    @Autowired
    private  ReplyService replyService;

    @Test
    void addReply() {
    }

    @Test
    void getRepliesByBoard() {
    }

    @Test
    void replyCountByBno() {
        log.info("Reply Count: {}",replyService.ReplyCountByBno(91L));
    }


    @Test
    void removeReplyByBno(){
        replyService.removeRepliesByBno(5L);
    }
}