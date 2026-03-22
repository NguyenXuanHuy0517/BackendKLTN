package com.project.datalayer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ContractServiceId implements Serializable {
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "service_id")
    private Long serviceId;
}
