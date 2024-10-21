<script setup>
import axios from 'axios'
import { computed, onMounted, ref } from 'vue'
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

let displayType = ref('logs') // logs configs
let logs = ref([])
let logsAutoRefresh = ref(true)
let logsLoading = ref(false)
let baseUrl = ref('/feign-proxy/')
let logDetailsDialogLogId = ref('')
let logDetailsDialogVisible = ref(false)
let logFilterDialogVisible = ref(false)
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
  return _find(logs.value, (n) => n.id === logDetailsDialogLogId.value) || {}
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

let filterLogs = computed(() => {
  return _filter(logs.value, (n) => {
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

onMounted(async () => {
  logsLoading.value = true
  await getLogsFromApi()
  logsLoading.value = false
})

async function getLogsFromApi() {
  let response = await axios.get(`${baseUrl.value}logs`)
  let data = response.data
  if (data.returncode !== 0) {
    return
  }

  let responseLogs = _map(data.result, (n) => {
    let ret = {
      ...n
    }
    ret.queryString = beautifyJson(ret.queries)
    ret.responseBody = beautifyJson(ret.responseBody)
    return ret
  })
  logs.value = responseLogs
}

function parseJson(jsonString) {
  try {
    return JSON.parse(jsonString)
  } catch {
    return jsonString
  }
}

function beautifyJson(jsonStringOrObject) {
  if (typeof jsonStringOrObject === 'string') {
    return JSON.stringify(parseJson(jsonStringOrObject), null, 2)
  } else {
    return JSON.stringify(jsonStringOrObject, null, 2)
  }
}

function logsTableRowClassName({ row }) {
  if (row.useProxy) {
    return 'warning-row'
  }
  return ''
}

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
  <div class="page">
    <div class="page-header">
      <span class="logo">Remote Api Proxy</span>
      <div class="page-header-right"></div>
    </div>
    <div class="page-body">
      <div class="container-header">
        <el-radio-group v-model="displayType">
          <el-radio-button label="请求记录" value="logs" />
          <el-radio-button label="代理配置" value="configs" />
        </el-radio-group>
        <div class="container-header-right logs-container-header-right">
          <el-button type="primary" @click="logFilterDialogVisible = true">设置过滤条件</el-button>

          <el-button type="primary" @click="getLogsFromApi">刷新</el-button>
          <el-checkbox v-model="logsAutoRefresh" label="自动刷新" border />
        </div>
      </div>
      <div class="logs-container">
        <el-table
          v-loading="logsLoading"
          :data="filterLogs"
          height="100%"
          width="100%"
          border
          highlight-current-row
          :row-class-name="logsTableRowClassName"
        >
          <el-table-column :resizable="false" width="100px">
            <template #header>
              <el-icon v-if="filterLogsByUseProxy" class="filter-icon"><Filter /></el-icon>
              是否代理
            </template>
            <template #default="scope">
              {{ scope.row.useProxy ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column :resizable="false" width="160px">
            <template #header>
              <el-icon v-if="filterLogsByRequestTime" class="filter-icon"><Filter /></el-icon>
              请求时间
            </template>
            <template #default="scope">
              {{ scope.row.requestTime }}
            </template>
          </el-table-column>

          <el-table-column :resizable="false" width="100px">
            <template #header>
              <el-icon v-if="filterLogsByHttpMethod" class="filter-icon"><Filter /></el-icon>
              请求方法
            </template>
            <template #default="scope">
              {{ scope.row.httpMethod }}
            </template>
          </el-table-column>
          <el-table-column>
            <template #header>
              <el-icon v-if="filterLogsByPath || filterLogsByQueries" class="filter-icon"
                ><Filter
              /></el-icon>
              完整路径
            </template>
            <template #default="scope">
              {{ scope.row.url }}
            </template>
          </el-table-column>

          <el-table-column
            fixed="right"
            label="操作"
            width="180"
            :resizable="false"
            class-name="operations-column"
          >
            <template #default="scope">
              <div class="operations">
                <el-button type="primary" size="small" @click="showLogDetailsOnClick(scope.row.id)"
                  >查看详情</el-button
                >
                <el-button type="primary" size="small">创建代理</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
  <el-dialog
    v-model="logDetailsDialogVisible"
    :show-close="false"
    title="请求详情"
    width="900"
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
    width="900"
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
</template>

<style lang="scss">
.container-header {
  padding: 1rem 0;
  display: flex;
  align-items: center;
}
.container-header-right {
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
.logs-container {
  height: 0;
  flex-grow: 1;
  border-radius: 4px;

  .cell {
    white-space: nowrap;
  }
}

.el-table .el-table__row.warning-row {
  color: var(--el-color-warning);
}

.el-table__header .cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  line-height: 1;
  height: 23px;

  .el-icon {
    cursor: pointer;

    &.active {
      color: var(--el-color-primary);
    }
  }
}

.el-table__cell .operations {
  display: flex;
  justify-content: space-between;

  & > * {
    margin: 0;
  }
}

.log-detials__item {
  display: flex;
  flex-direction: row;
  margin-bottom: 1rem;
  line-height: 1.4;
}
.log-details__item-title {
  min-width: 8rem;
  text-align: right;
  padding-right: 1rem;
}
.log-details__item-content {
  width: 0;
  flex-grow: 1;
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

.el-dialog {
  --el-dialog-margin-top: 5rem;
}

.log-details__editor-container {
  position: relative;
  height: 20rem;
  padding: 0 !important;
}

.log-details__querystring-editor-container {
  height: 20rem;
}
.log-details__editor {
  position: absolute;
  width: 100%;
  left: 0;
  top: 0;

  .cm-editor {
    height: 20rem;
  }
}

.log-details__querystring-editor {
  .cm-editor {
    height: 20rem;
  }
}

.el-table--default .el-table__cell.operations-column {
  padding: 0;
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
