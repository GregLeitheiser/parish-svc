package org.servantscode.parish.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.db.DBAccess;
import org.servantscode.commons.search.QueryBuilder;
import org.servantscode.commons.search.SearchParser;
import org.servantscode.commons.security.OrganizationContext;
import org.servantscode.parish.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDB extends DBAccess {
    private static final Logger LOG = LogManager.getLogger(DepartmentDB.class);

    private SearchParser<Department> searchParser;

    public DepartmentDB() {
        this.searchParser = new SearchParser<>(Department.class, "name");
    }

    public int getCount(String search) {
        QueryBuilder query = count().from("departments").search(searchParser.parse(search)).inOrg();
        try (Connection conn = getConnection();
             PreparedStatement stmt = query.prepareStatement(conn);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve department count '" + search + "'", e);
        }
        return 0;
    }

    public Department getDepartment(int id) {
        QueryBuilder query = selectAll().from("departments").withId(id).inOrg();
        try (Connection conn = getConnection();
             PreparedStatement stmt = query.prepareStatement(conn);
        ) {
            List<Department> departments = processResults(stmt);
            return firstOrNull(departments);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve department: " + id, e);
        }
    }

    public List<Department> getDepartments(String search, String sortField, int start, int count) {
        QueryBuilder query = selectAll().from("departments").search(searchParser.parse(search)).inOrg()
                .sort(sortField).limit(count).offset(start);
        try ( Connection conn = getConnection();
              PreparedStatement stmt = query.prepareStatement(conn)
        ) {
            return processResults(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve departments.", e);
        }
    }

    public Department create(Department department) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO departments(name, department_head_id, org_id) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){

            stmt.setString(1, department.getName());
            stmt.setInt(2, department.getDepartmentHeadId());
            stmt.setInt(3, OrganizationContext.orgId());

            if(stmt.executeUpdate() == 0) {
                throw new RuntimeException("Could not create department: " + department.getName());
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next())
                    department.setId(rs.getInt(1));
            }
            return department;
        } catch (SQLException e) {
            throw new RuntimeException("Could not add department: " + department.getName(), e);
        }
    }

    public Department updateDepartment(Department department) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE departments SET name=?, department_head_id=? WHERE id=? AND org_id=?")
        ) {

            stmt.setString(1, department.getName());
            stmt.setInt(2, department.getDepartmentHeadId());
            stmt.setInt(3, department.getId());
            stmt.setInt(4, OrganizationContext.orgId());

            if (stmt.executeUpdate() == 0)
                throw new RuntimeException("Could not update department: " + department.getName());

            return department;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update department: " + department.getName(), e);
        }
    }

    public boolean deleteDepartment(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM departments WHERE id=? AND org_id=?")
        ) {

            stmt.setInt(1, id);
            stmt.setInt(2, OrganizationContext.orgId());
            return stmt.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete department: " + id, e);
        }
    }

    // ----- Private -----
    private List<Department> processResults(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            List<Department> departments = new ArrayList<>();
            while (rs.next()) {
                Department r = new Department();
                r.setId(rs.getInt("id"));
                r.setName(rs.getString("name"));
                r.setDepartmentHeadId(rs.getInt("department_head_id"));
                departments.add(r);
            }
            return departments;
        }
    }
}
