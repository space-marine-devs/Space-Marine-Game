import sys

def header():
	print("<?xml version=\"1.0\" encoding=\"utf-8\"?>")

def addBorder(width, height):
	for i in range(0, height, 32):
		addObject(0, i)
		addObject(width, i)
	for i in range(0, width+32, 32):
		addObject(i, 0)
		addObject(i, height)
	


def addObject(x, y, typeObj=None):
	entity = ("\t\t<entity x=\"%s\" y=\"%s\" width=\"32\" height=\"32\" type=\"metalbox\"/>" % (x, y))
	print(entity)


def level(width, height):
	print("\t<level width=\"%s\" height=\"%s\">" % (width, height))
	#addBorder(int(width), int(height))
	addObject( 10, 10 )
	addObject( 20, 20 )
	addObject( 50, 50 )
	print("</level>")
		
def main():
	height, width = sys.argv[1:]
	heigth = int(height)
	width = int(width)
	header()
	level(height, width)

main()
