// Copyright 2019 Yandex LLC. All rights reserved.

public struct CacheRecord: Codable, Equatable {
  public let key: String
  public let size: Int

  public init(key: String, size: Int) {
    self.key = key
    self.size = size
  }
}
