import Foundation

final class AnimatorActionHandler {
  private let animatorController: DivAnimatorController
  private let reporter: DivReporter

  init(animatorController: DivAnimatorController, reporter: DivReporter) {
    self.animatorController = animatorController
    self.reporter = reporter
  }

  func handle(_ action: DivActionAnimatorStart, context: DivActionHandlingContext) {
    let resolver = context.expressionResolver
    let definition = animatorController.definition(
      path: context.path,
      id: action.animatorId
    )

    let startValue = action.startValue?.resolveVariableValue(resolver)
      ?? definition?.resolveStartValue(
        resolver: resolver,
        variablesStorage: context.variablesStorage,
        path: context.path
      )
    let endValue = action.endValue?.resolveVariableValue(resolver)
      ?? definition?.resolveEndValue(resolver)
    let duration = action.resolveDuration(resolver)
      ?? definition?.base.resolveDuration(resolver)
    let startDelay = action.resolveStartDelay(resolver)
      ?? definition?.base.resolveStartDelay(resolver)
    let repeatCount = action.repeatCount?.resolve(resolver)
      ?? definition?.base.repeatCount.resolve(resolver)
    let interpolator = action.resolveInterpolator(resolver)?.progressInterpolator
      ?? definition?.base.resolveInterpolator(resolver).progressInterpolator
    let direction = action.resolveDirection(resolver)?.direction
      ?? definition?.base.resolveDirection(resolver).direction

    let result = animatorController.startAnimator(
      path: context.path,
      id: action.animatorId,
      startValue: startValue,
      endValue: endValue,
      duration: duration,
      startDelay: startDelay,
      direction: direction,
      progressInterpolator: interpolator,
      repeatCount: repeatCount
    )

    reportErrorIfNeeded(
      result: result,
      animatorId: action.animatorId,
      context: context
    )
  }

  func handle(_ action: DivActionAnimatorStop, context: DivActionHandlingContext) {
    let result = animatorController.stopAnimator(
      path: context.path,
      id: action.animatorId
    )

    reportErrorIfNeeded(
      result: result,
      animatorId: action.animatorId,
      context: context
    )
  }

  private func scopeSuffix(_ context: DivActionHandlingContext) -> String {
    context.scopePath == nil ? "" : " in scope"
  }

  private func reportErrorIfNeeded(
    result: DivAnimatorController.ActionResult,
    animatorId: String,
    context: DivActionHandlingContext
  ) {
    guard let message: String = switch result {
    case .success:
      nil
    case .notFound:
      "Animator with id '\(animatorId)' not found\(scopeSuffix(context))"
    case .ambiguousId:
      "Animator with id '\(animatorId)' is ambiguous\(scopeSuffix(context))"
    } else { return }

    reporter.reportError(
      cardId: context.cardId,
      error: DivUnknownError(message: message, path: context.sourcePath)
    )
  }
}
