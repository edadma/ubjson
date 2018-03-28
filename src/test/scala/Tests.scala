package xyz.hyperreal.ubjson

import org.scalatest._
import prop.PropertyChecks


class Tests extends FreeSpec with PropertyChecks with Matchers {
	
	"basic" in {
		val obj = Map( "a" -> "asdf", "b" -> 123, "c" -> List(true, null, 5, Map("d" -> 34.5)) )
		val data = writeUBJSON( obj )

    readUBJSON( data ) shouldBe obj
	}
	
}