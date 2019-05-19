package com.app2.engine.entity.app;

import com.app2.engine.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(of = {"id"})

public class ParameterHeader extends BaseEntity {

    @NotNull
    @Column(unique = true)
    private String code;

    private String parameterDescription;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parameterHeader")
    private Set<ParameterDetail> parameterDetails = new HashSet<ParameterDetail>();
}
