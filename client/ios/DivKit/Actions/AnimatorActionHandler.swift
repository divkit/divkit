import Foundation

final class AnimatorActionHandler {
  private let animatorController: DivAnimatorController

  init(animatorController: DivAnimatorController) {
    self.animatorController = animatorController
  }

  func handle(_ action: DivActionAnimatorStart, context: DivActionHandlingContext) {
    let resolver = context.expressionResolver
    let startValue = action.startValue?.resolveVariableValue(resolver)
    let endValue = action.endValue?.resolveVariableValue(resolver)
    let duration = action.resolveDuration(resolver)
    let startDelay = action.resolveStartDelay(resolver)
    let repeatCount = action.repeatCount?.resolve(resolver)
    let interpolator = action.resolveInterpolator(resolver)?.progressInterpolator
    let direction = action.resolveDirection(resolver)?.direction
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
