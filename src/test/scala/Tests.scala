package xyz.hyperreal.ubjson

import org.scalatest._
import prop.PropertyChecks


class Tests extends FreeSpec with PropertyChecks with Matchers {
	
//	"basic" in {
//		val obj = Map( "a" -> "asdf", "b" -> 123, "c" -> List(true, false, null, "c", "\u03B1", -5, 200, 300, 67000, 3000000000L, BigInt("10000000000000000000"), BigDecimal("0.1234567890123456789"), Map("d" -> 34.5, "e" -> 67.8F)) )
//
//    readUBJSON( writeUBJSON(obj) ) shouldBe obj
//	}

	"noop" in {
		readUBJSON( writeUBJSON(List(1, NOOP, 2, NOOP, NOOP, 3)) ) shouldBe List(1, 2, 3)
	}

	"char" in {
		readUBJSON( writeUBJSON('a') ) shouldBe "a"
		readUBJSON( writeUBJSON('a'), charOpt = true ) shouldBe 'a'
		readUBJSON( writeUBJSON('\u03B1') ) shouldBe "\u03B1"
		readUBJSON( writeUBJSON('\u03B1'), charOpt = true ) shouldBe "\u03B1"
	}

	"byte" in {
		readUBJSON( writeUBJSON(5.toByte) ) shouldBe 5
	}

	"short" in {
		readUBJSON( writeUBJSON(5.toShort) ) shouldBe 5
		readUBJSON( writeUBJSON(200.toShort) ) shouldBe 200
		readUBJSON( writeUBJSON(300.toShort) ) shouldBe 300
	}

	"long" in {
		readUBJSON( writeUBJSON(5L) ) shouldBe 5
		readUBJSON( writeUBJSON(200L) ) shouldBe 200
		readUBJSON( writeUBJSON(300L) ) shouldBe 300
		readUBJSON( writeUBJSON(67000L) ) shouldBe 67000
		readUBJSON( writeUBJSON(5L, longOpt = true) ) shouldBe 5L	// todo: test doesn't actually check the type for Long
		readUBJSON( writeUBJSON(200L, longOpt = true) ) shouldBe 200L
		readUBJSON( writeUBJSON(300L, longOpt = true) ) shouldBe 300L
		readUBJSON( writeUBJSON(67000L, longOpt = true) ) shouldBe 67000L
	}

	"bigint" in {
		readUBJSON( writeUBJSON(BigInt(5L)) ) shouldBe 5
		readUBJSON( writeUBJSON(BigInt(200L)) ) shouldBe 200
		readUBJSON( writeUBJSON(BigInt(300L)) ) shouldBe 300
		readUBJSON( writeUBJSON(BigInt(67000L)) ) shouldBe 67000
		readUBJSON( writeUBJSON(BigInt(3000000000L)) ) shouldBe 3000000000L
		readUBJSON( writeUBJSON(BigInt(5L), bigIntOpt = true) ) shouldBe BigInt(5L)	// todo: test doesn't actually check the type for BigInt
		readUBJSON( writeUBJSON(BigInt(200L), bigIntOpt = true) ) shouldBe BigInt(200L)
		readUBJSON( writeUBJSON(BigInt(300L)) ) shouldBe BigInt(300L)
		readUBJSON( writeUBJSON(BigInt(67000L)) ) shouldBe BigInt(67000L)
		readUBJSON( writeUBJSON(BigInt(3000000000L)) ) shouldBe BigInt(3000000000L)
	}

	"string" in {
		val longstring = "a"*200
		val longerstring = "a"*67000

		readUBJSON( writeUBJSON(longstring) ) shouldBe longstring
		readUBJSON( writeUBJSON(longerstring) ) shouldBe longerstring
	}

	"error" in {
		a [RuntimeException] should be thrownBy {readUBJSON( writeUBJSON(123).slice(0,1) )}
	}
	
}