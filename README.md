# hespr

A modified version of Shank—the modern, procedural, statically-typed, simple systems language.

## Run

### From IntelliJ

Clone the repo, open the root project folder in IntelliJ Idea, then run the main class (
com.wharvex.hespr.Hespr) with exactly one command line argument: the filepath of a hespr file.  
A file written in hespr that implements the GCD algorithm and demonstrates some features of hespr is
provided in the root project folder and named `gcd.hespr`

### From Command Line

Make sure you have Java and Maven installed, then run the following commands (tested in Linux):

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

* Every keyword that can start a line is four letters long and indents are 5 spaces long, giving the
  code a kind of "aligned" look.
* Builtin functions start with a capital letter.
* Function calls are the function name followed by a bang (`!`) followed by space-separated
  arguments.
    * If an argument is a math operation, it must be surrounded by parentheses.
* Shank's "from" and "to" are replaced by an arrow syntax (`->`).
* Shank's assignment operator (`:=`) is replaced by what I call "the shovel" (`=_`), which you can
  think of as shoveling the value to the right of it back into the variable to the left of it.

### Keyword Replacements

| hespr  | Shank       |
|:-------|:------------|
| `blok` | `define`    |
| `flux` | `variables` |
| `perm` | `constants` |
| `with` | `for`       |
| `whil` | `while`     |
| `till` | `repeat`    |
| `when` | `if`        |
| `elif` | `elsif`     |
| `else` | `else`      |
| `true` | `true`      |
| `fals` | `false`     |
| `real` | `real`      |
| `str`  | `string`    |
| `int`  | `integer`   |
| `arr`  | `array`     |

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

## TODO

* Fix parsing logic to allow different parameter types in a function declaration.
* Rework Lexer.
* Rework Interpreter and/or write compiler.
* Make error handling more systematic/better organized.
