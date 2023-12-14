## 1. Functions

In this project, we implemented a top-down LL(1) method for analyzing Ada programs. The
Ada program is divided into six statements at the top layer: assignment statement, if statement,
while statement, procedure statement, until statement and for statement. After inputting tokens,
the statement is recognized based on their first sets. Then, the program utilizes follow sets to
identify the corresponding block. Besides, our program can look ahead one next input token to
improve the parsing efficiency. At last, the output document is then segmented according to our
defined rules with appropriate tree structure. The rules are enumerated as follows.
