## Process this with automake to create Makefile.in

AUTOMAKE_OPTIONS = foreign

SUBDIRS = include src

EXTRA_DIST = LICENSE

test: 
	(cd src; $(MAKE) test)

lint:
	(cd src; $(MAKE) lint)
