// Copyright 2017 Yandex LLC. All rights reserved.

public final class CompletionAccumulator: CompletionAccumulating {
  private let incrementAndReturnCompletedCounter: () -> Int
  private let getCompletedCounter: () -> Int
  private let getGeneratedCounter: () -> Int
  private let incrementGeneratedCounter: () -> Void
  private let addCumulativeCompletion: (@escaping Completion) -> Void
  private let performCumulativeCompletions: () -> Void

  public init() {
    var completedCounter = 0
    incrementAndReturnCompletedCounter = {
      completedCounter += 1
      return completedCounter
    }

    getCompletedCounter = { completedCounter }

    var generatedCounter = 0
    getGeneratedCounter = { generatedCounter }
    incrementGeneratedCounter = { generatedCounter += 1 }

    var cumulativeCompletions: [Completion] = []
    addCumulativeCompletion = {
      cumulativeCompletions.append($0)
    }

    performCumulativeCompletions = {
      cumulativeCompletions.forEach { $0() }
      cumulativeCompletions.removeAll()
    }
  }

  public func getPartialCompletion() -> Completion {
    incrementGeneratedCounter()
    var performed = false
    return { [incrementAndReturnCompletedCounter, getGeneratedCounter, performCumulativeCompletions] in
      guard !performed else {
        return
      }

      performed = true

      if incrementAndReturnCompletedCounter() == getGeneratedCounter() {
        performCumulativeCompletions()
      }
    }
  }

  public func whenAllPartialCompletionsCalled(perform cumulativeCompletion: @escaping Completion) {
    addCumulativeCompletion(cumulativeCompletion)
    if getCompletedCounter() == getGeneratedCounter() {
      performCumulativeCompletions()
    }
  }
}
