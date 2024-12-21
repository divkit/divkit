import Foundation
import VGSL

enum ExpressionValueConverter {
  static func cast<T>(_ anyValue: Any) -> T? {
    if let value = anyValue as? T {
      return value
    }

    switch T.self {
    case is String.Type:
      return stringify(anyValue) as? T
    case is Bool.Type:
      if let numberValue = anyValue as? NSNumber {
        if numberValue == 0 {
          return false as? T
        } else if numberValue == 1 {
          return true as? T
        }
      }
      return nil
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
    case let array as DivArray:
      return "[\(array.map { formatValue($0) }.joined(separator: ","))]"
    case let dict as DivDictionary:
      let properties = dict
        .keys.sorted()
        .map { "\"\($0)\":\(formatValue(dict[$0] ?? "null"))" }
        .joined(separator: ",")
      return "{\(properties)}"
    default:
      return "\(value)"
    }
  }

  static func unescape(_ value: String, errorTracker: ExpressionErrorTracker?) -> String? {
    if !value.contains("\\") {
      return value
    }

    var value = value
    var index = value.startIndex
    let escapingValues = ["@{", "'", "\\"]
    while index < value.endIndex {
      if value[index] != "\\" {
        index = value.index(after: index)
        continue
      }
      let nextIndex = value.index(index, offsetBy: 1)
      let next = value[nextIndex...]
      if let escaped = escapingValues.first(where: { next.starts(with: $0) }) {
        let distance = value.distance(from: value.startIndex, to: index)
        value.remove(at: index)
        index = value.index(value.startIndex, offsetBy: distance + escaped.count)
      } else {
        let message = next.isEmpty ? "Error tokenizing '\(value)'." : "Incorrect string escape"
        errorTracker?(ExpressionError(message, expression: value))
        return nil
      }
    }
    return value
  }
}

extension AnyHashable {
  var isBool: Bool {
    if let number = self as? NSNumber {
      return UnicodeScalar(UInt8(number.objCType.pointee)) == "c"
    }
    return false
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

func formatTypeForError(_ type: Any.Type) -> String {
  switch type {
  case is String.Type:
    "String"
  case is Bool.Type:
    "Boolean"
  case is Int.Type:
    "Integer"
  case is Double.Type:
    "Number"
  case is RGBAColor.Type:
    "Color"
  case is Date.Type:
    "DateTime"
  case is DivArray.Type:
    "Array"
  case is DivDictionary.Type, is [AnyHashable: AnyHashable].Type:
    "Dict"
  default:
    String(describing: type)
  }
}

func formatTypeForError(_ value: Any) -> String {
  let valueType = type(of: value)
  if valueType is AnyHashable.Type, let hashableValue = value as? AnyHashable {
    switch type(of: hashableValue.base) {
    case is Double.Type, is Int.Type, is Int64.Type:
      return "Number"
    case is NSNull.Type:
      return "Null"
    default:
      return formatTypeForError(hashableValue.base)
    }
  }
  return formatTypeForError(valueType)
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
