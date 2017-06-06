package mik.module2_1.opdracht2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/** Database Object voor PLEK database 
 * author Robin Langerak, KIK/AMC */

public class PlekDatabase
{			   
    private DataSource dataSource;
    	
    public PlekDatabase(DataSource dataSource)
    {        
        this.dataSource = dataSource;
    }

    /** Voegt nieuwe patient aan db toe. Het patientnr mag nog niet in gebruik zijn.
     * @return true als de patient is toegevoegd, anders false */
    public boolean voegPatientToe(int patientnr, String voorletters,
            String voornaam, String geboortenaam, String geslacht,
            String overleden, String geboortedatum, String adres,
            String postcode, String woonplaats, String fam_aand)
            throws SQLException
    {                
        try (Connection connection = dataSource.getConnection(); 
             PreparedStatement insertPatientStmt = connection.prepareStatement(
             "INSERT INTO Patient(patientnr, voorletters, voornaam, geboortenaam, geslacht, overleden, geboortedatum, adres, "
             + "postcode, woonplaats, fam_aand) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) 
        {            
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 

            // TODO Opdracht 2c: Test niet alleen of patientnummer al bestaat, maar zorg ook dat de combinatie achternaam-geboortedatum maar een keer voorkomt
            //if (exists(connection, patientnr)) return false;
            if (existsgeboorte(connection, geboortedatum, geboortenaam)) return false;
            
            insertPatientStmt.setInt(1, patientnr);
            insertPatientStmt.setString(2, voorletters);
            insertPatientStmt.setString(3, voornaam);
            insertPatientStmt.setString(4, geboortenaam);
            if(geslacht == null)
                insertPatientStmt.setInt(5, 1);
            else
            {
                if(geslacht.equals("man"))
                    insertPatientStmt.setInt(5, 1);
                else
                    insertPatientStmt.setInt(5, 2);
            }
            if(overleden == null)
                insertPatientStmt.setBoolean(6, true);
            else
                insertPatientStmt.setBoolean(6, overleden.equals("on"));
            insertPatientStmt.setString(7, geboortedatum);
            insertPatientStmt.setString(8, adres);
            insertPatientStmt.setString(9, postcode);
            insertPatientStmt.setString(10, woonplaats);
            insertPatientStmt.setString(11, fam_aand);
            insertPatientStmt.executeUpdate();
            
            connection.commit();
            connection.setAutoCommit(true);
        }

        return true;
    }

    /** Controleert of patientnr al bestaat. 
     * */
    private boolean exists(Connection con, int patientnr) throws SQLException
    {
        //TODO Opdracht 2b: controleer of patientnr al bestaat
        PreparedStatement statement = con.prepareStatement
        ("SELECT Patient.patientnr FROM Patient WHERE Patient.patientnr = ?");
        
        statement.setInt(1, patientnr);
        ResultSet rs = statement.executeQuery();
            
        if(rs.next())
        {
            return true;
        }else
        {
            return false;
        }
    
    }
    /*private boolean existsgeboorte(Connection con, String geboortedatum, String geboortenaam) throws SQLException
    {
        
    PreparedStatement statement1 = con.prepareStatement("SELECT Patient.geboortedatum FROM Patient WHERE Patient.geboortedatum = ????");
    PreparedStatement statement2 = con.prepareStatement("SELECT Patient.geboortenaam FROM Patient WHERE Patient.geboortenaam = ???????");
    
    statement1.setString(4, geboortedatum);
    statement2.setString(7, geboortenaam);
    ResultSet rs1 = statement1.executeQuery();
    ResultSet rs2 = statement2.executeQuery();
    
    if (rs1.next() && rs2.next())
    {
        return true;
    }
    else
    { 
        return false;
    }
    }*/
    
    private boolean existsgeboorte(Connection con, String geboortedatum, String geboortenaam) throws SQLException
    {
        PreparedStatement statement = con.prepareStatement
        ("SELECT Patient.geboortedatum, Patient.geboortenaam FROM Patient WHERE (Patient.geboortedatum = ? AND Patient.geboortenaam = ?)");
        //PreparedStatement statementnaam = con.prepareStatement
        //("SELECT Patient.geboortenaam FROM Patient WHERE Patient.geboortenaam = ?");
        
        statement.setString(1, geboortedatum);
        statement.setString(2, geboortenaam);
        
        ResultSet rs = statement.executeQuery();
        //ResultSet rsnaam = statementnaam.executeQuery();    
        
        if(rs.next())
        {
            return true;
        }else
        {
            return false;
        }
    }
    public boolean voegTumorToe(int patientnr, int tumorID, String soort,
            String diagnose, String lokalisatie, String stadium,
            String bijzonderheden)
            throws SQLException
    {                
        try (Connection connection = dataSource.getConnection(); 
             PreparedStatement insertTumorStmt = connection.prepareStatement(
             "INSERT INTO Tumor(patientnr, tumorID, soort, diagnose, lokalisatie, stadium, bijzonderheden) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)")) 
        {            
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 

            insertTumorStmt.setInt(1, patientnr);
            insertTumorStmt.setInt(2, tumorID);
            insertTumorStmt.setString(3,soort);
            insertTumorStmt.setString(4, diagnose);
            
            insertTumorStmt.setString(5, lokalisatie);
            insertTumorStmt.setString(6, stadium);
            insertTumorStmt.setString(5, bijzonderheden);
           
            insertTumorStmt.executeUpdate();
            
            connection.commit();
            connection.setAutoCommit(true);
        }

        return true;
    }

}