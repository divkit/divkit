// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

enum HTTPRetryAfterHeader: Equatable {
  case date(Date)
  case seconds(TimeInterval)
}
