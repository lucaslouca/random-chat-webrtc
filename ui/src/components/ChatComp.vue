<template>
  <!-- <WaitModalComp v-if="showModalWait" :message="waitMessage" /> -->
  <div id="wrapper">
    <div id="menu">
      <div class="menu-button-holder">
        <a href="/" id="exit"
          ><span><font-awesome-icon icon="fa-solid fa-xmark" /></span>Exit</a
        >
      </div>
      <button id="disconnect"><font-awesome-icon icon="fa-solid fa-xmark" /></button>
      <canvas id="canvas" style="display: none"></canvas>
    </div>

    <div id="container">
      <div id="video-wrapper">
        <span></span>
        <main>
          <section data-peer="" data-state="" data-camera="" id="video-section">
            <div id="content">
              <div id="video-box">
                <div class="video" id="peer-video-box">
                  <video id="peer-video" autoplay playsinline></video>
                  <div class="video-placeholder">Waiting for peer...</div>
                </div>
                <div class="video" id="user-video-box">
                  <video id="user-video" muted autoplay playsinline></video>
                  <div class="video-placeholder">Attaching to camera...</div>
                </div>

                <!-- <select id="peer" disabled>
                  <option value="">Select a peer...</option>
                </select> -->

                <!-- <button id="disconnect">❌</button> -->

                <input id="audio" type="checkbox" style="display: none" />

                <div id="video-controls">
                  <span></span>
                  <!--CAMERA BUTTON-->
                  <div class="control-button">
                    <button class="inner" @click="toggleVideo">
                      <!-- <font-awesome-icon v-if="isVideo" icon="fa-solid fa-video-camera" /> -->
                      <span class="video-icon" :class="{ disabled: isVideo }"></span>
                    </button>
                    <span v-if="!isVideo" class="muted">Video is disabled</span>
                    <span v-if="isVideo">Video is enabled</span>
                  </div>

                  <!-- <div class="control" id="camera-control">
                    <select id="camera">
                      <option value=""></option>
                    </select>
                  </div> -->

                  <!--NEXT BUTTON-->
                  <div class="next-button-holder">
                    <button class="next" @click="next">
                      <font-awesome-icon icon="fa-solid fa-rotate" />
                    </button>
                  </div>
                  <!--MUTE BUTTON-->
                  <div class="control-button">
                    <button id="audio-control" class="inner" @click="toggleMic">
                      <font-awesome-icon v-if="isAudio" icon="fa fa-microphone-slash" />
                      <font-awesome-icon v-if="!isAudio" icon="fa-solid fa-microphone" />
                    </button>
                    <span v-if="!isAudio" class="muted">Microphone is disabled</span>
                    <span v-if="isAudio">Microphone is enabled</span>
                  </div>
                </div>
                <span></span>
                <!--END OF VIDEO CONTROLS-->
              </div>
            </div>
          </section>
        </main>
      </div>
      <div id="resizer"><span></span></div>
      <div id="sidebar">
        <section id="sidebar-content">
          <div class="chat-container clearfix">
            <div class="chat">
              <div class="chat-header clearfix">
                <!-- <img
                  src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/195612/chat_avatar_01_green.jpg"
                  alt="avatar"
                /> -->

                <div class="chat-about">
                  <div v-if="peerName.length > 0" class="chat-with">
                    You are chatting with {{ peerName }}
                  </div>
                  <div v-if="messages.length > 0" class="chat-num-messages">
                    already {{ messages.length }} messages
                  </div>
                </div>
              </div>
              <!-- end chat-header -->
              <div class="chat-history">
                <div class="loader" v-if="!peerName">
                  <div class="wrapper">
                    <div class="circle"></div>
                    <div class="circle"></div>
                    <div class="circle"></div>
                    <div class="shadow"></div>
                    <div class="shadow"></div>
                    <div class="shadow"></div>
                    <span>Loading...</span>
                  </div>
                </div>

                <ul id="chat-messages">
                  <li
                    v-for="(msg, index) in messages"
                    v-bind:key="index"
                    :class="{ clearfix: !msg.me }"
                  >
                    <div
                      class="message-data"
                      :class="{
                        'align-right': !msg.me,
                        'message-data-name': !msg.me
                      }"
                    >
                      <!-- Me -->
                      <template v-if="msg.me">
                        <span class="message-data-name">
                          <font-awesome-icon icon="fa fa-circle" class="me" />
                          {{ msg.name }}
                        </span>
                        <span class="message-data-time">{{ msg.time }}</span> &nbsp; &nbsp;
                      </template>

                      <!-- Other -->
                      <template v-if="!msg.me">
                        <span class="message-data-time">{{ msg.time }}</span> &nbsp; &nbsp;
                        <span class="message-data-name">
                          {{ msg.name }}
                          <font-awesome-icon icon="fa fa-circle" class="online" />
                        </span>
                      </template>
                    </div>
                    <div
                      class="message"
                      :class="{
                        'float-right': !msg.me,
                        'other-message': !msg.me,
                        'my-message': msg.me
                      }"
                    >
                      {{ msg.message }}
                    </div>
                  </li>

                  <li v-if="isPeerTyping">
                    <div class="message-data align-right message-data-name">
                      <span class="message-data-name">
                        {{ peerName }}
                        <font-awesome-icon icon="fa fa-circle" class="online" />
                      </span>
                    </div>
                    <div class="message other-message float-right">
                      <!-- <font-awesome-icon icon="fa fa-circle" class="online" />
                      <font-awesome-icon
                        icon="fa fa-circle"
                        class="online"
                        style="color: #aed2a6"
                      />
                      <font-awesome-icon
                        icon="fa fa-circle"
                        class="online"
                        style="color: #dae9da"
                      /> -->
                      <div className="typing">
                        <div className="typing__dot"></div>
                        <div className="typing__dot"></div>
                        <div className="typing__dot"></div>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <!-- end chat-history -->
              <div
                class="chat-message clearfix"
                v-if="isChatting || (isPeerDisconnected && peerName)"
              >
                <input
                  name="message-to-send"
                  id="chat-input"
                  placeholder="Type your message"
                  @keydown="onChatInputKeydown"
                  v-model="currentChatMessage"
                  v-if="isChatting"
                />
                <button id="chat-button" v-if="isChatting" @click="sendMessage">Send</button>
                <span id="peer-left" v-if="isPeerDisconnected && peerName"
                  >{{ peerName }} left the chat</span
                >
              </div>
              <!-- end chat-message -->
            </div>
          </div>
          <!-- end chat-container -->
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { API } from '@/api'
import { reactive, ref, onMounted } from 'vue'
import { WebRTCClient } from '@/rtc'
import type { Offer, Peer } from '@/types'
import { loadState, getElement } from '@/common'
import type { State } from '@/common'

const messages: Array<any> = reactive([] as Array<any>)
let peerName = ref<string>('')
let currentChatMessage = ref<string>('')
let isChatting = ref<boolean>(false)
let isPeerDisconnected = ref<boolean>(false)
let isPeerTyping = ref<boolean>(false)
let isTyping = ref<boolean>(false)
let isAudio = ref<boolean>(false)
let isVideo = ref<boolean>(true)
let stateKey = 'rtc-state'

const state = loadState(stateKey)
let client = new WebRTCClient(state.name)
client.on('connected', handleConnected)
client.on('disconnected', handleDisconnected)
client.on('peeradded', handlePeerAdded)
client.on('invite', handleInvite)
client.on('peerremoved', handlePeerRemoved)
client.on('peerupdated', handlePeerUpdated)
client.on('peerconnected', handlePeerConnected)
client.on('peerdisconnected', handlePeerDisconnected)
client.on('offer', handleOffer)
client.on('chat', handleChat)
client.on('typing', handleTyping)

onMounted(() => {
  // API.getEndpoints('stats')!.get('getById')!('123456')
  //   .then(
  //     function (response: any) {
  //       console.log(response)
  //     }.bind(this)
  //   )
  //   .catch(
  //     function (error: any) {
  //       console.log(error)
  //     }.bind(this)
  //   )

  // initChatInput()
  // initNameInput()
  // initAudioCheckbox()
  // initPeerSelect()
  initDisconnectButton()
  // initCameraSelect() // await
  // selectDefaultCamera()
  updateCameraStream() // await

  setMainData('state', 'ready')

  const resizer = getElement('#resizer')
  const wrapper = getElement('#video-wrapper')
  resizer!.addEventListener('mousedown', (event) => {
    document.addEventListener('mousemove', resize, false)
    document.addEventListener(
      'mouseup',
      () => {
        document.removeEventListener('mousemove', resize, false)
      },
      false
    )
  })
  function resize(e: any) {
    const offsetLeft = wrapper!.getBoundingClientRect().left
    const size = `${e.x - offsetLeft}px`
    wrapper!.style.flexBasis = size
  }
  wrapper!.style.flexBasis = '70%'

  console.log('App is ready')
})

/**
 * Set a data attribute on the main element
 */
function setMainData(name: string, value: string): void {
  getElement('main').setAttribute(`data-${name}`, value)
}

/**
 * Update stored app state
 */
function updateState(newState: Partial<State>): void {
  let state = loadState(stateKey)
  if (newState.name) {
    client.name = newState.name
  }
  const updatedState = { ...state, ...newState }
  localStorage.setItem(stateKey, JSON.stringify(updatedState))
}

/**
 * Update the local camera stream
 */
async function updateCameraStream(): Promise<void> {
  setMainData('camera', 'attaching')
  const state = loadState(stateKey)

  // Detach the video stream before creating a new stream so the video
  // element doesn't flash when the original stream stops
  const userVideo = getElement<HTMLVideoElement>('#user-video')
  userVideo.srcObject = null

  if (state.audio) {
    isAudio.value = true
  } else {
    isAudio.value = false
  }

  if (state.camera) {
    userVideo.srcObject = await client.openStream({
      cameraId: state.camera,
      audioDisabled: !state.audio
    })
    setMainData('camera', 'attached')
    isVideo.value = true

    setInterval(() => {
      capture()
    }, 5000)
  } else {
    isVideo.value = false
    client.closeStream()
    setMainData('camera', '')
  }
}

/**
 * Setup a listener for the name input
 */
function initNameInput(): void {
  const state = loadState(stateKey)
  const nameInput = getElement<HTMLInputElement>('#name')
  nameInput.value = state.name
  nameInput.addEventListener('change', async () => {
    updateState({ name: nameInput.value })
  })
}

/**
 * Setup a listener for the camera selector
 */
async function initCameraSelect(): Promise<void> {
  const state = loadState(stateKey)
  const cameras = await client.getCameras()
  const cameraSelect = getElement<HTMLSelectElement>('#camera')
  cameraSelect.innerHTML = '<option value="">Disabled</option>'

  for (const camera of cameras) {
    const opt = document.createElement('option')
    opt.value = camera.deviceId
    opt.textContent = camera.label
    if (camera.deviceId === state.camera) {
      opt.selected = true
    }
    cameraSelect.append(opt)
  }

  cameraSelect.addEventListener('change', async () => {
    updateState({ camera: cameraSelect.value })
  })
}

async function selectDefaultCamera(): Promise<void> {
  const state = loadState(stateKey)
  // const cameras = await client.getCameras()
  // if (cameras.length > 0) {
  //   const camera = cameras[0]
  //   if (camera.deviceId !== state.camera) {
  //     updateState({ camera: camera.deviceId })
  //   }
  // }
}

/**
 * Setup a listener for the audio checkbox
 */
function initAudioCheckbox(): void {
  const state = loadState(stateKey)
  const audioCheckbox = getElement<HTMLInputElement>('#audio')
  audioCheckbox.checked = state.audio
  audioCheckbox.addEventListener('change', async () => {
    updateState({ audio: audioCheckbox.checked })
  })

  const audioButton = getElement<HTMLInputElement>('#audio-control')
  audioButton.addEventListener('click', async () => {
    updateState({ audio: !state.audio })
  })
}

/**
 * Setup a listener for the peer selector
 */
function initPeerSelect(): void {
  const peerSelect = getElement<HTMLSelectElement>('#peer')
  peerSelect.addEventListener('change', async () => {
    const peer = peerSelect.value
    if (!peer) {
      return
    }

    setMainData('peer', 'connecting')
    client?.invite(peer)
  })
}

/**
 * Setup a listener for the disconnect button
 */
function initDisconnectButton(): void {
  const disconnect = getElement<HTMLButtonElement>('#disconnect')
  disconnect.addEventListener('click', () => {
    client.disconnect()
  })
}

/**
 * Add listener for chat input
 */
function initChatInput() {
  const input = getElement<HTMLInputElement>('#chat-input')
  input.addEventListener('keydown', (event) => {
    if (event.key === 'Enter') {
      const message = input.value
      client.sendChat(message)
      addChatMessage(message, undefined)
      input.value = ''
    }
  })
  getElement<HTMLInputElement>('#chat-input').disabled = true
  getElement<HTMLInputElement>('#chat-button').disabled = true
}

function onChatInputKeydown(event: any) {
  const message = event.target.value
  if (event.key === 'Enter') {
    client.sendChat(message)
    isTyping.value = false
    client.sendTyping(false)
    addChatMessage(message, undefined)
    event.target.value = ''
    currentChatMessage.value = ''
  } else {
    if (message.length === 0 && !isTyping.value && event.key != 'Backspace') {
      isTyping.value = true
      client.sendTyping(true)
    } else if (isTyping.value && message.length === 1 && event.key === 'Backspace') {
      isTyping.value = false
      client.sendTyping(false)
    }
  }
}

function sendMessage() {
  client.sendChat(currentChatMessage.value)
  isTyping.value = false
  client.sendTyping(false)
  addChatMessage(currentChatMessage.value, undefined)
  currentChatMessage.value = ''
}

/**
 * The client has connected to the RTC server
 */
function handleConnected(): void {
  console.log('Connected to signal server')
}

/**
 * The client has disconnected from the RTC server
 */
function handleDisconnected(): void {
  console.log('Disconnected from signal server')
}

/**
 * Update the peer selector when a peer is added
 */
// function handlePeerAdded(peer: Peer): void {
//   const peerSelect = getElement<HTMLSelectElement>('#peer')
//   const option = document.createElement('option')
//   option.value = peer.id
//   option.textContent = peer.name
//   peerSelect.append(option)
// }
function handlePeerAdded(peer: Peer): void {}

function handleInvite(peer: Peer): void {
  setMainData('peer', 'connecting')
  client?.invite(peer.id)
}

/**
 * Update the peer selector when a peer is removed
 */
// function handlePeerRemoved(peer: Peer): void {
//   const peerSelect = getElement<HTMLSelectElement>('#peer')
//   for (const option of peerSelect.options) {
//     if (option.value === peer.id) {
//       option.remove()
//       break
//     }
//   }
//   client.disconnect()
// }
function handlePeerRemoved(peer: Peer): void {
  client.disconnect()
}

/**
 * Update the peer selector when a peer's name changes
 */
// function handlePeerUpdated(peer: Peer): void {
//   const peerSelect = getElement<HTMLSelectElement>('#peer')
//   for (const option of peerSelect.options) {
//     if (option.value === peer.id) {
//       option.textContent = peer.name
//       break
//     }
//   }

// }
function handlePeerUpdated(peer: Peer): void {}

/**
 * Attach the peer video stream and enable chat when a peer connects
 */
function handlePeerConnected(event: { stream: MediaStream; peer: Peer }): void {
  setMainData('peer', 'connected')
  peerName.value = event.peer.name
  isChatting.value = true
  isPeerDisconnected.value = false
  const peerVideo = getElement<HTMLVideoElement>('#peer-video')
  peerVideo.srcObject = event.stream

  // getElement<HTMLInputElement>('#chat-input').disabled = false
  // getElement<HTMLInputElement>('#chat-button').disabled = false
}

/**
 * Reset the peer video element and disable chat when a peer disconnects
 */
// function handlePeerDisconnected(): void {
//   const peerVideo = getElement<HTMLVideoElement>('#peer-video')
//   peerVideo.srcObject = null
//   setMainData('peer', '')
//   const peer = getElement<HTMLSelectElement>('#peer')
//   peer.value = ''
//   getElement<HTMLInputElement>('#chat-input').disabled = true
// }

function handlePeerDisconnected(): void {
  const peerVideo = getElement<HTMLVideoElement>('#peer-video')
  peerVideo.srcObject = null
  setMainData('peer', '')
  // getElement<HTMLInputElement>('#chat-input').disabled = true
  // getElement<HTMLInputElement>('#chat-button').disabled = true
  isChatting.value = false
  isPeerDisconnected.value = true
  isPeerTyping.value = false
  isTyping.value = false
}

/**
 * Confirm an offer from a peer
 */
// function handleOffer(offer: Offer): void {
//   const peer = client.getPeer(offer.source)
//   if (confirm(`Accept offer from ${peer?.name ?? offer.source}?`)) {
//     client.accept(offer)
//   } else {
//     client.reject(offer)
//   }
// }

function handleOffer(offer: Offer): void {
  client.accept(offer)
}

/**
 * Add incoming chat messages to the message list
 */
function handleChat(data: { peer: Peer; message: string }): void {
  addChatMessage(data.message, data.peer)
}

/**
 * Add incoming signal from the peer about its typing status
 */
function handleTyping(data: { peer: Peer; typing: boolean }): void {
  isPeerTyping.value = data.typing
}

/**
 * Add a chat message to the displayed message list
 */
// function addChatMessage(message: string, peer: Peer | undefined) {
//   const li = document.createElement('li')
//   const from = document.createElement('span')
//   from.textContent = peer ? peer.name : 'Me'
//   li.append(from)
//   li.append(message)
//   const chatList = getElement<HTMLUListElement>('#chat-messages')
//   chatList.append(li)
// }

function currentTime() {
  var currentdate = new Date()
  var datetime =
    // currentdate.getDate() +
    // '/' +
    // (currentdate.getMonth() + 1) +
    // '/' +
    // currentdate.getFullYear() +
    // ' @ ' +
    currentdate.getHours() + ':' + currentdate.getMinutes()
  return datetime
}

function addChatMessage(message: string, peer: Peer | undefined) {
  messages.push({
    me: peer ? false : true,
    name: peer ? peer.name : 'Me',
    message: message,
    time: currentTime()
  })
}

function next() {
  peerName.value = ''
  isChatting.value = false
  isPeerDisconnected.value = false
  isPeerTyping.value = false
  isTyping.value = false
  messages.splice(0)
  client.disconnect()
  client.next()
}

function toggleMic() {
  isAudio.value = !isAudio.value
  client.toggleMic(isAudio.value)
  updateState({ audio: isAudio.value })
}

function toggleVideo() {
  isVideo.value = !isVideo.value
  client.toggleVideo(isVideo.value)
}

function capture() {
  var resultb64 = ''

  var canvas = getElement<HTMLCanvasElement>('canvas')
  var video = getElement<HTMLVideoElement>('#user-video')
  canvas.width = 200
  canvas.height = 200
  canvas.getContext('2d')!.drawImage(video, 0, 0, 200, 200)
  resultb64 = canvas.toDataURL()

  API.getEndpoints('statistics')!.get('capture')!({ clientId: client.id, data: resultb64 }).then(
    function (response: any) {
      console.log('[Chat] Post capture response', response)
    }
  )
}
</script>

<style lang="scss" scoped>
@import '../scss/chat.scss';

#wrapper {
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  height: 100%;
  width: 100%;
  display: grid;
  grid-template-columns: 100px 1fr;
  margin: 0;
  padding: 0;
}

/***************************************************************************************/
/* LEFT MENU */
/***************************************************************************************/
#menu {
  display: grid;
  grid-template-rows: auto auto auto 1fr 1fr auto auto;
  grid-gap: 10px;
  height: 100vh;
  padding: 1rem;
  justify-content: center;
  background-color: rgb(247, 247, 247);
}

#menu h1 {
  text-align: center;
  font-size: 2rem;
}

#menu .menu-button-holder {
  display: grid;
  text-align: center;
  gap: 0;
  font-size: 1.5rem;
}

#menu .menu-button-holder:hover,
#menu .menu-button-holder:hover a {
  cursor: pointer;
  color: $orange;
}
#menu .menu-button-holder a {
  display: grid;
  grid-template-rows: auto 1fr;
  font-size: 0.7rem;
  background: none;
  border: none;
  color: $theme-light-1;
  text-decoration: none;
}

#menu .menu-button-holder a span {
  font-size: 1rem;
}
/***************************************************************************************/
/* MIDDLE */
/***************************************************************************************/
#container {
  border-left: 2px white solid;
  width: 100%;
  height: 100%;
  flex-shrink: 0;
  position: relative;
  display: flex;
  overflow: hidden;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#resizer {
  flex-basis: 2px;
  position: relative;
  z-index: 2;
  cursor: col-resize;
  background: white;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  display: table;
}

#resizer:hover {
  background: $blue;
}

#resizer span {
  display: table-cell;
  vertical-align: middle;
  text-align: right;
}

#resizer span:before {
  // content: '\2022';
  // color: $theme-light-2;
}

#sidebar {
  flex-basis: 0;
  flex-grow: 1;
  min-width: 0;
  height: 100%;
  flex-direction: row;
  position: relative;
  display: flex;
  margin: 0;
  padding: 0;
  font-size: 0.8rem;
}

#empty {
  height: 100%;
  display: grid;
  text-align: center;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  background-color: #afafaf;
  color: transparent;
  text-shadow: 1px 1px 1px rgba(255, 255, 255, 0.2);
  -webkit-background-clip: text;
  -moz-background-clip: text;
  background-clip: text;
}

#sidebar-content {
  width: 100%;
  // display: flex;
  // flex-direction: column;
  // box-sizing: border-box;
  // overflow: scroll;
  // overflow-x: auto;
  // overflow-y: auto;
  background-color: $theme-dark-1;
}

#sidebar-content p {
  width: 100%;
}

#sidebar-content section {
  display: block;
  width: 100%;
  margin-bottom: 1rem;
}

#sidebar-content section#empty-sidebar {
  display: block;
  height: 100%;
}

/***************************************************************************************/
/* VIDEO */
/***************************************************************************************/
#video-wrapper {
  display: grid;
  grid-template-rows: auto 1fr;
  padding: 0rem;
  background-color: rgb(250, 250, 250);
  align-items: center;
  // background-repeat: repeat;
  // background-image: url('../assets/img/bg.svg');
}

/***************************************************************************************/

input,
select {
  font-size: 1rem;
  padding: 0.1rem 0.2rem;
  border: none;
  background: none;
}

select:hover {
  cursor: pointer;
}

/***************************************************************************************/
/* VIDEO */
/***************************************************************************************/
#video-section {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  max-height: 100%;
  overflow: hidden;
  border-radius: 5px;
  box-shadow:
    0 0 1px 5px rgba($green, 0.4),
    0 0 1px 10px rgba(#e5dc15, 0.1);
  // box-shadow:
  //   rgba(60, 64, 67, 0.3) 0px 1px 2px 0px,
  //   rgba(60, 64, 67, 0.15) 0px 1px 3px 1px;
  margin: 3rem;
  padding: 1rem;
  // background-color: rgb(52, 53, 60);
  // border: 3px rgb(62, 62, 70) solid;
}

#content {
  overflow: hidden;
}

.video {
  box-sizing: border-box;
  position: relative;
  width: 100%;
  max-height: 100%;
  overflow: hidden;
  border-radius: 0rem;
}

video {
  width: 100%;
  display: block;
}

.video-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  display: none;
}

#video-box {
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  max-height: calc(100vh - 200px);
  border-radius: 0.5rem;
}

/***********************************************************/
/* VIDEO CONTROLS */
/***********************************************************/
#video-controls {
  display: grid;
  grid-template-columns: 1fr 1fr auto 1fr 1fr;
  align-items: center;
  justify-content: center;
  position: absolute;
  gap: 2rem;
  bottom: 2rem;
  margin: auto;
  position: absolute;
  left: 0;
  right: 0;
}

// #video-controls > * {
//   background: cyan;
// }

// #video-controls > *:nth-child(2) {
//   background: yellow;
// }

// #video-controls > *:nth-child(4) {
//   background: red;
// }

#disconnect {
  display: none;
  position: absolute;
  top: var(--space-small);
  left: var(--space-small);
  padding: 0.25rem;
  background: white;
  border: var(--content-border);
  border-radius: var(--content-border-radius);
}

// #peer {
//   position: absolute;
//   top: var(--space-small);
//   right: var(--space-small);
//   background: #fff;
//   border: var(--content-border);
//   border-radius: var(--content-border-radius);
//   display: none;
// }

#peer-video-box {
  display: none;
}

[data-camera='attaching'] #user-video-box .video-placeholder {
  display: block;
}

// [data-camera='attached'] #peer {
//   display: block;
// }

[data-peer='connected'] #disconnect {
  display: block;
}

[data-peer='connected'] #chat {
  display: flex;
}

// [data-peer='connected'] #peer {
//   display: none;
// }

[data-peer='connected'] #peer-video-box .video-placeholder {
  display: none;
}

[data-peer='connected'] #user-video-box {
  position: absolute;
  width: 15%;
  top: 1rem;
  right: 1rem;
  border-radius: 0.5rem;
  border: 2px $green solid;
  box-shadow: 0 0 1px 5px rgba($green, 0.2);
}

[data-peer='connected'] #peer-video-box {
  display: flex;
  border-radius: 0.5rem;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  max-height: calc(100vh - 100px);
}

main:not([data-state='ready']) {
  visibility: hidden;
}
</style>
@/Api
