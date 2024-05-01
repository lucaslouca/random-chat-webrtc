<template>
  <div class="container">
    <h2>Get Started!</h2>
    <div id="login-box" class="credentials-box">
      <!-- <select id="camera">
        <option value=""></option>
      </select> -->

      <AppDropdown>
        <template v-slot:toggler="{}"
          ><button id="toggler">{{ selectedCamera }}</button></template
        >
        <AppDropdownContent>
          <AppDropdownItem>
            ><template v-slot:activator="{}">
              <span @click="onCameraSelect({ deviceId: '', label: 'Disabled' })">Disabled</span>
            </template></AppDropdownItem
          >
          <AppDropdownItem v-for="(cam, index) in cameraIds" v-bind:key="index">
            ><template v-slot:activator="{}">
              <span @click="onCameraSelect(cam)">{{ cam.label }} </span>
            </template></AppDropdownItem
          >
        </AppDropdownContent>
      </AppDropdown>

      <input
        type="text"
        id="username"
        class="input-field"
        placeholder="Choose your username"
        v-model="username"
        @change="updateUsername"
      />
      <!-- <div>
        <button
          type="button"
          class="btn b-1"
          @click="onStartButtonClick"
          :disabled="username.length == 0"
        >
          Start
        </button>
      </div> -->

      <button
        class="my-super-cool-btn"
        @click="onStartButtonClick"
        :disabled="username.length == 0"
      >
        <span>Go!</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { loadState } from '@/common'
import type { State } from '@/common'
import { WebRTCClient } from '@/rtc'

import AppDropdown from '@/components/AppDropdown.vue'
import AppDropdownContent from '@/components/AppDropdownContent.vue'
import AppDropdownItem from '@/components/AppDropdownItem.vue'

const router = useRouter()
let stateKey = 'rtc-state'
const cameraIds: Array<any> = reactive([] as Array<any>)
const state = loadState(stateKey)
const selectedCamera = ref(state.camera ? state.camera : 'Select camera')
const username = ref(state.name)
let client = new WebRTCClient(state.name)

onMounted(() => {
  initCameraSelect()
})

function onCameraSelect(camera: any) {
  selectedCamera.value = camera.label
  updateState({ camera: camera.deviceId })
}

function updateUsername() {
  updateState({ name: username.value })
}

/**
 * Update stored app state
 */
function updateState(newState: Partial<State>): void {
  const state = loadState(stateKey)
  const updatedState = { ...state, ...newState }
  localStorage.setItem(stateKey, JSON.stringify(updatedState))
}

/**
 * Setup a listener for the camera selector
 */
// async function initCameraSelect(): Promise<void> {
//   const state = loadState(stateKey)
//   const cameras = await client.getCameras()
//   const cameraSelect = getElement<HTMLSelectElement>('#camera')
//   cameraSelect.innerHTML = '<option value="">Disabled</option>'

//   for (const camera of cameras) {
//     const opt = document.createElement('option')
//     opt.value = camera.deviceId
//     opt.textContent = camera.label
//     if (camera.deviceId === state.camera) {
//       opt.selected = true
//     }
//     cameraSelect.append(opt)
//     cameraIds.push({ deviceId: camera.deviceId, label: camera.label })
//   }

//   cameraSelect.addEventListener('change', async () => {
//     updateState({ camera: cameraSelect.value })
//   })
// }

async function initCameraSelect(): Promise<void> {
  const state = loadState(stateKey)

  const cameras = await client.getCameras()
  for (const camera of cameras) {
    if (camera.deviceId === state.camera) {
      selectedCamera.value = camera.label
    }
    cameraIds.push({ deviceId: camera.deviceId, label: camera.label })
  }
}

function start() {
  router.push({ path: '/chat' })
}

function onStartButtonClick() {
  start()
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
* {
  font-family: 'Product Sans', sans-serif;
}

input.input-field {
  display: block;
  width: 100%;
  margin-bottom: 0.5em;
  box-sizing: border-box;
  padding: 1em;
  border: 3px rgba(255, 255, 255, 0.9) solid;
  border-radius: 300px;
  font-size: 1em;
  background-color: #e3ded9;
  box-shadow: inset 0 0 3px 1px rgba(0, 0, 0, 0.07);
}

input.input-field:focus {
  outline: none;
  background-color: #beb9b3;
  border: 3px #94c2ed solid;
}

#toggler {
  display: block;
  width: 100%;
  margin: 0 auto;
  font-family: 'Open Sans', 'Helvetica Neue', 'Segoe UI', 'Calibri', 'Arial', sans-serif;
  font-size: 18px;
  border: none;
  background: rgb(255, 255, 255);
  background: linear-gradient(180deg, rgba(255, 255, 255, 1) 0%, rgba(251, 251, 251, 1) 100%);
  box-shadow:
    rgba(0, 0, 0, 0.1) 0px 1px 3px 0px,
    rgba(0, 0, 0, 0.06) 0px 1px 2px 0px;
  margin-top: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  padding: 1em;
  border: 1px rgba(255, 255, 255, 0.9) solid;
  border-radius: 300px;
  font-size: 1em;
}

#toggler:hover {
  background-color: rgb(247, 247, 247);
}

/*********************************************************/
.my-super-cool-btn {
  letter-spacing: 1px;
  font-size: 2rem;
  width: 100%;
  border: 7px solid #86bb71;
  border-radius: 300px;
  box-sizing: border-box;
  transition: all 0.85s cubic-bezier(0.25, 1, 0.33, 1);
  box-shadow:
    0 30px 85px rgba(0, 0, 0, 0.14),
    0 15px 35px rgba(0, 0, 0, 0.14);
  background: rgb(255, 255, 255);
  background: linear-gradient(180deg, rgba(255, 255, 255, 1) 0%, rgba(249, 249, 249, 1) 100%);
  cursor: pointer;
}

.my-super-cool-btn span {
  font-family: 'Montserrat', sans-serif;
  background-color: $yellow;
  color: transparent;
  text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.3);
  -webkit-background-clip: text;
  -moz-background-clip: text;
  background-clip: text;
  padding: 5px;
  clip-path: inset(2px);
}

.my-super-cool-btn:hover {
  transform: scale(0.8);
  box-shadow:
    0 20px 55px rgba(0, 0, 0, 0.14),
    0 15px 35px rgba(0, 0, 0, 0.14);
}

button:disabled,
button[disabled] {
  background: white;
  border: 1px solid rgb(208, 208, 208);
  color: gray;
  box-shadow: none;
}

button:disabled span,
button[disabled] span {
  background: white;
  color: rgb(234, 234, 234);
  box-shadow: none;
  text-shadow: none;
  -webkit-background-clip: none;
  -moz-background-clip: none;
  background-clip: none;
  padding: 0;
  clip-path: none;
}

button:disabled:hover,
button[disabled]:hover {
  box-shadow: none;
  transform: none;
  cursor: not-allowed;
}
</style>
