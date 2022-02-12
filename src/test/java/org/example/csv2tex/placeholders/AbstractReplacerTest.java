package org.example.csv2tex.placeholders;

import org.example.csv2tex.data.SchoolCompetencyData;
import org.example.csv2tex.data.SchoolReportData;

public abstract class AbstractReplacerTest {


    protected SchoolReportData generateSchoolReportData() {
        SchoolReportData schoolReportData = new SchoolReportData();
        schoolReportData.birthDay = "29.12.1985";
        schoolReportData.schoolClass = "5c";
        schoolReportData.schoolYear = "2021/2022";
        schoolReportData.givenName = "Michael";
        schoolReportData.surName = "Pöhle";
        schoolReportData.partOfYear = "Halbjahr";
        schoolReportData.absenceDaysTotal = "8";
        schoolReportData.absenceDaysUnexcused = "5";
        schoolReportData.absenceHoursTotal = "7";
        schoolReportData.absenceHoursUnexcused = "3";
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData1());
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData2());
        schoolReportData.schoolCompetencies.add(generateSchoolCompetencyData3());

        return schoolReportData;
    }

    private SchoolCompetencyData generateSchoolCompetencyData1() {
        SchoolCompetencyData schoolCompetencyData1 = new SchoolCompetencyData();

        schoolCompetencyData1.schoolSubject = "Mathematik";
        schoolCompetencyData1.schoolCompetency = "Rechnen";
        schoolCompetencyData1.schoolSubCompetency = "Addition";
        schoolCompetencyData1.description = "Kann addieren.";
        schoolCompetencyData1.grade = "1";
        schoolCompetencyData1.level = "1";

        return schoolCompetencyData1;
    }

    private SchoolCompetencyData generateSchoolCompetencyData2() {
        SchoolCompetencyData schoolCompetencyData2 = new SchoolCompetencyData();

        schoolCompetencyData2.schoolSubject = "Mathematik";
        schoolCompetencyData2.schoolCompetency = "Rechnen";
        schoolCompetencyData2.schoolSubCompetency = "Subtraktion";
        schoolCompetencyData2.description = "Kann subtrahieren.";
        schoolCompetencyData2.grade = "2";
        schoolCompetencyData2.level = "1";

        return schoolCompetencyData2;
    }

    private SchoolCompetencyData generateSchoolCompetencyData3() {
        SchoolCompetencyData schoolCompetencyData3 = new SchoolCompetencyData();

        schoolCompetencyData3.schoolSubject = "Fremdsprache";
        schoolCompetencyData3.schoolCompetency = "Französisch";
        schoolCompetencyData3.schoolSubCompetency = "sc";
        schoolCompetencyData3.description = "des";
        schoolCompetencyData3.grade = "1";
        schoolCompetencyData3.level = "3";

        return schoolCompetencyData3;
    }
}
