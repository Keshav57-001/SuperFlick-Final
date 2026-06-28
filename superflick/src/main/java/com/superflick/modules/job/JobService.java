package com.superflick.modules.job;

import com.superflick.modules.hr.repository.HRRepository;
import com.superflick.modules.job.dto.JobRequest;
import com.superflick.modules.job.dto.JobResponse;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.job.entity.JobSkill;
import com.superflick.modules.job.mapper.JobMapper;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.job.repository.JobSkillRepository;
import com.superflick.modules.skill.repository.SkillRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.shared.enums.JobStatus;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepo;
    private final HRRepository hrRepo;
    private final SkillRepository skillRepo;
    private final JobSkillRepository jobSkillRepo;
    private final JobMapper jobMapper;

    // Manual constructor for dependency injection (Lombok stripped)
    public JobService(JobRepository jobRepo,
                      HRRepository hrRepo,
                      SkillRepository skillRepo,
                      JobSkillRepository jobSkillRepo,
                      JobMapper jobMapper) {
        this.jobRepo = jobRepo;
        this.hrRepo = hrRepo;
        this.skillRepo = skillRepo;
        this.jobSkillRepo = jobSkillRepo;
        this.jobMapper = jobMapper;
    }

    public JobResponse postJob(User user, JobRequest req) {
        var hr = hrRepo.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("HR profile not found"));

        Job job = new Job();
        job.setHr(hr);
        job.setCompany(hr.getCompany());
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(req.getLocation());
        job.setCtcMin(req.getCtcMin());
        job.setCtcMax(req.getCtcMax());
        job.setExperienceRequired(req.getExperienceRequired());
        job.setShiftTimings(req.getShiftTimings());
        job.setBoosted(false);
        job.setStatus(JobStatus.ACTIVE);

        // Save initial job to generate its primary UUID key
        Job savedJob = jobRepo.save(job);

        // Link skills with structural reference context
        Job finalSavedJob = savedJob;
        List<JobSkill> skills = req.getSkillIds().stream()
                .map(skillId -> {
                    var skill = skillRepo.findById(UUID.fromString(skillId))
                            .orElseThrow(() -> new NotFoundException("Skill not found: " + skillId));
                    JobSkill js = new JobSkill();
                    js.setJob(finalSavedJob);
                    js.setSkill(skill);
                    boolean required = req.getSkillRequired() == null
                            || req.getSkillRequired().getOrDefault(skillId, true);
                    js.setRequired(required);
                    return js;
                })
                .collect(Collectors.toList());

        jobSkillRepo.saveAll(skills);
        savedJob.setSkills(skills);

        // Final flush save to sync relational entities to database completely
        savedJob = jobRepo.save(savedJob);

        log.info("Job posted and synced: id={} title={}", savedJob.getId(), savedJob.getTitle());
        return jobMapper.toResponse(savedJob);
    }

    public JobResponse updateJob(User user, UUID jobId, JobRequest req) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!job.getHr().getUser().getId().equals(user.getId()))
            throw new ForbiddenException("Not your job");

        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(req.getLocation());
        job.setCtcMin(req.getCtcMin());
        job.setCtcMax(req.getCtcMax());
        job.setExperienceRequired(req.getExperienceRequired());
        job.setShiftTimings(req.getShiftTimings());

        jobSkillRepo.deleteAllByJobId(jobId);
        List<JobSkill> skills = req.getSkillIds().stream()
                .map(skillId -> {
                    var skill = skillRepo.findById(UUID.fromString(skillId))
                            .orElseThrow(() -> new NotFoundException("Skill not found: " + skillId));
                    JobSkill js = new JobSkill();
                    js.setJob(job);
                    js.setSkill(skill);
                    js.setRequired(true);
                    return js;
                })
                .collect(Collectors.toList());

        jobSkillRepo.saveAll(skills);
        job.setSkills(skills);

        return jobMapper.toResponse(jobRepo.save(job));
    }

    public void deleteJob(User user, UUID jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!job.getHr().getUser().getId().equals(user.getId()))
            throw new ForbiddenException("Not your job");
        jobRepo.delete(job);
    }

    public void boostJob(UUID jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));
        job.setBoosted(true);
        jobRepo.save(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByHR(UUID userId) {
        var hr = hrRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("HR profile not found"));
        return jobRepo.findByHr(hr).stream()
                .map(jobMapper::toResponse)
                .collect(Collectors.toList());
    }
}