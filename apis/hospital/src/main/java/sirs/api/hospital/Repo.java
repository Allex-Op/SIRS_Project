package sirs.api.hospital;

import org.springframework.stereotype.Component;
import sirs.api.hospital.entities.Patient;
import sirs.api.hospital.entities.TestResults;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

@Component
public class Repo {
    private Dbconn conn = new Dbconn();
    Patient getPatientName(int id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement("SELECT name FROM patients WHERE patient_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            rs.next();
            String name = rs.getString(1);
            rs.close();
            ps.close();

            Patient p = new Patient();
            p.name = name;
            return p;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return null;
        }
    }

    Patient getPatientDiseases(int id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement("SELECT name, diseases FROM patients WHERE patient_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            rs.next();
            String name = rs.getString(1);
            String diseases = rs.getString(2);
            rs.close();
            ps.close();

            Patient p = new Patient();
            p.name = name;
            p.diseases = diseases;
            return p;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return null;
        }
    }

    Patient getPatientTestResults(int id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "SELECT name, result FROM patients as p INNER JOIN tests as t ON p.patient_id=t.patient_id  " +
                            "WHERE t.patient_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            LinkedList<String> results = new LinkedList<>();
            rs.next();
            String name = rs.getString(1);

            do {
                String result = rs.getString(2);
                results.add(result);
            } while(rs.next());

            rs.close();
            ps.close();

            Patient p = new Patient();
            p.name = name;
            p.results = results;
            return p;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return null;
        }
    }

    Patient getPatientTreatment(int id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement("SELECT name, treatment FROM patients WHERE patient_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            rs.next();

            String name = rs.getString(1);
            String treatment = rs.getString(2);
            rs.close();
            ps.close();

            Patient p = new Patient();
            p.name = name;
            p.treatment = treatment;
            return p;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return null;
        }
    }

    String[] getResult(int id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "SELECT result,digital_signature,public_key,l.lab_id " +
                    "FROM tests as t " +
                    "INNER JOIN labs as l ON t.lab_id=l.lab_id WHERE test_id = ?");

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            rs.next();
            String result = rs.getString(1);
            String digitalSignature = rs.getString(2);
            String publicKey = rs.getString(3);
            rs.close();
            ps.close();

            return new String[]{result, digitalSignature, publicKey};
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return null;
        }
    }

    int sendTestsDataToLab(int patient_id, int lab_id) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "INSERT INTO pendingtests(patient_id,lab_id) VALUES(?,?)"
            );

            ps.setInt(1, patient_id);
            ps.setInt(2, lab_id);
            int affectedRows = ps.executeUpdate();

            // This query is possible because the primary key of the pendingtests table
            // is the composite of the values test_id, patient_id and lab_id.
            PreparedStatement st = conn.conn.prepareStatement(
                    "SELECT test_id FROM pendingtests WHERE patient_id=? AND lab_id=?"
            );

            st.setInt(1, patient_id);
            st.setInt(2, lab_id);

            ResultSet rs = st.executeQuery();
            rs.next();
            int test_id = rs.getInt(1);
            rs.close();
            ps.close();

            return test_id;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return -1;
        }
    }

    public boolean updateTestResults(int test_id, TestResults results) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "SELECT patient_id,lab_id FROM pendingtests WHERE test_id=?"
            );

            ps.setInt(1, test_id);
            ResultSet rs = ps.executeQuery();

            // Trying to update test results without a pending test
            if(!rs.next())
                return false;

            int patient_id = rs.getInt(1);
            int lab_id = rs.getInt(2);
            rs.close();
            ps.close();

            // Upgrade from pending test to completed test with the necessary data
            PreparedStatement ps2 = conn.conn.prepareStatement(
                    "INSERT INTO tests(patient_id,result,lab_id,digital_signature) " +
                            "VALUES (?,?,?,?)"
            );

            ps2.setInt(1, patient_id);
            ps2.setString(2, results.getResults());
            ps2.setInt(3, lab_id);
            ps2.setString(4, results.getDigitalSignature());

            int affectedRows = ps2.executeUpdate();

            // Cleanup the pendingtests
            PreparedStatement ps3 = conn.conn.prepareStatement(
                    "DELETE FROM pendingtests WHERE patient_id=? AND lab_id=?"
            );

            ps3.setInt(1, patient_id);
            ps3.setInt(2, lab_id);
            ps3.executeUpdate();

            return true;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return false;
        }
    }
}
