package xyz.hyperreal.ubjson

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}


object Main extends App {

  val bout = new ByteArrayOutputStream
  val out = new Writer( bout )

  out.write( Map("a" -> "asdf", "b" -> 123, "c" -> List(true, null, 5, Map("d" -> 34.5))) )
  println( bout.toByteArray.toList map (b => (b, b.toChar)) )

  val bin = new ByteArrayInputStream( bout.toByteArray )
  val in = new Reader( bin )

  println( in.read )

}