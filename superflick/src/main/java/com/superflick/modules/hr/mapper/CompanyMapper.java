package com.superflick.modules.hr.mapper;

import com.superflick.modules.hr.dto.CompanyRequest;
import com.superflick.modules.hr.dto.CompanyResponse;
import com.superflick.modules.hr.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        if (company == null) return null;
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .size(company.getSize())
                .location(company.getLocation())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .premium(company.isPremium())
                .build();
    }

    public Company toEntity(CompanyRequest req) {
        Company c = new Company();
        applyRequest(c, req);
        return c;
    }

    public void updateEntity(Company company, CompanyRequest req) {
        applyRequest(company, req);
    }

    private void applyRequest(Company c, CompanyRequest req) {
        c.setName(req.getName());
        c.setWebsite(req.getWebsite());
        c.setIndustry(req.getIndustry());
        c.setSize(req.getSize());
        c.setLocation(req.getLocation());
        c.setDescription(req.getDescription());
    }
}