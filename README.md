ubjson [![Build Status](https://www.travis-ci.org/edadma/ubjson.svg?branch=master)](https://www.travis-ci.org/edadma/ubjson)
[![Version](https://img.shields.io/badge/latest_release-v0.2.1-orange.svg)](https://www.scala-sbt.org/)
[![License](https://img.shields.io/badge/license-ISC-blue.svg)](https://opensource.org/licenses/ISC)
======

*ubjson* is an implementation of the [Universal Binary JSON](http://ubjson.org/) (UBJSON) computer data interchange format for the [Scala](http://scala-lang.org) programming language.


Example
-------

The following example program shows how to use the convenience functions to write a Scala `Map` object to a UBJSON byte array, and then read that array back to create an equivalent `Map`.

    import xyz.hyperreal.ubjson._

    object Main extends App {

      val ubjson = writeUBJSON( Map("a" -> "asdf", "b" -> 5) )

      println( ubjson.toList map (b => if (b < ' ') b.toString else b.toChar) mkString " " )
      println( readUBJSON(ubjson) )

    }

This program prints

    { i 1 a S i 4 a s d f i 1 b i 5 }
    Map(a -> asdf, b -> 5)

which is the contents of the UBJSON byte array, and the resulting `Map` that was read from it.


Usage
-----

Use the following definition to use *ubjson* in your Maven project:

	<repository>
	  <id>hyperreal</id>
	  <url>https://dl.bintray.com/edadma/maven</url>
	</repository>

	<dependency>
	  <groupId>xyz.hyperreal</groupId>
	  <artifactId>ubjson</artifactId>
	  <version>0.2.1</version>
	</dependency>

Add the following to your `build.sbt` file to use *ubjson* in your SBT project:

	resolvers += "Hyperreal Repository" at "https://dl.bintray.com/edadma/maven"

	libraryDependencies += "xyz.hyperreal" %% "ubjson" % "0.2.1"


Building
--------

### Requirements

- Java 8
- SBT 1.1.1+
- Scala 2.12.5+

### Clone and Run the Tests

	git clone git://github.com/edadma/ubjson.git
	cd ubjson
	sbt test


License
-------

ISC © 2018 Edward Maxedon