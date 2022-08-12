// Copyright 2018 Yandex LLC. All rights reserved.

public enum HTTPCode: CaseIterable {
  case ok
  case noContent
  case forbidden
  case notFound
  case conflict
  case unknown

  public init(value: Int) {
    switch value {
    case 200:
      self = .ok
    case 204:
      self = .noContent
    case 403:
      self = .forbidden
    case 404:
      self = .notFound
    case 409:
      self = .conflict
    default:
      self = .unknown
    }
  }
}
