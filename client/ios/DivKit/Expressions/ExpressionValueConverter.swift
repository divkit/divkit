import Foundation

import CommonCorePublic

enum ExpressionValueConverter {
  static func cast<T>(_ anyValue: Any) -> T? {
    if let value = anyValue as? T {
      return value
    }

    switch T.self {
    case is String.Type:
      return stringify(anyValue) as? T
    case let numericType as _Numeric.Type:
      if anyValue is Bool {
        return nil
      }
      return (anyValue as? NSNumber).map { numericType.init(truncating: $0) as! T }
    default:
      return nil
    }
  }

  static func stringify(_ value: Any) -> String {
    switch value {
    case let number as NSNumber:
      switch UnicodeScalar(UInt8(number.objCType.pointee)) {
      case "c":
        return number == 0 ? "false" : "true"
      case "d":
        let formatter = NumberFormatter()
        let double = number.doubleValue
        if double >= 1e7 || double <= -1e7 {
          formatter.numberStyle = NumberFormatter.Style.scientific
        }
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 15
        formatter.locale = Locale(identifier: "en")
        return formatter.string(from: number)!
      default:
        return "\(value)"
      }
    case let color as RGBAColor:
      return color.argbString
    case let date as Date:
      let dateFormatter = DateFormatter()
      dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
      dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
      return dateFormatter.string(from: date)
    case let array as [AnyHashable]:
      return "[\(array.map { formatValue($0) }.joined(separator: ","))]"
    case let dict as [String: AnyHashable]:
      let properties = dict
        .keys.sorted()
        .map { "\"\($0)\":\(formatValue(dict[$0] ?? "null"))" }
        .joined(separator: ",")
      return "{\(properties)}"
    default:
      return "\(value)"
    }
  }
}

func formatArgForError(_ value: Any) -> String {
  switch value {
  case is String:
    "'\(value)'".replacingOccurrences(of: "\\", with: "\\\\")
  case is [Any]:
    "<array>"
  case is [String: Any]:
    "<dict>"
  default:
    ExpressionValueConverter.stringify(value)
  }
}

func formatTypeForError(_ value: Any) -> String {
  switch value {
  case is Bool:
    "Boolean"
  case is Int:
    "Integer"
  case is Double:
    "Number"
  case is Date:
    "DateTime"
  default:
    "\(type(of: value))"
  }
}

private func formatValue(_ value: Any) -> String {
  switch value {
  case is String:
    "\"\(value)\""
  default:
    ExpressionValueConverter.stringify(value)
  }
}

private protocol _Numeric {
  init(truncating: NSNumber)
}

extension Int: _Numeric {}
extension Double: _Numeric {}
