import Foundation
#if os(iOS)
import Flutter
#elseif os(macOS)
import FlutterMacOS
#else
#error("Unsupported platform.")
#endif

private func wrapResult(_ result: Any?) -> [Any?] {
  return [result]
}

private func wrapError(_ error: Any) -> [Any?] {
  if let flutterError = error as? FlutterError {
    return [
      flutterError.code,
      flutterError.message,
      flutterError.details
    ]
  }
  return [
    "\(error)",
    "\(type(of: error))",
    "Stacktrace: \(Thread.callStackSymbols)"
  ]
}

private func isNullish(_ value: Any?) -> Bool {
  return value is NSNull || value == nil
}

private func nilOrValue<T>(_ value: Any?) -> T? {
  if value is NSNull { return nil }
  return value as! T?
}

extension FlutterError: Error {}

struct IntColor {
  var value: Int

  static func fromList(_ list: [Any?]) -> IntColor? {
    let value = list[0] is Int ? list[0] as! Int : Int(list[0] as! Int64)

    return IntColor(
      value: value
    )
  }
  func toList() -> [Any?] {
    return [
      value,
    ]
  }
}

struct StringUrl {
  var value: String

  static func fromList(_ list: [Any?]) -> StringUrl? {
    let value = list[0] as! String

    return StringUrl(
      value: value
    )
  }
  func toList() -> [Any?] {
    return [
      value,
    ]
  }
}

private class NativeDivExpressionsResolverCodecReader: FlutterStandardReader {
  override func readValue(ofType type: UInt8) -> Any? {
    switch type {
      case 128:
        return IntColor.fromList(self.readValue() as! [Any?])
      case 129:
        return StringUrl.fromList(self.readValue() as! [Any?])
      default:
        return super.readValue(ofType: type)
    }
  }
}

private class NativeDivExpressionsResolverCodecWriter: FlutterStandardWriter {
  override func writeValue(_ value: Any) {
    if let value = value as? IntColor {
      super.writeByte(128)
      super.writeValue(value.toList())
    } else if let value = value as? StringUrl {
      super.writeByte(129)
      super.writeValue(value.toList())
    } else {
      super.writeValue(value)
    }
  }
}

private class NativeDivExpressionsResolverCodecReaderWriter: FlutterStandardReaderWriter {
  override func reader(with data: Data) -> FlutterStandardReader {
    return NativeDivExpressionsResolverCodecReader(data: data)
  }

  override func writer(with data: NSMutableData) -> FlutterStandardWriter {
    return NativeDivExpressionsResolverCodecWriter(data: data)
  }
}

class NativeDivExpressionsResolverCodec: FlutterStandardMessageCodec {
  static let shared = NativeDivExpressionsResolverCodec(readerWriter: NativeDivExpressionsResolverCodecReaderWriter())
}

protocol NativeDivExpressionsResolver {
  func resolve(expression: String, context: [String: Any], completion: @escaping (Result<Any?, Error>) -> Void)
  func clearVariables(completion: @escaping (Result<Void, Error>) -> Void)
}

public class NativeDivExpressionsResolverSetup {
  /// The codec used by NativeDivExpressionsResolver.
  static var codec: FlutterStandardMessageCodec { NativeDivExpressionsResolverCodec.shared }
  /// Sets up an instance of `NativeDivExpressionsResolver` to handle messages through the `binaryMessenger`.
  static func setUp(binaryMessenger: FlutterBinaryMessenger, api: NativeDivExpressionsResolver?) {
    let resolveChannel = FlutterBasicMessageChannel(name: "div_expressions_resolver.NativeDivExpressionsResolver.resolve", binaryMessenger: binaryMessenger, codec: codec)
    if let api = api {
      resolveChannel.setMessageHandler { message, reply in
        let args = message as! [Any?]
        let expressionArg = args[0] as! String
        let contextArg = args[1] as! [String: Any]
        api.resolve(expression: expressionArg, context: contextArg) { result in
          switch result {
            case .success(let res):
              reply(wrapResult(res))
            case .failure(let error):
              reply(wrapError(error))
          }
        }
      }
    } else {
      resolveChannel.setMessageHandler(nil)
    }
    let clearVariablesChannel = FlutterBasicMessageChannel(name: "div_expressions_resolver.NativeDivExpressionsResolver.clearVariables", binaryMessenger: binaryMessenger, codec: codec)
    if let api = api {
      clearVariablesChannel.setMessageHandler { _, reply in
        api.clearVariables() { result in
          switch result {
            case .success:
              reply(wrapResult(nil))
            case .failure(let error):
              reply(wrapError(error))
          }
        }
      }
    } else {
      clearVariablesChannel.setMessageHandler(nil)
    }
  }
}
