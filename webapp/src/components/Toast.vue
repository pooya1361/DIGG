<template>
  <div class="fixed top-0 right-0 z-50 m-6">
    <Transition
      enter-active-class="transform ease-out duration-300 transition"
      enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
      leave-active-class="transition ease-in duration-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        class="max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden"
      >
        <div class="p-4">
          <div class="flex items-start">
            <div class="flex-shrink-0">
              <CheckCircleIcon v-if="type === 'success'" class="h-6 w-6 text-green-400" />
              <XCircleIcon v-else-if="type === 'error'" class="h-6 w-6 text-red-400" />
              <ExclamationTriangleIcon
                v-else-if="type === 'warning'"
                class="h-6 w-6 text-yellow-400"
              />
              <InformationCircleIcon v-else class="h-6 w-6 text-blue-400" />
            </div>
            <div class="ml-3 w-0 flex-1 pt-0.5">
              <p class="text-sm font-medium text-gray-900">
                {{ getTitle() }}
              </p>
              <p class="mt-1 text-sm text-gray-500">
                {{ message }}
              </p>
            </div>
            <div class="ml-4 flex-shrink-0 flex">
              <button
                @click="$emit('close')"
                class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <span class="sr-only">Close</span>
                <XMarkIcon class="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import {
  CheckCircleIcon,
  XCircleIcon,
  ExclamationTriangleIcon,
  InformationCircleIcon,
  XMarkIcon,
} from "@heroicons/vue/24/outline";

export default {
  name: "Toast",
  components: {
    CheckCircleIcon,
    XCircleIcon,
    ExclamationTriangleIcon,
    InformationCircleIcon,
    XMarkIcon,
  },
  emits: ["close"],
  props: {
    type: {
      type: String,
      default: "info",
      validator: (value) => ["success", "error", "warning", "info"].includes(value),
    },
    message: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const getTitle = () => {
      switch (props.type) {
        case "success":
          return "Success!";
        case "error":
          return "Error!";
        case "warning":
          return "Warning!";
        default:
          return "Info";
      }
    };

    return {
      getTitle,
    };
  },
};
</script>
