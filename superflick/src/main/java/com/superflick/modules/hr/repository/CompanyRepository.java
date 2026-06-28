package com.superflick.modules.hr.repository;

import com.superflick.modules.hr.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {}
