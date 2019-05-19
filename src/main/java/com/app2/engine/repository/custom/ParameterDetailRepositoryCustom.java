package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.ParameterDetail;

public interface ParameterDetailRepositoryCustom {
    ParameterDetail findByParameterCodeAndParameterValue1(String code, String parameterValue1);
}
