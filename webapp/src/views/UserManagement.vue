<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="bg-white shadow rounded-lg px-6 py-4">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
          <p class="mt-1 text-sm text-gray-500">
            Manage users in the Digg system ({{ totalUsers }} total users)
          </p>
        </div>
        <button @click="openCreateModal"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
          <PlusIcon class="h-4 w-4 mr-2" />
          Add User
        </button>
      </div>
    </div>

    <!-- Search -->
    <div class="bg-white shadow rounded-lg px-6 py-4">
      <div class="flex items-center justify-between space-x-4">
        <div class="flex-1 flex items-center space-x-4">
          <div class="flex-1">
            <label for="search" class="sr-only">Search users</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <MagnifyingGlassIcon class="h-5 w-5 text-gray-400" />
              </div>
              <input id="search" v-model="searchQuery" @input="handleSearch" type="text"
                placeholder="Search users by name..."
                class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500" />
            </div>
          </div>
          <button @click="refreshUsers" :disabled="loading"
            class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50">
            <ArrowPathIcon class="h-4 w-4 mr-2" :class="{ 'animate-spin': loading }" />
            Refresh
          </button>
        </div>

        <!-- Items per page selector -->
        <div class="flex items-center space-x-2">
          <label for="pageSize" class="text-sm text-gray-700">Show:</label>
          <select id="pageSize" v-model="itemsPerPage" @change="handlePageSizeChange"
            class="block w-20 px-3 py-1 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="25">25</option>
            <option :value="50">50</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Users Table -->
    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div v-if="loading && tableItems.length === 0" class="px-6 py-12 text-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
        <p class="mt-2 text-sm text-gray-500">Loading users...</p>
      </div>

      <div v-else-if="tableItems.length === 0" class="px-6 py-12 text-center">
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

      <div v-else>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Address
                </th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in tableItems" :key="user.id" class="hover:bg-gray-50">
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
                    <button @click="openEditModal(user)" class="text-blue-600 hover:text-blue-900 p-1 rounded"
                      title="Edit user">
                      <PencilIcon class="h-4 w-4" />
                    </button>
                    <button @click="confirmDelete(user)" class="text-red-600 hover:text-red-900 p-1 rounded"
                      title="Delete user">
                      <TrashIcon class="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
          <div class="flex-1 flex justify-between sm:hidden">
            <!-- Mobile pagination -->
            <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1"
              class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed">
              Previous
            </button>
            <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages"
              class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed">
              Next
            </button>
          </div>

          <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
            <div>
              <p class="text-sm text-gray-700">
                Showing
                <span class="font-medium">{{ startItem }}</span>
                to
                <span class="font-medium">{{ endItem }}</span>
                of
                <span class="font-medium">{{ totalUsers }}</span>
                results
              </p>
            </div>

            <div>
              <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                <!-- Previous button -->
                <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1"
                  class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed">
                  <span class="sr-only">Previous</span>
                  <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                      d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z"
                      clip-rule="evenodd" />
                  </svg>
                </button>

                <!-- Page numbers -->
                <template v-for="page in visiblePages" :key="page">
                  <button v-if="page !== '...'" @click="goToPage(page as number)" :class="[
                    page === currentPage
                      ? 'z-10 bg-blue-50 border-blue-500 text-blue-600'
                      : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50',
                    'relative inline-flex items-center px-4 py-2 border text-sm font-medium'
                  ]">
                    {{ page }}
                  </button>
                  <span v-else
                    class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                    ...
                  </span>
                </template>

                <!-- Next button -->
                <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages"
                  class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed">
                  <span class="sr-only">Next</span>
                  <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                      d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                      clip-rule="evenodd" />
                  </svg>
                </button>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- User Modal -->
    <UserModal :show="showModal" :user="editingUser" :mode="modalMode" @close="closeModal" @save="handleSave" />

    <!-- Delete Confirmation Modal -->
    <ConfirmModal :show="showDeleteConfirm" :title="'Delete User'"
      :message="`Are you sure you want to delete ${userToDelete?.name}? This action cannot be undone.`"
      @close="showDeleteConfirm = false" @confirm="deleteUser" />
  </div>
</template>

<script setup lang="ts">
import ConfirmModal from "@/components/ConfirmModal.vue";
import UserModal from "@/components/UserModal.vue";
import { userApi } from "@/services/api";
import {
  ArrowPathIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  PlusIcon,
  TrashIcon,
  UserIcon,
} from "@heroicons/vue/24/outline";
import { computed, onMounted, ref, type Ref } from "vue";

// Type definitions
interface User {
  id: number;
  name: string;
  email: string;
  address: string;
  telephone: string;
}

type ModalMode = "create" | "edit";

interface UserFormData {
  name: string;
  email: string;
  address: string;
  telephone: string;
}


interface ApiError {
  response?: {
    data?: {
      error?: string;
    };
  };
}

// Reactive state with proper typing
const users: Ref<User[]> = ref([]);
const tableItems: Ref<User[]> = ref([]);
const loading: Ref<boolean> = ref(false);
const searchQuery: Ref<string> = ref("");
const totalUsers: Ref<number> = ref(0);

// Pagination state
const currentPage: Ref<number> = ref(1);
const itemsPerPage: Ref<number> = ref(10);
const totalPages: Ref<number> = ref(0);

// Modal state
const showModal: Ref<boolean> = ref(false);
const modalMode: Ref<ModalMode> = ref("create");
const editingUser: Ref<User | null> = ref(null);

// Delete confirmation state
const showDeleteConfirm: Ref<boolean> = ref(false);
const userToDelete: Ref<User | null> = ref(null);

// Computed properties for pagination display
const startItem = computed(() => {
  return (currentPage.value - 1) * itemsPerPage.value + 1;
});

const endItem = computed(() => {
  return Math.min(currentPage.value * itemsPerPage.value, totalUsers.value);
});

const visiblePages = computed(() => {
  const pages: (number | string)[] = [];
  const total = totalPages.value;
  const current = currentPage.value;

  if (total <= 7) {
    // Show all pages if 7 or fewer
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    // Always show first page
    pages.push(1);

    if (current > 4) {
      pages.push('...');
    }

    // Show pages around current page
    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if (current < total - 3) {
      pages.push('...');
    }

    // Always show last page
    if (total > 1) {
      pages.push(total);
    }
  }

  return pages;
});

// Load users from API with pagination
const loadUsers = async (): Promise<void> => {
  try {
    loading.value = true;

    // If API supports pagination, use it. Otherwise, simulate client-side pagination
    const data: Array<User> = await userApi.getAllUsers();
    users.value = data
    tableItems.value = data
    pagination(1)
  } catch (error) {
    console.error("Error loading users:", error);
  } finally {
    loading.value = false;
  }
};

const pagination = (page: number) => {
  const filteredUsers = users.value.filter(user => user.name.toLowerCase().includes(searchQuery.value.trim().toLowerCase()));
  totalUsers.value = filteredUsers.length;
  totalPages.value = Math.ceil(totalUsers.value / itemsPerPage.value);

  const startIndex = (page - 1) * itemsPerPage.value;
  const endIndex = startIndex + itemsPerPage.value;
  currentPage.value = page
  tableItems.value = filteredUsers.slice(startIndex, endIndex);
}

// Handle search input with debouncing
let searchTimeout: ReturnType<typeof setTimeout>;
const handleSearch = (): void => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    pagination(1);
  }, 300); // 300ms debounce
};

// Refresh users
const refreshUsers = (): void => {
  searchQuery.value = "";
  currentPage.value = 1;
  loadUsers();
};

// Pagination functions
const goToPage = (page: number): void => {
  if (page >= 1 && page <= totalPages.value && page !== currentPage.value) {
    pagination(page);
  }
};

const handlePageSizeChange = (): void => {
  currentPage.value = 1;
  pagination(1);
};

// Modal functions
const openCreateModal = (): void => {
  modalMode.value = "create";
  editingUser.value = null;
  showModal.value = true;
};

const openEditModal = (user: User): void => {
  modalMode.value = "edit";
  editingUser.value = { ...user };
  showModal.value = true;
};

const closeModal = (): void => {
  showModal.value = false;
  editingUser.value = null;
};

// Handle user save
const handleSave = async (userData: UserFormData): Promise<void> => {
  try {
    if (modalMode.value === "create") {
      await userApi.createUser(userData);
      alert("User created successfully");
    } else if (editingUser.value) {
      await userApi.updateUser(editingUser.value.id, userData);
    }
    closeModal();
    loadUsers(); // Reload current page
  } catch (error: unknown) {
    const apiError = error as ApiError;
    const message = apiError.response?.data?.error || "Operation failed";
    alert("error: " + message);
    console.error("Save error:", error);
  }
};

// Delete functions
const confirmDelete = (user: User): void => {
  userToDelete.value = user;
  showDeleteConfirm.value = true;
};

const deleteUser = async (): Promise<void> => {
  try {
    if (userToDelete.value) {
      await userApi.deleteUser(userToDelete.value.id);
      showDeleteConfirm.value = false;
      userToDelete.value = null;

      // If we're on the last page and it becomes empty, go to previous page
      if (users.value.length === 1 && currentPage.value > 1) {
        goToPage(currentPage.value - 1);
      } else {
        loadUsers(); // Reload current page
      }
    }
  } catch (error) {
    alert("Failed to delete user");
    console.error("Delete error:", error);
  }
};

// Load users on mount
onMounted(() => {
  loadUsers();
});
</script>