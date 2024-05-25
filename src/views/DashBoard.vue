<template>
  <div>
    <h1>主面板</h1>
    <PowerControl />
    <WorkModeControl />
    <TemperatureMonitor />
    <ReportView />
  </div>
</template>

<!--<template>-->
<!--    <div>-->
<!--        <el-row :gutter="20" class="mgb20">-->
<!--            <el-col :span="6">-->
<!--                <el-card shadow="hover" body-class="card-body">-->
<!--                    <el-icon class="card-icon bg1">-->
<!--                        <User />-->
<!--                    </el-icon>-->
<!--                    <div class="card-content">-->
<!--                        <countup class="card-num color1" :end="6666" />-->
<!--                        <div>test</div>-->
<!--                    </div>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="6">-->
<!--                <el-card shadow="hover" body-class="card-body">-->
<!--                    <el-icon class="card-icon bg2">-->
<!--                        <ChatDotRound />-->
<!--                    </el-icon>-->
<!--                    <div class="card-content">-->
<!--                        <countup class="card-num color2" :end="168" />-->
<!--                        <div>test</div>-->
<!--                    </div>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="6">-->
<!--                <el-card shadow="hover" body-class="card-body">-->
<!--                    <el-icon class="card-icon bg3">-->
<!--                        <Goods />-->
<!--                    </el-icon>-->
<!--                    <div class="card-content">-->
<!--                        <countup class="card-num color3" :end="8888" />-->
<!--                        <div>test</div>-->
<!--                    </div>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="6">-->
<!--                <el-card shadow="hover" body-class="card-body">-->
<!--                    <el-icon class="card-icon bg4">-->
<!--                        <ShoppingCartFull />-->
<!--                    </el-icon>-->
<!--                    <div class="card-content">-->
<!--                        <countup class="card-num color4" :end="568" />-->
<!--                        <div>test</div>-->
<!--                    </div>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--        </el-row>-->

<!--        <el-row :gutter="20" class="mgb20">-->
<!--            <el-col :span="18">-->
<!--                <el-card shadow="hover">-->
<!--                    <div class="card-header">-->
<!--                        <p class="card-header-title">test</p>-->
<!--                        <p class="card-header-desc">test</p>-->
<!--                    </div>-->
<!--                    <v-chart class="chart" :option="dashOpt1" />-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="6">-->
<!--                <el-card shadow="hover">-->
<!--                    <div class="card-header">-->
<!--                        <p class="card-header-title">test</p>-->
<!--                        <p class="card-header-desc">test</p>-->
<!--                    </div>-->
<!--                    <v-chart class="chart" :option="dashOpt2" />-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--        </el-row>-->
<!--        <el-row :gutter="20">-->
<!--            <el-col :span="7">-->
<!--                <el-card shadow="hover" :body-style="{ height: '400px' }">-->
<!--                    <div class="card-header">-->
<!--                        <p class="card-header-title">test</p>-->
<!--                        <p class="card-header-desc">test</p>-->
<!--                    </div>-->
<!--                    <el-timeline>-->
<!--                        <el-timeline-item v-for="(activity, index) in activities" :key="index" :color="activity.color">-->
<!--                            <div class="timeline-item">-->
<!--                                <div>-->
<!--                                    <p>{{ activity.content }}</p>-->
<!--                                    <p class="timeline-desc">{{ activity.description }}</p>-->
<!--                                </div>-->
<!--                                <div class="timeline-time">{{ activity.timestamp }}</div>-->
<!--                            </div>-->
<!--                        </el-timeline-item>-->
<!--                    </el-timeline>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="10">-->
<!--                <el-card shadow="hover" :body-style="{ height: '400px' }">-->
<!--                    <div class="card-header">-->
<!--                        <p class="card-header-title">test</p>-->
<!--                        <p class="card-header-desc">test</p>-->
<!--                    </div>-->
<!--                    <v-chart class="map-chart" :option="mapOptions" />-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--            <el-col :span="7">-->
<!--                <el-card shadow="hover" :body-style="{ height: '400px' }">-->
<!--                    <div class="card-header">-->
<!--                        <p class="card-header-title">test</p>-->
<!--                        <p class="card-header-desc">test</p>-->
<!--                    </div>-->
<!--                    <div>-->
<!--&lt;!&ndash;                        <div class="rank-item" v-for="(rank, index) in ranks">&ndash;&gt;-->
<!--&lt;!&ndash;                            <div class="rank-item-avatar">{{ index + 1 }}</div>&ndash;&gt;-->
<!--&lt;!&ndash;                            <div class="rank-item-content">&ndash;&gt;-->
<!--&lt;!&ndash;                                <div class="rank-item-top">&ndash;&gt;-->
<!--&lt;!&ndash;                                    <div class="rank-item-title">{{ rank.title }}</div>&ndash;&gt;-->
<!--&lt;!&ndash;                                    <div class="rank-item-desc">销量：{{ rank.value }}</div>&ndash;&gt;-->
<!--&lt;!&ndash;                                </div>&ndash;&gt;-->
<!--&lt;!&ndash;                                <el-progress&ndash;&gt;-->
<!--&lt;!&ndash;                                    :show-text="false"&ndash;&gt;-->
<!--&lt;!&ndash;                                    striped&ndash;&gt;-->
<!--&lt;!&ndash;                                    :stroke-width="10"&ndash;&gt;-->
<!--&lt;!&ndash;                                    :percentage="rank.percent"&ndash;&gt;-->
<!--&lt;!&ndash;                                    :color="rank.color"&ndash;&gt;-->
<!--&lt;!&ndash;                                />&ndash;&gt;-->
<!--&lt;!&ndash;                            </div>&ndash;&gt;-->
<!--&lt;!&ndash;                        </div>&ndash;&gt;-->
<!--                    </div>-->
<!--                </el-card>-->
<!--            </el-col>-->
<!--        </el-row>-->
<!--    </div>-->
<!--</template>-->

<script>
import PowerControl from '../components/PowerControl.vue';
import WorkModeControl from '../components/WorkModeControl.vue';
import TemperatureMonitor from '../components/TemperatureMonitor.vue';
import ReportView from '../components/ReportView.vue';

export default {
  name: 'DashBoard',
  components: {
    PowerControl,
    WorkModeControl,
    TemperatureMonitor,
    ReportView
  }
};
</script>


