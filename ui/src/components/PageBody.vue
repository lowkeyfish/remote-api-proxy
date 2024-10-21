<script setup>
import { onMounted, ref } from 'vue'
import CommonHeader from '@/components/CommonHeader.vue'
import LogContainer from '@/components/LogContainer.vue'
import ConfigContainer from '@/components/ConfigContainer.vue'
import { map as _map } from 'lodash-es'
import axios from 'axios'

let baseUrl = ref('/feign-proxy/')
let displayType = ref('log')
let logs = ref([])
let logLoading = ref(false)
let logFilterDialogVisible = ref(false)
let configs = ref([])
let configLoading = ref(false)

onMounted(async () => {
  await refreshLog()
  await refreshConfig()
})

async function refreshLog() {
  logLoading.value = true
  await requestLogFromApi()
  logLoading.value = false
}
async function requestLogFromApi() {
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

async function refreshConfig() {
  configLoading.value = true
  await requestConfigFromApi()
  configLoading.value = false
}
async function requestConfigFromApi() {
  let response = await axios.get(`${baseUrl.value}proxy-configs`)
  let data = response.data
  if (data.returncode !== 0) {
    return
  }

  let responseConfigs = _map(data.result, (n) => {
    let ret = {
      ...n
    }
    ret.responseBody = beautifyJson(ret.responseBody)
    return ret
  })
  configs.value = responseConfigs
}
</script>

<template>
  <div class="page-body">
    <CommonHeader
      v-model:display-type="displayType"
      v-model:log-filter-dialog-visible="logFilterDialogVisible"
      @refresh-log="refreshLog"
      @refresh-config="refreshConfig"
    />
    <LogContainer
      v-show="displayType === 'log'"
      :logs="logs"
      :log-loading="logLoading"
      v-model:log-filter-dialog-visible="logFilterDialogVisible"
    />
    <ConfigContainer v-show="displayType === 'config'" :configs="configs" />
  </div>
</template>
