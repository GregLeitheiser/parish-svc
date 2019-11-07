package org.servantscode.parish.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.rest.PaginatedResponse;
import org.servantscode.commons.rest.SCServiceBase;
import org.servantscode.commons.security.OrganizationContext;
import org.servantscode.parish.Parish;
import org.servantscode.parish.db.ParishDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/parish")
public class ParishSvc extends SCServiceBase {
    private static final Logger LOG = LogManager.getLogger(ParishSvc.class);

    private ParishDB db;

    public ParishSvc() {
        db = new ParishDB();
    }

    @GET @Produces(MediaType.APPLICATION_JSON)
    public PaginatedResponse<Parish> getParishes(@QueryParam("start") @DefaultValue("0") int start,
                                                 @QueryParam("count") @DefaultValue("10") int count,
                                                 @QueryParam("sort_field") @DefaultValue("id") String sortField,
                                                 @QueryParam("search") @DefaultValue("") String nameSearch) {
        try {
            int totalPeople = db.getCount(nameSearch);

            List<Parish> results = db.getParishes(nameSearch, sortField, start, count);

            return new PaginatedResponse<>(start, results.size(), totalPeople, results);
        } catch (Throwable t) {
            LOG.error("Retrieving parishes failed:", t);
            throw t;
        }
    }

    @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
    public Parish getParish(@PathParam("id") int id) {
        try {
            return db.getParish(id);
        } catch (Throwable t) {
            LOG.error("Retrieving parish failed:", t);
            throw t;
        }
    }

    @GET @Path("/active") @Produces(MediaType.APPLICATION_JSON)
    public Parish getActiveParish() {
        try {
            return db.getParishByOrgId(OrganizationContext.orgId());
        } catch (Throwable t) {
            LOG.error("Retrieving parish failed:", t);
            throw t;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Parish createParish(Parish parish) {
        verifyUserAccess("parish.create");
        try {
            db.create(parish);
            LOG.info("Created parish: " + parish.getName());
            return parish;
        } catch (Throwable t) {
            LOG.error("Creating parish failed:", t);
            throw t;
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
    public Parish updateParish(Parish parish) {
        verifyUserAccess("parish.update");
        try {
            Parish existingParish = db.getParish(parish.getId());
            if(existingParish.getOrgId() > 0) {
                if(existingParish.getOrgId() != OrganizationContext.orgId())
                    throw new RuntimeException("Must be a member of an organization to update it");
                verifyUserAccess("parish.admin.update");
            }
            db.updateParish(parish);
            LOG.info("Edited parish: " + parish.getName());
            return parish;
        } catch (Throwable t) {
            LOG.error("Updating parish failed:", t);
            throw t;
        }
    }

    @PUT @Path("/{id}/bannerPhoto") @Consumes(MediaType.TEXT_PLAIN)
    public void attachBanner(@PathParam("id") int id, String guid) {
        verifyUserAccess("parish.admin.update");

        LOG.debug("Attaching banner photo: " + guid);
        try {
            db.attachBannerPhoto(id, guid);
        } catch (Throwable t) {
            LOG.error("Attaching photo to person failed.", t);
            throw t;
        }
    }

    @PUT @Path("/{id}/portraitPhoto") @Consumes(MediaType.TEXT_PLAIN)
    public void attachPortrait(@PathParam("id") int id, String guid) {
        verifyUserAccess("parish.admin.update");

        LOG.debug("Attaching portrait photo: " + guid);
        try {
            db.attachPortraitPhoto(id, guid);
        } catch (Throwable t) {
            LOG.error("Attaching photo to person failed.", t);
            throw t;
        }
    }

    @DELETE @Path("/{id}")
    public void deleteParish(@PathParam("id") int id) {
        verifyUserAccess("parish.admin.delete");
        if(id <= 0)
            throw new NotFoundException();
        try {
            Parish parish = db.getParish(id);
            if(parish == null)
                throw new NotFoundException();

            if(parish.getOrgId() > 0)
                verifyUserAccess("system.parish.delete");

            if(!db.deleteParish(id))
                throw new NotFoundException();
            LOG.info("Deleted parish: " + parish.getName());
        } catch (Throwable t) {
            LOG.error("Deleting parish failed:", t);
            throw t;
        }
    }
}
