import Foundation

final class AnimatorActionHandler {
  private let animatorController: DivAnimatorController

  init(animatorController: DivAnimatorController) {
    self.animatorController = animatorController
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

    animatorController.startAnimator(
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
  }

  func handle(_ action: DivActionAnimatorStop, context: DivActionHandlingContext) {
    animatorController.stopAnimator(path: context.path, id: action.animatorId)
  }
}
