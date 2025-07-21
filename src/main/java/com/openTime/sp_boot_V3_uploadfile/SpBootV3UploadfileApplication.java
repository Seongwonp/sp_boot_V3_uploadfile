package com.openTime.sp_boot_V3_uploadfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // jpa auditing(듣다, 감시하다, 청강하다 등..)
public class SpBootV3UploadfileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpBootV3UploadfileApplication.class, args);
    }

}
