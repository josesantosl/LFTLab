main:
	@echo "-----------LFT-------------"
	@cat test.lft
	@echo "\n--------LEXER-------------"
	@javac Translator.java
	java Translator
	@echo "---------JASMIN-------------"
	cat Output.j
	@java -jar jasmin.jar Output.j
	@echo "---------Output-------------"
	@java Output
