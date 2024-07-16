import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/AmanC")
public class AmanC extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String INSERT_QUERY = "INSERT INTO td (name, city, email) VALUES (?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM td";
    private static final String UPDATE_QUERY = "UPDATE td SET name=?, city=?, email=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM td WHERE id=?";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");

        String action = req.getParameter("action");

        if ("read".equals(action)) {
            readData(pw);
        } else if ("edit".equals(action)) {
            editData(req, pw);
        } else if ("delete".equals(action)) {
            deleteData(req, pw);
        } else {
            insertData(req, pw);
        }
    }

    private void insertData(HttpServletRequest req, PrintWriter pw) {
        String name = req.getParameter("name");
        String city = req.getParameter("city");
        String email = req.getParameter("email");

        if (name == null || city == null || email == null || name.isEmpty() || city.isEmpty() || email.isEmpty()) {
            pw.println("Please fill all fields.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            pw.println("Database connection error: Unable to load driver class.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/firstdb", "root", "");
             PreparedStatement ps = con.prepareStatement(INSERT_QUERY)) {

            ps.setString(1, name);
            ps.setString(2, city);
            ps.setString(3, email);

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                pw.println("Data inserted successfully.");
            } else {
                pw.println("Failed to insert data.");
            }

        } catch (SQLException ex) {
            pw.println("Database error: " + ex.getMessage());
        }
    }

    private void readData(PrintWriter pw) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            pw.println("Database connection error: Unable to load driver class.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/firstdb", "root", "");
             PreparedStatement ps = con.prepareStatement(SELECT_QUERY);
             ResultSet rs = ps.executeQuery()) {

            pw.println("<table border='1'>");
            pw.println("<tr><th>ID</th><th>Name</th><th>City</th><th>Email</th><th>Actions</th></tr>");

            while (rs.next()) {
                pw.println("<tr>");
                pw.println("<td>" + rs.getInt("id") + "</td>");
                pw.println("<td>" + rs.getString("name") + "</td>");
                pw.println("<td>" + rs.getString("city") + "</td>");
                pw.println("<td>" + rs.getString("email") + "</td>");
                pw.println("<td><a href='AmanC?action=edit&id=" + rs.getInt("id") + "'>Edit</a> | <a href='AmanC?action=delete&id=" + rs.getInt("id") + "'>Delete</a></td>");
                pw.println("</tr>");
            }

            pw.println("</table>");

        } catch (SQLException ex) {
            pw.println("Database error: " + ex.getMessage());
        }
    }

    private void editData(HttpServletRequest req, PrintWriter pw) {
        int id = Integer.parseInt(req.getParameter("id"));
        pw.println("<form action='AmanC' method='post'>");
        pw.println("<input type='hidden' name='action' value='update'>");
        pw.println("<input type='hidden' name='id' value='" + id + "'>");
        pw.println("Name: <input type='text' name='name'><br>");
        pw.println("City: <input type='text' name='city'><br>");
        pw.println("Email: <input type='email' name='email'><br>");
        pw.println("<input type='submit' value='Update'>");
        pw.println("</form>");
    }

    private void updateData(HttpServletRequest req, PrintWriter pw) {
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        String city = req.getParameter("city");
        String email = req.getParameter("email");

        if (name == null || city == null || email == null || name.isEmpty() || city.isEmpty() || email.isEmpty()) {
            pw.println("Please fill all fields.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            pw.println("Database connection error: Unable to load driver class.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/firstdb", "root", "");
             PreparedStatement ps = con.prepareStatement(UPDATE_QUERY)) {

            ps.setString(1, name);
            ps.setString(2, city);
            ps.setString(3, email);
            ps.setInt(4, id);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                pw.println("Data updated successfully.");
            } else {
                pw.println("Failed to update data.");
            }

        } catch (SQLException ex) {
            pw.println("Database error: " + ex.getMessage());
        }
    }

    private void deleteData(HttpServletRequest req, PrintWriter pw) {
        int id = Integer.parseInt(req.getParameter("id"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            pw.println("Database connection error: Unable to load driver class.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/firstdb", "root", "");
             PreparedStatement ps = con.prepareStatement(DELETE_QUERY)) {

            ps.setInt(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                pw.println("Data deleted successfully.");
            } else {
                pw.println("Failed to delete data.");
            }

        } catch (SQLException ex) {
            pw.println("Database error: " + ex.getMessage());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        PrintWriter pw = res.getWriter();

        if ("update".equals(action)) {
            updateData(req, pw);
        } else {
            doGet(req, res);
        }
    }
}
