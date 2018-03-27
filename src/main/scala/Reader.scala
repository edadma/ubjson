//@
package xyz.hyperreal.ubjson

import java.io.{DataInputStream, InputStream, PushbackInputStream}

import scala.collection.mutable.ArrayBuffer


// char unmarshall char values to Char instead of single character String
class Reader( in: InputStream, options: Symbol* ) {

  protected val pin = new PushbackInputStream( in )
  protected val din = new DataInputStream( pin )
  protected val charflag = options contains 'char

  protected def readutf: String = {
    val buf = new Array[Byte]( read.asInstanceOf[Number].intValue )

    din.readFully( buf )
    new String( io.Codec.fromUTF8(buf) )
  }

  def read: Any =
    din.readByte match {
      case -1 => sys.error( "unexpected end of input" )
      case 'Z' => null
      case 'N' => NOOP
      case 'T' => true
      case 'F' => false
      case 'i' => din.readByte
      case 'U' => din.readUnsignedByte
      case 'I' => din.readShort
      case 'l' => din.readInt
      case 'L' => din.readLong
      case 'd' => din.readFloat
      case 'D' => din.readDouble
      case 'H' =>
        val v = readutf

        if (v contains '.')
          BigDecimal( v )
        else
          BigInt( v )
      case 'C' =>
        val c = din.readByte.toChar

        if (charflag)
          c
        else
          c.toString
      case 'S' => readutf
      case '[' =>
        val buf = new ArrayBuffer[Any]

        def readArray: Vector[Any] =
          pin.read match {
            case -1 => sys.error( "unexpected end of input while reading array" )
            case ']' => buf.toVector
            case b =>
              pin.unread( b )
              buf += read
              readArray
          }

        readArray
      case '{' =>
        val buf = new ArrayBuffer[(String, Any)]

        def readPairs: Seq[(String, Any)] =
          pin.read match {
            case -1 => sys.error( "unexpected end of input while reading object" )
            case '}' => buf
            case b =>
              pin.unread( b )
              buf += (readutf -> read)
              readPairs
          }

        readPairs.toMap
    }

}