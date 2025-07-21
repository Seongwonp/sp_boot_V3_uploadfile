package com.openTime.sp_boot_V3_uploadfile.controller;

import com.openTime.sp_boot_V3_uploadfile.dto.PageRequestDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageResponseDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.ReplyDTO;
import com.openTime.sp_boot_V3_uploadfile.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyController {

    private final ReplyService replyService;

    // 댓글 등록
    @Operation(summary = "댓글 등록", description = "댓글을 JSON으로 받아 등록합니다.")
    @PostMapping("")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                      BindingResult bindingResult) throws BindException {
        log.info("댓글 등록 요청 : {}", replyDTO);
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);

        Long rno = replyService.addReply(replyDTO);
        return ResponseEntity.ok(Map.of("rno", rno));
    }

    // 댓글 목록 조회 (페이징 포함)
    @Operation(summary = "댓글 목록", description = "게시글 번호 기준으로 댓글 목록을 페이징 처리하여 조회")
    @GetMapping("/list/{bno}")
    public ResponseEntity<PageResponseDTO<ReplyDTO>> getList(@PathVariable("bno") Long bno,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getRepliesByBoard(bno, pageRequestDTO);
        log.info("댓글 목록: {}", responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 특정 댓글 조회
    @Operation(summary = "댓글 조회", description = "댓글 번호(rno)으로 상세 댓글을 조회")
    @GetMapping("/{rno}")
    public ResponseEntity<ReplyDTO> getReply(@PathVariable("rno") Long rno) {
        return ResponseEntity.ok(replyService.getReply(rno));
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정", description = "댓글 번호(rno)를 이용하여 내용 수정")
    @PutMapping("/{rno}")
    public ResponseEntity<Map<String, Long>> update(@PathVariable("rno") Long rno,
                                                    @RequestBody ReplyDTO replyDTO) {
        replyDTO.setRno(rno);
        replyService.modifyReply(replyDTO);
        return ResponseEntity.ok(Map.of("rno", rno));
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "댓글 번호(rno)를 이용해 삭제 처리")
    @DeleteMapping("/{rno}")
    public ResponseEntity<Map<String, Long>> delete(@PathVariable("rno") Long rno) {
        replyService.removeReply(rno);
        return ResponseEntity.ok(Map.of("rno", rno));
    }
}
