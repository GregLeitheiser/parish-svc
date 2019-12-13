package org.servantscode.parish.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.Address;
import org.servantscode.commons.Identity;
import org.servantscode.commons.db.DBAccess;
import org.servantscode.commons.db.EasyDB;
import org.servantscode.commons.search.InsertBuilder;
import org.servantscode.commons.search.QueryBuilder;
import org.servantscode.commons.search.SearchParser;
import org.servantscode.commons.search.UpdateBuilder;
import org.servantscode.commons.security.OrganizationContext;
import org.servantscode.parish.Parish;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParishDB extends EasyDB<Parish> {
    private static final Logger LOG = LogManager.getLogger(ParishDB.class);

    public ParishDB() {
        super(Parish.class, "name");
    }

    private QueryBuilder query(QueryBuilder selection) {
        return selection.from("parishes");
    }

    public int getCount(String search) {
        return getCount(query(count()).search(searchParser.parse(search)));
    }

    public Parish getParish(int id) {
        return getOne(query(selectAll()).withId(id));
    }

    public Parish getParishByOrgId(int orgId) {
        return getOne(query(selectAll()).with("org_id", orgId));
    }

    public List<Parish> getParishes(String search, String sortField, int start, int count) {
        QueryBuilder query = query(selectAll()).search(searchParser.parse(search))
                .page(sortField, start, count);
        return get(query);
    }

    public Parish create(Parish parish) {
        InsertBuilder cmd = insertInto("parishes")
                .value("name", parish.getName())
                .value("banner_guid", parish.getBannerGuid())
                .value("portrait_guid", parish.getPortraitGuid())
                .value("addr_street1", parish.getAddress().getStreet1())
                .value("addr_street2", parish.getAddress().getStreet2())
                .value("addr_city", parish.getAddress().getCity())
                .value("addr_state", parish.getAddress().getState())
                .value("addr_zip", parish.getAddress().getZip())
                .value("phone_number", parish.getPhoneNumber())
                .value("website", parish.getWebsite())
                .value("pastor_name", parish.getPastor().getName())
                .value("pastor_id", parish.getPastor().getId())
                .value("fy_start_month", parish.getFiscalYearStartMonth())
                .value("org_id", parish.getOrgId());
        if(!create(cmd))
            throw new RuntimeException("Could not create parish record");
        return parish;
    }

    public Parish updateParish(Parish parish) {
        UpdateBuilder cmd = update("parishes")
                .value("name", parish.getName())
                .value("banner_guid", parish.getBannerGuid())
                .value("portrait_guid", parish.getPortraitGuid())
                .value("addr_street1", parish.getAddress().getStreet1())
                .value("addr_street2", parish.getAddress().getStreet2())
                .value("addr_city", parish.getAddress().getCity())
                .value("addr_state", parish.getAddress().getState())
                .value("addr_zip", parish.getAddress().getZip())
                .value("phone_number", parish.getPhoneNumber())
                .value("website", parish.getWebsite())
                .value("pastor_name", parish.getPastor().getName())
                .value("pastor_id", parish.getPastor().getId())
                .value("fy_start_month", parish.getFiscalYearStartMonth())
                .value("org_id", parish.getOrgId())
                .withId(parish.getId());
        if(!update(cmd))
            throw new RuntimeException("Could not update parish record");
        return parish;
    }

    public void attachBannerPhoto(int id, String guid) {
        UpdateBuilder cmd = update("parishes")
                .value("banner_guid", guid)
                .withId(id);
        if(!update(cmd))
            throw new RuntimeException("Could not update parish banner photo");
    }

    public void attachPortraitPhoto(int id, String guid) {
        UpdateBuilder cmd = update("parishes")
                .value("portrait_guid", guid)
                .withId(id);
        if(!update(cmd))
            throw new RuntimeException("Could not update parish portrait photo");
    }

    public boolean deleteParish(int id) {
        return delete(deleteFrom("parishes").withId(id));
    }

    // ----- Private -----
    @Override
    protected Parish processRow(ResultSet rs) throws SQLException {
        Parish parish = new Parish();
        parish.setId(rs.getInt("id"));
        parish.setName(rs.getString("name"));
        parish.setBannerGuid(rs.getString("banner_guid"));
        parish.setPortraitGuid(rs.getString("portrait_guid"));
        Address addr = new Address(rs.getString("addr_street1"),
                rs.getString("addr_street2"),
                rs.getString("addr_city"),
                rs.getString("addr_state"),
                rs.getInt("addr_zip"));
        parish.setAddress(addr);
        parish.setPhoneNumber(rs.getString("phone_number"));
        parish.setWebsite(rs.getString("website"));
        parish.setOrgId(rs.getInt("org_id"));
        parish.setPastor(new Identity(rs.getString("pastor_name"), rs.getInt("pastor_id")));
        parish.setFiscalYearStartMonth(rs.getInt("fy_start_month"));
        return parish;
    }

}
