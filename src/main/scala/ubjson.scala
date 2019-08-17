//@
package xyz.hyperreal

import scala.collection.mutable.ArrayBuffer


package object ubjson {

  case object NOOP

  def writeUBJSON( obj: Any, longOpt: Boolean = false, bigIntOpt: Boolean = false, countOpt: Boolean = true ) = {
    val buf = new ArrayBuffer[Byte]

    def writeb( b: Int ) = buf += b.toByte

    def write8( n: Int ): Unit = {
      writeb( 'i' )
      writeb( n )
    }

    def writeu8( n: Int ): Unit = {
      writeb( 'U' )
      writeb( n )
    }

    def writeShort( n: Int ): Unit = {
      writeb( n >> 8 )
      writeb( n )
    }

    def write16( n: Int ): Unit = {
      writeb( 'I' )
      writeShort( n )
    }

    def writeInt( n: Int ): Unit = {
      writeShort( n >> 16 )
      writeShort( n )
    }

    def write32( n: Int ): Unit = {
      writeb( 'l' )
      writeInt( n )
    }

    def writechar( c: Char ): Unit = {
      writeb( 'C' )
      writeb( c )
    }

    def writeString( s: String ): Unit = {
      val bytes = io.Codec.toUTF8( s )
      val len = bytes.length

      if (len <= Byte.MaxValue)
        write8( len )
      else if (len <= Short.MaxValue)
        write16( len )
      else
        write32( len )

      buf ++= bytes
    }

    def writehuge( s: String ): Unit = {
      writeb( 'H' )
      writeString( s )
    }

    def writeLong( n: Long ): Unit = {
      writeInt( (n >> 32).toInt )
      writeInt( n.toInt )
    }

    def write64( n: Long ): Unit = {
      writeb( 'L' )
      writeLong( n )
    }

    def writes( s: String ): Unit = {
      writeb( 'S' )
      writeString( s )
    }

    def writecount( c: Int ): Unit = {
      writeb( '#' )
      write( c )
    }

    def writea( s: collection.Seq[Any] ): Unit = {
      writeb( '[' )

      if (countOpt)
        writecount( s.length )

      s foreach write

      if (!countOpt)
        writeb( ']' )
    }

    def write( data: Any ): Unit =
      data match {
        case null => writeb( 'Z' )
        case NOOP => writeb( 'N' )
        case true => writeb( 'T' )
        case false => writeb( 'F' )
        case b: Byte => write8( b )
        case s: Short if s.isValidByte => write8( s )
        case s: Short if 128 <= s && s <= 255 => writeu8( s )
        case s: Short => write16( s )
        case n: Int if n.isValidByte => write8( n )
        case n: Int if 128 <= n && n <= 255 => writeu8( n )
        case n: Int if n.isValidShort => write16( n )
        case n: Int => write32( n )
        case l: Long if !longOpt && l.isValidByte => write8( l.toInt )
        case l: Long if !longOpt && 128 <= l && l <= 255 => writeu8( l.toInt )
        case l: Long if !longOpt && l.isValidShort => write16( l.toInt )
        case l: Long if !longOpt && l.isValidInt => write32( l.toInt )
        case l: Long => write64( l )
        case bi: BigInt if !bigIntOpt && bi.isValidByte => write8( bi.toInt )
        case bi: BigInt if !bigIntOpt && 128 <= bi && bi <= 255 => writeu8( bi.toInt )
        case bi: BigInt if !bigIntOpt && bi.isValidShort => write16( bi.toInt )
        case bi: BigInt if !bigIntOpt && bi.isValidInt => write32( bi.toInt )
        case bi: BigInt if !bigIntOpt && !longOpt && bi.isValidLong => write64( bi.toLong )
        case bi: BigInt => writehuge( bi.toString )
        case bd: BigDecimal => writehuge( bd.toString )
        case f: Float =>
          writeb( 'd' )
          writeInt( java.lang.Float.floatToIntBits(f) )
        case d: Double =>
          writeb( 'D' )
          writeLong( java.lang.Double.doubleToLongBits(d) )
        case c: Char if c <= Byte.MaxValue => writechar( c )
        case c: Char => writes( c.toString )
        case s: String if s.length == 1 && s.head <= Byte.MaxValue => writechar( s.head )
        case s: String => writes( s )
        case s: Seq[_] => writea( s )
        case a: Array[_] => writea( a )
        case m: collection.Map[_, _] =>
          writeb( '{' )

          for ((k, v) <- m.asInstanceOf[collection.Map[String, Any]]) {
            writeString( k )
            write( v )
          }

          writeb( '}' )
      }

    write( obj )
    buf.toArray
  }

  def readUBJSON( data: Array[Byte], charOpt: Boolean = false ) = {
    var index = 0

    def peek =
      if (index >= data.length)
        sys.error( "unexpected end of input" )
      else
        data(index)

    def skip = index += 1

    def readb = {
      val b = peek

      skip
      b
    }

    def readub = readb&0xFF

    def readShort = readb << 8 | readub

    def readInt = readShort << 16 | (readShort&0xFFFF)

    def readLong = readInt.toLong << 32 | (readInt&0xFFFFFFFFL)

    def readString: String = {
      val buf = new Array[Byte]( read.asInstanceOf[Number].intValue )

      for (i <- buf.indices)
        buf(i) = readb

      new String( io.Codec.fromUTF8(buf) )
    }

    def readint = read.asInstanceOf[Number].intValue

    def read: Any =
      readb match {
        case 'Z' => null
        case 'N' => NOOP
        case 'T' => true
        case 'F' => false
        case 'i' => readb
        case 'U' => readub
        case 'I' => readShort
        case 'l' => readInt
        case 'L' => readLong
        case 'd' => java.lang.Float.intBitsToFloat( readInt )
        case 'D' => java.lang.Double.longBitsToDouble( readLong )
        case 'H' =>
          val v = readString

          if (v contains '.')
            BigDecimal( v )
          else
            BigInt( v )
        case 'C' =>
          val c = readb.toChar

          if (charOpt)
            c
          else
            c.toString
        case 'S' => readString
        case '[' =>
          val buf = new ArrayBuffer[Any]

          def readArray: Unit =
            peek match {
              case ']' => skip
              case _ =>
                read match {
                  case NOOP =>
                  case e => buf += e
                }

                readArray
            }

          if (peek == '#') {
            skip

            val array = new Array[Any]( readint )

            for (i <- array.indices)
              array(i) = read

            array.toVector
          } else {
            readArray
            buf.toVector
          }
        case '{' =>
          val buf = new ArrayBuffer[(String, Any)]

          def readPairs: Unit =
            peek match {
              case '}' => skip
              case b =>
                buf += (readString -> read)
                readPairs
            }

          readPairs
          buf.toMap
      }

    read
  }

}