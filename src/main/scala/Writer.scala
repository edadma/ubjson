//@
package xyz.hyperreal.ubjson

import java.io.{File, DataOutputStream, FileOutputStream, OutputStream}


// long marshall Long values to int64 no mater their magnitude
class Writer( out: OutputStream, options: Symbol* ) {

  def this( file: String, options: Symbol* ) = this( new FileOutputStream(file), options: _* )

  def this( file: File, options: Symbol* ) = this( new FileOutputStream(file), options: _* )

  protected val dout = new DataOutputStream( out )
  protected val longflag = options contains 'long

  protected def marker( c: Char ) = dout writeByte c

  protected def writeb( n: Int ): Unit = {
    marker( 'i' )
    dout writeByte n
  }

  protected def writeub( n: Int ): Unit = {
    marker( 'U' )
    dout writeByte n
  }

  protected def writes( n: Int ): Unit = {
    marker( 'I' )
    dout writeShort n
  }

  protected def writen( n: Int ): Unit = {
    marker( 'l' )
    dout writeInt n
  }

  protected def writec( c: Char ): Unit = {
    require( c <= Byte.MaxValue, s"character is not single byte UTF8: $c" )
    marker( 'C' )
    dout writeByte c
  }

  protected def writeutf( s: String ): Unit = {
    val bytes = io.Codec.toUTF8( s )
    val len = bytes.length

    if (len <= Byte.MaxValue)
      writeb( len )
    else if (len <= Short.MaxValue)
      writes( len )
    else if (len <= Int.MaxValue)
      writen( len )
    else
      writel( len )   // can't happen: JVM arrays are limited to Int.MaxValue elements
  }

  protected def writeh( s: String ): Unit = {
    marker( 'H' )
    writeutf( s )
  }

  protected def writel( l: Long ): Unit = {
    marker( 'L' )
    dout writeLong l
  }

  protected def write( data: Any ) =
    data match {
      case null => marker( 'Z' )
      case NOOP => marker( 'N' )
      case true => marker( 'T' )
      case false => marker( 'F' )
      case b: Byte => writeb( b )
      case s: Short if s.isValidByte => writeb( s )
      case s: Short if 0 <= s && s <= 255 => writeub( s )
      case s: Short => writes( s )
      case n: Int if n.isValidByte => writeb( n )
      case n: Int if 0 <= n && n <= 255 => writeub( n )
      case n: Int if n.isValidShort => writes( n )
      case n: Int => writen( n )
      case l: Long if longflag && l.isValidByte => writeb( l.toInt )
      case l: Long if longflag && 0 <= l && l <= 255 => writeub( l.toInt )
      case l: Long if longflag && l.isValidShort => writes( l.toInt )
      case l: Long if longflag && l.isValidInt => writen( l.toInt )
      case l: Long => writel( l )
      case bi: BigInt if bi.isValidByte => writeb( bi.toInt )
      case bi: BigInt if 0 <= bi && bi <= 255 => writeub( bi.toInt )
      case bi: BigInt if bi.isValidShort => writes( bi.toInt )
      case bi: BigInt if bi.isValidInt => writen( bi.toInt )
      case bi: BigInt if bi.isValidLong => writel( bi.toInt )
      case bi: BigInt => writeh( bi.toString )
      case bd: BigDecimal => writeh( bd.toString )
      case f: Float =>
        marker( 'd' )
        dout writeFloat f
      case d: Double =>
        marker( 'D' )
        dout writeDouble d
      case c: Char => writec( c )
      case s: String if s.length == 1 => writec( s.head )
      case s: String =>
        marker( 'S' )
        writeutf( s )
    }

  def write( m: collection.Map[String, Any] ): Unit = {
    marker( '{' )

    for ((k, v) <- m) {
      writeutf( k )
      write( v )
    }

    marker( '}' )
  }

  def write( l: Seq[Any] ): Unit = {
    marker( '[' )
    l foreach write
    marker( ']' )
  }

}