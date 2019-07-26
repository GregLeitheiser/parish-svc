package org.servantscode.parish.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.db.DBAccess;
import org.servantscode.commons.search.QueryBuilder;
import org.servantscode.commons.search.SearchParser;
import org.servantscode.commons.security.OrganizationContext;
import org.servantscode.parish.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDB extends DBAccess {
    private static final Logger LOG = LogManager.getLogger(CategoryDB.class);

    private SearchParser<Category> searchParser;

    public CategoryDB() {
        this.searchParser = new SearchParser<>(Category.class, "name");
    }

    public int getCount(String search) {
        QueryBuilder query = count().from("categories").search(searchParser.parse(search)).inOrg();
        try (Connection conn = getConnection();
             PreparedStatement stmt = query.prepareStatement(conn);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve category count '" + search + "'", e);
        }
        return 0;
    }

    public Category getCategory(int id) {
        QueryBuilder query = selectAll().from("categories").withId(id).inOrg();
        try (Connection conn = getConnection();
             PreparedStatement stmt = query.prepareStatement(conn);
        ) {
            List<Category> categories = processResults(stmt);
            return firstOrNull(categories);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve category: " + id, e);
        }
    }

    public List<Category> getCategories(String search, String sortField, int start, int count) {
        QueryBuilder query = selectAll().from("categories").search(searchParser.parse(search)).inOrg()
                .sort(sortField).limit(count).offset(start);
        try ( Connection conn = getConnection();
              PreparedStatement stmt = query.prepareStatement(conn)
        ) {
            return processResults(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve categories.", e);
        }
    }

    public Category create(Category category) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories(name, org_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){

            stmt.setString(1, category.getName());
            stmt.setInt(2, OrganizationContext.orgId());

            if(stmt.executeUpdate() == 0) {
                throw new RuntimeException("Could not create category: " + category.getName());
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next())
                    category.setId(rs.getInt(1));
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Could not add category: " + category.getName(), e);
        }
    }

    public Category updateCategory(Category category) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE categories SET name=? WHERE id=? AND org_id=?")
        ) {

            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            stmt.setInt(3, OrganizationContext.orgId());

            if (stmt.executeUpdate() == 0)
                throw new RuntimeException("Could not update category: " + category.getName());

            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update category: " + category.getName(), e);
        }
    }

    public boolean deleteCategory(int id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM categories WHERE id=? AND org_id=?")
        ) {

            stmt.setInt(1, id);
            stmt.setInt(2, OrganizationContext.orgId());
            return stmt.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete category: " + id, e);
        }
    }

    // ----- Private -----
    private List<Category> processResults(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category r = new Category();
                r.setId(rs.getInt("id"));
                r.setName(rs.getString("name"));
                categories.add(r);
            }
            return categories;
        }
    }
}
