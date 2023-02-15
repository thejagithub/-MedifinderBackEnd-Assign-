package com.mediFinder.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import javax.validation.Valid;


import com.mediFinder.emailService.EmailService;
import com.mediFinder.models.ERole;
import com.mediFinder.models.Role;
import com.mediFinder.models.User;
import com.mediFinder.payload.request.LoginRequest;
import com.mediFinder.payload.request.SignupRequest;
import com.mediFinder.payload.response.JwtResponse;
import com.mediFinder.payload.response.MessageResponse;
import com.mediFinder.repository.RoleRepository;
import com.mediFinder.repository.UserRepository;
import com.mediFinder.security.jwt.JwtUtils;
import com.mediFinder.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mediFinder.emailService.EmailDetails;
import com.mediFinder.emailService.EmailService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;


	private EmailService emailService;



	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser( @RequestBody LoginRequest loginRequest) {

		//System.out.println(loginRequest.getUsername());


		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getId(), 
												 userDetails.getUsername(),
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")


	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
					case "company":
						Role companyRole = roleRepository.findByName(ERole.ROLE_Company)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(companyRole);


				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);

		//email method


		// Sending a simple Email
		//@PostMapping("/sendMail")


//		// Sending email with attachment
//		@PostMapping("/sendMailWithAttachment")
//		public String sendMailWithAttachment(
//				@RequestBody EmailDetails details)
//		{
//			String status
//					= emailService.sendMailWithAttachment(details);
//
//			return status;

		userRepository.save(user);




		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

//		public String sendMail(EmailDetails details)
//		{
//        String status = emailService.sendSimpleMail(details);
//
//        return status;
//    }
	}

	@PostMapping("api/auth/signup")
    public String
    sendMail(@RequestBody EmailDetails details,SignupRequest signUpRequest)
    {
        String status
                = emailService.sendSimpleMail(details);

        return status;
    }
}
