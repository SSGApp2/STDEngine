package com.app2.engine.entity.vcc.iot;


import lombok.Data;

import java.util.List;

@Data
public class IotSensorCombine {
    private Long id;
    private List<IotSensorCombineView> iotSensorCombineViews;
}
