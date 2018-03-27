package xyz.hyperreal

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}


package object ubjson {

  case object NOOP

  def write( obj: collection.Map[String, Any], options: Symbol* ) = {
    val buf = new ByteArrayOutputStream

    new Writer( buf, options: _* ).write( obj )

    buf.toByteArray
  }

  def write( array: Seq[Any], options: Symbol* ) = {
    val buf = new ByteArrayOutputStream

    new Writer( buf, options: _* ).write( array )

    buf.toByteArray
  }

  def read( data: Array[Byte], options: Symbol* ) =
    new Reader( new ByteArrayInputStream(data), options: _* ).read

}