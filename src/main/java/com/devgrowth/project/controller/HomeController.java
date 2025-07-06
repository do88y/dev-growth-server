
package com.devgrowth.project.controller;

import com.devgrowth.project.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("name", userDetails.getUsername());
            model.addAttribute("email", userDetails.getUser().getEmail());
        } else {
            model.addAttribute("name", "Guest");
            model.addAttribute("email", "N/A");
        }
        return "home"; // Renders home.html template
    }
}
