package sit.int221.sj1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sit.int221.sj1.dtos.UserAddAndEditDTO;
import sit.int221.sj1.dtos.UserDTO;
import sit.int221.sj1.dtos.UserWithPasswordDTO;
import sit.int221.sj1.repositories.UserRepository;
import sit.int221.sj1.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService service;

    @GetMapping("")
    public List<UserDTO> getAllUser() {
        return service.getAllUser();
    }

    @GetMapping("/{userId}")
//    @PreAuthorize("hasRole('student') or hasRole('admin')")
    public ResponseEntity getUserById(@PathVariable Integer userId, HttpServletRequest request) {
        return service.getUserById(userId, request);
    }

    @PostMapping("/register")
//    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody UserWithPasswordDTO newUser, HttpServletRequest request) {
        return service.create(newUser, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity delete(@PathVariable Integer userId, HttpServletRequest request) {
       return service.delete(userId, request);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@RequestBody UserAddAndEditDTO updateUser, @PathVariable Integer userId, HttpServletRequest request) {
        return service.update(updateUser, userId, request);
    }
}
