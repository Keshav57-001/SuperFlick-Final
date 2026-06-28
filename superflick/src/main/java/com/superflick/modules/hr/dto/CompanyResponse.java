package com.superflick.modules.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyResponse {
    private UUID id;
    private String name;
    private String website;
    private String industry;
    private String size;
    private String location;
    private String description;
    private String logoUrl;
    private boolean premium;
}