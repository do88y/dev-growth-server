package com.devgrowth.project.controller;

import com.devgrowth.project.model.GrowthLog;
import com.devgrowth.project.security.CustomUserDetails;
import com.devgrowth.project.service.GrowthLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GrowthLogController {

    private final GrowthLogService growthLogService;

    @GetMapping("/growth-log")
    public String getGrowthLog(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/";
        }

        // Fetch last 30 days of growth logs
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<GrowthLog> growthLogs = growthLogService.findGrowthLogs(userDetails.getUser(), startDate, endDate);

        model.addAttribute("growthLogs", growthLogs);
        return "growth-log";
    }
}
