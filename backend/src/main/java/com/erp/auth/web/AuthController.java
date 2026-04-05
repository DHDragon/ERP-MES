package com.erp.auth.web;

import com.erp.common.dto.Result;
import com.erp.common.security.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        // 验收链路：账号密码先做最小可跑通（后续接 DB + 密码加密）
        if (!"admin".equals(req.getUsername()) || !"admin123".equals(req.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }
        String token = jwtUtil.createToken(req.getUsername());
        return Result.ok(new LoginResp(token, 3600));
    }

    @GetMapping("/me")
    public Result<String> me() {
        return Result.ok("admin");
    }

    public static class LoginReq {
        @NotBlank(message = "username不能为空")
        private String username;
        @NotBlank(message = "password不能为空")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResp {
        private String token;
        private long expiresIn;

        public LoginResp(String token, long expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
