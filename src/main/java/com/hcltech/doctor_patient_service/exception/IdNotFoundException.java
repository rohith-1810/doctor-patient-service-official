package com.hcltech.doctor_patient_service.exception;

public class IdNotFoundException extends RuntimeException {

    public IdNotFoundException(String message) {
        super(message);
    }
}