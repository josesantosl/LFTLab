all:
	javac Valutatore.java
	$(MAKE) valid
	$(MAKE) invalid
valid:
	@echo "==== VALID TESTS ===="
	@echo "simple sum 5+2 = 7"
	@echo "5+2" > test.txt
	java Valutatore
	@echo "substraction 5-2 =3"
	@echo "5-2" > test.txt
	java Valutatore
	@echo "multiplication 5*2 = 10"
	@echo "5*2" > test.txt
	java Valutatore
	@echo "division 10/5 = 2"
	@echo "10/5" > test.txt
	java Valutatore
	@echo "composed equation (7+3)/5 = 2"
	@echo "(7+3)/5" > test.txt
	java Valutatore
	@echo "composed (8+(2*4))/2"
	@echo "(8+(2*4))/2" > test.txt
	java Valutatore
invalid:
	@echo "==== INVALID TESTS ===="
	@echo "simple sum 5++2 = 10 ERROR"
	@echo "5++2" > test.txt
	- java Valutatore
	@echo "substraction 5+-2 ERROR"
	@echo "5+-2" > test.txt
	- java Valutatore
	@echo "multiplication 5*!2 ERROR"
	@echo "5*!2" > test.txt
	- java Valutatore
	@echo "division 10/5( = 2 ERROR"
	@echo "10/5(" > test.txt
	- java Valutatore
	@echo "composed equation ((7+3)/5 = 2 ERROR"
	@echo "((7+3)/5" > test.txt
	- java Valutatore
	@echo "composed ((9)(2*4))/2 = 36 ERROR"
	@echo "((9)(2*4))/2" > test.txt
	- java Valutatore
