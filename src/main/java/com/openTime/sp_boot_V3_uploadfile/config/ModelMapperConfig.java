package com.openTime.sp_boot_V3_uploadfile.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE) // private 설정
                .setMatchingStrategy(MatchingStrategies.LOOSE) // 엄격 근엄 진지
                .setFieldMatchingEnabled(true);
        return modelMapper;
    }
}
