import Foundation

import VGSL

public struct Binding<T: Equatable>: Equatable {
  private let name: String
  @Property public var value: T

  public init(
    name: String,
    value: Property<T>
  ) {
    self.name = name
    self._value = value
  }

  public static func ==(lhs: Binding<T>, rhs: Binding<T>) -> Bool {
    lhs.name == rhs.name && lhs.value == rhs.value
  }
}

extension Binding where T: AdditiveArithmetic {
  public static var zero: Binding<T> {
    Binding(name: "", value: .init(initialValue: .zero))
  }
}

extension Binding where T == String {
  public static var zero: Binding<T> {
    Binding(name: "", value: "")
  }
}
