# hespr
A modified version of Shank  
You should be able to clone the repo and then open the root project folder in IntelliJ Idea and run the main class (com.wharvex.hespr.Hespr) with exactly one command line argument that is the filepath of a hespr file. A file with the GCD algorithm as well as some feature demonstrations in hespr is provided at hespr/src/main/java/com/wharvex/hespr/gcd.hespr  
If this does not work, or if you want to run the project from the command line, just make sure you have Java and Maven installed, then run the following commands (tested in Linux):
```
git clone https://github.com/wharvex/hespr.git
cd hespr
mvn clean install
mvn exec:java -Dexec.mainClass=com.wharvex.hespr.Hespr -Dexec.args="$(pwd)/src/main/java/com/wharvex/hespr/gcd.hespr"
```
