import Foundation

extension [String: Function] {
  mutating func addStringFunctions() {
    addFunction("contains", _contains)
    addFunction("decodeUri", _decodeUri)
    addFunction("encodeUri", _encodeUri)
    addFunction("index", _index)
    addFunction("len", _len)
    addFunction("lastIndex", _lastIndex)
    addFunction("padEnd", _padEnd)
    addFunction("padStart", _padStart)
    addFunction("replaceAll", _replaceAll)
    addFunction("substring", _substring)
    addFunction("testRegex", _testRegex)
    addFunction("trim", _trim)
    addFunction("trimLeft", _trimLeft)
    addFunction("trimRight", _trimRight)
    addFunction("toUpperCase", _toUpperCase)
    addFunction("toLowerCase", _toLowerCase)
  }
}

private let dontNeedEncoding = CharacterSet(charactersIn: "a"..."z")
  .union(CharacterSet(charactersIn: "A"..."Z"))
  .union(CharacterSet(charactersIn: "0"..."9"))
  .union(CharacterSet(charactersIn: "-_.*!~'()"))

private var _len = FunctionUnary<String, Int> {
  $0.count
}

private var _contains = FunctionBinary<String, String, Bool> {
  $1.isEmpty || $0.range(of: $1) != nil
}

private var _substring = FunctionTernary<String, Int, Int, String> {
  guard $1 <= $2 else {
    throw ExpressionError("Indexes should be in ascending order.")
  }
  guard $1 >= 0, $2 <= $0.count else {
    throw ExpressionError("Indexes are out of bounds.")
  }
  return String($0[$0.rangeOfCharsIn($1..<$2)])
}

private var _replaceAll = FunctionTernary<String, String, String, String> {
  $0.replacingOccurrences(of: $1, with: $2)
}

private var _index = FunctionBinary<String, String, Int> {
  if $1.isEmpty {
    return 0
  }
  guard let range = $0.range(of: $1) else {
    return -1
  }
  return $0.distance(to: range.lowerBound)
}

private var _lastIndex = FunctionBinary<String, String, Int> {
  guard let range = $0.range(of: $1, options: .backwards) else {
    return -1
  }
  return $0.distance(to: range.lowerBound)
}

private var _trim = FunctionUnary<String, String> {
  $0.trimmed
}

private var _trimLeft = FunctionUnary<String, String> { value in
  for index in value.indices {
    if !value[index].isWhitespace {
      return String(value[index...])
    }
  }
  return ""
}

private var _trimRight = FunctionUnary<String, String> { value in
  for index in value.indices.reversed() {
    if !value[index].isWhitespace {
      return String(value[...index])
    }
  }
  return ""
}

private var _toUpperCase = FunctionUnary<String, String> {
  $0.uppercased()
}

private var _toLowerCase = FunctionUnary<String, String> {
  $0.lowercased()
}

private var _encodeUri = FunctionUnary<String, String> {
  guard let value = $0.addingPercentEncoding(withAllowedCharacters: dontNeedEncoding) else {
    throw ExpressionError("String is empty after encoding.")
  }
  return value
}

private var _decodeUri = FunctionUnary<String, String> {
  guard let value = $0.removingPercentEncoding else {
    throw ExpressionError("String is empty after decoding.")
  }
  return value
}

private var _padStart = OverloadedFunction(functions: [
  FunctionTernary<String, Int, String, String> {
    padStart(value: $0, len: $1, pad: $2)
  },
  FunctionTernary<Int, Int, String, String> {
    padStart(value: String($0), len: $1, pad: $2)
  },
])

private var _padEnd = OverloadedFunction(functions: [
  FunctionTernary<String, Int, String, String> {
    padEnd(value: $0, len: $1, pad: $2)
  },
  FunctionTernary<Int, Int, String, String> {
    padEnd(value: String($0), len: $1, pad: $2)
  },
])

private func padStart(value: String, len: Int, pad: String) -> String {
  let prefix = calcPad(value: value, len: len, pad: pad)
  return prefix + value
}

private func padEnd(value: String, len: Int, pad: String) -> String {
  let suffix = calcPad(value: value, len: len, pad: pad)
  return value + suffix
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
  if part.count > 0, part.count + value.count > len {
    part = String(part.prefix(len - value.count))
  }
  return part
}

private var _testRegex = FunctionBinary<String, String, Bool> { text, regex in
  do {
    let regex = try NSRegularExpression(pattern: regex)
    let range = NSRange(text.startIndex..., in: text)
    return regex.firstMatch(in: text, range: range) != nil
  } catch {
    throw ExpressionError("Invalid regular expression.")
  }
}

extension String {
  fileprivate func distance(to index: Index) -> Int {
    distance(from: startIndex, to: index)
  }
}
