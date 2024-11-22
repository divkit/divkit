import LayoutKit

extension DivSwitch: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let resolver = context.expressionResolver
    let switchVariable = context.makeBinding(
      variableName: isOnVariable,
      defaultValue: false
    )

    return try applyBaseProperties(
      to: {
        SwitchBlock(
          widthTrait: resolveWidthTrait(context),
          heightTrait: resolveHeightTrait(context),
          on: switchVariable,
          enabled: resolveIsEnabled(resolver),
          action: nil,
          onTintColor: resolveOnColor(resolver)
        )
      },
      context: context,
      actionsHolder: nil,
      customAccessibilityParams: CustomAccessibilityParams(
        defaultTraits: .switchButton
      ) { [unowned self] in
        accessibility?.resolveDescription(resolver)
      }
    )
  }
}
