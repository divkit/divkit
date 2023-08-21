import Foundation

import CommonCorePublic

#if os(iOS)
import UIKit
#endif

@propertyWrapper
public struct Binding<T: Equatable>: Equatable {
  #if os(iOS)
  public typealias ResponderType = UIResponder
  #else
  public typealias ResponderType = AnyObject
  #endif
  private var value: T
  private let name: String
  private let getValue: (String) -> T
  private let userInterfaceActionFactory: (String, T) -> UserInterfaceAction?

  public var wrappedValue: T {
    getValue(name)
  }

  public var projectedValue: Binding<T> {
    get { self }
    set { self = newValue }
  }

  public mutating func setValue(_ value: T, responder: ResponderType?) {
    if let responder = responder, self.value != value {
      DispatchQueue.main.async { [self] in
        self.userInterfaceActionFactory(name, value)?.perform(sendingFrom: responder)
      }
    }
    self.value = value
  }

  public init(
    name: String,
    getValue: @escaping (String) -> T,
    userInterfaceActionFactory: @escaping (String, T) -> UserInterfaceAction?
  ) {
    self.name = name
    self.value = getValue(name)
    self.getValue = getValue
    self.userInterfaceActionFactory = userInterfaceActionFactory
  }

  public static func ==(lhs: Binding<T>, rhs: Binding<T>) -> Bool {
    lhs.name == rhs.name && lhs.value == rhs.value
  }
}

extension Binding where T == String {
  public static var empty: Self {
    .init(name: "", getValue: { _ in "" }, userInterfaceActionFactory: { _, _ in nil })
  }
}
