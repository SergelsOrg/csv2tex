package org.example.csv2tex.placeholders;


import com.google.common.annotations.VisibleForTesting;
import org.example.csv2tex.data.SchoolReportData;


public class PlaceholderReplacerImpl implements PlaceholderReplacer {

    private static final String TEX_TEMPLATE_PLACEHOLDER_GIVEN_NAME = "#givenName";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SURNAME = "#surName";
    private static final String TEX_TEMPLATE_PLACEHOLDER_BIRTHDAY = "#birthDay";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SCHOOL_CLASS = "#schoolClass";
    private static final String TEX_TEMPLATE_PLACEHOLDER_SCHOOL_YEAR = "#schoolYear";
    private static final String TEX_TEMPLATE_PLACEHOLDER_PART_OF_YEAR = "#partOfYear";

    @Override
    public String replacePlaceholdersInTexTemplate(String texTemplateAsString, SchoolReportData schoolReportData) {
        String texFileContent = replaceBaseData(texTemplateAsString, schoolReportData);
        texFileContent = getTablePlaceholderReplacer().replaceTableData(schoolReportData, texFileContent);
        return texFileContent;
    }


    @VisibleForTesting
    String replaceBaseData(String texFileContent, SchoolReportData schoolReportData) {
        texFileContent = texFileContent
                .replace(TEX_TEMPLATE_PLACEHOLDER_GIVEN_NAME, schoolReportData.givenName)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SURNAME, schoolReportData.surName)
                .replace(TEX_TEMPLATE_PLACEHOLDER_BIRTHDAY, schoolReportData.birthDay)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SCHOOL_CLASS, schoolReportData.schoolClass)
                .replace(TEX_TEMPLATE_PLACEHOLDER_SCHOOL_YEAR, schoolReportData.schoolYear)
                .replace(TEX_TEMPLATE_PLACEHOLDER_PART_OF_YEAR, schoolReportData.partOfYear)
                .replace("#absenceDays", schoolReportData.absenceDaysTotal)
                .replace("#absenceDaysUnauthorized", schoolReportData.absenceDaysUnexcused)
                .replace("#absenceHours", schoolReportData.absenceHoursTotal)
                .replace("#absenceHoursUnauthorized", schoolReportData.absenceHoursUnexcused);

        return texFileContent;
    }

    private TablePlaceholderReplacer getTablePlaceholderReplacer() {
        return TablePlaceholderReplacerFactory.getTablePlaceholderReplacer();
    }
}
