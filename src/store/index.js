import { createStore } from 'vuex';

export default createStore({
  state: {
    currentTemp: 22,
    targetTemp: 22,
    currentCost: 0
  },
  mutations: {
    updateTemperature(state, payload) {
      state.currentTemp = payload.currentTemp;
      state.targetTemp = payload.targetTemp;
    },
    updateCost(state, cost) {
      state.currentCost = cost;
    }
  },
  actions: {
    setTemperature({ commit }, payload) {
      commit('updateTemperature', payload);
    },
    setCost({ commit }, cost) {
      commit('updateCost', cost);
    }
  }
});
