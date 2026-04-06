<template>
  <el-card shadow="never">
    <template #header>数据权限配置说明</template>
    <el-alert title="当前版本为占位页面，后续可扩展为角色维度数据权限配置界面" type="info" :closable="false" />
    <el-descriptions :column="1" style="margin-top: 12px" border>
      <el-descriptions-item label="后端表">cfg_data_scope</el-descriptions-item>
      <el-descriptions-item label="当前能力">已建表 + Base实体/Mapper + 数据权限切面占位</el-descriptions-item>
      <el-descriptions-item label="建议下一步">补充角色绑定范围、组织树选择、SQL注入规则</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>
