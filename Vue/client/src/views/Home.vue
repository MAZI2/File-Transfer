<template xmlns:th="http://www.w3.org/1999/xhtml">
  <div class="home">

    <button class="button" @click="client">Sync</button>

    <div class="console">
      <p class="consoleText">{{console}}</p>
    </div>

    <button class="button" @click="update">Refresh</button>

    <div class="ips" v-for="ip in ips" v-bind:key="ip">
      <p class="consoleText" >{{ip}}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Home',
  components: {
  },
  data() {
    return {
      console: "",
      ips: []
    }
  },
  created() {
    this.server();
    axios.get('/ips');
  },
  methods: {
    update() {
      axios.get('/update')
          .then((getResponse) => {
            this.ips = getResponse.data
          })
    },
    server() {
      axios.get('/Server')
          .then((getResponse) => {
            this.console = getResponse.data
          })
    },
    client() {
      axios.get('/Client')
        .then((response) => {
          this.server();
          return response;
        })
    }
  }
}
</script>
<style>
  .button {
    background-color: white;
    border: none;
    padding: 10px 10px;
    text-align: center;
    display: inline-block;
    font-size: 16px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  }
  .button:hover {
    background-color: #F3F3F3;
  }
  .console {
    margin-top: 10px;
    margin-bottom: 10px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
    width: 30%;
    height: 200px;
  }
  .consoleText {
    padding: 5px;
    white-space: pre-wrap;
  }
  .ips {
    margin-bottom: -5px;
    width: 200px;
    box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  }
</style>
