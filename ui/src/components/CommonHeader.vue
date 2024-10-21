<script setup>
import { onUnmounted, ref } from 'vue'
import { MoreFilled } from '@element-plus//icons-vue'

let displayType = defineModel('displayType')
let logFilterDialogVisible = defineModel('logFilterDialogVisible')
let emit = defineEmits(['refreshLog', 'refreshConfig'])
let logAutoRefresh = ref(true)

let logAutoRefreshInterval = setInterval(() => {
  if (logAutoRefresh.value) {
    emit('refreshLog')
  }
}, 1000 * 10)

onUnmounted(() => {
  clearInterval(logAutoRefreshInterval)
})
</script>

<template>
  <div class="common-header">
    <el-radio-group v-model="displayType">
      <el-radio-button label="请求日志" value="log" />
      <el-radio-button label="代理配置" value="config" />
    </el-radio-group>
    <div class="common-header__right">
      <template v-if="displayType === 'log'">
        <el-button type="primary" @click="logFilterDialogVisible = true">过滤</el-button>
        <!-- <el-button type="primary" @click="$emit('refreshLog')">刷新</el-button>
        <el-checkbox v-model="logAutoRefresh" label="自动刷新" border /> -->
        <el-button type="primary">设置</el-button>
      </template>
      <template v-else>
        <el-button type="primary">过滤</el-button>
        <el-button type="primary" @click="$emit('refreshConfig')">刷新</el-button>
        <el-button type="primary">新增配置</el-button>
        <el-button circle>
          <el-icon><MoreFilled /></el-icon>
        </el-button>
      </template>
    </div>
  </div>
</template>

<style lang="scss">
.common-header {
  padding: 0 2rem;
  height: 5rem;
  min-height: 5rem;
  display: flex;
  align-items: start;
  position: sticky;
  top: 6rem;
  background-color: white;
  z-index: 10;
}

.common-header__right {
  width: 0;
  flex-grow: 1;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-end;
  gap: 1rem;

  .el-button + .el-button {
    margin: 0;
  }
}
</style>
