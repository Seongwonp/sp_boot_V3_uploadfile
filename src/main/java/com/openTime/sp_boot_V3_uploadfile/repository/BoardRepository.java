package com.openTime.sp_boot_V3_uploadfile.repository;


import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch { //Board 상속, id의 데이터 타입 지정.
    // 인터페이스 까지만 만들면... crud 완성.
    @Query(value = "SELECT now()", nativeQuery = true)
    String getTime();


    @EntityGraph(attributePaths = {"imageSet"})
    @Query("SELECT b FROM Board b WHERE b.bno = :bno")
    Optional<Board> findByBnoWithImage(Long bno);

}
