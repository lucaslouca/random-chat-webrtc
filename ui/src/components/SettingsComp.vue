<template>
  <div class="settings-component-content">
    <SpinnerComp :color="`red`" v-if="loading" />
    <div v-if="!loading" class="settings">
      <section>
        <h2>Picture</h2>
        <SpinnerComp :color="`red`" v-if="changingPicture" />
        <div class="field-label-container" v-if="!changingPicture">
          <label for="username" class="required"> Picture </label>
          <div class="profile-picture-wrapper">
            <img :src="dataUrl" />
          </div>
          <input type="file" @change="onChangePicture" name="image" id="image" accept="image/*" />
        </div>
        <span
          id="validation-message"
          v-if="changePictureValidationMessage"
          v-bind:class="{ success: changePictureError === false }"
        >
          {{ changeEmailValidationMessage }}
        </span>
        <div class="clearfix"></div>
      </section>
      <section>
        <h2>Profile</h2>
        <div class="field-label-container">
          <label for="username"> Username </label>
          <span id="username" class="user">{{ username }}</span>
        </div>
        <SpinnerComp :color="`red`" v-if="changingEmail" />
        <div class="field-label-container" v-if="!changingEmail">
          <label for="email" class="required"> E-Mail </label>
          <input id="email" class="input-field" v-model="email" placeholder="Your E-mail" />
        </div>
        <span
          id="validation-message"
          v-if="changeEmailValidationMessage"
          v-bind:class="{ success: changeEmailError === false }"
        >
          {{ changeEmailValidationMessage }}
        </span>
        <button class="button" @click="onChangeEmailClick" :disabled="changingEmail">
          Update profile
        </button>
        <div class="clearfix"></div>
      </section>
      <section>
        <h2>Change password</h2>
        <SpinnerComp :color="`red`" v-if="changingPassword" />
        <div class="field-label-container" v-if="!changingPassword">
          <label for="old-password" class="required"> Old password </label>
          <input id="old-password" type="password" class="input-field" v-model="oldPassword" />
        </div>
        <div class="field-label-container" v-if="!changingPassword">
          <label for="new-password" class="required"> New password </label>
          <input id="new-password" type="password" class="input-field" v-model="newPassword" />
        </div>
        <div class="field-label-container" v-if="!changingPassword">
          <label for="new-password-confirm" class="required"> Confirm new password </label>
          <input
            id="new-password-confirm"
            type="password"
            class="input-field"
            v-model="newPasswordConfirm"
          />
        </div>
        <span
          id="validation-message"
          v-if="changePasswordValidationMessage"
          v-bind:class="{ success: changePasswordError === false }"
        >
          {{ changePasswordValidationMessage }}
        </span>
        <button
          class="button"
          @click="onChangePasswordClick"
          :disabled="changingPassword"
          style="clear: right"
        >
          Update password
        </button>
        <div class="clearfix"></div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { API } from '@/api'
import SpinnerComp from '@/components/SpinnerComp.vue'
import { ref, computed } from 'vue'
import userImage from '@/assets/img/user.jpg'

let loading = ref<boolean>(false)
let picture = ref<string | null>(null)

let changingPicture = ref<boolean>(false)
let changePictureError = ref<boolean>(false)
let changePictureValidationMessage = ref<string | null>(null)
let username = ref<string | null>(null)
let email = ref<string | null>(null)
let changingEmail = ref<boolean>(false)
let changeEmailError = ref<boolean>(false)
let changeEmailValidationMessage = ref<string | null>(null)
let oldPassword = ref<string | null>(null)
let newPassword = ref<string | null>(null)
let newPasswordConfirm = ref<string | null>(null)
let changingPassword = ref<boolean>(false)
let changePasswordError = ref<boolean>(false)
let changePasswordValidationMessage = ref<string | null>(null)

GETEmail()
GETPicture()
GETUsername()

const dataUrl = computed(() => {
  if (picture.value) {
    return 'data:image/jpeg;base64,' + picture.value
  } else {
    return userImage
  }
})

function GETUsername() {
  API.getEndpoints('users')!.get('getUsername')!({}).then(function (response: any) {
    console.log('[Members] Get username response', response)
    username.value = response.data
  })
}
function GETEmail() {
  loading.value = true
  API.getEndpoints('users')!.get('getEmail')!({}).then(function (response: any) {
    console.log('[Settings] Get email response', response)
    email.value = response.data
    loading.value = false
  })
}
function GETPicture() {
  changingPicture.value = true
  API.getEndpoints('users')!.get('getPicture')!({}).then(function (response: any) {
    console.log('[Settings] Get picture response', response)
    if (response.data) {
      API.getEndpoints('users')!.get('getPictureResource')!({ fileName: response.data }).then(
        function (response: any) {
          console.log('[Settings] Get picture resource response', response)
          picture.value = response.data
          changingPicture.value = false
        }
      )
    } else {
      picture.value = null
      changingPicture.value = false
    }
  })
}

function onChangePasswordClick() {
  changePasswordError.value = false
  changePasswordValidationMessage.value = null
  changingPassword.value = false
  if (oldPassword.value && newPassword.value && newPasswordConfirm.value) {
    if (newPassword.value === newPasswordConfirm.value) {
      POSTChangePassword({
        oldPassword: oldPassword.value,
        newPassword: newPassword.value,
        newPasswordConfirm: newPasswordConfirm.value
      })
    } else {
      changePasswordError.value = true
      changePasswordValidationMessage.value =
        'New password entry does not match with confirmation entry!'
    }
  } else {
    changePasswordError.value = true
    changePasswordValidationMessage.value = 'Please fill out all the fields!'
  }
}

function onChangePicture(e: any) {
  picture.value = e.target.files[0]
  changePictureError.value = false
  changePictureValidationMessage.value = null
  changingPicture.value = false

  const formData = new FormData()
  formData.append('file', picture.value!)

  if (picture.value) {
    POSTChangePicture(formData)
  } else {
    changePictureError.value = true
    changePictureValidationMessage.value = 'Please provide an picture!'
  }
}

function onChangeEmailClick() {
  changeEmailError.value = false
  changeEmailValidationMessage.value = null
  changingEmail.value = false
  if (email.value) {
    POSTChangeEmail({ email: email.value })
  } else {
    changeEmailError.value = true
    changeEmailValidationMessage.value = 'Please provide an e-mail!'
  }
}

function POSTChangePassword(cridentials: any) {
  changingPassword.value = true
  console.log('[Settings] POST change password ', cridentials)
  API.getEndpoints('users')!.get('changePassword')!(cridentials).then(function (response: any) {
    console.log('[Settings] POST Change Password response', response)
    var result = response.data
    changePasswordValidationMessage.value = result.message
    if (!result.success) {
      changePasswordError.value = true
    } else {
      oldPassword.value = null
      newPassword.value = null
      newPasswordConfirm.value = null
      setTimeout(function () {
        changePasswordValidationMessage.value = null
      }, 2000)
    }
    changingPassword.value = false
  })
}

function POSTChangeEmail(cridentials: any) {
  changingEmail.value = true
  console.log('[Settings] POST change Email ', cridentials)
  API.getEndpoints('users')!.get('changeEmail')!(cridentials).then(function (response: any) {
    console.log('[Settings] POST Change Email response', response)
    var result = response.data
    changeEmailValidationMessage.value = result.message
    if (!result.success) {
      changeEmailError.value = true
    } else {
      setTimeout(function () {
        changeEmailValidationMessage.value = null
      }, 2000)
    }
    changingEmail.value = false
  })
}

function POSTChangePicture(file: any) {
  changingPicture.value = true
  console.log('[Settings] POST change Picture ', file)
  API.getEndpoints('users')!.get('changePicture')!(file).then(function (response: any) {
    console.log('[Settings] POST Change Picture response', response)
    var result = response.data
    changePictureValidationMessage.value = result.message
    if (!result.success) {
      changePictureError.value = true
    } else {
      GETPicture()
      setTimeout(function () {
        changePictureValidationMessage.value = null
      }, 2000)
    }
  })
}
</script>

<style lang="scss" scoped>
.settings-component-content {
  width: 100%;
  height: 100%;
  display: table;
}

.settings {
  width: 1000px;
  margin: 0 auto;
  display: table;
}

section {
  background-color: white;
  margin-top: 1em;
  margin-bottom: 1em;
  padding: 1em;
  box-shadow: 0px 1px 3px rgba(24, 0, 69, 0.1);
}

section .field-label-container {
  display: table;
  width: 100%;
  margin-top: 1em;
  margin-bottom: 1em;
}

section .field-label-container label {
  display: table-cell;
  width: 20%;
  text-align: right;
  padding-right: 1em;
}

section .field-label-container .input-field {
  display: table-cell;
  margin-bottom: 1em;
  height: 2em;
  line-height: 2em;
  border-radius: 2em;
  padding-left: 1em;
  border: 1px solid #5f6368;
}

section .field-label-container .input-field:focus {
  border: 1px solid rgb(97, 169, 249);
  -webkit-box-shadow: 0px 0px 5px 2px rgba(146, 70, 70, 0.5);
  box-shadow: 0px 0px 5px 2px rgba(0, 0, 0, 0.5);
}

section .field-label-container .input-field:hover {
  border: 1px solid rgb(97, 169, 249);
  -webkit-box-shadow: 0px 0px 5px 2px rgba(0, 0, 0, 0.1);
  box-shadow: 0px 0px 5px 2px rgba(0, 0, 0, 0.1);
  -moz-box-shadow: 0px 0px 5px 2px rgba(0, 0, 0, 0.1);
}

section .profile-picture-wrapper {
  padding: 0.5em;
  width: 100px;
  background-color: rgb(97, 169, 249);
  box-shadow: 0px 1px 3px rgba(24, 0, 69, 0.1);
  border-radius: 4px;
  margin-bottom: 1em;
}

section .profile-picture-wrapper img {
  max-width: 100%;
  max-height: 100%;
  display: block;
}

section .button {
  float: right;
  cursor: pointer;

  letter-spacing: 1px;
  border: 3px solid #86bb71;
  border-radius: 300px;
  box-sizing: border-box;
  transition: all 0.85s cubic-bezier(0.25, 1, 0.33, 1);
  background: rgb(255, 255, 255);
  background: linear-gradient(180deg, rgba(255, 255, 255, 1) 0%, rgba(249, 249, 249, 1) 100%);
  cursor: pointer;
}

#validation-message {
  color: tomato;
  display: inline-block;
  float: left;
  font-weight: bold;
}

#validation-message.success {
  color: #8bc34ad9;
}

.clearfix {
  clear: both;
}
</style>
