package com.openTime.sp_boot_V3_uploadfile.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.domain.QBoard;
import com.openTime.sp_boot_V3_uploadfile.domain.QReply;
import com.openTime.sp_boot_V3_uploadfile.dto.BoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;


public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        QBoard board = QBoard.board; // Q 도메인 객체.

        // 1. JPQL 작성
        // JPQLQuery 는 @Query 로 작성했던 JPQL를 코드를 통해서 생성할 수 있게 함.
        JPQLQuery<Board> query = from(board); // select * from board
//        query.where(board.title.contains("1")); //where title like '%1%'

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // 쿼리에 괄호 ( 삽입 용도. , and 연산 우선을 막기위함.
        booleanBuilder.or(board.title.contains("1")); // title like '%1%'
        booleanBuilder.or(board.content.contains("1")); // or content like '%1%'

        query.where(booleanBuilder); // or 조건을 쿼리에 넣어주기 , where ( title ... or content ... )
        query.where(board.bno.gt(0L)); // and( bno > 0 ) and 조건. (gt는 getter)


        // Pageable 을 처리하는 방법은 QuerydslRepositorySupport 라는 클래스의 기능을 이용.
        this.getQuerydsl().applyPagination(pageable, query);

        //2. JPQL 실행 -> JPQLQuery 실행은 fetch(). fetchCount() 이용시 count 쿼리를 실행.
        List<Board> boardList = query.fetch(); // fetch() : 쿼리 실행
        long count = query.fetchCount(); // fetchCount : count 쿼리 실행

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board; // Q 도메인 객체.

        // 1. JPQL 작성
        JPQLQuery<Board> query = from(board); // select * from board b

        // 검색 조건과 키워드가 있다면... ( t type , c content , w writer)
        if ((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder(); // 쿼리에 괄호 ( 삽입 용도. , and 연산 우선을 막기위함.
            for (String type : types) { // 배열 이상으로 검사할 필요는 없음.
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword)); // t 가있는지 검사 , 있으면 or 추가
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword)); // c 가 있는지 검사 , 있으면 or 추가
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword)); // w 가 있는지 검사 , 있으면 or 추가
                        break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(board.bno.gt(0L)); // and( bno > 0 )
        this.getQuerydsl().applyPagination(pageable, query);// Pageable 처리

        // 2. JPQL 실행.
        List<Board> boardList = query.fetch(); // fetch() : 쿼리 실행
        long count = query.fetchCount(); // fetchCount : count 쿼리 실행

        // 3. 결과 확인
        return new PageImpl<>(boardList, pageable, count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board);

        if (types != null && keyword != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t" -> builder.or(board.title.contains(keyword));
                    case "w" -> builder.or(board.writer.contains(keyword));
                    case "c" -> builder.or(board.content.contains(keyword));
                }
            }
            query.where(builder);
        }

        query.where(board.bno.gt(0L));
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.constructor(
                BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count()
        ));
        getQuerydsl().applyPagination(pageable, dtoQuery);
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
        long total = dtoQuery.fetchCount(); // 전체 검색 수 (groupBy 기준이라 정확한 페이징 보장)

        return new PageImpl<>(dtoList, pageable, total);
    }
}
