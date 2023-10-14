# hespr
A modified version of Shank—the modern, procedural, statically-typed, simple systems language.  
## Run
### From IntelliJ
Clone the repo, open the root project folder in IntelliJ Idea, then run the main class (com.wharvex.hespr.Hespr) with exactly one command line argument: the filepath of a hespr file.  
A file written in hespr that implements the GCD algorithm and demonstrates some features of hespr is provided at hespr/gcd.hespr  
### From Command Line
If this does not work, or if you want to run the project from the command line, just make sure you have Java and Maven installed, then run the following commands (tested in Linux):
```
git clone https://github.com/wharvex/hespr.git
cd hespr
mvn clean install
mvn exec:java -Dexec.mainClass=com.wharvex.hespr.Hespr -Dexec.args="$(pwd)/gcd.hespr"
```
## Motivation
I wanted hespr to have a distinctive look but the same basic functionality as Shank.
## Syntax
### Overview
* Every keyword is four letters long and indents are 5 spaces long, giving the code a kind of "aligned" look. 
* Builtin functions start with a capital letter.
* Function calls are the function name followed by a bang (!) followed by space-separated arguments.
* Several other changes were made to the main symbols used in Shank's syntax.
### Keyword Replacements
| hespr | Shank |
| ---- | --- |
| blok | define |
| flux | variables |
| perm | constants |
| with | for |
| whil | while |
| when | if |
| elif | elsif |
| else | else |
## Example
```
{{ comment }}

blok gcdIter|a b $divisor, int|
flux newA newB remainder, int
     newA =_ a
     newB =_ b
     whil newA mod newB > 0
          remainder =_ newA mod newB
          newA =_ newB
          newB =_ remainder
     divisor =_ newB

blok gcdRec|a b $divisor, int|
     when b = 0
          divisor =_ a
     else
          gcdRec! b (a mod b) $divisor

blok yes||
     Write! "yes"

blok load||
flux divisorArg divisorArg2 a, int
flux myStr myStr2, str 1 -> 5
flux myArr, str arr 5 -> 7
flux myChar2, char
perm myStr3 "what"; myInt (5 + 7); myInt2 99; myChar 'a'
     {{ expected output: 1 }}
     gcdRec! 57 (25 + 1) $divisorArg
     myChar2 =_ 'b'
     Write! ("GCD of 57 and 26. Expected: 1. Calculated: " + divisorArg)
     with divisorArg2: (a + 5) -> 20
          myStr =_ myStr + 5
     myArr[4] =_ "hi"

     {{ Expected output: 5 }}
     gcdIter! 55 25 $divisorArg
     Write! ("GCD of 55 and 25. Expected: 5. Calculated: " + divisorArg)

     {{ Expected output: 4 }}
     gcdRec! 460 64 $divisorArg
     Write! ("GCD of 460 and 64. Expected: 4. Calculated: " + divisorArg)

     {{ Expected output: 9 }}
     gcdIter! 1035 747 $divisorArg
     Write! "GCD of 1035 and 747. Expected: 9. Calculated:" divisorArg
     yes!
     myStr2 =_ "when"
     Write! myStr myInt myStr2 myChar myChar2
     Write! ""
```
