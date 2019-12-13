package org.servantscode.parish.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.db.AbstractDBUpgrade;

import java.sql.SQLException;

public class DBUpgrade extends AbstractDBUpgrade {
    private static final Logger LOG = LogManager.getLogger(DBUpgrade.class);

    @Override
    public void doUpgrade() throws SQLException {
        LOG.info("Verifying database structures.");

        if(!tableExists("departments")) {
            LOG.info("-- Creating departments table");
            runSql("CREATE TABLE departments(id SERIAL PRIMARY KEY, " +
                                            "name TEXT, " +
                                            "department_head_id INTEGER REFERENCES people(id) ON DELETE SET NULL, " +
                                            "org_id INTEGER REFERENCES organizations(id) ON DELETE CASCADE)");
        }

        if(!tableExists("categories")) {
            LOG.info("-- Creating categories table");
            runSql("CREATE TABLE categories(id SERIAL PRIMARY KEY, " +
                    "name TEXT, " +
                    "org_id INTEGER REFERENCES organizations(id) ON DELETE CASCADE)");
        }

        if(!tableExists("parishes")) {
            LOG.info("-- Creating parishes table");
            runSql("CREATE TABLE parishes(id SERIAL PRIMARY KEY, " +
                    "name TEXT, " +
                    "banner_guid TEXT, " +
                    "portrait_guid TEXT, " +
                    "addr_street1 TEXT, " +
                    "addr_street2 TEXT, " +
                    "addr_city TEXT, " +
                    "addr_state TEXT, " +
                    "addr_zip INTEGER, " +
                    "phone_number TEXT, " +
                    "website TEXT, " +
                    "pastor_name TEXT, " +
                    "pastor_id INTEGER REFERENCES people(id) ON DELETE SET NULL, " +
                    "fy_start_month INTEGER DEFAULT 1, " +
                    "org_id INTEGER references organizations(id) ON DELETE SET NULL)");
            runSql("INSERT INTO parishes(name, org_id) SELECT name, id from organizations");
        }

        ensureColumn("parishes", "fy_start_month", "INTEGER DEFAULT 1");
    }
}
