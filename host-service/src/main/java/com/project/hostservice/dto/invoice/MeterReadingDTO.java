package com.project.hostservice.dto.invoice;

import lombok.Data;

@Data
public class MeterReadingDTO {
    private int elecOld;
    private int elecNew;
    private int waterOld;
    private int waterNew;
}