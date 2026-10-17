@startuml
class Doctor {
    id: Long
    name: String
    qualification: String
    specialist: SpecialistType
    patients: List<Patient>
}

class Patient {
    id: Long
    name: String
    gender: Gender
    age: Integer
    basicHealthcare: BasicHealthcare
    advancedHealthcare: AdvancedHealthcare
    doctor: Doctor
}

class BasicHealthcare {
    id: Long
    height: Double
    weight: Double
    bloodPressure: String
    sugar: String
    bloodGroup: BloodGroup
    patient: Patient
}

class AdvancedHealthcare {
    id: Long
    heartDisease: Boolean
    diabetes: Boolean
    hypertension: Boolean
    chronicCondition: List<ChronicCondition>
    allergies: List<Allergy>
    medications: List<Medication>
}

class ChronicCondition {
    id: Long
    condition: String
    advancedHealthcare: AdvancedHealthcare
}

class Allergy {
    id: Long
    types: String
    advancedHealthcare: AdvancedHealthcare
}

class Medication {
    id: Long
    name: String
    dosage: String
    advancedHealthcare: AdvancedHealthcare
}

Doctor "1" -- "0..4" Patient : has

Patient "1" -- "1" BasicHealthcare : has
Patient "1" -- "1" AdvancedHealthcare : has

AdvancedHealthcare "1" -- "0..*" ChronicCondition : has
AdvancedHealthcare "1" -- "0..*" Allergy : has
AdvancedHealthcare "1" -- "0..*" Medication : has

@enduml