<template>
  <div id="members">
    <div id="header">
      <div class="header-content">
        <div id="logo-text">
          <router-link to="/members/dashboard">
            <h1>Home</h1>
          </router-link>
        </div>
        <nav class="header-content-menu">
          <div class="menu-item">
            <router-link
              to="/members/settings"
              v-bind:class="{ selected: route.path === '/members/settings' }"
            >
              <font-awesome-icon icon="gear"></font-awesome-icon> Settings
            </router-link>
          </div>
          <div class="menu-item">
            <div id="submenu" class="dropdown" @click.stop="toggle">
              <span id="account" class="account">
                <img :src="dataUrl" class="profile-circle" />
              </span>
              <div class="submenu" v-if="showMenu">
                <ul class="root">
                  <li>
                    <router-link
                      to="/members/settings"
                      v-bind:class="{
                        selected: route.path === '/members/settings'
                      }"
                    >
                      Settings
                    </router-link>
                  </li>
                  <li>
                    <a @click="logout"> Logout </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </nav>
      </div>
    </div>

    <div id="container">
      <LoadingModal v-if="loggingOut" :message="loggingOutMessage" />
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import userImage from '@/assets/img/user.jpg'
import LoadingModal from '@/components/LoadingModalComp.vue'
import { API } from '@/api'
import { getElement } from '@/common'

const router = useRouter()
const route = useRoute()

function toggle() {
  const closeListerner = (e: any) => {
    const dropdown = getElement('#submenu')
    if (catchOutsideClick(e, dropdown)) {
      document.removeEventListener('click', closeListerner), (showMenu.value = false)
    }
  }

  // Add event listener to watch clicks outside the menu
  document.addEventListener('click', closeListerner)

  showMenu.value = !showMenu.value
}

function catchOutsideClick(event: any, dropdown: any) {
  // When user clicks menu — do nothing
  if (dropdown == event.target) {
    return false
  }

  // When user clicks outside of the menu — close the menu
  if (showMenu && dropdown != event.target) {
    return true
  }
}

let currentYear = ref<number | undefined>(undefined)
let domain = ref<string | undefined>(undefined)
let loggingOut = ref<boolean>(false)
let showMenu = ref<boolean>(false)
let loggingOutMessage = ref<string>('Logging Out...')
let picture = ref<string | null>(null)

currentYear.value = new Date().getFullYear()
domain.value = window.location.hostname
GETPicture()

const dataUrl = computed(() => {
  if (picture.value) {
    return 'data:image/jpeg;base64,' + picture.value
  } else {
    return userImage
  }
})

function GETPicture() {
  API.getEndpoints('users')!.get('getPicture')!({}).then(function (response: any) {
    console.log('[Settings] Get picture response', response)
    if (response.data) {
      API.getEndpoints('users')!.get('getPictureResource')!({ fileName: response.data }).then(
        function (response: any) {
          console.log('[Settings] Get picture resource response', response)
          picture.value = response.data
        }
      )
    } else {
      picture.value = null
    }
  })
}

function logout() {
  console.log('[Members] logout')
  loggingOut.value = true
  API.getEndpoints('users')!.get('logout')!({}).then(function (response: any) {
    console.log('[Members] Logout response', response)
    var result = response.data
    console.log(result)
    if (result.success) {
      localStorage.removeItem('user')
      router.replace({ name: 'Login' })
    }
    loggingOut.value = false
  })
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  border: none;
}

.clearfix {
  clear: both;
}

body {
  overflow-x: hidden;
}

body,
html {
  margin: 0;
  padding: 0;
  border: none;
  color: #474645;
  font-family: 'Oxygen', sans-serif;
  font-weight: 300;
  font-size: 16px;
}

#members {
  -webkit-overflow-scrolling: touch;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: rgba(247, 247, 247, 1);
  background: linear-gradient(0deg, rgb(255, 255, 255) 0%, rgba(247, 247, 247, 1) 100%);
  border-radius: 0;

  -moz-box-shadow: -3px 0 6px darken(#556270, 5%);
  -webkit-box-shadow: -3px 0 6px darken(#556270, 5%);
  box-shadow: -3px 0 6px darken(#556270, 5%);

  -moz-transition: all 300ms;
  -webkit-transition: all 300ms;
  transition: all 300ms;

  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  -ms-box-sizing: border-box;
  box-sizing: border-box;

  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  display: table;
}

#header {
  background: rgba(0, 0, 0, 0.05);
  width: 100%;
  height: 75px;
  display: table;
  /* border-bottom: 1px #dcdcdc solid; */
}

#container {
  display: table;
  width: 100%;
  margin: 0;
  height: 85vh;
  /* #0b0a1f; */
}

#footer {
  width: 100%;
  height: 10vh;
  text-align: center;
  font-size: 0.8em;
  display: table-row;
}

/************************************************/
/****************** HEADER **********************/
/************************************************/
#logo-text {
  sizing: border-box;
  vertical-align: middle;
  display: table-cell;
  width: 70%;
  padding-left: 1.5em;
}

#logo-text a {
  font-weight: 500;
  color: rgb(15, 15, 15);
  text-decoration: none;
}

#logo-text h1 {
  padding: 0;
  height: 1.2em;
  line-height: 1.2em;
  font-weight: 500;
}

#logo-text .user {
  font-size: 0.9em;
  margin-top: 15px;
}

#footer span {
  line-height: 60px;
  color: rgb(200, 200, 200);
}

.header-content {
  margin: 0 auto;
  height: 75px;
  width: 100%;
  display: grid;
  align-items: center;
  grid-template-columns: 1fr auto auto;
  gap: 2rem;
}

.header-content-menu {
  height: 75px;
  float: right;
  vertical-align: middle;
  display: table;
  text-align: right;
}

.header-content-menu .menu-item {
  display: table-cell;
  vertical-align: middle;
  text-align: right;
}

.header-content-menu a {
  margin-left: 10px;
  text-decoration: none;
  cursor: pointer;
  vertical-align: middle;
  margin-right: 30px;
  color: rgb(15, 15, 15);
}

.header-content-menu a.active {
  color: #1e94e8;
}

.header-content-menu a.active:hover {
  border: none;
}

.header-content-menu a:hover {
  border-bottom: 2px solid rgb(15, 15, 15);
}

.header-content-menu a.highlight {
  background-color: #72bf7b;
  padding: 5px;
  border-radius: 20px;
  -webkit-transition: background-color 500ms ease-out;
  -moz-transition: background-color 500ms ease-out;
  -o-transition: background-color 500ms ease-out;
  transition: background-color 500ms ease-out;
}

.header-content-menu a.highlight:hover {
  border: none;
}

.header-content-menu a.selected {
  border-bottom: 2px rgb(15, 15, 15) solid;
}

/************************************************/
/******************* DROPDOWN *******************/
/************************************************/
.dropdown a:hover {
  border: none;
}

div.dropdown span {
  margin: 0 auto;
}

div.dropdown {
  color: #555;
  position: relative;
  max-width: 100px;
  /* height: 17px; */
  text-align: center;
  cursor: pointer;
}

div.submenu {
  position: absolute;
  z-index: 100;
  width: 100px;
  margin-left: 0px;
  padding: 40px 0 5px;
  border-radius: 6px;
  top: 0;
  right: -1.5rem;
  text-align: left;
}

.dropdown li a {
  margin: 0;
}

.dropdown li a {
  color: #555555;
  display: block;
  font-family: arial;
  font-weight: bold;
  padding: 10px;
  cursor: pointer;
  text-decoration: none;
}

.dropdown li a:hover {
  background: #155fb0;
  color: #ffffff;
  text-decoration: none;
}

a.account {
  font-size: 11px;
  line-height: 16px;
  color: #555;
  position: absolute;
  z-index: 110;
  display: block;
  padding: 11px 0 0 20px;
  height: 28px;
  width: 221px;
  text-decoration: none;
  /* background: url(images/arrow.png) 80px 32px no-repeat; */
  cursor: pointer;
}

.root {
  list-style: none;
  font-size: 11px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.45);
  background: #fff;
  padding: 10px;
  border-radius: 4px;
  margin: 21px 0 0 0;
}

.root:before {
  content: '';
  display: inline-block;
  border-left: 7px solid transparent;
  border-right: 7px solid transparent;
  border-bottom: 7px solid #ccc;
  border-bottom-color: #ffffff;
  position: absolute;
  color: #ffffff;
  top: 54px;
  left: calc(50% - 7px);
}

.profile-circle {
  width: 45px;
  height: 44px;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center;
  -webkit-border-radius: 99em;
  -moz-border-radius: 99em;
  border-radius: 99em;
  border: 2px solid #eee;
  box-shadow: 0px 1px 3px rgba(24, 0, 69, 0.1);
  vertical-align: middle;
  transition: all 0.5s;
  -webkit-transition: all 0.5s;
}

.profile-circle:hover {
  box-shadow: 0px 0px 10px rgba(255, 255, 255, 0.5);
}
</style>
