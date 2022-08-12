// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public protocol DivStateUpdater: AnyObject {
  func set(
    path: DivStatePath,
    cardId: DivCardID,
    lifetime: DivStateLifetime
  )
}
