<template>
  <div id="container">
    <div id="center-cell">Ooops!</div>
  </div>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
* {
  font-family: 'Montserrat', sans-serif;
}

#container {
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
  text-align: center;
  z-index: 1000;
  font-size: 3em;
}
</style>
