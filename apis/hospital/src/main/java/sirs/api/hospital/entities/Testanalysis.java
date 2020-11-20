package sirs.api.hospital.entities;

public class Testanalysis {
    int patient_id;
    int lab_id;

    public int getLab_id() {
        return lab_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setLab_id(int lab_id) {
        this.lab_id = lab_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }
}
