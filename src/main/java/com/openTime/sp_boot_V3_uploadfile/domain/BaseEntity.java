package com.openTime.sp_boot_V3_uploadfile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 공통으로 작성되는 부모 클래스를 말함.
@EntityListeners(AuditingEntityListener.class) // 데이터 베이스에 추가되거나 변경될 때 자동으로 시간 값을 지정할 수 있음.
@Getter
public class BaseEntity {

    @CreatedDate // 데이터가 생성된 시간 작성
    @Column(name="reg_date", updatable=false) // 언더바법사용, sql 에서 대문자 인식불가 문제해결. 업데이트 실행시 빠지게
    private LocalDateTime regDate; // 등록일

    @LastModifiedDate
    @Column(name="mod_date")
    private LocalDateTime modDate; // 수정일
}
