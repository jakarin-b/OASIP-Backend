package sit.int221.sj1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.sj1.dtos.LoginFormDTO;
import sit.int221.sj1.services.LoginService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private LoginService service;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody LoginFormDTO user, HttpServletRequest request) throws Exception{
        return service.match(user,request);
    }

    @GetMapping("/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getNewTokenWithRefreshToken(HttpServletRequest request) throws Exception{
        return service.refreshToken(request);
    }
}
