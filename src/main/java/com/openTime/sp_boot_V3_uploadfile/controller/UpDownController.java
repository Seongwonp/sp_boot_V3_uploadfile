package com.openTime.sp_boot_V3_uploadfile.controller;

import com.openTime.sp_boot_V3_uploadfile.dto.UploadFileDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.UploadResultDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Log4j2
@RestController
@RequestMapping("/api/file")
public class UpDownController {
    @Value("${com.opentime.path}")
    private String uploadPath;

    @Operation(summary = "POST 방식으로 파일등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        List<UploadResultDTO> list = new ArrayList<>();
        if (uploadFileDTO.getFiles() != null) {
            for (MultipartFile file : uploadFileDTO.getFiles()) {
                String fileName = file.getOriginalFilename();
                log.info("originalFilename: {}", fileName);

                //UUID + _ + 원래 파일명
                String uuid = UUID.randomUUID().toString();

                // 저장할 경로를 가져옴(파일이름 포함)
                Path path = Paths.get(uploadPath, uuid + "_" + fileName);

                boolean isImage = false;
                //실제 파일 저장
                try {
                    file.transferTo(path);

                    // 저장된 파일이 이미지파일일 경우 썸네일 생성
                    if (Files.probeContentType(path).startsWith("image")) {
                        log.info(Files.probeContentType(path));
                        isImage = true;
                        File thumbnail = new File(uploadPath, "s_" + uuid + "_" + fileName);
                        // (원본파일, 새로 생성할 파일, width,height) width,height 줄 긴축 기준
                        Thumbnailator.createThumbnail(path.toFile(), thumbnail, 200, 200);

                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(fileName)
                        .isImage(isImage)
                        .build());
            }
        }
        return list;
    }

    @Operation(summary = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> view(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        HttpHeaders headers = new HttpHeaders();
        try{
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }


    @Operation(summary = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean isRemoved = false; // 파일 삭제 결과를 저장할 함수

        //삭제 처리 (이미지일 경우 섬네일도 삭제)
        try{
            // 순서가 중요. 삭제전에 타입을 들고옴
            String contentType = Files.probeContentType(resource.getFile().toPath()); // 이미지인지 확인하기 위해
            isRemoved = resource.getFile().delete();
            resultMap.put(fileName, isRemoved);

            // 이미지라면 -> 섬네일이 존재한다면
            if (contentType.startsWith("image")) {
                File thumbnail = new File(uploadPath + File.separator + "s_" + fileName);
                if(thumbnail.delete()){
                    resultMap.put(thumbnail.getName(), isRemoved);
                }
            }

        }catch(IOException e){
            log.error(e.getMessage());
        }

        return resultMap;
    }

}
