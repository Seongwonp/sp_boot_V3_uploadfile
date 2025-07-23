package com.openTime.sp_boot_V3_uploadfile.service;

import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.domain.BoardImage;
import com.openTime.sp_boot_V3_uploadfile.dto.*;

import java.util.ArrayList;
import java.util.List;

public interface BoardService {
    Long add(BoardDTO boardDTO); // 번호를 반환하도록 메서드 설정해보자.
    BoardDTO searchOne(Long bno);
    void modify(BoardDTO boardDTO);
    void remove(Long bno);
    PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListReplyCountDTO> getListWithReplyCount(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListAllDTO> getListWithAll(PageRequestDTO pageRequestDTO);


    default Board dtoToEntity(BoardDTO boardDTO) {
        Board board  = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();


        // 첨부파일 추가
        if(boardDTO.getFileNames() != null){
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }

    default BoardDTO entityToDto(Board board) {

        // 첨부파일 제외처리
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(String.valueOf(board.getRegDate()))
                .modDate(String.valueOf(board.getModDate()))
                .build();

        // 첨부파일 처리
        if(board.getImageSet() != null && !board.getImageSet().isEmpty()){
            List<String> fileNameList = new ArrayList<>();
            for(BoardImage boardImage : board.getImageSet()){
                String combinedName = boardImage.getUuid() + "_" + boardImage.getFileName();
                fileNameList.add(combinedName);
            }
            boardDTO.setFileNames(fileNameList);
        }
        return boardDTO;
    }

}
