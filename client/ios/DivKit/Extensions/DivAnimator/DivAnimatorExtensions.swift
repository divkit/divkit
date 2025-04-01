import VGSL

extension DivAnimator {
  func resolve(_ context: DivBlockModelingContext) -> Animator? {
    let resolver = context.expressionResolver

    guard let duration = base.resolveDuration(resolver) else {
      incorrectValue(field: "duration", id: base.id)
      return nil
    }
    guard let repeatCount = base.repeatCount.resolve(resolver) else {
      incorrectValue(field: "repeatCount", id: base.id)
      return nil
    }

    let interpolator = base.resolveInterpolator(resolver).progressInterpolator
    let startDelay = base.resolveStartDelay(resolver)
    let endAction: Action = {
      base.endActions?
        .uiActions(context: context)
        .compactMap(\.payload.divActionParams)
        .forEach { [weak actionHandler = context.actionHandler] in
          actionHandler?.handle(params: $0, sender: nil)
        }
    }
    let cancelAction: Action = {
      base.cancelActions?
        .uiActions(context: context)
        .compactMap(\.payload.divActionParams)
        .forEach { [weak actionHandler = context.actionHandler] in
          actionHandler?.handle(params: $0, sender: nil)
        }
    }
    let variableName = DivVariableName(rawValue: base.variableName)
    let direction = base.resolveDirection(resolver).direction

    switch self {
    case let .divColorAnimator(colorAnimator):
      guard let startValue = colorAnimator.resolveStartValue(resolver) ?? context.variablesStorage
        .getVariableValue(path: context.path, name: variableName) else {
        incorrectValue(field: "startValue", id: base.id)
        return nil
      }
      guard let endValue = colorAnimator.resolveEndValue(resolver) else {
        incorrectValue(field: "endValue", id: base.id)
        return nil
      }
      let configuration = ValueAnimator<ColorInterpolator>.Configuration(
        startValue: startValue,
        endValue: endValue,
        duration: duration,
        startDelay: startDelay,
        direction: direction,
        progressInterpolator: interpolator,
        repeatCount: repeatCount
      )
      return ValueAnimator(
        id: colorAnimator.id,
        valueInterpolator: ColorInterpolator(),
        configuration: configuration,
        cancelAction: cancelAction,
        endAction: endAction
      ) { [weak variablesStorage = context.variablesStorage, parentPath = context.path] in
        variablesStorage?.update(
          path: parentPath,
          name: variableName,
          value: .color($0)
        )
      }
    case let .divNumberAnimator(numberAnimator):
      guard let startValue = numberAnimator.resolveStartValue(resolver) ?? context.variablesStorage
        .getVariableValue(path: context.path, name: variableName) else {
        incorrectValue(field: "startValue", id: base.id)
        return nil
      }
      guard let endValue = numberAnimator.resolveEndValue(resolver) else {
        incorrectValue(field: "endValue", id: base.id)
        return nil
      }
      let configuration = ValueAnimator<DoubleInterpolator>.Configuration(
        startValue: startValue,
        endValue: endValue,
        duration: duration,
        startDelay: startDelay,
        direction: direction,
        progressInterpolator: interpolator,
        repeatCount: repeatCount
      )
      return ValueAnimator(
        id: numberAnimator.id,
        valueInterpolator: DoubleInterpolator(),
        configuration: configuration,
        cancelAction: cancelAction,
        endAction: endAction
      ) { [weak variablesStorage = context.variablesStorage, parentPath = context.path] in
        variablesStorage?.update(
          path: parentPath,
          name: variableName,
          value: $0.description
        )
      }
    }
  }
}

extension DivCount {
  func resolve(_ expressionResolver: ExpressionResolver) -> RepeatCount? {
    switch self {
    case let .divFixedCount(count):
      count.resolveValue(expressionResolver).flatMap { .fixed($0) }
    case .divInfinityCount:
      .infinity
    }
  }
}

extension DivAnimationInterpolator {
  var progressInterpolator: ProgressInterpolator {
    switch self {
    case .linear:
      LinearInterpolator()
    case .easeInOut:
      EaseInOutInterpolator()
    case .ease:
      EaseInterpolator()
    case .easeIn:
      EaseInInterpolator()
    case .easeOut:
      EaseOutInterpolator()
    case .spring:
      SpringInterpolator()
    }
  }
}

private func incorrectValue(field: String, id: String) {
  DivKitLogger.error("Incorrect \(field) value for animator \(id)")
}

extension DivAnimator {
  var base: DivAnimatorBase {
    switch self {
    case let .divColorAnimator(colorAnimator):
      colorAnimator
    case let .divNumberAnimator(numberAnimator):
      numberAnimator
    }
  }
}

extension DivAnimationDirection {
  var direction: AnimationDirection {
    switch self {
    case .alternate:
      .alternate
    case .normal:
      .normal
    case .alternateReverse:
      .alternateReverse
    case .reverse:
      .reverse
    }
  }
}
