package com.openTime.sp_boot_V3_uploadfile.repository;

import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.domain.Reply;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@SpringBootTest
@Log4j2
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {
        Long bno = 91L;
        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("재밌어요!")
                .replyWriter("작성자5")
                .build();
        replyRepository.save(reply);
    }

    @Test
    public void listOfBoardTest(){
        Long bno = 91L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());
        Page<Reply> replies = replyRepository.listOfBoard(bno, pageable);
        replies.getContent().forEach(log::info);
    }


}