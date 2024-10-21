<script setup>
import { computed, ref } from 'vue'
import {
  groupBy as _groupBy,
  map as _map,
  orderBy as _orderBy,
  indexOf as _indexOf,
  remove as _remove,
  forEach as _forEach,
  filter as _filter
} from 'lodash-es'
import CodeMirror from '@/components/CodeMirror.vue'
import { json } from '@codemirror/lang-json'
import { ArrowUp, ArrowDown, MoreFilled } from '@element-plus/icons-vue'

let props = defineProps(['configs'])
let closedPathGroups = ref([])
let closedConfigs = ref([])

let stats = computed(() => {
  let configs = props.configs
  return {
    count: configs.length,
    activatedCount: _filter(configs, (n) => n.activated).length,
    filteredCount: 0,
    filteredActivatedCount: 0
  }
})

let groupByPathConfigs = computed(() => {
  let groupByPathObject = _groupBy(props.configs, (n) => n.httpMethod + ',' + n.path)
  let ret = _map(groupByPathObject, (value, key) => {
    let keyItems = key.split(',')
    return {
      httpMethod: keyItems[0],
      path: keyItems[1],
      configs: value
    }
  })
  return _orderBy(
    ret,
    [
      (n) => n.path,
      (n) => {
        return n.httpMethod === 'GET'
          ? 1
          : n.httpMethod === 'POST'
            ? 2
            : n.httpMethod === 'PUT'
              ? 3
              : n.httpMethod === 'DELETE'
                ? 4
                : 5
      }
    ],
    ['asc', 'asc']
  )
})
let isPathGroupClosed = computed(() => {
  return (httpMethodPath) => _indexOf(closedPathGroups.value, httpMethodPath) > -1
})
let isConfigClosed = computed(() => {
  return (configId) => _indexOf(closedConfigs.value, configId) > -1
})

function closePathGroup(httpMethodPath) {
  if (_indexOf(closedPathGroups.value, httpMethodPath) < 0) {
    closedPathGroups.value.push(httpMethodPath)
  }
}
function openPathGroup(httpMethodPath) {
  let index = _indexOf(closedPathGroups.value, httpMethodPath)
  if (index >= 0) {
    closedPathGroups.value.splice(index, 1)
  }
}
function closeConfig(configId) {
  if (_indexOf(closedConfigs.value, configId) < 0) {
    closedConfigs.value.push(configId)
  }
}
function openConfig(configId) {
  let index = _indexOf(closedConfigs.value, configId)
  if (index >= 0) {
    closedConfigs.value.splice(index, 1)
  }
}
</script>

<template>
  <div class="config-container">
    <div class="config-stats">
      <span>总数: {{ stats.count }} (过滤后: {{ stats.filteredCount }})</span>
      <span>已开启: {{ stats.activatedCount }} (过滤后: {{ stats.filteredActivatedCount }})</span>
      <span
        >未开启: {{ stats.count - stats.activatedCount }} (过滤后:
        {{ stats.filteredCount - stats.filteredActivatedCount }})</span
      >
      <span>请求地址数: 0 (过滤后: 0)</span>
    </div>
    <div
      class="config-group"
      :class="{ closed: isPathGroupClosed(g.httpMethod + ',' + g.path) }"
      v-for="g in groupByPathConfigs"
      :key="g.httpMethod + g.path"
    >
      <div class="config-group__header">
        <el-icon
          v-if="!isPathGroupClosed(g.httpMethod + ',' + g.path)"
          class="open-close"
          @click="closePathGroup(g.httpMethod + ',' + g.path)"
          ><ArrowUp
        /></el-icon>
        <el-icon
          v-if="isPathGroupClosed(g.httpMethod + ',' + g.path)"
          class="open-close"
          @click="openPathGroup(g.httpMethod + ',' + g.path)"
          ><ArrowDown
        /></el-icon>
        <div class="method-path">{{ g.httpMethod + ' ' + g.path }}</div>

        <el-icon><MoreFilled /></el-icon>
      </div>
      <div class="config-group__body">
        <div
          class="config"
          :class="{ closed: isConfigClosed(c.id), inactivated: !c.activated }"
          v-for="c in g.configs"
          :key="c.id"
        >
          <div class="config__header">
            <el-icon v-if="!isConfigClosed(c.id)" class="open-close" @click="closeConfig(c.id)"
              ><ArrowUp
            /></el-icon>
            <el-icon v-if="isConfigClosed(c.id)" class="open-close" @click="openConfig(c.id)"
              ><ArrowDown
            /></el-icon>
            <div class="config__description" :class="{ activated: c.activated }">
              {{ c.description }}
              <span class="config-status">{{ c.activated ? '已开启' : '未开启' }}</span>
            </div>
            <div class="config__tags">
              <span class="config-id">{{ c.id }}</span>
              <div class="config__tag">订单</div>
              <div class="config__tag">支付</div>
              <div class="config__tag">报价</div>
            </div>
            <el-icon><MoreFilled /></el-icon>
          </div>
          <div class="config__body">
            <div class="config__parameters">
              <div class="config__parameter qs">a = b</div>
              <div class="config__parameter qs">b = 哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
              <div class="config__parameter qs">a=b</div>
              <div class="config__parameter qs">b=哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
              <div class="config__parameter qs">a=b</div>
              <div class="config__parameter qs">b=哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
              <div class="config__parameter qs">a=b</div>
              <div class="config__parameter qs">b=哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
              <div class="config__parameter qs">a=b</div>
              <div class="config__parameter qs">b=哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
              <div class="config__parameter qs">a=b</div>
              <div class="config__parameter qs">b=哈哈</div>
              <div class="config__parameter rb">c=123</div>
              <div class="config__parameter rb">d=132123</div>
            </div>
            <div class="config__response">
              <div class="config__response-item">状态码: {{ c.responseStatus }}</div>
              <div class="config__response-item">Content-Type: {{ c.responseContentType }}</div>
            </div>
            <div class="config-response-body-editor">
              <CodeMirror
                :model-value="c.responseBody"
                readonly
                :extensions="[json()]"
              ></CodeMirror>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
$page-header-height: 6rem;
$common-header-height: 5rem;
$config-stats-top: $page-header-height + $common-header-height;
$config-stats-height: 3.2rem;
$config-group-header-top: $config-stats-top + $config-stats-height - 0.1rem;
$config-group-header-height: 3.2rem;
$config-header-height: 3.2rem;
$config-header-top: $config-group-header-top + $config-header-height - 0.1rem;
$tag-height: 2rem;

.config-container {
  margin: 0rem 2rem 2rem;
}
.config-stats {
  border: 1px solid var(--el-border-color);
  height: $config-stats-height;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding: 0 1rem;
  margin-bottom: 1rem;
  gap: 2rem;
  position: sticky;
  top: $config-stats-top;
  background-color: white;
  z-index: 20;
}
.config-group {
  border: 1px solid var(--el-border-color);
  border-top: none;
  margin-bottom: 1rem;

  &:last-of-type {
    margin-bottom: 0;
  }

  &.closed {
    border-bottom: none;

    .config-group__body {
      display: none;
    }
  }
}
.config-group__header {
  display: flex;
  flex-direction: row;
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-left: none;
  border-right: none;
  height: $config-group-header-height;
  box-sizing: border-box;
  padding: 0 1rem;
  position: sticky;
  top: $config-group-header-top;
  background-color: white;
  z-index: 3;
  gap: 1rem;

  .method-path {
    white-space: nowrap;
    text-overflow: ellipsis;
    flex-grow: 1;
  }
}
.config-group__body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
}
.config {
  position: relative;
  border: 1px solid var(--el-border-color);
  border-top: none;

  &.closed {
    border-bottom: none;

    .config__body {
      display: none;
    }
  }

  .config-response-body-editor {
    border-top: 1px solid var(--el-border-color);
  }

  .response {
    height: 3rem;
    padding: 0 1rem;
    display: flex;
    align-items: center;
  }

  .cm-editor {
    height: 30rem;
  }
}
.config__header {
  display: flex;
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-left: none;
  border-right: none;
  height: $config-header-height;
  box-sizing: border-box;
  padding: 0 1rem;
  position: sticky;
  top: $config-header-top;
  background-color: white;
  z-index: 2;
  gap: 1rem;

  .el-switch {
    height: unset;
  }
}
.config__description {
  width: 0;
  flex-grow: 1;
  line-height: 1;
  display: flex;
  align-items: center;
  gap: 1rem;
  color: var(--el-text-color-placeholder);

  &.activated {
    color: unset;

    .config-status {
      background-color: var(--el-color-success);
    }
  }
}
.config-status,
.config-id {
  border-radius: 0.4rem;
  padding: 0 0.5rem;
  color: white;
  font-size: 1.2rem;
  height: $tag-height;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  line-height: 1;
}
.config-status {
  background-color: var(--el-color-info);

  &.activated {
    background-color: var(--el-color-success);
  }
}
.config-id {
  background-color: var(--el-color-primary);

  &::before {
    content: 'ID';
    margin-right: 0.5rem;
  }
}
.config__parameters {
  border-bottom: 1px solid var(--el-border-color);
  padding: 1rem;
  display: flex;
  flex-direction: row;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}
.config__parameter {
  border: 1px solid var(--el-border-color);
  border-radius: 0.4rem;
  padding: 0 0.5rem;
  height: $tag-height;
  box-sizing: border-box;
  font-size: 1.2rem;
  display: flex;
  align-items: center;

  &.qs::before {
    content: 'queryString';
    color: silver;
    margin-right: 0.2rem;
  }

  &.rb::before {
    content: 'requestBody';
    color: silver;
    margin-right: 0.2rem;
  }
}

.open-close {
  cursor: pointer;
}

.config__tags {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 0.5rem;
  margin-left: 1rem;
}
.config__tag {
  border: 1px solid var(--el-border-color);
  border-radius: 0.4rem;
  padding: 0 0.5rem;
  height: $tag-height;
  box-sizing: border-box;
  font-size: 1.2rem;
  line-height: 1;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;

  &::before {
    content: '#';
    margin-right: 0.2rem;
  }
}

.config__response {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 2rem;
  height: 3rem;
  padding: 0 1rem;
}
.config__response-item {
  // border: 1px solid var(--el-border-color);
  border-radius: 0.4rem;
  // padding: 0 0.5rem;
  height: 2rem;
  line-height: 1;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
}

.config-table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;

  td {
    border: 1px solid var(--el-border-color);
  }

  tr td:first-of-type {
    width: 10rem;
    text-align: right;
    padding: 0.4rem 1rem;
  }
}
</style>
