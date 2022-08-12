// Copyright 2018 Yandex LLC. All rights reserved.

public enum DivError: Error, Equatable {
  case generic
  case noValueForLink(Link)
  case incompatibleValueType(Link, expectedType: String)
  case circularReference([String])
}
