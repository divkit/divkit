import Foundation
import VGSL

extension [String: Function] {
  mutating func addDecimalFormatFunctions() {
    self["decimalFormat"] = OverloadedFunction(functions: [
      _decimalFormatInteger,
      _decimalFormatDouble,
      _decimalFormatIntegerWithLocale,
      _decimalFormatDoubleWithLocale,
    ])
  }
}

private let _decimalFormatInteger = FunctionBinary<Int, String, String> { num, template in
  try defaultFormat(num, template)
}

private let _decimalFormatDouble = FunctionBinary<Double, String, String> { num, template in
  try defaultFormat(num, template)
}

private let _decimalFormatIntegerWithLocale = FunctionTernary<
  Int,
  String,
  String,
  String
> { num, template, locale in
  try defaultFormat(num, template, locale)
}

private let _decimalFormatDoubleWithLocale = FunctionTernary<
  Double,
  String,
  String,
  String
> { num, template, locale in
  try defaultFormat(num, template, locale)
}

private func defaultFormat(
  _ num: FormatNum,
  _ template: String,
  _ locale: String? = nil
) throws -> String {
  let validator = DecimalFormatTemplateValidator()

  let formatter = NumberFormatter()
  formatter.positiveFormat = template
  formatter.negativeFormat = template
  locale.map {
    formatter.locale = Locale(identifier: $0)
  }

  guard validator.validate(template),
        let result = formatter.string(from: num.toNSNumber()) else {
    throw ExpressionError("Incorrect format pattern.")
  }

  return result
}

fileprivate protocol FormatNum {
  func toNSNumber() -> NSNumber
}

extension Int: FormatNum {
  func toNSNumber() -> NSNumber {
    NSNumber(value: self)
  }
}

extension Double: FormatNum {
  func toNSNumber() -> NSNumber {
    NSNumber(value: self)
  }
}

fileprivate struct DecimalFormatTemplateValidator {
  private let checks: [DecimalFormatTemplateCheck]

  init(checks: [DecimalFormatTemplateCheck]? = nil) {
    self.checks = checks ?? DecimalFormatTemplateCheck.allCases
  }

  func validate(_ template: String) -> Bool {
    checks.allSatisfy { $0.check(template) }
  }
}

fileprivate enum DecimalFormatTemplateCheck: CaseIterable {
  case notEmpty
  case correctSymbols
  case singleDecimalPoint
  case singleDigitsSeparator
  case digitsSeparatorBeforeDecimalPoint
  case minOneSymbolBetweenDigitsSeparatorAndDecimalPoint

  typealias TemplateCheck = (String) -> Bool

  var check: TemplateCheck {
    switch self {
    case .notEmpty:
      { !$0.isEmpty }
    case .correctSymbols:
      { $0.unicodeScalars.allSatisfy { CharacterSet(charactersIn: "0#,.").contains($0) }}
    case .singleDecimalPoint:
      { $0.filter { $0 == "." }.count < 2 }
    case .singleDigitsSeparator:
      { $0.filter { $0 == "," }.count < 2 }
    case .digitsSeparatorBeforeDecimalPoint:
      { $0.range(of: #"^(?!.*\..*,).*$"#, options: .regularExpression) != nil }
    case .minOneSymbolBetweenDigitsSeparatorAndDecimalPoint:
      { $0.range(of: #"^(?!.*,\.).*$"#, options: .regularExpression) != nil }
    }
  }
}
