<template>
  <div id="login-component-container">
    <div id="center-cell">
      <div id="login" v-bind:class="{ shake: error === true }">
        <div v-if="!loading">
          <h1>Sign Up</h1>
          <input
            type="text"
            name="username"
            class="input-field"
            v-model="input.username"
            placeholder="Username"
          />
          <input
            type="password"
            name="password"
            class="input-field"
            v-model="input.password"
            placeholder="Password"
          />
          <input
            type="text"
            name="email"
            class="input-field"
            v-model="input.email"
            placeholder="E-mail"
          />
          <div class="button-container">
            <!-- <router-link v-if="!loading" to="/login"> Log In </router-link> -->
            <button type="button" class="button primary" @click="onSignUpClick">
              <span>Sign Up</span>
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
import { useRouter } from 'vue-router'

const router = useRouter()

const input = {
  username: null,
  password: null,
  email: null
}
let loading = ref<boolean>(false)
let validationMessage: string = ''
let msg: string = ''
let error = ref<boolean>(false)

const emit = defineEmits<{
  (e: 'authenticated', success: boolean): void
}>()

function POSTUser(user: any) {
  loading.value = true
  console.log('[Signup] POST user ', user)
  msg = 'Signing up...'
  API.register(user.username, user.password, user.email).then(function (response: any) {
    console.log('[Signup] POST user response', response)
    var result = response.data
    if (result.success) {
      login(user.username, user.password)
    } else {
      loading.value = false
      error.value = true
      setTimeout(function () {
        error.value = false
      }, 500)
      validationMessage = result.message
    }
  })
}

function login(username: string, password: string) {
  loading.value = true
  msg = 'Logging in...'

  API.login(username, password)
    .then(function (response) {
      console.log('[Signup] Login response: ', response)
      localStorage.setItem('user', JSON.stringify(response.data))
      console.log('[Signup] Stored:', localStorage.getItem('user'))

      emit('authenticated', true)
      router.replace({ path: 'members/dashboard' })
    })
    .catch(function (error) {
      console.log('[Signup] Error logging in: ', error.response)
    })
}

function onSignUpClick() {
  if (input.email && input.password && input.email) {
    var user = {
      username: input.username,
      email: input.email,
      password: input.password
    }
    POSTUser(user)
  } else {
    error.value = true
    setTimeout(function () {
      error.value = false
    }, 500)
    validationMessage = 'An email and password must be present'
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
