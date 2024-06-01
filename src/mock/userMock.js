/* // src/mock/userMock.js
import Mock from 'mockjs';

Mock.mock('/api/login', 'post', (options) => {
    const { roomNumber, idNumber } = JSON.parse(options.body);
    console.log(roomNumber, idNumber);
    if (idNumber.endsWith('2') && roomNumber.endsWith('1')) {
        console.log('usermock登录成功');
        return {
            status: 200,
            message: '登录成功',
            data: {
                roomNumber,
                idNumber,
                token: 'fake-jwt-token'
            }
        };
    } else {
      return {
        status: 401,
        message: '登录失败，身份证号或房间号无效'
      };
    }
  }); 
Mock.mock('/api/currentTemperature', 'get', () => {
    // 使用 Mock.js 的 Random 对象来生成一个 20 到 30 之间的随机数
    const randomTemperature = Mock.Random.integer(20, 30);
    return {
      status: 200,
      message: '获取当前温度成功',
      temperature: randomTemperature  // 返回一个对象，包含温度属性
    };
  });

  Mock.mock('/api/settings', 'post', (options) => {
    const { targetTemperature, fanSpeed, roomNumber } = JSON.parse(options.body);
    console.log(targetTemperature, fanSpeed, roomNumber);
      if (roomNumber != null) {
        console.log('温度设置成功');
        return {
            status: 200,
            message: '设置成功',
            data: {
                token: 'fake-jwt-token'
            }
        };
      }
      else {
          return {
            status: 401,
            message: '设置失败'
          };
      }
      
    
  }); */