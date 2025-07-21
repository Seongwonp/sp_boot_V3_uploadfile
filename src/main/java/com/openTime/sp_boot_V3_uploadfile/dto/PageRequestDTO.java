package com.openTime.sp_boot_V3_uploadfile.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default //빌더로 만들 때 기본값으로 사용하라~
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; // 검색 종류 1) title 2) content 3) writer t, tc , tcw , tw , c, w ,cw
    private String keyword;

    public String[] getTypes() {
        // type 문자열을 배열로 반환하기
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }
    public Pageable getPageable(String... props){
        return PageRequest.of(page - 1,size, Sort.by(props).descending());
    }

    public String getLink() {
        StringBuilder builder = new StringBuilder();

        builder.append("?page=").append(this.page);
        builder.append("&size=").append(this.size);

        if(type != null && type.isEmpty()) {
            builder.append("&types=").append(type);
        }
        if(keyword != null) {
            builder.append("&keyword=").append(keyword).append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
