package com.app2.engine.entity.vcc.iot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Immutable
@Table(name = "v_device_machine")
@EqualsAndHashCode(of = {"id"})
public class DeviceMachineView {
    @Id
    private Long id;
    private String deviceCode;
    private String ouCode;
}
