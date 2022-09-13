package sit.int221.sj1.services;

import io.jsonwebtoken.impl.DefaultClaims;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.sj1.checkexeption.Error;
import sit.int221.sj1.dtos.AuthResponse;
import sit.int221.sj1.dtos.LoginFormDTO;
import sit.int221.sj1.dtos.NewTokenWithRefreshDTO;
import sit.int221.sj1.entities.User;
import sit.int221.sj1.repositories.UserRepository;
import sit.int221.sj1.securities.JwtTokenUtil;
import sit.int221.sj1.utils.ListMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class LoginService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

//    @Autowired AuthenticationManager authManager;
//    @Autowired JwtTokenUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService jwtInMemoryUserDetailsService;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(8,32,1,15*1024,3);

    Map<String,String> checkMessage = new HashMap<>();

    private ResponseEntity errorResponse(HttpServletRequest request, HttpStatus status){
        ZoneId bangkokTimeZone = ZoneId.of("Asia/Bangkok");
        Instant currentTime = Instant.now();
        Error error = new Error(currentTime.atZone(bangkokTimeZone).toString(),status.value(),request.getRequestURI(),status,"Validate Error",checkMessage);
        return new ResponseEntity<>(error,error.getHttpStatus());
    }

    public Boolean checkValidateEmail(String email){
        String regexPattern = "^([a-zA-Z0-9._-])+@\\w+([a-zA-Z0-9._-])*(\\.[a-zA-Z0-9_-]{2,10})+$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    private boolean getResponseEntity(@RequestBody LoginFormDTO user) {
        checkMessage.clear();
        if (user.getEmail() == null || user.getEmail() == "") {
            checkMessage.put("email-null", "Must have input email before login");
        }
        if(user.getEmail() != null && user.getEmail().trim().length() > 50){
            checkMessage.put("email-length","Email size must between 1 to 50");
        }
        if (user.getEmail() != null && !checkValidateEmail(user.getEmail().trim())){
            checkMessage.put("email-form","Email must be a well-formed email address");
        }
        if(user.getEmail() != null && checkMessage.isEmpty()){
            User us = repository.findByEmail(user.getEmail().trim());
            if(us == null){
                checkMessage.put("email-not-found","email is not found.");
            }
        }
        if(user.getPassword() == null || user.getPassword() == ""){
            checkMessage.put("password-null","please input password first");
        }
        if(user.getPassword() != null && user.getPassword().length() != 86 &&(user.getPassword().length() < 8 || user.getPassword().length() > 14)){
            checkMessage.put("password-length","password must have 8 to 14 length");
        }
        if(user.getPassword() != null && repository.findByEmail(user.getEmail().trim()) != null && checkMessage.isEmpty()){
            if(!encoder.matches(user.getPassword(),repository.findByEmail(user.getEmail()).getPassword()) && checkMessage.get("email-not-found") == null){
                checkMessage.put("password-invalid","password is not valid.");
            }
        }
        if(checkMessage.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    public ResponseEntity match(LoginFormDTO user, HttpServletRequest request) throws Exception{
        if(getResponseEntity(user)){
            authenticate(user.getEmail(),user.getPassword());
            final UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(user.getEmail());
            final String accessToken = jwtTokenUtil.generateToken(userDetails);
            final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(user.getEmail(),accessToken,refreshToken));
        }else{
             if(checkMessage.get("email-not-found") != null){
                return errorResponse(request, HttpStatus.NOT_FOUND);
            }else if(checkMessage.get("password-invalid") != null){
                 return errorResponse(request, HttpStatus.UNAUTHORIZED);
             }
             else{
                return errorResponse(request, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public ResponseEntity refreshToken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        String username = null;
        String jwtToken = null;
        final String requestTokenHeader = request.getHeader("Authorization");
        jwtToken = requestTokenHeader.substring(7);
        username = jwtTokenUtil.getUsernameFromToken(jwtToken);
        final UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username);
        final String newAccessToken = jwtTokenUtil.generateTokenWithRefreshToken(userDetails,jwtToken);
        return ResponseEntity.ok(new NewTokenWithRefreshDTO(newAccessToken));
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
