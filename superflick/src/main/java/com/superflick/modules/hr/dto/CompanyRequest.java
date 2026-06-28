package com.superflick.modules.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 255)
    private String name;

    @Pattern(regexp = "^(https?://)?[\\w\\-]+(\\.[\\w\\-]+)+.*$",
            message = "Enter a valid website URL")
    private String website;

    private String industry;
    private String size;

    @NotBlank(message = "Company location is required")
    private String location;

    @Size(max = 2000)
    private String description;
}