// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public protocol DivVariableUpdater {
  func update(cardId: DivCardID, name: DivVariableName, value: String)
}
