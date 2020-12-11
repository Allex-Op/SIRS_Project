package sirs.api.hospital.db;

import org.springframework.stereotype.Component;
import sirs.api.hospital.Crypto;
import sirs.api.hospital.entities.LoginBody;
import sirs.api.hospital.entities.Patient;
import sirs.api.hospital.entities.TestResults;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

@Component
public class Repo {
    private Dbconn conn = new Dbconn();
    private Crypto cr = new Crypto();

    public Patient getPatientName(int id) {
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

    public Patient getPatientDiseases(int id) {
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

    public Patient getPatientTestResults(int id) {
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

    public Patient getPatientTreatment(int id) {
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

    public String[] getResult(int id) {
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

    public boolean insertTestResultsFromLab(int patient_id, String result, int lab_id, String digital_signature) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "INSERT INTO tests(patient_id, result, lab_id, digital_signature) VALUES(?,?,?,?)"
            );

            ps.setInt(1, patient_id);
            ps.setString(2, result);
            ps.setInt(3, lab_id);
            ps.setString(4, digital_signature);
            int affectedRows = ps.executeUpdate();
            ps.close();

            return true;
        } catch(Exception e) {
            System.out.println("Unlucky something went wrong, no sql queries for you...");
            return false;
        }
    }

    public String getSessionToken(LoginBody credentials) {
        try {
            String token = "";

            // Reads the password hash from the database for comparison with the provided password
            PreparedStatement ps = conn.conn.prepareStatement(
                    "SELECT employee_id,password FROM employees WHERE username=?"
            );

            ps.setString(1, credentials.getUsername());
            ResultSet rs = ps.executeQuery();

            // Return if no user found
            if(!rs.next())
                return "";
            else {
                // If passwords don't match return
                if(!cr.matchPasswords(credentials.getPassword(), rs.getString(2)))
                    return "";
            }

            // If user already has session return same token
            int employee_id = rs.getInt(1);
            token = checkSession(employee_id);
            if(!token.equals(""))
                return token;

            // Otherwise, create a new session
            PreparedStatement ps3 = conn.conn.prepareStatement(
                    "INSERT INTO sessions(token, employee_id) VALUES (?,?)"
            );

            String generatedToken = cr.createToken();
            ps3.setString(1, generatedToken);
            ps3.setInt(2, employee_id);

            int affectedRows = ps3.executeUpdate();

            //And then return the new token
            token = checkSession(employee_id);
            if(!token.equals(""))
                return token;

            //Fallback when everything goes wrong and there is no session/token
            return "";
        } catch(Exception e) {
            System.out.println("[Repo] Error executing getSessionToken function.");
            e.printStackTrace();
            return "";
        }
    }

    private String checkSession(int employee_id) throws SQLException {
        PreparedStatement ps2 = conn.conn.prepareStatement(
                "SELECT token FROM sessions WHERE employee_id=?"
        );

        ps2.setInt(1, employee_id);
        ResultSet rs2 = ps2.executeQuery();

        if(rs2.next())
            return rs2.getString(1);
        return "";
    }

    public String validateToken(String token) {
        try {
            PreparedStatement ps = conn.conn.prepareStatement(
                    "SELECT rolename FROM " +
                            "sessions as s INNER JOIN employees as e ON s.employee_id = e.employee_id " +
                            "INNER JOIN roles as r ON r.role_id = e.role_id " +
                            "WHERE s.token = ?"
            );

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if(!rs.next())
                return "";

            return rs.getString(1);
        } catch(Exception e) {
            System.out.println("[Repo] Error executing validateToken function");
            e.printStackTrace();
            return "";
        }
    }
}
