package org.servantscode.parish.rest;

import org.servantscode.commons.db.ConfigDB;
import org.servantscode.parish.ParishSettings;

import javax.ws.rs.*;

import java.time.Month;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.servantscode.commons.StringUtils.isSet;

@Path("/parish/settings")
public class ParishSettingsSvc {

    public static final String PARISH_FISCALYEARSTART = "parish.fiscalyearstart";
    private ConfigDB configDB;

    public ParishSettingsSvc() {
        configDB = new ConfigDB();
    }

    @GET @Produces(APPLICATION_JSON)
    public ParishSettings getParishSettings() {
        ParishSettings results = new ParishSettings();
        int fiscalYearStartMonth = Integer.parseInt(configDB.getConfiguration(PARISH_FISCALYEARSTART));

        results.setFiscalYearStart(Month.of(fiscalYearStartMonth));
        return results;
    }

    @PUT @Consumes(APPLICATION_JSON) @Produces(APPLICATION_JSON)
    public ParishSettings setParishSettings(ParishSettings settings) {
        Month start = settings.getFiscalYearStart();
        if(start != null)
            configDB.patchConfiguration(PARISH_FISCALYEARSTART, String.valueOf(start.getValue()));

        return getParishSettings();
    }
}
