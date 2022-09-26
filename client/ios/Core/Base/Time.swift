// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public typealias Milliseconds = Int64
public typealias Nanoseconds = Int64
public typealias UNanoseconds = UInt64

extension TimeInterval {
  public var milliseconds: Milliseconds {
    let ms = self * 1000
    return Milliseconds(ms.rounded())
  }

  public var nanoseconds: Nanoseconds {
    let nsecs = self * 1_000_000_000
    let bottomBounded = max(nsecs, 0)
    let topBounded = min(bottomBounded, TimeInterval(Nanoseconds.max))
    return Nanoseconds(topBounded)
  }

  public static func from(nanoseconds: Nanoseconds) -> Self {
    TimeInterval(nanoseconds) / TimeInterval(NSEC_PER_SEC)
  }
}
