//package com.app2.engine.job;
//
//import com.app2.engine.Service.linenotify.LineNotifyService;
//import com.app2.engine.entity.vcc.iot.IotSensorRange;
//import com.app2.engine.entity.vcc.iot.IotSensorRangeLog;
//import com.app2.engine.repository.IotSensorRangeLogRepository;
//import com.app2.engine.repository.IotSensorRangeRepository;
//import com.app2.engine.util.BeanUtils;
//import com.app2.engine.util.LineMessageUtil;
//import com.google.gson.Gson;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//public class IotSensorRangeJobService {
//    private static final Logger LOGGER = LogManager.getLogger(IotSensorRangeJobService.class);
//
//
//    @Autowired
//    private LineNotifyService lineNotifyService;
//    @Autowired
//    private IotSensorRangeRepository iotSensorRangeRepository;
//    @Autowired
//    private IotSensorRangeLogRepository iotSensorRangeLogRepository;
//
//    @Transactional
//    @Scheduled(fixedDelay = 2500)
//    public void IotSensorRangeJob() {
//        Gson gson=new Gson();
//        long stTime = System.currentTimeMillis();
//        for (IotSensorRange iotSensorRange : iotSensorRangeRepository.findByStatusActive()) {
//            try{
//                Long id=iotSensorRange.getId();
//                String sensorCode = iotSensorRange.getSensorCode();
//                String deviceCode = iotSensorRange.getDeviceCode();
//                String key = deviceCode + "#" + sensorCode;
//
////                Double currentValue = SensorValueCurrent.getValue(key);
//                Double currentValue =new Random().nextDouble();
//                String sensorStatus = iotSensorRange.getSensorStatus(currentValue);
//
//                //Find Last Log
//                IotSensorRangeLog iotSensorRangeLog=iotSensorRangeLogRepository.findByIotSensorRangeId(id);
//                Boolean isEmptyLog = BeanUtils.isNull(iotSensorRangeLog);
//                if (BeanUtils.isNotNull(sensorStatus)) {
//                    Boolean isAlertDanger = sensorStatus.equals("danger") && (iotSensorRange.isRepeatDanger() == false || isEmptyLog);
//                    Boolean isAlertWarning = sensorStatus.equals("warning") && (iotSensorRange.isRepeatWarning() == false || isEmptyLog);
//                    if (isAlertDanger || isAlertWarning) {
//                        //ถึงเกณฑ์ที่ต้องเริ่มแจ้งเตือน
//
//                        String lineMessage = LineMessageUtil.toMessage(sensorStatus, deviceCode, sensorCode, currentValue);
//                        String messageFooter = iotSensorRange.getMessageRepeatAlert(sensorStatus);//repeat alert
////                        if (AppUtils.isNotNull(messageFooter)) {
////                            lineMessage += "\n" + messageFooter;
////                        }
//
//                        String lineToken=iotSensorRange.getLineToken();
//
//                        LOGGER.debug("Alert : {}", lineMessage);
//                        long stTime1 = System.currentTimeMillis();
//                        //////////////////////////////////////////////////////////
//                        Map<String,String> mapPostAlertJson=new HashMap<>();
//                        mapPostAlertJson.put("message",lineMessage);
//                        mapPostAlertJson.put("token",lineToken);
//                        mapPostAlertJson.put("iotSensor",String.valueOf(id));
//
//                        lineNotifyService.postAsyncMessageWithToken(gson.toJson(mapPostAlertJson)).addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                LOGGER.error("Error Line {}",throwable.getMessage(),throwable);
//                            }
//                            @Override
//                            public void onSuccess(ResponseEntity<String> stringResponseEntity) {
//                                Gson gson=new Gson();
//                                Map<String, String> mapping = gson.fromJson(stringResponseEntity.getBody(), Map.class);
//                                Long iotSensor=Long.parseLong(mapping.get("iotSensor"));
//                                IotSensorRangeLog iotSensorRangeLog=iotSensorRangeLogRepository.findByIotSensorRangeId(iotSensor);
//                                if(BeanUtils.isNull(iotSensorRangeLog)){
//                                    iotSensorRangeLog=new IotSensorRangeLog();
//                                }
//                                iotSensorRangeLog.setAlertTime(new Date());
//                                iotSensorRangeLog.setIotSensorRangeId(iotSensor);
//                                iotSensorRangeLogRepository.save(iotSensorRangeLog);
//                            }
//                        });
//
//                        LOGGER.debug("================================================================");
//                        LOGGER.debug("SendMessageUsedTime {}", System.currentTimeMillis() - stTime1);
//                    }
//                }else{
//                    if(BeanUtils.isNotNull(iotSensorRangeLog)){
//                        iotSensorRangeLogRepository.delete(iotSensorRangeLog);
//                    }
//                }
//            }catch (Exception e){
//                LOGGER.error("Error : {}",e.getMessage());
//            }
//        }
//        LOGGER.info("used Time {}", System.currentTimeMillis() - stTime);
//    }
//}
