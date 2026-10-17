package com.hcltech.doctor_patient_service.controller;

import com.hcltech.doctor_patient_service.dto.AuthenticationRequestDto;
import com.hcltech.doctor_patient_service.dto.AuthenticationResponseDto;
import com.hcltech.doctor_patient_service.exception.ErrorResponse;
import com.hcltech.doctor_patient_service.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a user", description = "This will register a user to use the APIs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the username and roles"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username already exists",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationRequestDto> register(@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        logger.info("Received registration request for user: {}", authenticationRequestDto.getUsername());
        AuthenticationRequestDto registeredUser = authenticationService.register(authenticationRequestDto);
        logger.info("User registered successfully: {}", registeredUser.getUsername());
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Login a user", description = "This will give a JWT Bearer token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT token"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(
                    responseCode = "404",
                    description = "user not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @RequestBody AuthenticationRequestDto authenticationRequestDto)  {
        logger.info("Received login request for user: {}", authenticationRequestDto.getUsername());

            AuthenticationResponseDto response = authenticationService.login(authenticationRequestDto);
            logger.info("User logged in successfully: {}", authenticationRequestDto.getUsername());
            return ResponseEntity.ok(response);


    }

    // @PostMapping("/logout")
    // public ResponseEntity<AuthenticationResponseDto> logout(
    //         @RequestBody AuthenticationRequestDto authenticationRequestDto)  {
    //     logger.info("Received logout request.");

    //     return null;
    // }
}

