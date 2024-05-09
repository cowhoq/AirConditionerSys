<template>
    <div>
      <h3>当前费用: {{ currentCost }}¥</h3>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        currentCost: 0
      };
    },
    mounted() {
      this.fetchCurrentTemperature();
    },
    methods: {
      fetchCurrentTemperature() {
        axios.get('/api/current-temperature')
          .then(response => {
            this.currentTemp = response.data.temperature;
          })
          .catch(error => {
            console.error('Error fetching temperature', error);
          });
      }
    }
  }
  </script>
  