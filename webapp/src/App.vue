<template>
  <div id="app" class="min-h-screen bg-gray-100">
    <!-- Navigation Header -->
    <nav class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <router-link to="/" class="text-xl font-bold text-gray-900">
              🔗 Digg User Management
            </router-link>
          </div>
          <div class="flex space-x-8">
            <router-link
              to="/"
              class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
              :class="{ 'text-blue-600 font-semibold': $route.path === '/' }"
            >
              Users
            </router-link>
            <router-link
              to="/health"
              class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
              :class="{ 'text-blue-600 font-semibold': $route.path === '/health' }"
            >
              Health Status
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <router-view />
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-t mt-12">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <p class="text-gray-500 text-sm">
            © 2025 Digg User Management System
          </p>
          <div class="flex items-center space-x-4">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="backendStatus.connected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
              <span class="w-2 h-2 mr-1.5 rounded-full"
                    :class="backendStatus.connected ? 'bg-green-400' : 'bg-red-400'"></span>
              Backend {{ backendStatus.connected ? 'Connected' : 'Disconnected' }}
            </span>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { healthApi } from './services/api'
import type { BackendStatus } from './types'

const backendStatus = ref<BackendStatus>({
  connected: false,
  lastCheck: null
})

const checkBackendHealth = async (): Promise<void> => {
  try {
    await healthApi.getHealthStatus()
    backendStatus.value.connected = true
    backendStatus.value.lastCheck = new Date()
  } catch (error) {
    backendStatus.value.connected = false
    backendStatus.value.lastCheck = new Date()
    console.warn('Backend health check failed:', error)
  }
}

onMounted(() => {
  checkBackendHealth()
  // Check backend health every 30 seconds
  setInterval(checkBackendHealth, 30000)
})
</script>

<style>
/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* Smooth transitions */
* {
  transition: all 0.2s ease-in-out;
}
</style>