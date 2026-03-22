package com.project.hostservice.scheduler;

import com.project.datalayer.entity.User;
import com.project.datalayer.repository.UserRepository;
import com.project.hostservice.dto.report.ReportDTO;
import com.project.hostservice.service.EmailService;
import com.project.hostservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final UserRepository userRepository;
    private final ReportService reportService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 1 * ?")
    public void sendMonthlyReports() {
        List<User> hosts = userRepository.findByRole_RoleName("HOST");

        for (User host : hosts) {
            ReportDTO report = reportService.getDashboard(host.getUserId());
            if (host.getEmail() != null) {
                emailService.sendMonthlyReport(host.getEmail(), report);
                log.info("Gửi báo cáo tháng đến host: {}", host.getEmail());
            }
        }
    }
}