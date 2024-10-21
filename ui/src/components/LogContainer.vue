<script setup>
import { computed, ref } from 'vue'
import { Filter, Plus, Minus } from '@element-plus/icons-vue'
import {
  find as _find,
  filter as _filter,
  trim as _trim,
  lowerCase as _lowerCase,
  indexOf as _indexOf,
  map as _map
} from 'lodash-es'
import { json } from '@codemirror/lang-json'
import CodeMirror from '@/components/CodeMirror.vue'
import dayjs from 'dayjs'

let props = defineProps(['logs', 'logLoading'])
let logFilterDialogVisible = defineModel('logFilterDialogVisible')

let logDetailsDialogLogId = ref('')
let logDetailsDialogVisible = ref(false)
let logFilter = ref({
  requestTimeBegin: '',
  requestTimeEnd: '',
  useProxy: 'all',
  httpMethod: 'all',
  path: '',
  queries: [
    {
      id: 'default',
      name: '',
      value: ''
    }
  ]
})

let logDetailsDialogLog = computed(() => {
  return _find(props.logs, (n) => n.id === logDetailsDialogLogId.value) || {}
})

let filterLogsByRequestTime = computed(
  () => logFilter.value.requestTimeBegin || logFilter.value.requestTimeEnd
)
let filterLogsByUseProxy = computed(() => logFilter.value.useProxy !== 'all')
let filterLogsByHttpMethod = computed(() => logFilter.value.httpMethod !== 'all')
let filterLogsByPath = computed(() => _trim(logFilter.value.path))
let filterLogsByQueries = computed(
  () => _filter(logFilter.value.queries, (q) => _trim(q.name) !== '').length > 0
)
let filtered = computed(() => {
  return filterLogsByUseProxy || filterLogsByHttpMethod || filterLogsByPath || filterLogsByQueries
})

let filterLogs = computed(() => {
  return _filter(props.logs, (n) => {
    let filter = logFilter.value
    if (filter.requestTimeBegin) {
      let requestTimeBegin = dayjs(filter.requestTimeBegin)
      let requestTime = dayjs(n.requestTime)
      if (requestTime.isBefore(requestTimeBegin)) {
        return false
      }
    }

    if (filter.requestTimeEnd) {
      let requestTimeEnd = dayjs(filter.requestTimeEnd)
      let requestTime = dayjs(n.requestTime)
      if (requestTime.isAfter(requestTimeEnd)) {
        return false
      }
    }

    if (filter.useProxy === 'true') {
      if (!n.useProxy) {
        return false
      }
    } else if (filter.useProxy === 'false') {
      if (n.useProxy) {
        return false
      }
    }

    if (filter.httpMethod !== 'all') {
      if (filter.httpMethod !== n.httpMethod) {
        return false
      }
    }

    if (_trim(filter.path)) {
      if (_lowerCase(_trim(filter.path)) !== _lowerCase(n.path)) {
        return false
      }
    }

    if (
      _indexOf(
        _map(
          _filter(filter.queries, (q) => _trim(q.name) !== ''),
          (q) => {
            return n.queries[_trim(q.name)] === _trim(q.value)
          }
        ),
        false
      ) >= 0
    ) {
      return false
    }

    return true
  })
})

let stats = computed(() => {
  let logs = props.logs
  return {
    count: logs.length,
    useProxyCount: _filter(logs, (n) => n.useProxy).length,
    filteredCount: filterLogs.value.length,
    filteredUseProxyCount: _filter(filterLogs.value, (n) => n.useProxy).length
  }
})

function showLogDetailsOnClick(logId) {
  logDetailsDialogLogId.value = logId
  logDetailsDialogVisible.value = true
}

function logFilterAddQueryOnClick(index) {
  logFilter.value.queries.splice(index, 0, {
    id: Math.random().toString(),
    name: '',
    value: ''
  })
}

function logFilterRemoveQueryOnClick(index) {
  if (logFilter.value.queries.length > 1) {
    logFilter.value.queries.splice(index, 1)
  }
}

function resetLogFilterOnClick() {
  logFilter.value = {
    requestTimeBegin: '',
    requestTimeEnd: '',
    useProxy: 'all',
    httpMethod: 'all',
    path: '',
    queries: [
      {
        id: 'default',
        name: '',
        value: ''
      }
    ]
  }
}
</script>

<template>
  <div class="log-container">
    <div class="log-stats">
      <span>{{
        filtered === true
          ? `总数: ${stats.count} (过滤后: ${stats.filteredCount})`
          : `总数: ${stats.count}`
      }}</span>
      <span>{{
        filtered === true
          ? `使用代理: ${stats.useProxyCount} (过滤后: ${stats.filteredUseProxyCount})`
          : `使用代理: ${stats.useProxyCount}`
      }}</span>
    </div>
    <table class="log-table">
      <colgroup>
        <col style="width: 10rem" />
        <col style="width: 16rem" />
        <col style="width: 10rem" />
        <col style="" />
        <col style="width: 16rem" />
      </colgroup>
      <thead class="log-table__header">
        <tr>
          <th>
            <div class="column">
              <el-icon v-if="filterLogsByUseProxy" class="filter-icon"><Filter /></el-icon> 是否代理
            </div>
          </th>
          <th>
            <div class="column">
              <el-icon v-if="filterLogsByRequestTime" class="filter-icon"><Filter /></el-icon>
              请求时间
            </div>
          </th>
          <th>
            <div class="column">
              <el-icon v-if="filterLogsByHttpMethod" class="filter-icon"><Filter /></el-icon>
              请求方法
            </div>
          </th>
          <th>
            <div class="column">
              <el-icon v-if="filterLogsByPath || filterLogsByQueries" class="filter-icon"
                ><Filter
              /></el-icon>
              完整路径
            </div>
          </th>
          <th><div class="column">操作</div></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="log in filterLogs" :key="log.id" :class="{ 'log-use-proxy': log.useProxy }">
          <td>{{ log.useProxy ? '是' : '否' }}</td>
          <td>{{ log.requestTime }}</td>
          <td>{{ log.httpMethod }}</td>
          <td>{{ log.url }}</td>
          <td>
            <div class="operations">
              <el-button type="primary" link @click="showLogDetailsOnClick(log.id)"
                >查看详情</el-button
              >
              <el-button type="primary" link>创建代理</el-button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="log-table-empty" v-if="filterLogs.length === 0">无数据</div>
    <el-dialog
      v-model="logDetailsDialogVisible"
      :show-close="false"
      title="请求详情"
      width="1000"
      :close-on-press-escape="false"
      :close-on-click-modal="false"
    >
      <div class="log-details">
        <table>
          <tbody>
            <tr>
              <td>是否代理</td>
              <td>
                {{
                  logDetailsDialogLog.useProxy ? `是 (${logDetailsDialogLog.proxyConfigId})` : '否'
                }}
              </td>
            </tr>
            <tr>
              <td>请求时间</td>
              <td>{{ logDetailsDialogLog.requestTime }}</td>
            </tr>

            <tr>
              <td>请求方法</td>
              <td>{{ logDetailsDialogLog.httpMethod }}</td>
            </tr>
            <tr>
              <td>完整路径</td>
              <td>{{ logDetailsDialogLog.url }}</td>
            </tr>
            <tr>
              <td>接口地址</td>
              <td>{{ logDetailsDialogLog.path }}</td>
            </tr>
            <tr>
              <td>queryString</td>
              <td class="log-details__editor-container log-details__querystring-editor-container">
                <CodeMirror
                  class="log-details__editor log-details__querystring-editor"
                  v-model="logDetailsDialogLog.queryString"
                  readonly
                  :extensions="[json()]"
                ></CodeMirror>
              </td>
            </tr>
            <tr>
              <td>响应状态码</td>
              <td>{{ logDetailsDialogLog.responseStatus }}</td>
            </tr>
            <tr>
              <td>响应内容类型</td>
              <td>{{ logDetailsDialogLog.responseContentType }}</td>
            </tr>
            <tr>
              <td>响应内容</td>
              <td class="log-details__editor-container">
                <CodeMirror
                  class="log-details__editor"
                  v-model="logDetailsDialogLog.responseBody"
                  readonly
                  :extensions="[json()]"
                ></CodeMirror>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="logDetailsDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="createProxyConfigFromLogOnClick(logDetailsDialogLogId)">
            创建代理
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="logFilterDialogVisible"
      :show-close="false"
      :close-on-press-escape="false"
      :close-on-click-modal="false"
      title="设置请求记录过滤条件"
      width="1000"
    >
      <div class="log-filters">
        <table>
          <tbody>
            <tr>
              <td>请求时间</td>
              <td>
                <el-date-picker
                  v-model="logFilter.requestTimeBegin"
                  type="datetime"
                  placeholder="请求时间开始"
                  style="margin-right: 1rem"
                />
                <el-date-picker
                  v-model="logFilter.requestTimeEnd"
                  type="datetime"
                  placeholder="请求时间结束"
                />
              </td>
            </tr>
            <tr>
              <td>是否代理</td>
              <td>
                <el-select v-model="logFilter.useProxy" style="width: 22rem">
                  <el-option label="不限" value="all" />
                  <el-option label="是" value="true" />
                  <el-option label="否" value="false" />
                </el-select>
              </td>
            </tr>
            <tr>
              <td>请求方法</td>
              <td>
                <el-select v-model="logFilter.httpMethod" style="width: 22rem">
                  <el-option label="不限" value="all" />
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                  <el-option label="PUT" value="PUT" />
                  <el-option label="DELETE" value="DELETE" />
                </el-select>
              </td>
            </tr>
            <tr>
              <td>接口地址</td>
              <td>
                <el-input
                  v-model="logFilter.path"
                  clearable
                  placeholder="请输入接口地址 (不包含查询参数)"
                ></el-input>
              </td>
            </tr>
            <tr>
              <td>查询参数</td>
              <td>
                <div class="queries">
                  <div class="query" v-for="(q, index) in logFilter.queries" :key="q.id">
                    <el-input v-model="q.name" class="" clearable placeholder="请输入参数名" />
                    <el-input v-model="q.value" clearable placeholder="请输入参数值" />
                    <el-icon @click="logFilterRemoveQueryOnClick(index)"><Minus /></el-icon>
                    <el-icon @click="logFilterAddQueryOnClick(index + 1)"><Plus /></el-icon>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="resetLogFilterOnClick"> 重置过滤条件 </el-button>
          <el-button @click="logFilterDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss">
$page-header-height: 6rem;
$common-header-height: 5rem;
$log-stats-top: $page-header-height + $common-header-height;
$log-stats-height: 3.2rem;
$log-table-top: $log-stats-top + $log-stats-height - 0.1rem;
$log-table-row-height: 3.2rem;

.log-container {
  padding: 0rem 2rem 2rem;
}

.log-stats {
  border: 1px solid var(--el-border-color);
  height: $log-stats-height;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  padding: 0 1rem;
  margin-bottom: 1rem;
  gap: 2rem;
  position: sticky;
  top: $log-stats-top;
  background-color: white;
  z-index: 20;
}

.log-table {
  border-collapse: collapse;
  border: none;
  width: 100%;
  table-layout: fixed;

  td,
  th {
    border: 1px solid var(--el-border-color);
    height: $log-table-row-height;
    padding: 0 1rem;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }

  thead th .column {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  thead tr th {
    border-bottom: none;
  }

  tbody tr:first-of-type td {
    border-top: none;
  }

  .el-button.is-link {
    padding: 0;
  }
}
.log-table__header {
  position: sticky;
  top: $log-table-top;
  background-color: white;
  z-index: 10;
  border: 0;

  &::before,
  &::after {
    content: '';
    display: block;
    position: absolute;
    left: 0;
    right: 0;
    height: 1px;
    background-color: var(--el-border-color);
  }
  &::before {
    top: 0;
  }
  &::after {
    bottom: 0;
  }
}

.log-use-proxy {
  color: var(--el-color-warning);
}

.log-table-empty {
  padding: 5rem;
  margin: auto;
  text-align: center;
  color: var(--el-text-color-placeholder);
}

.log-details,
.log-filters {
  table {
    border-collapse: collapse;
    width: 100%;
  }

  td {
    border: 1px solid var(--el-border-color);
    padding: 0.5rem 1rem;
    word-break: break-all;
  }

  td:first-of-type {
    text-align: right;
    white-space: nowrap;
    vertical-align: baseline;
    width: 9rem;
  }
}

.log-filters {
  td:first-of-type {
    vertical-align: middle;
  }
}

.log-details__editor-container {
  position: relative;
  height: 40rem;
  padding: 0 !important;
}

.log-details__querystring-editor-container {
  height: 15rem;
}
.log-details__editor {
  position: absolute;
  width: 100%;
  left: 0;
  top: 0;

  .cm-editor {
    height: 40rem;
  }
}

.log-details__querystring-editor {
  .cm-editor {
    height: 15rem;
  }
}

.queries {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  .query {
    display: flex;
    flex-direction: row;
    gap: 1rem;
    align-items: center;

    .el-input:first-of-type {
      max-width: 22rem;
      min-width: 22rem;
    }

    .el-icon {
      cursor: pointer;
    }
  }
}

.filter-icon {
  color: var(--el-color-primary);
}
</style>
