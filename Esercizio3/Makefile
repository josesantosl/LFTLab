all:
	javac Parser.java
	$(MAKE) valid
	$(MAKE) invalid
valid:
	@echo "==== VALID TESTS ===="
	@echo "simple sum 5+2"
	@echo "5+2" > test.txt
	java Parser
	@echo "substraction 5-2"
	@echo "5-2" > test.txt
	java Parser
	@echo "multiplication 5*2"
	@echo "5*2" > test.txt
	java Parser
	@echo "division 10/5"
	@echo "10/5" > test.txt
	java Parser
	@echo "composed equation (7+3)/5"
	@echo "(7+3)/5" > test.txt
	java Parser
	@echo "composed (8+(2*4))/2"
	@echo "(8+(2*4))/2" > test.txt
	java Parser
invalid:
	@echo "==== INVALID TESTS ===="
	@echo "simple sum 5++2"
	@echo "5++2" > test.txt
	- java Parser
	@echo "substraction 5+-2"
	@echo "5+-2" > test.txt
	- java Parser
	@echo "multiplication 5*!2"
	@echo "5*!2" > test.txt
	- java Parser
	@echo "division 10/5("
	@echo "10/5(" > test.txt
	- java Parser
	@echo "composed equation ((7+3)/5"
	@echo "((7+3)/5" > test.txt
	- java Parser
	@echo "composed ((9)(2*4))/2"
	@echo "((9)(2*4))/2" > test.txt
	- java Parser
