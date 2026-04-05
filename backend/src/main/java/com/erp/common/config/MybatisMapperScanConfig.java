package com.erp.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.erp..mapper")
public class MybatisMapperScanConfig {
}
