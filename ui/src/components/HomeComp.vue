<template>
  <div class="split left">
    <div class="centered">
      <h2>Unravel the Magic of Unexpected Conversations</h2>
    </div>
  </div>
  <div class="split right">
    <div class="centered">
      <StartComp />
    </div>
  </div>
</template>

<script setup lang="ts">
import StartComp from '@/components/StartComp.vue'
</script>

<style lang="scss" scoped>
* {
  font-family: 'Product Sans', sans-serif;
}

h2 {
  font-family: 'Lato', sans-serif;
  letter-spacing: 1px;
  font-weight: bold;
  background-image: linear-gradient(to right, #feac5e, #c779d0, #4bc0c8);
  -webkit-background-clip: text;
  -moz-background-clip: text;
  background-clip: text;
  color: transparent;
  padding: 5px;
  clip-path: inset(2px);
}

.split {
  height: 100%;
  width: 50%;
  position: fixed;
  z-index: 1;
  top: 0;
  overflow-x: hidden;
  padding-top: 20px;
}

.left {
  left: 0;
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  background-size: cover;
  background-position: center;
}

.left::before {
  content: '';
  background: url('../assets/img/wrapper.png') no-repeat;
  background-size: cover;
  position: absolute;
  top: 0px;
  right: 0px;
  bottom: 0px;
  left: 0px;
  opacity: 0.4;
}

.right {
  right: 0;
  background-color: rgb(241, 241, 241);
}

.centered {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.centerd img {
  width: 150px;
  border-radius: 50%;
}
</style>
