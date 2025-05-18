package com.stemapplication.Service;
import com.stemapplication.Repository.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final AdminRepository adminRepository;

    public UserDetailsServiceImp(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return (UserDetails) adminRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}