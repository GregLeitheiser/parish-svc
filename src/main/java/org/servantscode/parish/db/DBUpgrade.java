package org.servantscode.parish.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.servantscode.commons.db.AbstractDBUpgrade;

public class DBUpgrade extends AbstractDBUpgrade {
    private static final Logger LOG = LogManager.getLogger(DBUpgrade.class);

    @Override
    public void doUpgrade() {
        LOG.info("Verifying database structures.");

        if(!tableExists("departments")) {
            LOG.info("-- Created departments table");
            runSql("CREATE TABLE departments(id SERIAL PRIMARY KEY, " +
                                            "name TEXT, " +
                                            "department_head_id INTEGER REFERENCES people(id) ON DELETE SET NULL, " +
                                            "org_id INTEGER REFERENCES organizations(id) ON DELETE CASCADE)");
        }

        if(!tableExists("categories")) {
            LOG.info("-- Created categories table");
            runSql("CREATE TABLE categories(id SERIAL PRIMARY KEY, " +
                    "name TEXT, " +
                    "org_id INTEGER REFERENCES organizations(id) ON DELETE CASCADE)");
        }
    }
}
