// Copyright 2022 Yandex LLC. All rights reserved.

#if INTERNAL_BUILD
final class ExpressionErrorsStorage {
  private(set) var errors: [ExpressionError] = []

  func add(error: ExpressionError) {
    errors.append(error)
  }
}
#endif
