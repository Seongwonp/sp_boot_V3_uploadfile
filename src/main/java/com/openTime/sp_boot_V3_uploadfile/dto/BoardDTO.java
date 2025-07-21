package com.openTime.sp_boot_V3_uploadfile.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long bno;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String title;
    @NotEmpty
    private String content;
    @NotEmpty
    private String writer;
    private String regDate;
    private String modDate;

}
