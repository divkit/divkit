// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public protocol LocalImageProviding {
  func localImage(for url: URL) -> ImageHolder?
}
