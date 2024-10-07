package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;

import com.mergen.socialease.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
	Report findByreportid(long reportid);
	ArrayList<Report> findAllBysubClubid(long subClubid);
	ArrayList<Report> findAllByReportTypeAndSubClubid(long reportType, Long subClubid);
    Report findByreportType(long reportType);
}
