package mik.module2_1.opdracht2;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


/**
 * Servlet voor afhandeling van formulier voor toevoegen van een patient
 *
 * @author Robin Langerak, KIK/AMC
 */
@WebServlet(name = "AddPatientServlet", urlPatterns = { "/addpatientservlet" })
public class AddPatientServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private PlekDatabase database;
    
    @Resource(name = "jdbc/MyPlekDB")
    private DataSource dataSource;

    @Override
    public void init()
    {
        try
        {
            database = new PlekDatabase(dataSource);            
        }
        catch (Exception e)
        {
            System.out.println("Kan geen patiënten toevoegen want database is onbekend: " + e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html><head>"
                + "<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<link rel='stylesheet' href='general.css' type='text/css'>"
                + "<title>Resultaat toevoegen patiëntgegevens</title></head><body><p>");

        try
        {
            haalGegevensOp(request, out, database);
        }
        catch (SQLException | IllegalArgumentException e)
        {
            out.println("Kan patiënt niet toevoegen: " + e.getMessage());
        }

        out.println("</p></body></html>");
        out.close();
    }

    /**
     * Haalt patientgegevens uit request en voegt nieuwe patient toe aan
     * database.
     */
    private void haalGegevensOp(HttpServletRequest request, PrintWriter out,
            PlekDatabase database) throws SQLException
    {
        int patientnr = getIntParameter(request, "patientnr");
        boolean ok = database.voegPatientToe(
                patientnr,
                request.getParameter("voorletters"),
                request.getParameter("voornaam"),
                request.getParameter("achternaam"),
                request.getParameter("geslacht"),
                request.getParameter("overleden"),
                getDateParameters(request),
                request.getParameter("adres"),
                request.getParameter("postcode"),
                request.getParameter("woonplaats"),
                request.getParameter("familie"));

        if (ok)
        {
            out.println("Patiënt " + patientnr + " is toegevoegd.");
        } else
        {
            //out.println("De patiënt is <em>niet</em> toegevoegd: patiëntnummer " + patientnr + " bestaat al.");
            out.println("De patiënt is <em>niet</em> toegevoegd: combinatie achternaam en geboortedatum bestaat al.");
        }
    }

    /**
     * Geeft int-parameter uit request
     */
    private int getIntParameter(HttpServletRequest request, String parameter)
    {
        String value = request.getParameter(parameter);
        if (value == null)
        {
            throw new IllegalArgumentException("Getal ontbreekt");
        }

        int intValue = 0;
        try
        {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Geen geldig getal: " + value);
        }

        return intValue;
    }

    /**
     * Geeft datum-parameters uit request als Y-M-D string
     */
    private String getDateParameters(HttpServletRequest request)
    {
        int day = getIntParameter(request, "geboortedag");
        int month = getIntParameter(request, "geboortemaand");
        int year = getIntParameter(request, "geboortejaar");
        return year + "-" + month + "-" + day;
    }
}