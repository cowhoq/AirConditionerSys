import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';

Vue.use(Vuex);

const BASE_URL = 'http://localhost:8081'; // 将基 URL 提取出来

export default new Vuex.Store({
  state: {
    currentTemperature: '',
    targetTemperature: '',
    mode: 'cool',
    targetMode: 'cool',
    currentCost: 0,
    roomNumber: '',
    airConditioning: false,
    fanSpeed: 'medium',
    currentHostTemperature: 22,
    currentHostMode: 'cool',
    Host: true,
    energy: 0,
    fanMode: 'yes',
  },
  mutations: {
    TOGGLE_AIR_CONDITIONING(state) {
      state.airConditioning = !state.airConditioning;
    },
    SET_ROOM_NUMBER(state, payload) {
      console.log('hello');
      state.roomNumber = payload;
    },
    updateTemperature(state, payload) {
      state.targetTemperature = payload.targetTemp;
    },
    UPDATE_COST(state, cost) {
      state.currentCost = cost;
    },
    SET_CURRENT_TEMPERATURE(state, payload) {
      state.currentTemperature = payload;
    },
    SET_FAN_SPEED(state, speed) {
      state.fanSpeed = speed;
    },
    SET_CURRENT_HOST_TEMPERATURE(state, payload) {
      state.currentHostTemperature = payload;
    },
    SET_CURRENT_HOST_MODE(state, payload) {
      state.currentHostMode = payload;
    },
    SET_TARGET_TEMPERATURE(state, temperature) {
      state.targetTemperature = temperature;
    },
  },
  actions: {
    setRoomNumber({ commit }, roomNumber) {
      commit('SET_ROOM_NUMBER', roomNumber);
    },
    setTemperature({ commit }, payload) {
      commit('updateTemperature', payload);
    },
    updateCost({ commit }, cost) {
      commit('updateCost', cost);
    },
    setCurrentTemperature({ commit }, payload) {
      commit('SET_CURRENT_TEMPERATURE', payload);
    },
    setFanSpeed({ commit }, speed) {
      commit('SET_FAN_SPEED', speed);
    },
    async toggleAirConditioning({ state }) {
      if (state.airConditioning) {
        await this.dispatch('stopSlave');
      } else {
        await this.dispatch('startSlave');
      }
    },
    async startSlave({ state , commit}) {
      try {
        const response = await axios.post(`${BASE_URL}/PowerOn`, { params: { roomNumber: state.roomNumber } });
        console.log('Slave started', response.data);
        commit('TOGGLE_AIR_CONDITIONING');
      } catch (error) {
        console.error('Failed to start slave', error);
      }
    },
    async stopSlave({ state , commit}) {
      try {
        const response = await axios.post(`${BASE_URL}/PowerOff`, { params: { roomNumber: state.roomNumber } });
        console.log('Slave stopped', response.data);
        commit('TOGGLE_AIR_CONDITIONING');
      } catch (error) {
        console.error('Failed to stop slave', error);
      }
    },
    async getTargetTemperature({ state, commit }) { // 添加 commit 到参数中
      try {
        const response = await axios.post(`${BASE_URL}/setTemp`, { params: { roomNumber: state.roomNumber } });
        console.log('Target temperature retrieved', response.data.data);
        commit('SET_TARGET_TEMPERATURE', response.data.data); // 使用 commit 更新 state
      } catch (error) {
        console.error('Failed to get target temperature', error);
      }
    },
    async increaseTemperature({ state , commit }) {
      try {
        const response = await axios.post(`${BASE_URL}/upSetTemp`, { params: { roomNumber: state.roomNumber }});
        console.log('Temperature increased', response.data);
        commit('SET_TARGET_TEMPERATURE', response.data.data); 
      } catch (error) {
        console.error('Failed to increase temperature', error);
      }
    },
    async decreaseTemperature({ state , commit}) {
      try {
        const response = await axios.post(`${BASE_URL}/downSetTemp`, { params: { roomNumber: state.roomNumber }});
        console.log('Temperature decreased', response.data);
        commit('SET_TARGET_TEMPERATURE', response.data.data); 
      } catch (error) {
        console.error('Failed to decrease temperature', error);
      }
    },
    async changeFanSpeed({ state }, newSpeed) {
      try {
        const response = await axios.post(`${BASE_URL}/changeFanSpeed`, { params: { roomNumber: state.roomNumber, fanSpeed: newSpeed }});
        console.log('Fan speed changed', response.data);
      } catch (error) {
        console.error('Failed to change fan speed', error);
      }
    },
    async getFee({ state, commit }) {
      try {
        const response = await axios.get(`${BASE_URL}/getFee`, { params: { roomNumber: state.roomNumber } });
        console.log('Fee retrieved', response.data);
        commit('UPDATE_COST', response.data.data);
      } catch (error) {
        console.error('Failed to get fee', error);
      }
    },
    async getCurrentTemperature({ state, commit }) {
      try {
        console.log("state.roomNumber", state.roomNumber);
        const response = await axios.post(`${BASE_URL}/curTemp`, { params: { roomNumber: state.roomNumber } });
        console.log('Current temperature retrieved', response.data);
        commit('SET_CURRENT_TEMPERATURE', response.data.data);
      } catch (error) {
        console.error('Failed to get current temperature', error);
      }
    },
  },
  getters: {
    roomNumber: state => state.roomNumber,
    airConditioning: state => state.airConditioning,
    currentTemperature: state => state.currentTemperature,
    targetTemperature: state => state.targetTemperature,
    targetMode: state => state.targetMode,
    currentCost: state => state.currentCost,
    fanSpeed: state => state.fanSpeed,
    currentHostTemperature: state => state.currentHostTemperature,
    currentHostMode: state => state.currentHostMode,
    mode: state => state.mode,
    Host: state => state.Host,
  }
});
