# CSV input data format

(!) As **the "ultimate truth" of how the program works lies in the code** and the code is verified by the 
software tests, any **test data in this project will give plenty of examples** of what input data can look like.   
However, files containing "faulty" as a part of the file name are to be avoided, as these are used to test correct 
exception handling in case of *invalid* input data. 

## Example we will work with 

So, let's look at a test dataset to see what is expected - this here is the content of `src/test/resources/csv/student_data_example_one_competency.csv`.
(For convenience, I translated the header row to English).

```
surname,given name,school class,school year,part of year,birth date,total absence days,unauthorized absence days,total absence hours,unauthorized absence hours,level,"German
textual reception
reading and aural comprehension
I can extract and reflect information from texts. I can give an account of the content of texts. I know the characteristics of fairy tales and legends. "
Otto,Karl,5a,2019/2020,1,27.12.85,7,5,5,4,1,1
```

## Data format

### School report + student base data (positional)

The first 10 columns are **fixed columns, regardless of the column titles** (so-called positional parameters, if you will):

1. student's surname (`Otto`)
1. student's given name (`Karl`)
1. school class (e.g. 5a for "a-class of the fifth-year students") (`5a`)
1. school year (reflecting the calendar years spanned by the school year) (`2019/2020`)
1. part of year (whether it is the 1st half of the year or the 2nd half) (`1`, but could be something like (`End of year`))
1. student's birth date (`27.12.85`, but there are no restrictions on the date format - `12/27/85` works just as well)
1. total absence days - How many days the student was absent from school in the school report's time frame. (`7`)
1. unauthorized absence days - How many of the aforementioned absence days were not authorized (e.g. there was no doctor's certificate of an illness). (`5`)
1. total absence hours - How many hours the student was absent from school in the school report's time frame. (`5`)
1. unauthorized absence hours - How many of the aforementioned absence hours were not authorized (e.g. there was no doctor's certificate of an illness). (`4`)

At the time of writing this, the program will not do "sanity checks" on the semantics of the data, 
i.e. there is no validation that the total number of absence days / hours is greater than or equal the unauthorized number.

### Level and subject data 

#### Level columns 

Technically, the above is already enough to generate some data - a school report without subjects nor grades. 
Realistically, we do want those.  

So, right after the base data, the program assumes that a column will set the level of the subject to be graded that 
will follow in the next columns.   
By the programming logic, one such column will be enough. The value will be remembered for all following columns until 
another level column sets a different value.

To mark a column as a level column, it must be named "Niveau" or "Level" (ignoring if letters are capital letters or not).  
(This may change over time, please confer `org.example.csv2tex.csv.CsvParsingUtil#isLevelSettingColumn`for the latest implementation.)

##### Current restrictions

The code for the pilot project in Erfurt assumes that the values are "1", "2", "3", "7", "8", "9" or will not do any output. |

#### Subject data - multi-line header cells 

The subject data can have various forms. 

1. It can just be a subject and a competency.
2. It can be a subject, a competency, and a detailed description.
3. It can be a subject, competency, sub-competency and a detailed description.

To make this easy to process in the program and as easy as possible for the user to enter, 
the parts of these different forms must be separated by "new line" characters in the CSV. 
If you are using a spreadsheet application (e.g. LibreOffice Calc, Microsoft Excel), you can add newline characters 
within cells by holding the `Control` key, then pressing `Enter` (this may differ depending on operating system and 
spreadsheet tool).

In our example, we have the 3rd form:
* subject (`German`)
* competency (`textual reception`)
* sub-competency (`reading and aural comprehension`)
* detailed description (`I can extract and reflect information ...`)

##### Current restrictions

The code for the pilot project in Erfurt assumes that the values are "1", "2", "3", "4", 
and allows special values "hj" (grade comes later, with the 2nd half-year) and "nb" (no grade was given / not graded).

## "I can't remember all that, what are you thinking?"

The implementers of this program spent **a lot of time** making sure that in some assumed-common cases, the user will be 
prompted with an error message of what is not correct about the data.  
We hope the program can be used out-of-the-box, without much of a need to go through a lot of documentation.  

Let us know how that went by filing any issues you have as GitHub issues as described in the main [README.md](../README.md).