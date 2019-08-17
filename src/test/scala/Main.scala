package xyz.hyperreal.ubjson


object Main extends App {

//  val ubjson = writeUBJSON( Map("a" -> "asdf", "b" -> 5) )
//
//  println( ubjson.toList map (b => if (b < ' ') b.toString else b.toChar) mkString " " )
//  println( readUBJSON(ubjson) )

//  println( 3000000000L.toHexString )
//  println( writeUBJSON(BigInt(3000000000L)).toList map (b => ((b&0xFF).toHexString, b.toChar)) mkString " " )
//  println( readUBJSON(writeUBJSON(BigInt(3000000000L))).asInstanceOf[Number].longValue.toHexString )

  println( readUBJSON( writeUBJSON(List(1, NOOP, 2, NOOP, NOOP, 3)) ) )

}