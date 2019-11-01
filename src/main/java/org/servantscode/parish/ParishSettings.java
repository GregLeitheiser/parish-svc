package org.servantscode.parish;

import java.time.Month;
import java.time.MonthDay;

public class ParishSettings {
    private Month fiscalYearStart;

    // ----- Accessors -----

    public Month getFiscalYearStart() { return fiscalYearStart; }
    public void setFiscalYearStart(Month fiscalYearStart) { this.fiscalYearStart = fiscalYearStart; }
}
