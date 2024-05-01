<template>
  <div class="table">
    <div class="loader-wrapper">
      <div class="loader" :style="{ 'border-left': `1.1em solid ${props.color}` }"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
let props = defineProps<{
  color?: string
}>()
</script>

<style scoped>
.table {
  display: table;
  width: 100%;
  height: 100%;
}

.loader-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.loader,
.loader:after {
  border-radius: 50%;
  width: 10em;
  height: 10em;
}

.loader {
  margin: 0 auto;
  font-size: 10px;
  position: relative;
  text-indent: -9999em;
  border-top: 1.1em solid #070714;
  border-right: 1.1em solid #070714;
  border-bottom: 1.1em solid #070714;
  border-left: 1.1em solid ghostwhite;
  -webkit-transform: translateZ(0);
  -ms-transform: translateZ(0);
  transform: translateZ(0);
  -webkit-animation: load8 1.1s infinite linear;
  animation: load8 1.1s infinite linear;
}

@-webkit-keyframes load8 {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
  }

  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}

@keyframes load8 {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
  }

  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
</style>
