<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="bg-white shadow rounded-lg px-6 py-4">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Health Status</h1>
          <p class="mt-1 text-sm text-gray-500">
            Monitor the health and status of the Digg User Management System
          </p>
        </div>
        <button
          @click="refreshHealth"
          :disabled="loading"
          class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
        >
          <ArrowPathIcon class="h-4 w-4 mr-2" :class="{ 'animate-spin': loading }" />
          Refresh
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading && !healthData" class="bg-white shadow rounded-lg px-6 py-12 text-center">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
      <p class="mt-2 text-sm text-gray-500">Loading health status...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-white shadow rounded-lg px-6 py-4">
      <div class="rounded-md bg-red-50 p-4">
        <div class="flex">
          <div class="flex-shrink-0">
            <XCircleIcon class="h-5 w-5 text-red-400" />
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Connection Error</h3>
            <div class="mt-2 text-sm text-red-700">
              <p>{{ error }}</p>
              <p class="mt-1 text-xs">Make sure the backend server is running on http://localhost:8080</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Health Status Cards -->
    <div v-else-if="healthData" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- Overall Status -->
      <div class="bg-white shadow rounded-lg p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-md"
                 :class="healthData.status === 'UP' ? 'bg-green-100' : 'bg-red-100'">
              <CheckCircleIcon v-if="healthData.status === 'UP'" class="h-6 w-6 text-green-600" />
              <XCircleIcon v-else class="h-6 w-6 text-red-600" />
            </div>
          </div>
          <div class="ml-4">
            <h3 class="text-lg font-medium text-gray-900">Overall Status</h3>
            <p class="text-sm text-gray-500">System health overview</p>
            <p class="text-xl font-bold mt-1"
               :class="healthData.status === 'UP' ? 'text-green-600' : 'text-red-600'">
              {{ healthData.status }}
            </p>
          </div>
        </div>
      </div>

      <!-- Liveness Status -->
      <div class="bg-white shadow rounded-lg p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-md"
                 :class="livenessStatus.status === 'UP' ? 'bg-green-100' : 'bg-red-100'">
              <HeartIcon v-if="livenessStatus.status === 'UP'" class="h-6 w-6 text-green-600" />
              <XCircleIcon v-else class="h-6 w-6 text-red-600" />
            </div>
          </div>
          <div class="ml-4">
            <h3 class="text-lg font-medium text-gray-900">Liveness</h3>
            <p class="text-sm text-gray-500">Application is alive</p>
            <p class="text-xl font-bold mt-1"
               :class="livenessStatus.status === 'UP' ? 'text-green-600' : 'text-red-600'">
              {{ livenessStatus.status }}
            </p>
          </div>
        </div>
      </div>

      <!-- Readiness Status -->
      <div class="bg-white shadow rounded-lg p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="flex items-center justify-center h-12 w-12 rounded-md"
                 :class="readinessStatus.status === 'UP' ? 'bg-green-100' : 'bg-red-100'">
              <ShieldCheckIcon v-if="readinessStatus.status === 'UP'" class="h-6 w-6 text-green-600" />
              <XCircleIcon v-else class="h-6 w-6 text-red-600" />
            </div>
          </div>
          <div class="ml-4">
            <h3 class="text-lg font-medium text-gray-900">Readiness</h3>
            <p class="text-sm text-gray-500">Ready to serve requests</p>
            <p class="text-xl font-bold mt-1"
               :class="readinessStatus.status === 'UP' ? 'text-green-600' : 'text-red-600'">
              {{ readinessStatus.status }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Detailed Health Checks -->
    <div v-if="healthData && healthData.checks" class="bg-white shadow rounded-lg overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900">Detailed Health Checks</h2>
      </div>
      <div class="divide-y divide-gray-200">
        <div v-for="check in healthData.checks" :key="check.name" class="px-6 py-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <CheckCircleIcon v-if="check.status === 'UP'" class="h-5 w-5 text-green-400" />
                <XCircleIcon v-else class="h-5 w-5 text-red-400" />
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-gray-900">{{ formatCheckName(check.name) }}</h3>
                <p class="text-sm text-gray-500">Status: {{ check.status }}</p>
              </div>
            </div>
            <div class="text-right">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="check.status === 'UP' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                {{ check.status }}
              </span>
            </div>
          </div>
          
          <!-- Check Data -->
          <div v-if="check.data && Object.keys(check.data).length > 0" class="mt-3 ml-8">
            <div class="bg-gray-50 rounded-md p-3">
              <h4 class="text-xs font-medium text-gray-700 uppercase tracking-wider mb-2">Details</h4>
              <dl class="grid grid-cols-1 gap-2 sm:grid-cols-2">
                <div v-for="(value, key) in check.data" :key="key">
                  <dt class="text-xs font-medium text-gray-500">{{ formatDataKey(key) }}</dt>
                  <dd class="text-sm text-gray-900">{{ formatDataValue(value, key) }}</dd>
                </div>
              </dl>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Last Updated -->
    <div v-if="lastUpdated" class="text-center text-sm text-gray-500">
      Last updated: {{ formatDateTime(lastUpdated) }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { healthApi } from '@/services/api'
import {
  ArrowPathIcon,
  CheckCircleIcon,
  XCircleIcon,
  HeartIcon,
  ShieldCheckIcon
} from '@heroicons/vue/24/outline'

const loading = ref(false)
const error = ref<string | null>(null)
const healthData = ref<HealthResponse | null>(null)
const livenessStatus = ref<HealthResponse>({ status: 'DOWN', checks: [] })
const readinessStatus = ref<HealthResponse>({ status: 'DOWN', checks: [] })
const lastUpdated = ref<Date | null>(null)
const refreshInterval = ref<number | null>(null)

const loadHealthData = async (): Promise<void> => {
  try {
    loading.value = true
    error.value = null

    // Load all health endpoints in parallel
    const [health, liveness, readiness] = await Promise.all([
      healthApi.getHealthStatus().catch(() => ({ status: 'DOWN' as const, checks: [] })),
      healthApi.getLivenessStatus().catch(() => ({ status: 'DOWN' as const, checks: [] })),
      healthApi.getReadinessStatus().catch(() => ({ status: 'DOWN' as const, checks: [] }))
    ])

    healthData.value = health
    livenessStatus.value = liveness
    readinessStatus.value = readiness
    lastUpdated.value = new Date()

  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Failed to load health status'
    console.error('Error loading health data:', err)
  } finally {
    loading.value = false
  }
}

const refreshHealth = (): void => {
  loadHealthData()
}

const formatCheckName = (name: string): string => {
  return name
    .replace(/[-_]/g, ' ')
    .replace(/\b\w/g, l => l.toUpperCase())
}

const formatDataKey = (key: string): string => {
  return key
    .replace(/[-_]/g, ' ')
    .replace(/\b\w/g, l => l.toUpperCase())
}

const formatDataValue = (value: any, key: string): string => {
  if (typeof value === 'boolean') {
    return value ? 'Yes' : 'No'
  }
  if (typeof value === 'number') {
    if (key === 'uptime') {
      return new Date(value).toLocaleString()
    }
    return value.toLocaleString()
  }
  return String(value)
}

const formatDateTime = (date: Date): string => {
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(date)
}

// Auto-refresh every 30 seconds
onMounted(() => {
  loadHealthData()
  refreshInterval.value = window.setInterval(loadHealthData, 30000)
})

onUnmounted(() => {
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
  }
})
</script>