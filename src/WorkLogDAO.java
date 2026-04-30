import java.sql.*;
import java.util.*;

public class WorkLogDAO {

    public boolean add(WorkLogDTO w) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO work_logs(client_id,description,hours) VALUES(?,?,?)")) {
            ps.setInt(1, w.getClientId());
            ps.setString(2, w.getDescription());
            ps.setInt(3, w.getHours());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<WorkLogDTO> getByClient(int clientId, boolean unbilledOnly) {
        List<WorkLogDTO> list = new ArrayList<>();
        String sql = unbilledOnly
                ? "SELECT * FROM work_logs WHERE client_id=? AND is_billed=FALSE"
                : "SELECT * FROM work_logs WHERE client_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new WorkLogDTO(rs.getInt("log_id"), rs.getInt("client_id"),
                        rs.getString("description"), rs.getInt("hours"), rs.getBoolean("is_billed")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean markBilled(int clientId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE work_logs SET is_billed=TRUE WHERE client_id=? AND is_billed=FALSE")) {
            ps.setInt(1, clientId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteByClient(int clientId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM work_logs WHERE client_id=?")) {
            ps.setInt(1, clientId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteById(int logId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM work_logs WHERE log_id=?")) {
            ps.setInt(1, logId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
