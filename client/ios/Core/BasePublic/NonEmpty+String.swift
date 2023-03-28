// Copyright 2019 Yandex LLC. All rights reserved.

public typealias NonEmptyString = NonEmpty<String>

extension NonEmpty where C == String {
  public init(_ head: Character) {
    self.init(head, "")
  }

  public func lowercased() -> NonEmptyString {
    let lowercasedHead = String(head).lowercased()
    return NonEmpty(lowercasedHead.first!, lowercasedHead.dropFirst() + tail.lowercased())
  }

  public func uppercased() -> NonEmptyString {
    let uppercasedHead = String(head).uppercased()
    return NonEmpty(uppercasedHead.first!, uppercasedHead.dropFirst() + tail.uppercased())
  }

  @inlinable
  public init?<S: LosslessStringConvertible>(_ value: S) {
    let string = String(value)
    guard let head = string.first else {
      return nil
    }
    self.init(head, String(string.dropFirst()))
  }

  public var string: String {
    String(head) + tail
  }
}

extension NonEmpty: ExpressibleByStringLiteral,
  ExpressibleByExtendedGraphemeClusterLiteral,
  ExpressibleByUnicodeScalarLiteral
  where C == String {
  public init(stringLiteral value: StaticString) {
    let string = value.withUTF8Buffer {
      String(decoding: $0, as: UTF8.self)
    }
    self.init(string)!
  }

  public init(extendedGraphemeClusterLiteral value: Character) {
    self.init(String(value))!
  }

  public init(unicodeScalarLiteral value: Unicode.Scalar) {
    self.init(String(value))!
  }
}
