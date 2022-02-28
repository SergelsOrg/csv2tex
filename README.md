**<img src="https://raw.githubusercontent.com/SergelsOrg/csv2tex/main/src/main/resources/org/example/csv2tex/ui/openmoji_flag-germany_1f1e9-1f1ea.png" alt="Flag DE" width="20"/>German version / Deutsche Version:** [README_DE.md](README_DE.md)

# csv2tex

[![CircleCI](https://circleci.com/gh/SergelsOrg/csv2tex/tree/main.svg?style=shield)](https://circleci.com/gh/SergelsOrg/csv2tex/tree/main)


A tool to replace placeholders in tex files with content from a given csv file.

Concrete use case: Automated generation of school reports as PDF.


## Running the software

To run the tool,

* check or download the content of this code repository
* run the script in your shell (command line):

```bash
$ ./runApplicationUsingGradle.sh 
```

The Gradle build tool will download all necessary libraries and launch the grahpical user interface (GUI).

As runtime environment, it is assumed that **Java version 11** or higher is installed.  
This may change in the future - it is the project's goal to be directly executable on a recent version of Ubuntu Linux. 

## Input data

### CSV file

One part of the required input data is the school report data in a CSV file.

This format is documented in [docs/CSV_Format.md](docs/CSV_Format.md).

### TEX template

Another required part of the input is a `.tex` file with placeholders that will be replaced by the program with data 
from the CSV. 

These placeholders are documented in [docs/TEX_Placeholders_Format.md](docs/TEX_Placeholders_Format.md).

## "It's broken for me" / "I found a bug" / "This feature is missing"

For any such issues, please create an issue in the public GitHub repository 
([==> link to GitHub issues <==](https://github.com/SergelsOrg/csv2tex/issues)).  
You have to register as a user, but GitHub is free of charge.

## Miscellaneous

### Project license

Find the project's license [here as a file](LICENSE). 

At the time of writing, the project is licensed under the 
[GNU General Public License (GPL)](https://www.gnu.org/licenses/gpl-3.0.en.html)
- that means, any changes you make, you must disclose (and, ideally, offer to merge them back here).

Find a quick overview and simplified explanation 
[here](https://tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)).

### Emoji: License

The flag emoji that are used are from [OpenMoji](https://openmoji.org/about/) and are licensed
under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/).

 