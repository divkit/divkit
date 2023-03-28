// Copyright 2017 Yandex LLC. All rights reserved.

public protocol CompletionAccumulating {
  typealias Completion = () -> Void
  func getPartialCompletion() -> Completion
  func whenAllPartialCompletionsCalled(perform: @escaping Completion)
}
