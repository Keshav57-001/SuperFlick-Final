package com.superflick.modules.hr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HRProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255)
    private String fullName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Company details are required")
    @Valid
    private CompanyRequest company;

    private String hiringDepartment;
    private String hiringRoles;

    @Min(value = 1, message = "Expected hiring volume must be at least 1")
    private Integer expectedHiringVolume;

    @AssertTrue(message = "You must accept the Terms and Conditions")
    private boolean termsAccepted;
}