package com.openTime.sp_boot_V3_uploadfile.service;

import com.openTime.sp_boot_V3_uploadfile.dto.BoardDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.BoardListReplyCountDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageRequestDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageResponseDTO;

public interface BoardService {
    Long add(BoardDTO boardDTO); // 번호를 반환하도록 메서드 설정해보자.
    BoardDTO searchOne(Long bno);
    void modify(BoardDTO boardDTO);
    void remove(Long bno);
    PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListReplyCountDTO> getListWithReplyCount(PageRequestDTO pageRequestDTO);
}
