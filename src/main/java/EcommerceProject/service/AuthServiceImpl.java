package EcommerceProject.service;

import EcommerceProject.Exception.APIException;
import EcommerceProject.Exception.ResourceNotFoundException;
import EcommerceProject.Model.AppRole;
import EcommerceProject.Model.Role;
import EcommerceProject.Model.User;
import EcommerceProject.Security.jwt.JwtUtils;
import EcommerceProject.Security.request.LoginRequest;
import EcommerceProject.Security.request.SignupRequest;
import EcommerceProject.Security.response.MessageResponse;
import EcommerceProject.Security.response.UserInfoResponse;
import EcommerceProject.Security.services.UserDetailsImpl;
import EcommerceProject.repositories.RoleRepository;
import EcommerceProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    ));
        } catch (AuthenticationException exception) {
            throw new APIException("Bad credentials");
        }

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get logged-in user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate JWT cookie for browser
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // Pick only one primary role
        String primaryRole = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        // Generate token string (for API use)
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        // Build user info response
        UserInfoResponse userInfo = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                List.of(primaryRole),// return only one role
                userDetails.getEmail(),
                jwtToken               // include token in response
        );

        // Prepare result map with cookie and response body
        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", userInfo);
        result.put("jwtCookie", jwtCookie);

        return result;
    }


    @Override
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new APIException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new APIException("Error: Email is already in use!");
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "ROLE_USER"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "ROLE_ADMIN"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "ROLE_SELLER"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "ROLE_USER"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
}

