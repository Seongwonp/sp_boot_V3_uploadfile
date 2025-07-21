package com.openTime.sp_boot_V3_uploadfile.repository;


import com.openTime.sp_boot_V3_uploadfile.domain.Board;
import com.openTime.sp_boot_V3_uploadfile.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardSearch { //Board 상속, id의 데이터 타입 지정.
    // 인터페이스 까지만 만들면... crud 완성.
}
