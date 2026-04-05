<template>
  <el-upload action="#" :auto-upload="false" drag>
    <el-icon><UploadFilled /></el-icon>
    <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
  </el-upload>
</template>

<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue'
</script>
