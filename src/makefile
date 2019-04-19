# makefile begins

JFLAGS = -g
JC = javac
JVM= java 

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java
		
CLASSES = \
		myrpal.java \
        ASTParser.java \
        CSEMachine.java \
        Lexer.java \
        Parser.java \
        STTransformer.java \

MAIN = myrpal

default: classes

classes: $(CLASSES:.java=.class)

run: $(MAIN).class
	$(JVM) $(MAIN)
