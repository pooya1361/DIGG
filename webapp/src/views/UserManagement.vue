<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="bg-white shadow rounded-lg px-6 py-4">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
          <p class="mt-1 text-sm text-gray-500">
            Manage users in the Digg system ({{ userCount }} total users)
          </p>
        </div>
        <button
          @click="openCreateModal"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          <PlusIcon class="h-4 w-4 mr-2" />
          Add User
        </button>
      </div>
    </div>

    <!-- Search -->
    <div class="bg-white shadow rounded-lg px-6 py-4">
      <div class="flex items-center space-x-4">
        <div class="flex-1">
          <label for="search" class="sr-only">Search users</label>
          <div class="relative">
            <div
              class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"
            >
              <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
            </div>
            <input
              id="search"
              v-model="searchQuery"
              @input="searchUsers"
              type="text"
              placeholder="Search users by name..."
              class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
        <button
          @click="refreshUsers"
          :disabled="loading"
          class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
        >
          <ArrowPathIcon class="h-4 w-4 mr-2" :class="{ 'animate-spin': loading }" />
          Refresh
        </button>
      </div>
    </div>

    <!-- Users Table -->
    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div v-if="loading && users.length === 0" class="px-6 py-12 text-center">
        <div
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"
        ></div>
        <p class="mt-2 text-sm text-gray-500">Loading users...</p>
      </div>

      <div v-else-if="users.length === 0" class="px-6 py-12 text-center">
        <UserIcon class="mx-auto h-12 w-12 text-gray-400" />
        <h3 class="mt-2 text-sm font-medium text-gray-900">No users found</h3>
        <p class="mt-1 text-sm text-gray-500">
          {{
            searchQuery
              ? "Try adjusting your search criteria."
              : "Get started by creating a new user."
          }}
        </p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                User
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Contact
              </th>
              <th
                class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Address
              </th>
              <th
                class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
              >
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ user.name }}</div>
                    <div class="text-sm text-gray-500">ID: {{ user.id }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ user.email }}</div>
                <div class="text-sm text-gray-500">{{ user.telephone }}</div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ user.address }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end space-x-2">
                  <button
                    @click="openEditModal(user)"
                    class="text-blue-600 hover:text-blue-900 p-1 rounded"
                    title="Edit user"
                  >
                    <PencilIcon class="h-4 w-4" />
                  </button>
                  <button
                    @click="confirmDelete(user)"
                    class="text-red-600 hover:text-red-900 p-1 rounded"
                    title="Delete user"
                  >
                    <TrashIcon class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- User Modal -->
    <UserModal
      :show="showModal"
      :user="editingUser"
      :mode="modalMode"
      @close="closeModal"
      @save="handleSave"
    />

    <!-- Delete Confirmation Modal -->
    <ConfirmModal
      :show="showDeleteConfirm"
      :title="'Delete User'"
      :message="`Are you sure you want to delete ${userToDelete?.name}? This action cannot be undone.`"
      @close="showDeleteConfirm = false"
      @confirm="deleteUser"
    />
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import { userApi } from "@/services/api";
import {
  PlusIcon,
  MagnifyingGlassIcon,
  ArrowPathIcon,
  UserIcon,
  PencilIcon,
  TrashIcon,
} from "@heroicons/vue/24/outline";
import UserModal from "../components/UserModal.vue";
import ConfirmModal from "../components/ConfirmModal.vue";

export default {
  name: "UserManagement",
  components: {
    UserModal,
    ConfirmModal,
    PlusIcon,
    MagnifyingGlassIcon,
    ArrowPathIcon,
    UserIcon,
    PencilIcon,
    TrashIcon,
  },
  setup() {
    const users = ref([]);
    const loading = ref(false);
    const searchQuery = ref("");
    const userCount = ref(0);

    // Modal state
    const showModal = ref(false);
    const modalMode = ref("create"); // 'create' or 'edit'
    const editingUser = ref(null);

    // Delete confirmation state
    const showDeleteConfirm = ref(false);
    const userToDelete = ref(null);

    // Load users from API
    const loadUsers = async () => {
      try {
        loading.value = true;
        const data = await userApi.getAllUsers();
        users.value = data;
        userCount.value = data.length;
      } catch (error) {
        console.error("Error loading users:", error);
      } finally {
        loading.value = false;
      }
    };

    // Search users
    const searchUsers = async () => {
      if (!searchQuery.value.trim()) {
        loadUsers();
        return;
      }

      try {
        loading.value = true;
        const data = await userApi.searchUsers(searchQuery.value);
        users.value = data;
      } catch (error) {
        console.error("Error searching users:", error);
      } finally {
        loading.value = false;
      }
    };

    // Refresh users
    const refreshUsers = () => {
      searchQuery.value = "";
      loadUsers();
    };

    // Modal functions
    const openCreateModal = () => {
      modalMode.value = "create";
      editingUser.value = null;
      showModal.value = true;
    };

    const openEditModal = (user) => {
      modalMode.value = "edit";
      editingUser.value = { ...user };
      showModal.value = true;
    };

    const closeModal = () => {
      showModal.value = false;
      editingUser.value = null;
    };

    // Handle user save
    const handleSave = async (userData) => {
      try {
        if (modalMode.value === "create") {
          await userApi.createUser(userData);
          alert("User created successfully");
        } else {
          await userApi.updateUser(editingUser.value.id, userData);
        }
        closeModal();
        refreshUsers();
      } catch (error) {
        const message = error.response?.data?.error || "Operation failed";
        alert("error: " + message);
        console.error("Save error:", error);
      }
    };

    // Delete functions
    const confirmDelete = (user) => {
      userToDelete.value = user;
      showDeleteConfirm.value = true;
    };

    const deleteUser = async () => {
      try {
        await userApi.deleteUser(userToDelete.value.id);
        showDeleteConfirm.value = false;
        userToDelete.value = null;
        refreshUsers();
      } catch (error) {
        alert("Failed to delete user");
        console.error("Delete error:", error);
      }
    };

    // Utility functions
    const getInitials = (name) => {
      return name
        .split(" ")
        .map((word) => word.charAt(0).toUpperCase())
        .join("")
        .slice(0, 2);
    };

    // Load users on mount
    onMounted(() => {
      loadUsers();
    });

    return {
      users,
      loading,
      searchQuery,
      userCount,
      showModal,
      modalMode,
      editingUser,
      showDeleteConfirm,
      userToDelete,
      loadUsers,
      searchUsers,
      refreshUsers,
      openCreateModal,
      openEditModal,
      closeModal,
      handleSave,
      confirmDelete,
      deleteUser,
    };
  },
};
</script>
