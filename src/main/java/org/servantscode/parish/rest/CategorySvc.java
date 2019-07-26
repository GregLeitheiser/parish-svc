package org.servantscode.parish.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.rest.PaginatedResponse;
import org.servantscode.commons.rest.SCServiceBase;
import org.servantscode.parish.Category;
import org.servantscode.parish.db.CategoryDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/category")
public class CategorySvc extends SCServiceBase {
    private static final Logger LOG = LogManager.getLogger(CategorySvc.class);

    private CategoryDB db;

    public CategorySvc() {
        db = new CategoryDB();
    }

    @GET @Produces(MediaType.APPLICATION_JSON)
    public PaginatedResponse<Category> getCategories(@QueryParam("start") @DefaultValue("0") int start,
                                                        @QueryParam("count") @DefaultValue("10") int count,
                                                        @QueryParam("sort_field") @DefaultValue("id") String sortField,
                                                        @QueryParam("partial_name") @DefaultValue("") String nameSearch) {

        verifyUserAccess("category.list");
        try {
            int totalPeople = db.getCount(nameSearch);

            List<Category> results = db.getCategories(nameSearch, sortField, start, count);

            return new PaginatedResponse<>(start, results.size(), totalPeople, results);
        } catch (Throwable t) {
            LOG.error("Retrieving categories failed:", t);
            throw t;
        }
    }

    @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
    public Category getCategory(@PathParam("id") int id) {
        verifyUserAccess("category.read");
        try {
            return db.getCategory(id);
        } catch (Throwable t) {
            LOG.error("Retrieving category failed:", t);
            throw t;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Category createCategory(Category category) {
        verifyUserAccess("category.create");
        try {
            db.create(category);
            LOG.info("Created category: " + category.getName());
            return category;
        } catch (Throwable t) {
            LOG.error("Creating category failed:", t);
            throw t;
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Category updateCategory(Category category) {
        verifyUserAccess("category.update");
        try {
            db.updateCategory(category);
            LOG.info("Edited category: " + category.getName());
            return category;
        } catch (Throwable t) {
            LOG.error("Updating category failed:", t);
            throw t;
        }
    }

    @DELETE @Path("/{id}")
    public void deleteCategory(@PathParam("id") int id) {
        verifyUserAccess("category.delete");
        if(id <= 0)
            throw new NotFoundException();
        try {
            Category category = db.getCategory(id);
            if(category == null || !db.deleteCategory(id))
                throw new NotFoundException();
            LOG.info("Deleted category: " + category.getName());
        } catch (Throwable t) {
            LOG.error("Deleting category failed:", t);
            throw t;
        }
    }
}
