// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import Base

public protocol LocalImageProviding {
  func localImage(forURL: URL) -> Image?
}
