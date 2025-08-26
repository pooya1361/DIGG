<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto">
    <div
      class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
    >
      <!-- Background overlay -->
      <div
        class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
        @click="$emit('close')"
      ></div>

      <!-- Modal -->
      <div
        class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
      >
        <form @submit.prevent="handleSubmit">
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div class="w-full mt-3 text-center sm:mt-0 sm:text-left">
                <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
                  {{ mode === "create" ? "Create New User" : "Edit User" }}
                </h3>

                <div class="space-y-4">
                  <!-- Name -->
                  <div>
                    <label for="name" class="block text-sm font-medium text-gray-700">
                      Full Name *
                    </label>
                    <input
                      id="name"
                      v-model="formData.name"
                      type="text"
                      required
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      :class="{ 'border-red-300': errors.name }"
                      placeholder="Enter full name"
                    />
                    <p v-if="errors.name" class="mt-1 text-sm text-red-600">
                      {{ errors.name }}
                    </p>
                  </div>

                  <!-- Email -->
                  <div>
                    <label for="email" class="block text-sm font-medium text-gray-700">
                      Email Address *
                    </label>
                    <input
                      id="email"
                      v-model="formData.email"
                      type="email"
                      required
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      :class="{ 'border-red-300': errors.email }"
                      placeholder="user@digg.com"
                    />
                    <p v-if="errors.email" class="mt-1 text-sm text-red-600">
                      {{ errors.email }}
                    </p>
                  </div>

                  <!-- Telephone -->
                  <div>
                    <label
                      for="telephone"
                      class="block text-sm font-medium text-gray-700"
                    >
                      Phone Number *
                    </label>
                    <input
                      id="telephone"
                      v-model="formData.telephone"
                      type="tel"
                      required
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      :class="{ 'border-red-300': errors.telephone }"
                      placeholder="(555) 123-4567"
                    />
                    <p v-if="errors.telephone" class="mt-1 text-sm text-red-600">
                      {{ errors.telephone }}
                    </p>
                  </div>

                  <!-- Address -->
                  <div>
                    <label for="address" class="block text-sm font-medium text-gray-700">
                      Address *
                    </label>
                    <textarea
                      id="address"
                      v-model="formData.address"
                      required
                      rows="3"
                      class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                      :class="{ 'border-red-300': errors.address }"
                      placeholder="123 Main St, City, State 12345"
                    ></textarea>
                    <p v-if="errors.address" class="mt-1 text-sm text-red-600">
                      {{ errors.address }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button
              type="submit"
              :disabled="saving"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
            >
              <span v-if="saving" class="inline-flex items-center">
                <svg
                  class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  ></circle>
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
                Saving...
              </span>
              <span v-else>
                {{ mode === "create" ? "Create User" : "Update User" }}
              </span>
            </button>
            <button
              type="button"
              @click="$emit('close')"
              class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch, reactive } from "vue";

export default {
  name: "UserModal",
  emits: ["close", "save"],
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    user: {
      type: Object,
      default: null,
    },
    mode: {
      type: String,
      default: "create", // 'create' or 'edit'
    },
  },
  setup(props, { emit }) {
    const saving = ref(false);
    const formData = reactive({
      name: "",
      email: "",
      telephone: "",
      address: "",
    });
    const errors = ref({});

    // Watch for prop changes to update form data
    watch(
      () => props.user,
      (newUser) => {
        if (newUser) {
          formData.name = newUser.name || "";
          formData.email = newUser.email || "";
          formData.telephone = newUser.telephone || "";
          formData.address = newUser.address || "";
        } else {
          // Reset form for create mode
          formData.name = "";
          formData.email = "";
          formData.telephone = "";
          formData.address = "";
        }
        errors.value = {};
      },
      { immediate: true }
    );

    // Form validation
    const validateForm = () => {
      const newErrors = {};

      if (!formData.name.trim()) {
        newErrors.name = "Name is required";
      }

      if (!formData.email.trim()) {
        newErrors.email = "Email is required";
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
        newErrors.email = "Please enter a valid email address";
      }

      if (!formData.telephone.trim()) {
        newErrors.telephone = "Phone number is required";
      }

      if (!formData.address.trim()) {
        newErrors.address = "Address is required";
      }

      errors.value = newErrors;
      return Object.keys(newErrors).length === 0;
    };

    // Handle form submission
    const handleSubmit = async () => {
      if (!validateForm()) {
        return;
      }

      saving.value = true;
      try {
        emit("save", {
          name: formData.name.trim(),
          email: formData.email.trim(),
          telephone: formData.telephone.trim(),
          address: formData.address.trim(),
        });
      } finally {
        saving.value = false;
      }
    };

    return {
      saving,
      formData,
      errors,
      handleSubmit,
      validateForm,
    };
  },
};
</script>
