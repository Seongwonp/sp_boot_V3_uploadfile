package com.openTime.sp_boot_V3_uploadfile.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

//일종의 vo 형태

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity{
    @Id //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 타입을 identity에 맡긴다.
    private Long bno;

    @Column(length=500 , nullable=false) // length = 테이블의 길이 , nullable 널가능 ? = 안됨
    private String title;

    @Column(length=2000 , nullable=false)
    private String content;

    @Column(length=50 , nullable=false)
    private String writer;

    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL}
            ,fetch = FetchType.LAZY,
            orphanRemoval = true) // BoardImage 의 board 변수
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages(){
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }


    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }
}
