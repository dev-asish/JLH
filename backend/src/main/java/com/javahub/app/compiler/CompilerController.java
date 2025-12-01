package com.javahub.app.compiler;

import com.javahub.app.model.User;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/compiler")
public class CompilerController {

    @Autowired
    private JavaCompilerService compilerService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    private int getUserIdFromRequest(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            }
        }
        if (username != null) {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                return userOpt.get().getId();
            }
        }
        return -1; // Return -1 if user not found (shouldn't happen with auth)
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, String>> runCode(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String code = request.get("code");
        
        if (code == null || code.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("output", "");
            error.put("errors", "Error: Code cannot be empty.");
            return ResponseEntity.badRequest().body(error);
        }

        JavaCompilerService.CompilationResult result = compilerService.compileAndRun(code);
        
        // Track compiler output in dashboard (only save output, not code)
        int userId = getUserIdFromRequest(httpRequest);
        if (userId > 0 && result.getOutput() != null && !result.getOutput().trim().isEmpty()) {
            dashboardService.updateLastCompiledOutput(userId, result.getOutput());
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("output", result.getOutput());
        response.put("errors", result.getErrors());
        
        return ResponseEntity.ok(response);
    }
}



