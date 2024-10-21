<script setup>
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import RemoteApiProxyView from '@/views/RemoteApiProxyView.vue'
const locale = zhCn
</script>

<template>
  <el-config-provider size="default" :button="{ autoInsertSpace: true }" :locale="locale">
    <RemoteApiProxyView />
  </el-config-provider>
</template>

<style lang="scss">
@use '@/assets/base.scss';
</style>
