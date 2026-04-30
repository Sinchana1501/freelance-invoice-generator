import java.sql.*;
import java.util.*;

public class ClientDAO {

    public boolean add(ClientDTO c) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO clients(name,hourly_rate) VALUES(?,?)")) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getHourlyRate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<ClientDTO> getAll() {
        List<ClientDTO> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM clients ORDER BY id")) {
            while (rs.next())
                list.add(new ClientDTO(rs.getInt("id"), rs.getString("name"), rs.getInt("hourly_rate")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ClientDTO getById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM clients WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new ClientDTO(rs.getInt("id"), rs.getString("name"), rs.getInt("hourly_rate"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean update(ClientDTO c) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE clients SET name=?,hourly_rate=? WHERE id=?")) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getHourlyRate());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                PreparedStatement p1 = con.prepareStatement("DELETE FROM work_logs WHERE client_id=?");
                p1.setInt(1, id); p1.executeUpdate();
                PreparedStatement p2 = con.prepareStatement("DELETE FROM clients WHERE id=?");
                p2.setInt(1, id); p2.executeUpdate();
                con.commit(); return true;
            } catch (SQLException ex) { con.rollback(); ex.printStackTrace(); return false; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
