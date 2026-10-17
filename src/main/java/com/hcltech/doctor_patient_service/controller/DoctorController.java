package com.hcltech.doctor_patient_service.controller;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcltech.doctor_patient_service.dto.DoctorDto;
import com.hcltech.doctor_patient_service.dto.PatientDto;
import com.hcltech.doctor_patient_service.service.DoctorService;
import com.hcltech.doctor_patient_service.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {
	private final DoctorService doctorService;
	private final PatientService patientService;

	private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

	public DoctorController(DoctorService doctorService, PatientService patientService) {
		this.doctorService = doctorService;
		this.patientService = patientService;
	}

	@Operation(summary = "Create Doctor", description = "With doctors details, this will create a doctor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "returns the doctor details back if created"),
			@ApiResponse(
					responseCode = "404",
					description = "User not found",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "409",
					description = "Doctor has exceeded the maximum patient limit of 4",
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
	@PostMapping
	public ResponseEntity<DoctorDto> create(@RequestBody @Valid DoctorDto doctorDto) {
		log.info("Received request to create doctor: {}", doctorDto);
		DoctorDto createdDoctorDto = doctorService.create(doctorDto);
		log.info("Doctor created successfully");
		return new ResponseEntity<>(createdDoctorDto, HttpStatus.CREATED);
	}

	@Operation(summary = "get doctor details by id", description = "This will return doctor details with id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "doctor details"),
			@ApiResponse(
					responseCode = "404",
					description = "doctor not found",
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
	public ResponseEntity<DoctorDto> getById(@PathVariable Long id) {
		log.info("Receive request to fetching doctor with ID: {}", id);
		DoctorDto doctorDto = doctorService.getById(id);
		log.info("Doctor retrieved: {} ", doctorDto);

		if (doctorDto == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(doctorDto, HttpStatus.OK);
		}
	}

	@Operation(summary = "get doctors list", description = "This will return list of doctors")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "list of doctor details"),
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
	public ResponseEntity<List<DoctorDto>> get() {
		log.info("Receive request to fetching all doctor");
		List<DoctorDto> doctorDtos = doctorService.get();
		log.info("Retrieved {} doctors", doctorDtos);
		return ResponseEntity.ok(doctorDtos);
	}

	@Operation(summary = "get list of patients for a doctor", description = "This will get the list of patients with doctor id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of patients of a doctor"),
            @ApiResponse(
                    responseCode = "403",
                    description = "unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
	})
	@GetMapping("/{id}/patients")
	public ResponseEntity<List<PatientDto>> getByDoctorId(@PathVariable("id") Long id) {
		log.info("Received request ot fetching all patients who  have doctor ID: {}", id);
		List<PatientDto> patientDto = patientService.getByDoctorId(id);
		log.info("Successfully fetching all Patient Details with Doctor ID: {}", id);
		return new ResponseEntity<>(patientDto, HttpStatus.OK);
	}

	@Operation(summary = "assign a patient to a doctor", description = "This will check for the doctor's patients' limit and assign the patient to that doctor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Patient assigned successfully"),
			@ApiResponse(
					responseCode = "409",
					description = "Doctor has exceeded the maximum patient limit of 4",
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
	@PutMapping("/{doctorId}/assign-patient/{patientId}")
	public ResponseEntity<String> assignPatient(@PathVariable Long doctorId, @PathVariable Long patientId) {
		log.info("Receive request to assigning patient {} to doctor {}", patientId, doctorId);
		doctorService.assignPatient(doctorId, patientId);
		log.info("patient {} assigned to doctor {} successfulyy ", patientId, doctorId);
		return ResponseEntity.ok("Patient assigned successfully");

	}

	@Operation(summary = "unassign a patient from a doctor", description = "This will unassign the patient to that doctor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "JWT token"),
			@ApiResponse(
					responseCode = "404",
					description = "patient isn't assigned to doctor",
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
	@PutMapping("/{doctorId}/unassign-patient/{patientId}")
	public ResponseEntity<String> unassignPatient(@PathVariable Long doctorId, @PathVariable Long patientId) {
		log.info("Receive request to  un-assigning patient {} to doctor {}", patientId, doctorId);
		doctorService.unassignPatient(doctorId, patientId);
		log.info("patient {} unassigned to doctor {} successfulyy ", patientId, doctorId);
		return ResponseEntity.ok("Patient unassigned successfully");

	}

	@Operation(summary = "delete the doctor", description = "This will hard delete the doctor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "doctor deleted"),
			@ApiResponse(
					responseCode = "404",
					description = "doctor not found",
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
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info(" Request to Delete doctor with ID: {}", id);

		boolean deleted = doctorService.delete(id);
		if (deleted) {
			log.info("Doctor with ID {} deleted successfully", id);
			return ResponseEntity.noContent().build();
		}
		log.warn("Doctor with ID {} not found for deletion",id);
		return ResponseEntity.notFound().build();
	}

	@Operation(summary = "update doctor details", description = "updates the doctor details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "JWT token"),
			@ApiResponse(
					responseCode = "409",
					description = "Doctor has exceeded the maximum patient limit of 4",
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
	@PutMapping
	public ResponseEntity<DoctorDto> update(@RequestBody DoctorDto doctorDto) {

		final Optional<DoctorDto> optionalDoctor= doctorService.update(doctorDto);
		if(optionalDoctor.isEmpty()) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok(optionalDoctor.get());
	}
}
