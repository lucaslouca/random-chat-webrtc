<template>
  <div id="login-component-container">
    <div id="center-cell">
      <div class="m-logo-container">
        <!-- <h1>
          <a class="black" href="/">Home</a>
        </h1> -->
      </div>
      <div id="login" v-bind:class="{ shake: error === true }">
        <div id="filed-container" v-if="!loading">
          <h1>Login</h1>
          <input
            type="text"
            name="username"
            class="input-field"
            v-model="input.username"
            placeholder="Username"
            v-on:keyup.enter="tryLogin"
          />
          <input
            type="password"
            name="password"
            class="input-field"
            v-model="input.password"
            placeholder="Password"
            v-on:keyup.enter="tryLogin"
          />
          <div class="button-container">
            <button type="button" class="button" @click="tryLogin"><span>Login</span></button>
          </div>

          <span id="validation-message">
            {{ validationMessage }}
          </span>

          <span id="account-links">
            <router-link v-if="!loading" to="/signup"> Sign Up </router-link>
            <router-link v-if="!loading" to="/resetpassword"> Forgot Password </router-link>
          </span>
        </div>

        <SpinnerComp v-if="loading" />
        <h1 v-bind:class="{ loading: loading === true }" v-if="loading">Logging in...</h1>
      </div>

      <div id="container">
        <router-view />
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

const emit = defineEmits<{
  (e: 'authenticated', success: boolean): void
}>()

const input = {
  username: null,
  password: null
}
let loading = ref<boolean>(false)
let validationMessage = ref<string | undefined>(undefined)
let error = ref<boolean>(false)

function login(username: string, password: string) {
  loading.value = true
  API.login(username, password)
    .then(function (response: any) {
      console.log('[Login] Response: ', response)
      localStorage.setItem('user', JSON.stringify(response.data))
      console.log('[Login] Stored:', localStorage.getItem('user'))
      emit('authenticated', true)
      router.replace({ path: 'members/dashboard', params: { username: 'foobar' } })
    })
    .catch(function (error: any) {
      console.log(error.response.status)
      var response = error.response
      loading.value = false
      error.value = true
      setTimeout(function () {
        error.value = false
      }, 500)
      validationMessage.value = response.data.error_description
    })
}

function tryLogin() {
  if (input.username && input.password) {
    login(input.username, input.password)
  } else {
    error.value = true
    setTimeout(function () {
      error.value = false
    }, 500)
    validationMessage.value = 'A username and password must be present'
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
  color: inherit;
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

#filed-container {
  display: grid;
  grid-template-columns: auto;
  grid-template-rows: auto;
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
  font-weight: bold;
  height: 5rem;
  display: flex;
  justify-content: center;
  align-items: end;
}

#login #account-links {
  display: grid;
  height: 5rem;
  grid-template-columns: 1fr 1fr;
  align-items: end;
  justify-items: center;
}

#login #account-links a {
  font-family: 'Montserrat', sans-serif;
  padding: 10px;
  border-radius: 100px;
  transition: all 0.85s cubic-bezier(0.25, 1, 0.33, 1);
  background: rgba(0, 0, 0, 0.05);
  color: rgb(15, 15, 15);
  cursor: pointer;
}

#login #account-links a:hover {
  background-color: rgba(0, 0, 0, 0.2);
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
