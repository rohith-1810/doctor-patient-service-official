package com.hcltech.doctor_patient_service.controller;

import java.util.List;

import com.hcltech.doctor_patient_service.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hcltech.doctor_patient_service.dto.PatientDto;
import com.hcltech.doctor_patient_service.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patientService;
    
    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "create a patient", description = "This will create a patient with patient details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "patient details without password"),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<PatientDto> create(@RequestBody @Valid PatientDto patientDto) {
    	log.info("Receive request to save patient details:{}",patientDto);
        PatientDto createdPatient = patientService.create(patientDto);
        log.info("Patient: {}  details saves successfully",createdPatient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @Operation(summary = "get patient by id", description = "gets the patient details with id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "patient details"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invalid username and password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getById(@PathVariable Long id) {
    	log.info("Receive request to get patient details with ID: {}",id);
        PatientDto patientDTO = patientService.getById(id);
        log.info("Patient: {}  detail retrieved successfully",patientDTO);
        if (patientDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(patientDTO, HttpStatus.OK);
        }
    }

    @Operation(summary = "get list of patients", description = "This will return list of patients' details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of patient details"),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<PatientDto>> get() {
    	log.info("Receive request to get all Patient details");
        List<PatientDto> patientDTO = patientService.get();
        log.info("successfully retrieved Patients details: {}",patientDTO.size());
        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }

    @Operation(summary = "update patient details", description = "updates the patient details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT token"),
            @ApiResponse(
                    responseCode = "404",
                    description = "patient not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> update(@PathVariable Long id,@RequestBody PatientDto patientDto ) {
    	log.info("Received request to update Patient details with ID:{}",id);
        PatientDto updatedDto =  patientService.update(id,patientDto);
        log.info("update successfuly with ID: {}",id);
        return ResponseEntity.ok(updatedDto);
    }

    @Operation(summary = "delete patient by id", description = "delete the patient with id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deletes the patient"),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean deleted = patientService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
