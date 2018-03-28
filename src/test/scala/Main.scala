package xyz.hyperreal.ubjson


object Main extends App {

  val ubjson = writeUBJSON( Map("a" -> "asdf", "b" -> 5) )

  println( ubjson.toList map (b => if (b < ' ') b.toString else b.toChar) mkString " " )
  println( readUBJSON(ubjson) )

}