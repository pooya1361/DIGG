import axios from 'axios'

// Configure axios defaults
const API_BASE_URL = 'http://localhost:8080'

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
})

// Request interceptor for logging
api.interceptors.request.use(
    (config) => {
        console.log(`🚀 API Request: ${config.method?.toUpperCase()} ${config.url}`)
        return config
    },
    (error) => {
        console.error('❌ Request Error:', error)
        return Promise.reject(error)
    }
)

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => {
        console.log(`✅ API Response: ${response.status} ${response.config.url}`)
        return response
    },
    (error) => {
        console.error('❌ Response Error:', error.response?.data || error.message)
        return Promise.reject(error)
    }
)

// User API functions
export const userApi = {
    // Get all users
    async getAllUsers() {
        const response = await api.get('/users')
        return response.data
    },

    // Get user by ID
    async getUserById(id) {
        const response = await api.get(`/users/${id}`)
        return response.data
    },

    // Create new user
    async createUser(userData) {
        const response = await api.post('/users', userData)
        return response.data
    },

    // Update user
    async updateUser(id, userData) {
        const response = await api.put(`/users/${id}`, userData)
        return response.data
    },

    // Delete user
    async deleteUser(id) {
        await api.delete(`/users/${id}`)
    },

    // Search users by name
    async searchUsers(name) {
        const response = await api.get(`/users/search?name=${encodeURIComponent(name)}`)
        return response.data
    },

    // Get user count
    async getUserCount() {
        const response = await api.get('/users/count')
        return response.data
    },

    // Find user by email
    async getUserByEmail(email) {
        const response = await api.get(`/users/email/${encodeURIComponent(email)}`)
        return response.data
    }
}

// Health API functions
export const healthApi = {
    // Get overall health status
    async getHealthStatus() {
        const response = await api.get('/q/health')
        return response.data
    },

    // Get liveness status
    async getLivenessStatus() {
        const response = await api.get('/q/health/live')
        return response.data
    },

    // Get readiness status
    async getReadinessStatus() {
        const response = await api.get('/q/health/ready')
        return response.data
    }
}

export default api