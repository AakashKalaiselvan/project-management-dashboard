package com.pms.controller;

import com.pms.dto.AuthRequest;
import com.pms.dto.AuthResponse;
import com.pms.entity.User;
import com.pms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information. Returns a JWT token upon successful registration."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "user": {
                            "id": 1,
                            "name": "John Doe",
                            "email": "john@example.com",
                            "role": "USER"
                          },
                          "message": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation error or user already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                        {
                          "token": null,
                          "user": null,
                          "message": "User with this email already exists"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> register(
        @Parameter(
            description = "User registration details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Registration Request",
                    value = """
                        {
                          "name": "John Doe",
                          "email": "john@example.com",
                          "password": "password123"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody AuthRequest request
    ) {
        AuthResponse response = authService.register(request);
        
        if (response.getMessage() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user with email and password. Returns a JWT token upon successful authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User authenticated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "user": {
                            "id": 1,
                            "name": "John Doe",
                            "email": "john@example.com",
                            "role": "USER"
                          },
                          "message": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                        {
                          "token": null,
                          "user": null,
                          "message": "Invalid email or password"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> login(
        @Parameter(
            description = "User login credentials",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Login Request",
                    value = """
                        {
                          "email": "john@example.com",
                          "password": "password123"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody AuthRequest request
    ) {
        System.out.println("Login attempt with email: " + request.getEmail());
        AuthResponse response = authService.login(request);
        
        if (response.getMessage() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @Operation(
        summary = "Test authentication",
        description = "Tests if the current JWT token is valid and returns user information. Used for debugging authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication test successful",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "Authentication working! User: john@example.com"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<String> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok("Authentication working! User: " + user.getEmail());
        }
        return ResponseEntity.ok("No authentication found");
    }
} 