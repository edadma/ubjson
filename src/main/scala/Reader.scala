//@
package xyz.hyperreal.ubjson

import java.io.{DataInputStream, InputStream}


// char unmarshall char values to Char instead of single character String
class UBJSONReader( in: InputStream, options: Symbol* ) {

  protected val din = new DataInputStream( in )
  protected val charflag = options contains 'char

  protected def readutf = {
    val buf = new Array[Byte]( read.asInstanceOf[Number].intValue )

    din.readFully( buf )
    new String( io.Codec.fromUTF8(buf) )
  }

  def read = {
    var marker = 0

    din.readByte match {
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
          while ({marker = din.readByte; marker != -1})

      }
  }

}