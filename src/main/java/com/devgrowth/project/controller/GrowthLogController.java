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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        // Reverse the list to have dates in ascending order for the chart
        Collections.reverse(growthLogs);

        List<String> labels = growthLogs.stream().map(log -> log.getDate().toString()).collect(Collectors.toList());
        List<Integer> commitCounts = growthLogs.stream().map(GrowthLog::getCommitCount).collect(Collectors.toList());
        List<Float> avgScores = growthLogs.stream().map(GrowthLog::getAvgScore).collect(Collectors.toList());
        List<Integer> streakDays = growthLogs.stream().map(GrowthLog::getStreakDay).collect(Collectors.toList());

        model.addAttribute("growthLogs", growthLogs);
        model.addAttribute("labels", labels);
        model.addAttribute("commitCounts", commitCounts);
        model.addAttribute("avgScores", avgScores);
        model.addAttribute("streakDays", streakDays);

        return "growth-log";
    }
}
