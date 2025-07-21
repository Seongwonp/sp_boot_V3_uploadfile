package com.openTime.sp_boot_V3_uploadfile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="reply", indexes = {
        @Index(name="idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    //외래키 설정
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    private Board board;

    private String replyText;
    private String replyWriter;


    public void changeText(String newText){
        this.replyText = newText;
    }

}
