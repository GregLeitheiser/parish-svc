package org.servantscode.parish.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.rest.PaginatedResponse;
import org.servantscode.commons.rest.SCServiceBase;
import org.servantscode.parish.Department;
import org.servantscode.parish.db.DepartmentDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/department")
public class DepartmentSvc extends SCServiceBase {
    private static final Logger LOG = LogManager.getLogger(DepartmentSvc.class);

    private DepartmentDB db;

    public DepartmentSvc() {
        db = new DepartmentDB();
    }

    @GET @Produces(MediaType.APPLICATION_JSON)
    public PaginatedResponse<Department> getDepartments(@QueryParam("start") @DefaultValue("0") int start,
                                                        @QueryParam("count") @DefaultValue("10") int count,
                                                        @QueryParam("sort_field") @DefaultValue("id") String sortField,
                                                        @QueryParam("partial_name") @DefaultValue("") String nameSearch) {

        verifyUserAccess("department.list");
        try {
            int totalPeople = db.getCount(nameSearch);

            List<Department> results = db.getDepartments(nameSearch, sortField, start, count);

            return new PaginatedResponse<>(start, results.size(), totalPeople, results);
        } catch (Throwable t) {
            LOG.error("Retrieving departments failed:", t);
            throw t;
        }
    }

    @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
    public Department getDepartment(@PathParam("id") int id) {
        verifyUserAccess("department.read");
        try {
            return db.getDepartment(id);
        } catch (Throwable t) {
            LOG.error("Retrieving department failed:", t);
            throw t;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Department createDepartment(Department department) {
        verifyUserAccess("department.create");
        try {
            db.create(department);
            LOG.info("Created department: " + department.getName());
            return department;
        } catch (Throwable t) {
            LOG.error("Creating department failed:", t);
            throw t;
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Department updateDepartment(Department department) {
        verifyUserAccess("department.update");
        try {
            db.updateDepartment(department);
            LOG.info("Edited department: " + department.getName());
            return department;
        } catch (Throwable t) {
            LOG.error("Updating department failed:", t);
            throw t;
        }
    }

    @DELETE @Path("/{id}")
    public void deleteDepartment(@PathParam("id") int id) {
        verifyUserAccess("department.delete");
        if(id <= 0)
            throw new NotFoundException();
        try {
            Department department = db.getDepartment(id);
            if(department == null || !db.deleteDepartment(id))
                throw new NotFoundException();
            LOG.info("Deleted department: " + department.getName());
        } catch (Throwable t) {
            LOG.error("Deleting department failed:", t);
            throw t;
        }
    }
}
