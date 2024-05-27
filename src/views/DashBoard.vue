<template>
  <div>
    <PowerControl />
    <WorkModeControl />
    <SlaveStatus />
    <ReportView />
  </div>
</template>

<script>
import PowerControl from '../components/PowerControl.vue';
import WorkModeControl from '../components/WorkModeControl.vue';
import SlaveStatus from '../components/SlaveStatus.vue';
import ReportView from '../components/ReportView.vue';

export default {
  name: 'DashBoard',
  components: {
    PowerControl,
    WorkModeControl,
    SlaveStatus,
    ReportView,
  },
};
</script>
