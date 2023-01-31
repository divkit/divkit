import Foundation

enum StringFunctions: String, CaseIterable {
  enum Error {
    case cast
    case indexesCast
    case indexesOrder(String)
    case indexesValue(String)
    case encodeEmpty
    case decodeEmpty

    private var description: String {
      switch self {
      case .cast:
        return "Argument couldn't be casted to String"
      case .indexesCast:
        return "Indexes couldn't be casted to Integer"
      case let .indexesOrder(expression):
        return "Failed to evaluate [\(expression)]. Indexes should be in ascending order."
      case let .indexesValue(expression):
        return "Failed to evaluate [\(expression)]. Indexes are out of bounds."
      case .encodeEmpty:
        return "String is empty after encoding."
      case .decodeEmpty:
        return "String is empty after decoding."
      }
    }

    var message: AnyCalcExpression.Error {
      AnyCalcExpression.Error.message(description)
    }
  }

  case len
  case contains
  case substring
  case replaceAll
  case index
  case lastIndex
  case trim
  case trimLeft
  case trimRight
  case toUpperCase
  case toLowerCase
  case encodeUri
  case decodeUri
  case padStart
  case padEnd

  var declaration: [AnyCalcExpression.Symbol: AnyCalcExpression.SymbolEvaluator] {
    [.function(rawValue, arity: function.arity): function.symbolEvaluator]
  }

  var function: Function {
    switch self {
    case .len:
      return FunctionUnary(impl: _len)
    case .contains:
      return FunctionBinary(impl: _contains)
    case .substring:
      return FunctionTernary(impl: _substring)
    case .replaceAll:
      return FunctionTernary(impl: _replaceAll)
    case .index:
      return FunctionBinary(impl: _index)
    case .lastIndex:
      return FunctionBinary(impl: _lastIndex)
    case .trim:
      return FunctionUnary(impl: _trim)
    case .trimLeft:
      return FunctionUnary(impl: _trimLeft)
    case .trimRight:
      return FunctionUnary(impl: _trimRight)
    case .toUpperCase:
      return FunctionUnary(impl: _toUpperCase)
    case .toLowerCase:
      return FunctionUnary(impl: _toLowerCase)
    case .encodeUri:
      return FunctionUnary(impl: _encodeUri)
    case .decodeUri:
      return FunctionUnary(impl: _decodeUri)
    case .padStart:
      return OverloadedFunction(
        functions: [
          FunctionTernary(impl: _padStart),
          FunctionTernary(impl: _padStartInt),
        ]
      )
    case .padEnd:
      return OverloadedFunction(
        functions: [
          FunctionTernary(impl: _padEnd),
          FunctionTernary(impl: _padEndInt),
        ]
      )
    }
  }
}

private let dontNeedEncoding = CharacterSet(charactersIn: "a"..."z")
  .union(CharacterSet(charactersIn: "A"..."Z"))
  .union(CharacterSet(charactersIn: "0"..."9"))
  .union(CharacterSet(charactersIn: "-_.*!~'()"))

private func _len(value: String) -> Int {
  value.count
}

private func _contains(first: String, second: String) -> Bool {
  guard !second.isEmpty else { return true }
  return first.range(of: second) != nil
}

private func _substring(first: String, second: Int, third: Int) throws -> String {
  guard second <= third else {
    throw StringFunctions.Error.indexesOrder("substring('\(first)', \(second), \(third))").message
  }

  guard second >= 0, third <= first.count
  else {
    throw StringFunctions.Error.indexesValue("substring('\(first)', \(second), \(third))").message
  }
  return String(first[first.rangeOfCharsIn(second..<third)])
}

private func _replaceAll(first: String, second: String, third: String) -> String {
  first.replacingOccurrences(of: second, with: third)
}

private func _index(first: String, second: String) -> Int {
  guard !second.isEmpty else { return 0 }
  guard let range = first.range(of: second) else { return -1 }
  return first.distance(to: range.lowerBound)
}

private func _lastIndex(first: String, second: String) -> Int {
  guard let range = first.range(of: second, options: .backwards)
  else { return -1 }
  return first.distance(to: range.lowerBound)
}

private func _trim(value: String) -> String {
  value.trimmed
}

private func _trimLeft(value: String) -> String {
  for index in value.indices {
    if !value[index].isWhitespace {
      return String(value[index...])
    }
  }
  return ""
}

private func _trimRight(value: String) -> String {
  for index in value.indices.reversed() {
    if !value[index].isWhitespace {
      return String(value[...index])
    }
  }
  return ""
}

private func _toUpperCase(value: String) -> String {
  value.uppercased()
}

private func _toLowerCase(value: String) -> String {
  value.lowercased()
}

private func _encodeUri(value: String) throws -> String {
  guard let encodedValue = value.addingPercentEncoding(withAllowedCharacters: dontNeedEncoding)
  else {
    throw StringFunctions.Error.encodeEmpty.message
  }
  return encodedValue
}

private func _decodeUri(value: String) throws -> String {
  guard let decodedValue = value.removingPercentEncoding else {
    throw StringFunctions.Error.decodeEmpty.message
  }
  return decodedValue
}

private func _padStart(value: String, len: Int, pad: String) throws -> String {
  let prefix = calcPad(value: value, len: len, pad: pad)
  return prefix + value
}

private func _padStartInt(value: Int, len: Int, pad: String) throws -> String {
  try _padStart(value: String(value), len: len, pad: pad)
}

private func _padEnd(value: String, len: Int, pad: String) throws -> String {
  let suffix = calcPad(value: value, len: len, pad: pad)
  return value + suffix
}

private func _padEndInt(value: Int, len: Int, pad: String) throws -> String {
  try _padEnd(value: String(value), len: len, pad: pad)
}

private func calcPad(value: String, len: Int, pad: String) -> String {
  var part = ""
  guard pad.count > 0 else {
    DivKitLogger.warning("String for padding is empty.")
    return part
  }
  while part.count + value.count < len {
    part += pad
  }
  if part.count > 0 && part.count + value.count > len {
    part = String(part.prefix(len - value.count))
  }
  return part
}

private func cast(_ value: Any) -> String? {
  value as? String
}

private func castToInt(_ value: Any) -> Int? {
  value as? Int
}

extension String {
  fileprivate func distance(to index: Index) -> Int {
    distance(from: startIndex, to: index)
  }
}
