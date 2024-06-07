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
        cost: 0,
        roomNumber: '',
        airConditioning: false,
        fanSpeed: 'FAST',
        hostTemperatureLow: 18,
        hostTemperatureHigh: 25,
        hostMode: '',
        Host: true,
        energy: 0,
        fanStatus: 'yes',
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
            state.cost = cost;
        },
        UPDATE_ENERGY(state, energy) {
            state.energy = energy;
        },
        SET_CURRENT_TEMPERATURE(state, payload) {
            state.currentTemperature = payload;
        },
        SET_FAN_SPEED(state, speed) {
            state.fanSpeed = speed;
        },
        SET_TARGET_TEMPERATURE(state, temperature) {
            state.targetTemperature = temperature;
        },
        SET_HOST_MODE(state, payload) {
            console.log('SET_CURRENT_HOST_MODE', payload);
            state.hostMode = payload;
        },
        SET_HOST_TEMPERATURE_LOW(state, payload) {
            state.hostTemperatureLow = payload;
        },
        SET_HOST_TEMPERATURE_HIGH(state, payload) {
            state.hostTemperatureHigh = payload;
        },
        SET_FAN_STATUS(state, payload) {
            state.fanStatus = payload;
        },
    },
    actions: {
        setRoomNumber({commit}, roomNumber) {
            commit('SET_ROOM_NUMBER', roomNumber);
        },
        setTemperature({commit}, payload) {
            commit('updateTemperature', payload);
        },
        updateCost({commit}, cost) {
            commit('updateCost', cost);
        },
        setCurrentTemperature({commit}, payload) {
            commit('SET_CURRENT_TEMPERATURE', payload);
        },
        setFanSpeed({commit}, speed) {
            commit('SET_FAN_SPEED', speed);
        },
        async toggleAirConditioning({state}) {
            if (state.airConditioning) {
                await this.dispatch('stopSlave');
            } else {
                await this.dispatch('startSlave');
            }
        },
        async startSlave({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/PowerOn`, {params: {roomNumber: state.roomNumber}});
                console.log('Slave started', response.data);
                commit('TOGGLE_AIR_CONDITIONING');
            } catch (error) {
                console.error('Failed to start slave', error);
            }
        },
        async stopSlave({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/PowerOff`, {params: {roomNumber: state.roomNumber}});
                console.log('Slave stopped', response.data);
                commit('TOGGLE_AIR_CONDITIONING');
            } catch (error) {
                console.error('Failed to stop slave', error);
            }
        },
        async getTargetTemperature({state, commit}) { // 添加 commit 到参数中
            try {
                const response = await axios.post(`${BASE_URL}/setTemp`, {params: {roomNumber: state.roomNumber}});
                console.log('Target temperature retrieved', response.data.data);
                commit('SET_TARGET_TEMPERATURE', response.data.data); // 使用 commit 更新 state
            } catch (error) {
                console.error('Failed to get target temperature', error);
            }
        },
        async increaseTemperature({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/upSetTemp`, {params: {roomNumber: state.roomNumber}});
                console.log('Temperature increased', response.data);
                commit('SET_TARGET_TEMPERATURE', response.data.data);
            } catch (error) {
                console.error('Failed to increase temperature', error);
            }
        },
        async decreaseTemperature({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/downSetTemp`, {params: {roomNumber: state.roomNumber}});
                console.log('Temperature decreased', response.data);
                commit('SET_TARGET_TEMPERATURE', response.data.data);
            } catch (error) {
                console.error('Failed to decrease temperature', error);
            }
        },
        async changeFanSpeed({state}) {
            console.log('changeFanSpeed', state.fanSpeed);
            try {
                const response = await axios.post(`${BASE_URL}/changeSpeed`, null, {
                    params: {newSpeed: state.fanSpeed}
                });
                console.log('Fan speed changed', response.data);
            } catch (error) {
                console.error('Failed to change fan speed', error);
            }
        },
        async getFee({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/getFee`, null, {params: {roomNumber: state.roomNumber}});
                console.log('Fee retrieved', response.data);
                commit('UPDATE_COST', response.data.data);
            } catch (error) {
                console.error('Failed to get fee', error);
                window.alert('主机连接失败，请检查网络连接');
            }
        },
        async getCurrentTemperature({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/curTemp`, {params: {roomNumber: state.roomNumber}});
                //console.log('Current temperature retrieved', response.data);
                commit('SET_CURRENT_TEMPERATURE', response.data.data);
            } catch (error) {
                console.error('Failed to get current temperature', error);
            }
        },
        async getMasterStatus({commit}) {
            try {
                const response = await axios.get(`${BASE_URL}/getMasterStatus`);
                console.log('Master status retrieved', response.data.data.workmode);
                commit('SET_HOST_TEMPERATURE_LOW', response.data.data.range[0] / 100);
                commit('SET_HOST_TEMPERATURE_HIGH', response.data.data.range[1] / 100);
                commit('SET_HOST_MODE', response.data.data.workmode);
            } catch (error) {
                console.warn('Failed to get master status', error);
                console.error('Failed to get master status', error);
            }
        },
        async getFanStatus({commit}) {
            try {
                const response = await axios.get(`${BASE_URL}/wind`);
                console.log('Fan mode retrieved', response.data.data);
                commit('SET_FAN_STATUS', response.data.data);
            } catch (error) {
                console.error('Failed to get fan mode', error);
            }
        },
        async getFeeAndEnergy({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/getFee`, null, {params: {roomId: state.roomNumber}});
                console.log('Fee and energy retrieved', response);
                commit('UPDATE_COST', response.data.data[1]);
                commit('UPDATE_ENERGY', response.data.data[0]);
            } catch (error) {
                console.error('Failed to get fee and energy', error);
                window.alert('主机连接失败，请检查网络连接');
            }
        },
        async logout({state, commit}) {
            try {
                const response = await axios.post(`${BASE_URL}/logout`, null, {params: {roomId: state.roomNumber}});
                console.log('Logout', response.data);
                commit('SET_ROOM_NUMBER', '');
            } catch (error) {
                console.error('Failed to logout', error);
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
        hostTemperatureLow: state => state.hostTemperatureLow,
        hostTemperatureHigh: state => state.hostTemperatureHigh,
        hostMode: state => state.hostMode,
        mode: state => state.mode,
        Host: state => state.Host,
        fanStatus: state => state.fanStatus,
        cost: state => state.currentCost,
    }
});
