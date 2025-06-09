package com.example.kulkita.security.oauth;

import com.example.kulkita.entity.User;
import com.example.kulkita.repository.UserRepository;
import com.example.kulkita.security.services.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        String provider = userRequest.getClientRegistration().getRegistrationId();

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if ("local".equals(user.getProvider())) {
                user.setProvider(provider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            user = new User();
            user.setEmail(email);
            user.setUsername(generateUniqueUsername(name, email));
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEnabled(true);
            user.setPassword("");
            userRepository.save(user);
        }

        return (OAuth2User) UserPrincipal.create(user);
    }

    private String generateUniqueUsername(String name, String email) {
        String baseUsername = name != null ? name.replaceAll("\\s+", "").toLowerCase() :
                email.substring(0, email.indexOf("@"));

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}