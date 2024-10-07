package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Report {
    
    @Id
    @GeneratedValue
    @Column
    private long reportid;

    private Long reportType;

    private Long subClubid;

    private String reporter;

    private String reported;

    private Long entityid;

    private String explanation;

    public long getReportid(){ 
        return this.reportid;
    }

    public void setReportid(long reportid) {
        this.reportid = reportid;
    }

    public long getSubClubid() {
        return subClubid;
    }

    public void setSubClubid(Long subClubid) {
        this.subClubid = subClubid;
    }

    public Long getReportType() {
        return reportType;
    }

    public Long getEntityid() {
        return entityid;
    }

    public void setEntityid(Long entityid) {
        this.entityid = entityid;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public void setReportType(Long reportType) {
        this.reportType = reportType;
    }
}
