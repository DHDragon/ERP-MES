package com.erp.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 扫描配置
 *
 * 注意：MapperScan 仅接受标准 package 名称（不支持 ".." 这种模式）。
 * 后续每新增一个模块的 mapper 包，可在此处追加。
 */
@Configuration
@MapperScan(basePackages = {
        "com.erp.system.mapper"
})
public class MybatisMapperScanConfig {
}
