package sit.int221.sj1.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.int221.sj1.repositories.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository repository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        if (repository.findByEmail(username) != null) {
//            sit.int221.sj1.entities.User us = repository.findByEmail(username);
//            return new User(us.getEmail(), us.getPassword(),
//                    new ArrayList<>());
//        } else {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles=null;
        if(repository.findByEmail(username) != null){
            sit.int221.sj1.entities.User us = repository.findByEmail(username);
//            if(us.getRole().name().equals("admin"))
//            {
//                roles = Arrays.asList(new SimpleGrantedAuthority(us.getRole().name()));
//                return new User(us.getEmail(), us.getPassword(),
//                        roles);
//            }
//            else if(us.getRole().name().equals("student"))
//            {
//                roles = Arrays.asList(new SimpleGrantedAuthority(us.getRole().name()));
//                return new User(us.getEmail(), us.getPassword(),
//                        roles);
//            }
//            else if(us.getRole().name().equals("lecturer"))
//            {
                roles = Arrays.asList(new SimpleGrantedAuthority(us.getRole().name()));
                return new User(us.getEmail(), us.getPassword(),
                        roles);
//            }
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
}

}
