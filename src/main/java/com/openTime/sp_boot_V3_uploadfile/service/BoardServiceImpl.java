package com.openTime.sp_boot_V3_uploadfile.service;

import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.domain.QBoard;
import com.openTime.sp_boot_V3_uploadfile.domain.QReply;
import com.openTime.sp_boot_V3_uploadfile.dto.*;
import com.openTime.sp_boot_V3_uploadfile.repository.BoardRepository;
import com.querydsl.jpa.JPQLQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional // 트랜잭션 가져올때 자카르타 가져오기 주의...
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long add(BoardDTO boardDTO) {
        Board board = dtoToEntity(boardDTO);
        return boardRepository.save(board).getBno();
    }

    @Override
    public BoardDTO searchOne(Long bno) {
        Board board = boardRepository.findById(bno).orElseThrow();
        return entityToDto(board);
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Board board = boardRepository.findById(boardDTO.getBno()).orElseThrow();
        board.change(boardDTO.getTitle(), boardDTO.getContent());

        board.clearImages();

        if(boardDTO.getFileNames() != null){
            for(String fileName : boardDTO.getFileNames()){
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> getList(PageRequestDTO pageRequestDTO) {

        // 1. 검색 조건 및 페이징 정보 설정
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 2. 레포지토리 호출 및 결과 처리
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        // 고전적인 변경방법.

//        List<BoardDTO> dtoList = new ArrayList<>();
//        for (Board board : result.getContent()) {
//            dtoList.add(modelMapper.map(board, BoardDTO.class));
//        }

        // 트랜드 1
//
//        List<BoardDTO> dtoList = result
//                .stream()
//                .map(board -> modelMapper.map(board, BoardDTO.class))
//                .collect(Collectors.toList());

        // 간략화
//        List<BoardDTO> dtoList = result
//                .stream()
//                .map(board -> modelMapper.map(board, BoardDTO.class))
//                .toList();

        // 더 간략화
        List<BoardDTO> dtoList = result
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .getContent();

        log.info("pageResponseDTO : {}", dtoList);

        // 3. PageResponseDTO 생성
        // 전체 개수 기준으로 총 페이지 계산
        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements()) // 전체 개수 기준으로 총 페이지 계산
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> getListWithReplyCount(PageRequestDTO pageRequestDTO) {
        // 1. 검색 조건 및 페이징 정보 설정
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 2. 레포지토리 호출 및 결과 처리
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        log.info("pageResponseDTO : {}", result);
        

        // 3. PageResponseDTO 생성
        // 전체 개수 기준으로 총 페이지 계산
        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements()) // 전체 개수 기준으로 총 페이지 계산
                .build();
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> getListWithAll(PageRequestDTO pageRequestDTO) {
        // 1. 검색 조건 및 페이징 정보 설정
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 2. 레포지토리 호출 및 결과 처리
        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, keyword, pageable);

        log.info("pageResponseDTO : {}", result);


        // 3. PageResponseDTO 생성
        // 전체 개수 기준으로 총 페이지 계산
        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements()) // 전체 개수 기준으로 총 페이지 계산
                .build();
    }


}
