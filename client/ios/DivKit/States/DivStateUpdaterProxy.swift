// Copyright 2022 Yandex LLC. All rights reserved.

public final class DivStateUpdaterProxy: DivStateUpdater {
  public weak var subject: DivStateUpdater?

  public init() {}

  public func set(path: DivStatePath, cardId: DivCardID, lifetime: DivStateLifetime) {
    subject?.set(path: path, cardId: cardId, lifetime: lifetime)
  }
}
