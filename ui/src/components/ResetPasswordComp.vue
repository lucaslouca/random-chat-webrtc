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
          <h1>Reset Password</h1>
          <input
            v-if="!linkSent"
            type="text"
            name="email"
            class="input-field"
            v-model="email"
            placeholder="E-mail"
          />
          <span id="success-message">
            {{ successMessage }}
          </span>
          <div class="button-container">
            <!-- <router-link v-if="!loading" to="/login"> Log In </router-link> -->
            <button v-if="!linkSent" type="button" class="button primary" @click="onResetClick">
              <span>Reset</span>
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

let email = ref<string | null>(null)
let loading = ref<boolean>(false)
let msg = ref<string | null>(null)
let validationMessage = ref<string | null>(null)
let successMessage = ref<string | null>(null)
let error = ref<boolean>(false)
let linkSent = ref<boolean>(false)

function POSTResetPassword(email: string) {
  loading.value = true
  console.log('[ResetPassword] POSTResetPassword ')
  msg.value = 'Resetting password...'
  API.resetPassword(email)
    .then(function (response: any) {
      console.log('[ResetPassword] response: ', response)
      if (response.data.success === false) {
        validationMessage.value = response.data.message
        error.value = true
        setTimeout(function () {
          error.value = false
        }, 500)
      } else {
        successMessage.value = response.data.message
        linkSent.value = true
      }
      loading.value = false
    })
    .catch(function (error: any) {
      console.log('[ResetPassword] Error: ', error.response)
      loading.value = false
      error.value = true
      setTimeout(function () {
        error.value = false
      }, 500)
      validationMessage.value = error.response.data.error
    })
}

function onResetClick() {
  validationMessage.value = null
  if (email.value) {
    POSTResetPassword(email.value)
  } else {
    error.value = true
    setTimeout(function () {
      error.value = false
    }, 500)
    validationMessage.value = 'An email must be present'
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
