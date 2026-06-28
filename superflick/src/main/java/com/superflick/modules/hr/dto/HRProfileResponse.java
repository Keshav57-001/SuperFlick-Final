package com.superflick.modules.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HRProfileResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private String designation;
    private boolean verified;
    private int dailySwipesUsed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime premiumUntil;
    private boolean premium;
    private String hiringDepartment;
    private String hiringRoles;
    private Integer expectedHiringVolume;
    private CompanyResponse company;
}