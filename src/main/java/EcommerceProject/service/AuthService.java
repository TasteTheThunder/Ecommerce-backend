package EcommerceProject.service;

import EcommerceProject.Security.request.LoginRequest;
import EcommerceProject.Security.request.SignupRequest;
import EcommerceProject.Security.response.MessageResponse;
import EcommerceProject.Security.response.UserInfoResponse;
import EcommerceProject.payload.AuthenticationResult;
import EcommerceProject.payload.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<MessageResponse> register(SignupRequest signUpRequest);

    UserInfoResponse getCurrentUserDetails(Authentication authentication);

    ResponseCookie logoutUser();

    UserResponse getAllSellers(Pageable pageable);
}

