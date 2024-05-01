<template>
  <div id="login-component-container">
    <div id="center-cell">
      <!-- <div class="m-logo-container">
        <h1>
          <a class="black" href="/">{{ $appName }}</a>
        </h1>
      </div> -->
      <div id="login" v-bind:class="{ shake: error === true }">
        <div v-if="!loading">
          <h1>Change Password</h1>
          <input
            type="password"
            name="password"
            class="input-field"
            v-model="password"
            placeholder="Password"
          />
          <div class="button-container">
            <button type="button" class="button primary" @click="onChangeClick">
              <span>Change</span>
            </button>
            <span id="validation-message">
              {{ validationMessage }}
            </span>
          </div>
          <div class="clearfix"></div>
        </div>

        <SpinnerComp v-if="loading" />
        <h1 v-bind:class="{ loading: loading === true }" v-if="loading">{{ msg }}</h1>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SpinnerComp from '@/components/SpinnerComp.vue'
import { API } from '@/api'
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
const route = useRoute()
const router = useRouter()

const emit = defineEmits<{
  (e: 'authenticated', success: boolean): void
}>()

let userId = ref<string | undefined>(undefined)
let password = ref<string | undefined>(undefined)
let token = ref<string | undefined>(undefined)
let loading = ref<boolean>(false)
let msg = ref<string | null>(null)
let validationMessage = ref<string | null>(null)
let error = ref<boolean>(false)

userId.value = route.query.id as string
token.value = route.query.token as string

function POSTChangePassword(user: { username?: string; password?: string; token?: string }) {
  loading.value = true
  console.log('[UpdatePassword] POST change password ', user)
  msg.value = 'Changing Password...'
  API.getEndpoints('users')!.get('changeLostPassword')!(user).then(function (response: any) {
    console.log('[UpdatePassword] POST Change Lost Password response', response)
    var result = response.data
    if (result.success) {
      login(result.username!, user.password!)
    } else {
      loading.value = false
      error.value = true
      setTimeout(function () {
        error.value = false
      }, 500)
      validationMessage.value = result.message
    }
  })
}

function login(username: string, password: string) {
  loading.value = true
  msg.value = 'Logging In...'
  API.login(username, password)
    .then(function (response) {
      console.log('[UpdatePassword] Response: ', response)
      localStorage.setItem('user', JSON.stringify(response.data))
      console.log('[UpdatePassword] Stored:', localStorage.getItem('user'))
      emit('authenticated', true)
      router.replace({ path: 'members/dashboard' })
    })
    .catch(function (error) {
      var response = error.response
      console.log('[UpdatePassword] Error: ', response)
      loading.value = false
      error.value = true
      setTimeout(function () {
        error.value = false
      }, 500)
      validationMessage.value = response.data.error_description
    })
}

function onChangeClick() {
  if (password.value) {
    var user = { userId: userId.value, password: password.value, token: token.value }
    POSTChangePassword(user)
  } else {
    error.value = true
    setTimeout(function () {
      error.value = false
    }, 500)
    validationMessage.value = 'A password must be present'
  }
}
</script>

<style lang="scss" scoped>
* {
  font-family: 'Product Sans', sans-serif;
}

#login-component-container {
  display: grid;
  margin: auto;
  height: 100vh;
  background-color: rgb(241, 241, 241);
}

#center-cell {
  width: 500px;
  display: table;

  align-self: center;
  justify-self: center;
  z-index: 1000;
}

a {
  text-decoration: none;
  margin-right: 1em;
}

a:visited {
}

.m-logo-container {
  text-align: center;
  display: table-row;
}

h1 {
  font-weight: 500;
  text-align: center;
}

.m-logo-container h1 a {
  font-weight: 500;
}

#login {
  width: 500px;
  padding: 50px;
  background-color: white;
  box-shadow: 0px 1px 3px rgba(24, 0, 69, 0.1);
  border-radius: 4px;
}

#login input.input-field {
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

#login input.input-field:focus {
  outline: none;
  background-color: #beb9b3;
  border: 3px #94c2ed solid;
}

#login .button-container {
  text-align: right;
  margin-top: 1em;
}

#login .button-container button {
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

#login .button-container button span {
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

#login .button-container button:hover {
  transform: scale(0.8);
  box-shadow:
    0 20px 55px rgba(0, 0, 0, 0.14),
    0 15px 35px rgba(0, 0, 0, 0.14);
}
#login h1.loading {
  text-align: center;
  margin-bottom: 0;
}

#login #validation-message {
  color: tomato;
  display: inline-block;
  float: left;
  font-weight: bold;
}

.shake {
  animation: shake 0.82s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
  transform: translate3d(0, 0, 0);
  backface-visibility: hidden;
  perspective: 1000px;
}

@keyframes shake {
  10%,
  90% {
    transform: translate3d(-1px, 0, 0);
  }

  20%,
  80% {
    transform: translate3d(2px, 0, 0);
  }

  30%,
  50%,
  70% {
    transform: translate3d(-4px, 0, 0);
  }

  40%,
  60% {
    transform: translate3d(4px, 0, 0);
  }
}
</style>
