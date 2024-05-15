import android.util.Log
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.StandardMessageCodec
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun wrapResult(result: Any?): List<Any?> {
  return listOf(result)
}

private fun wrapError(exception: Throwable): List<Any?> {
  return if (exception is FlutterError) {
    listOf(
      exception.code,
      exception.message,
      exception.details
    )
  } else {
    listOf(
      exception.javaClass.simpleName,
      exception.toString(),
      "Cause: " + exception.cause + ", Stacktrace: " + Log.getStackTraceString(exception)
    )
  }
}

/**
 * Error class for passing custom error details to Flutter via a thrown PlatformException.
 * @property code The error code.
 * @property message The error message.
 * @property details The error details. Must be a datatype supported by the api codec.
 */
class FlutterError (
  val code: String,
  override val message: String? = null,
  val details: Any? = null
) : Throwable()

data class IntColor (
  val value: Int
) {
  companion object {
    @Suppress("UNCHECKED_CAST")
    fun fromList(list: List<Any?>): IntColor {
      val value = list[0].let { if (it is Long) it.toInt() else it as Int }
      return IntColor(value)
    }
  }
  fun toList(): List<Any?> {
    return listOf<Any?>(
      value,
    )
  }
}

data class StringUrl (
  val value: String
) {
  companion object {
    @Suppress("UNCHECKED_CAST")
    fun fromList(list: List<Any?>): StringUrl {
      val value = list[0] as String
      return StringUrl(value)
    }
  }
  fun toList(): List<Any?> {
    return listOf<Any?>(
      value,
    )
  }
}

@Suppress("UNCHECKED_CAST")
private object NativeDivExpressionsResolverCodec : StandardMessageCodec() {
  override fun readValueOfType(type: Byte, buffer: ByteBuffer): Any? {
    return when (type) {
      128.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          IntColor.fromList(it)
        }
      }
      129.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          StringUrl.fromList(it)
        }
      }
      else -> super.readValueOfType(type, buffer)
    }
  }
  override fun writeValue(stream: ByteArrayOutputStream, value: Any?)   {
    when (value) {
      is IntColor -> {
        stream.write(128)
        writeValue(stream, value.toList())
      }
      is StringUrl -> {
        stream.write(129)
        writeValue(stream, value.toList())
      }
      else -> super.writeValue(stream, value)
    }
  }
}

interface NativeDivExpressionsResolver {
  fun resolve(expression: String, context: Map<String, Any>, callback: (Result<Any?>) -> Unit)
  fun clearVariables(callback: (Result<Unit>) -> Unit)

  companion object {
    /** The codec used by NativeDivExpressionsResolver. */
    val codec: MessageCodec<Any?> by lazy {
      NativeDivExpressionsResolverCodec
    }
    /** Sets up an instance of `NativeDivExpressionsResolver` to handle messages through the `binaryMessenger`. */
    @Suppress("UNCHECKED_CAST")
    fun setUp(binaryMessenger: BinaryMessenger, api: NativeDivExpressionsResolver?) {
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "div_expressions_resolver.NativeDivExpressionsResolver.resolve", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val expressionArg = args[0] as String
            val contextArg = args[1] as Map<String, Any>
            api.resolve(expressionArg, contextArg) { result: Result<Any?> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "div_expressions_resolver.NativeDivExpressionsResolver.clearVariables", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.clearVariables() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}
