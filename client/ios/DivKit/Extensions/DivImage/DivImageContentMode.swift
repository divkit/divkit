import VGSL

protocol DivImageContentMode {
  func resolveScale(_ expressionResolver: ExpressionResolver) -> DivImageScale

  func resolveContentAlignmentHorizontal(
    _ expressionResolver: ExpressionResolver
  ) -> DivAlignmentHorizontal

  func resolveContentAlignmentVertical(
    _ expressionResolver: ExpressionResolver
  ) -> DivAlignmentVertical
}

extension DivImageContentMode {
  func resolveContentMode(_ context: DivBlockModelingContext) -> ImageContentMode {
    let expressionResolver = context.expressionResolver
    return ImageContentMode(
      scale: resolveScale(expressionResolver).contentModeScale,
      verticalAlignment: resolveContentAlignmentVertical(expressionResolver).contentModeAlignment,
      horizontalAlignment: resolveContentAlignmentHorizontal(expressionResolver)
        .makeContentModeAlignment(uiLayoutDirection: context.layoutDirection)
    )
  }
}

extension DivImageScale {
  fileprivate var contentModeScale: ImageContentMode.Scale {
    switch self {
    case .fill:
      .aspectFill
    case .fit:
      .aspectFit
    case .stretch:
      .resize
    case .noScale:
      .noScale
    }
  }
}

extension DivAlignmentVertical {
  fileprivate var contentModeAlignment: ImageContentMode.VerticalAlignment {
    switch self {
    case .top:
      return .top
    case .center:
      return .center
    case .bottom:
      return .bottom
    case .baseline:
      DivKitLogger.warning("Baseline alignment not supported.")
      return .top
    }
  }
}

extension DivAlignmentHorizontal {
  fileprivate var contentModeAlignment: ImageContentMode.HorizontalAlignment {
    switch self {
    case .left, .start:
      .left
    case .center:
      .center
    case .right, .end:
      .right
    }
  }

  func makeContentModeAlignment(uiLayoutDirection: UserInterfaceLayoutDirection) -> ImageContentMode
    .HorizontalAlignment {
    switch self {
    case .left:
      .left
    case .center:
      .center
    case .right:
      .right
    case .start:
      uiLayoutDirection == .leftToRight ? .left : .right
    case .end:
      uiLayoutDirection == .leftToRight ? .right : .left
    }
  }
}
