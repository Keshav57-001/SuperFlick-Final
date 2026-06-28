package com.superflick.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    // Named "totalUsers" to match AdminService.builder().totalUsers(...)
    private long totalUsers;
    private long totalHR;
    private long premiumUsers;
    private long premiumCompanies;
    private long activeSubscriptions;
    private long expiredSubscriptions;
    private long totalJobsPosted;
    private long activeJobs;
    private long totalApplications;
    private long manualApplications;
    private long autoApplications;
    private long totalMatches;
    private long totalMessages;
    private BigDecimal totalRevenue;
    private BigDecimal razorpayRevenue;
    private BigDecimal stripeRevenue;
    private BigDecimal upiRevenue;
}