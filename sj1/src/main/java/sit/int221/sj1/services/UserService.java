package sit.int221.sj1.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.sj1.checkexeption.Error;
import sit.int221.sj1.dtos.UserAddAndEditDTO;
import sit.int221.sj1.dtos.UserDTO;
import sit.int221.sj1.dtos.UserWithPasswordDTO;
import sit.int221.sj1.entities.User;
import sit.int221.sj1.enums.Roles;
import sit.int221.sj1.repositories.UserRepository;
import sit.int221.sj1.utils.ListMapper;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(8,32,1,15*1024,3);

    Map<String,String> checkMessage = new HashMap<>();
    private Integer enumStatus = 0;

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

    private boolean getResponseEntity(@RequestBody User newUser) {
        checkMessage.clear();
        if (newUser.getName() == null || newUser.getName() == "") {
            checkMessage.put("name-null","Must have input name before save");
        }
        if(newUser.getName() != null && newUser.getName().trim().length() > 100){
            checkMessage.put("name-length","Name size must between 1 to 100");
        }
        if(newUser.getName() != null){
            List<User> nameList = repository.findByNameIgnoreCase(newUser.getName().trim());
            if(!nameList.isEmpty()){
                if(nameList.get(0).getId() != newUser.getId()){
                    checkMessage.put("name-duplicate","Name require unique");
                    if((newUser.getName() != null && newUser.getName() != "") && nameList.get(0).getId() == newUser.getId() && nameList.get(0).getName().trim().compareTo(newUser.getName().trim()) == 0){
                        checkMessage.remove("name-duplicate");
                    }
                }
            }
        }
        if (newUser.getEmail() == null || newUser.getEmail() == "") {
            checkMessage.put("email-null","Must have input email before save");
        }
        if(newUser.getEmail() != null && newUser.getEmail().trim().length() > 50){
            checkMessage.put("email-length","Email size must between 1 to 50");
        }
        if (newUser.getEmail() != null && !checkValidateEmail(newUser.getEmail().trim())){
            checkMessage.put("email-form","Email must be a well-formed email address");
        }
        if(newUser.getEmail() != null){
            List<User> emailList = repository.findByEmailIgnoreCase(newUser.getEmail().trim());
            if(!emailList.isEmpty()){
                if(emailList.get(0).getId() != newUser.getId()){
                    checkMessage.put("email-duplicate","Email require unique");
                    if((newUser.getEmail() != null && newUser.getEmail() != "") && emailList.get(0).getId() == newUser.getId() && emailList.get(0).getEmail().trim().compareTo(newUser.getEmail().trim()) == 0){
                        checkMessage.remove("email-duplicate");
                    }
                }
            }
        }
        if(enumStatus == -1){
            this.enumStatus = 0;
            checkMessage.put("invalid-role","Role must be admin, lecturer, or student");
        }
        if(newUser.getPassword() == null || newUser.getPassword() == ""){
            checkMessage.put("password-null","please input password first");
        }
        if(newUser.getPassword() != null && newUser.getPassword().length() != 86 &&(newUser.getPassword().length() < 8 || newUser.getPassword().length() > 14)){
            checkMessage.put("password-length","password must have 8 to 14 length");
        }
        if(checkMessage.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkEnum(String newRole) {
        for (Roles role : Roles.values()) {
            if (role.name().equals(newRole)) {
                return true;
            }
        }
        return false;
    }
    private void getRoles(){
        for (Roles role : Roles.values()) {
            System.out.println(role);
        }
    }

    private User mapUser(User existingUser, UserAddAndEditDTO updateUser) {
       if(updateUser.getName() != null) existingUser.setName(updateUser.getName().trim());
       if(updateUser.getEmail() != null) existingUser.setEmail(updateUser.getEmail().trim());
       if(updateUser.getRole() == null || updateUser.getRole() == "") updateUser.setRole(Roles.student.name());
            if(checkEnum(updateUser.getRole())){
                existingUser.setRole(Roles.valueOf(updateUser.getRole().trim()));
            }else{
                enumStatus = -1;
                existingUser.setRole(Roles.student);
            }

        return existingUser;
    }
    private User convertDTOtoEntity(UserWithPasswordDTO user) {
        User u = new User();
       if(user.getName() != null) u.setName(user.getName().trim());
       if(user.getEmail() != null) u.setEmail(user.getEmail().trim());
       if(user.getRole() == null || user.getRole() == "") {
           user.setRole(Roles.student.name());
       }
//        String password = "Hello1234";
//        u.setPassword(password);
//        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(8,32,1,15*1024,3);
//        String hash = encoder.encode(password);
//        System.out.println(hash);
//        String test = "Hello1234";
//        System.out.println(encoder.matches(test,hash));
       if(checkEnum(user.getRole())){
            u.setRole(Roles.valueOf(user.getRole()));
        }else{
            enumStatus = -1;
            u.setRole(Roles.student);
        }
       if(user.getPassword() != null && user.getPassword() != "" && user.getPassword().length() >= 8 && user.getPassword().length() <= 14){
           u.setPassword(encoder.encode(user.getPassword()));
       }else{
           u.setPassword(user.getPassword());
       }
        return u;
    }

//    @RolesAllowed("student")
    public List<UserDTO> getAllUser(){
        List<User> user = repository.findAll();
        return listMapper.mapList(user,UserDTO.class,modelMapper);
    }
    public ResponseEntity getUserById(Integer id,HttpServletRequest request){
//        return repository.findById(id).get();
        if(!repository.findById(id).isEmpty()){
            return ResponseEntity.status(200).body(modelMapper.map(repository.findById(id),UserDTO.class));
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity create(UserWithPasswordDTO newUser, HttpServletRequest request){
        User user = convertDTOtoEntity(newUser);
        if(getResponseEntity(user)){
            return ResponseEntity.status(201).body(modelMapper.map(repository.saveAndFlush(user),UserAddAndEditDTO.class));
        }else{
            return errorResponse(request,HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity delete(Integer userId,HttpServletRequest request){
        if(!repository.findById(userId).isEmpty()){
            repository.deleteById(userId);
            return ResponseEntity.status(200).body("delete id:"+ userId+" success.");
        }else {
            checkMessage.clear();
            checkMessage.put("id","id not found");
            return errorResponse(request,HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity update(UserAddAndEditDTO updateUser, Integer userId, HttpServletRequest request){
//        User user = repository.findById(userId).map(o -> mapUser(o, updateUser)).orElseGet(() -> {
//            updateUser.setId(userId);
//            return updateUser;
//        });
//        return repository.saveAndFlush(user);
//        Event event = repository.findById(eventId).map(e -> mapEventEdit(e,updateEvent)).get();
        User oldUser = repository.findById(userId).get();
        if(oldUser.getName().compareTo(updateUser.getName().trim()) == 0 && oldUser.getEmail().compareTo(updateUser.getEmail().trim()) == 0 && oldUser.getRole().name().compareTo(updateUser.getRole().trim()) == 0){
            //not complete almost finish

            return ResponseEntity.status(203).body("Notting to changes");
        }else{
            User user = repository.findById(userId).map(u->mapUser(u,updateUser)).get();
            if(getResponseEntity(user)){
                return ResponseEntity.status(200).body(modelMapper.map(repository.saveAndFlush(user),UserAddAndEditDTO.class));
            }else{
                return errorResponse(request,HttpStatus.BAD_REQUEST);
            }
        }


    }

}
