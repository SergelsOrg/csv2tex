(!) **If tables in this document are not shown correctly**, view it on the project's GitHub website (table rendering like this is an
[extension that is specific to GitHub-flavored markdown](https://github.github.com/gfm/#tables-extension-) -
[the original spec](https://daringfireball.net/projects/markdown/syntax#html) uses HTML tables instead).


# TEX templates: Placeholders that will get replaced from the CSV data

## Generic placeholders 

These placeholders are common to all formats for all schools potentially taking part: 


| Placeholder | Meaning |
| --- | --- |
| `#givenName` | First name of the student. |
| `#surName` | Last name of the student. |
| `#birthDay` | Student's birthday. |
| `#schoolClass` | Class (form) of the school report, e.g. `1c` for a student's first school year in the `c` class of multiple parallel classes. |
| `#schoolYear` | Actual calendar years spent in school, e.g. `2021 / 2022`. |
| `#partOfYear` | Marker for what half-year this was, e.g. `End of year`. |
| `#absenceDaysTotal` | How many days the student was absent from school in the school report's time frame. |
| `#absenceDaysUnauthorized` | How many of the aforementioned absence days were not authorized (e.g. there was no doctor's certificate of an illness). |
| `#absenceHoursTotal` | How many hours the student was absent from school in the school report's time frame. |
| `#absenceHoursUnauthorized` | How many of the aforementioned absence hours were not authorized (e.g. there was no doctor's certificate of an illness). |
| `#tables` | Table data of the school report, containing subject grades - to be rendered by specific code. |

In case this documentation becomes outdated, the constants in class
`org.example.csv2tex.placeholders.PlaceholderReplacerImpl` should give you a more
complete picture.

## Erfurt pilot project 

The project's pilot school has specific requirements, these are handled by dedicated code.

**While these could technically be used in a custom template,** they are currently all handled internally by the Java software.
Their usage is not recommended.

The only placeholder in the TEX template files is `#tables`, as mentioned in the previous section.

| Placeholder | Meaning |
| --- | --- |
| `#SUBJECT` |  School subject's name (per-subject placeholder). |
| `#COMPETENCIES` | Subject-specific competencies (intermediary placeholder for multiple `#COMPETENCY` ones). |
| `#COMPETENCY` | Subject-specific competency that gets graded, e.g. reading skills as part of German class. |
| `#LEVEL` | Level of the subject that gets graded. The code assumes values "1", "2", "3", "7", "8", "9" or will not do any output. |
| `#GRADE` | The subject grade. The code assumes values "1", "2", "3", "4", and allows special values "hj" (grade comes later, with the 2nd half-year) and "nb" (no grade was given / not graded).|

In case this documentation becomes outdated, the constants in class 
`org.example.csv2tex.placeholders.schoolspecific.ErfurtSchoolTablePlaceholderReplacer` should give you a more 
complete picture. 