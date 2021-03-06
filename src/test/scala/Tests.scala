package xyz.hyperreal.ubjson

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks


class Tests extends FreeSpec with ScalaCheckPropertyChecks with Matchers {
	
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
		readUBJSON( writeUBJSON(5L, longOpt = true) ) should (be (5) and be (a [java.lang.Long]))
		readUBJSON( writeUBJSON(200L, longOpt = true) ) should (be (200) and be (a [java.lang.Long]))
		readUBJSON( writeUBJSON(300L, longOpt = true) ) should (be (300) and be (a [java.lang.Long]))
		readUBJSON( writeUBJSON(67000L, longOpt = true) ) should (be (67000) and be (a [java.lang.Long]))
	}

	"bigint" in {
		readUBJSON( writeUBJSON(BigInt(5L)) ) shouldBe 5
		readUBJSON( writeUBJSON(BigInt(200L)) ) shouldBe 200
		readUBJSON( writeUBJSON(BigInt(300L)) ) shouldBe 300
		readUBJSON( writeUBJSON(BigInt(67000L)) ) shouldBe 67000
		readUBJSON( writeUBJSON(BigInt(3000000000L)) ) shouldBe 3000000000L
		readUBJSON( writeUBJSON(BigInt(5L), bigIntOpt = true) ) should (be (5) and be (a [BigInt]))
		readUBJSON( writeUBJSON(BigInt(200L), bigIntOpt = true) ) should (be (200) and be (a [BigInt]))
		readUBJSON( writeUBJSON(BigInt(300L), bigIntOpt = true) ) should (be (300) and be (a [BigInt]))
		readUBJSON( writeUBJSON(BigInt(67000L), bigIntOpt = true) ) should (be (67000) and be (a [BigInt]))
		readUBJSON( writeUBJSON(BigInt(3000000000L), bigIntOpt = true) ) should (be (3000000000L) and be (a [BigInt]))
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