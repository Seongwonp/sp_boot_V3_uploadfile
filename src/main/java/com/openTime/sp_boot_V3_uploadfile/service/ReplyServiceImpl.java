package com.openTime.sp_boot_V3_uploadfile.service;

import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.domain.Reply;
import com.openTime.sp_boot_V3_uploadfile.dto.PageRequestDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageResponseDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.ReplyDTO;
import com.openTime.sp_boot_V3_uploadfile.repository.BoardRepository;
import com.openTime.sp_boot_V3_uploadfile.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional // 트랜잭션 가져올때 자카르타 가져오기 주의...
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    @Override
    public Long addReply(ReplyDTO replyDTO) {
        Board board = boardRepository.findById(replyDTO.getBno())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다!"));

        Reply reply = Reply.builder()
                .board(board)
                .replyText(replyDTO.getReplyText())
                .replyWriter(replyDTO.getReplyWriter())
                .build();

        reply = replyRepository.save(reply);
        return reply.getRno();
    }

    @Override
    public PageResponseDTO<ReplyDTO> getRepliesByBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList = result.getContent().stream().map(reply -> ReplyDTO.builder()
                .rno(reply.getRno())
                .bno(reply.getBoard().getBno())
                .replyText(reply.getReplyText())
                .replyWriter(reply.getReplyWriter())
                .replyDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build()
        ).toList();

        return PageResponseDTO.<ReplyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .total((int) result.getTotalElements())
                .build();
    }


    @Override
    public Long ReplyCountByBno(Long bno) {
        return replyRepository.countByBoardBno(bno);
    }

    @Override
    public void modifyReply(ReplyDTO replyDTO) {
        Optional<Reply> result = replyRepository.findById(replyDTO.getRno());
        Reply reply = result.orElseThrow();
        reply.changeText(replyDTO.getReplyText());
        replyRepository.save(reply);
    }

    @Override
    public void removeReply(Long rno ) {
        replyRepository.deleteById(rno);
    }

    @Override
    public void removeRepliesByBno(Long bno) {
        log.info("removeRepliesByBno : {}", bno);
        replyRepository.deleteByBoardBno(bno);
        replyRepository.flush();
    }

    @Override
    public ReplyDTO getReply(Long rno) {
        Optional<Reply> replyOptional =  replyRepository.findById(rno);
        Reply reply = replyOptional.orElseThrow();
        return ReplyDTO.builder()
                .rno(reply.getRno())
                .bno(reply.getBoard().getBno())
                .replyText(reply.getReplyText())
                .replyWriter(reply.getReplyWriter())
                .replyDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }
}
