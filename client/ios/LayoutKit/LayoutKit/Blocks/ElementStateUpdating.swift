// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public protocol ElementStateUpdating {
  func updated(withStates states: BlocksState) throws -> Self
}

public protocol ElementStateUpdatingDefaultImpl: Block {}

extension ElementStateUpdatingDefaultImpl {
  public func updated(withStates _: BlocksState) -> Self { self }
}
