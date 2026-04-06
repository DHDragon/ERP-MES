package com.erp.auth.web;

import com.erp.auth.service.AuthService;
import com.erp.base.dto.MenuNodeDto;
import com.erp.common.dto.Result;
import com.erp.common.util.SecurityContextUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        return Result.ok(new LoginResp(token, 3600));
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.ok("OK");
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        return Result.ok(authService.me(SecurityContextUtil.getUsername()));
    }

    @GetMapping("/menus")
    public Result<List<MenuNodeDto>> menus() {
        return Result.ok(authService.menusByUsername(SecurityContextUtil.getUsername()));
    }

    @GetMapping("/permissions")
    public Result<List<String>> permissions() {
        return Result.ok(authService.listPermissionsByUsername(SecurityContextUtil.getUsername()));
    }

    public static class LoginReq {
        @NotBlank(message = "username不能为空")
        private String username;
        @NotBlank(message = "password不能为空")
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResp {
        private String token;
        private long expiresIn;
        public LoginResp(String token, long expiresIn) { this.token = token; this.expiresIn = expiresIn; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    }
}
