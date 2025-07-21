package com.openTime.sp_boot_V3_uploadfile.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Getter
@ToString
@Log4j2
public class PageResponseDTO<E> {

    private final int page;
    private final int size;
    private final int total;

    // 시작 페이지 번호
    private final int start;

    // 끝 페이지 번호
    private  int end;

    // 이전 페이지의 존재 여부
    private final boolean prev;

    // 다음페이지의 존재 여부
    private final boolean next;

    // 리스트를 어떤 타입으로 오더라도 사용 가능하도록 하는 제네릭 리스트.
    private final List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        this.size = pageRequestDTO.getSize();
        this.dtoList = dtoList;
        this.total = total;

        int pageBlock = 10; // 한 번에 보여줄 페이지 번호 수
        int last = (int) Math.ceil((double) total / size);
        if (last == 0) last = 1; // total이 0일 때도 페이지 1은 존재하게끔

        int currentPage = pageRequestDTO.getPage();
        if (currentPage > last) {
            currentPage = last;
        } else if (currentPage < 1) {
            currentPage = 1;
        }

        // 중앙 정렬 계산
        int half = pageBlock / 2;
        int tempStart = Math.max(1, currentPage - half);
        int tempEnd = tempStart + pageBlock - 1;

        if (tempEnd > last) {
            tempEnd = last;
            tempStart = Math.max(1, tempEnd - pageBlock + 1);
        }

        this.page = currentPage;
        this.start = tempStart;
        this.end = tempEnd;

        this.prev = this.start > 1;
        this.next = currentPage < last;

    }
}
