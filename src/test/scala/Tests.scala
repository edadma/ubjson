package xyz.hyperreal.ubjson

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.scalatest._
import prop.PropertyChecks


class Tests extends FreeSpec with PropertyChecks with Matchers {
	
	"basic" in {
		val bout = new ByteArrayOutputStream
		val out = new Writer( bout )
		val obj = Map( "a" -> "asdf", "b" -> 123, "c" -> List(true, null, 5, Map("d" -> 34.5)) )

		out.write( obj )

		val bin = new ByteArrayInputStream( bout.toByteArray )
		val in = new Reader( bin )

		in.read shouldBe obj
	}
	
}