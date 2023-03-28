import CommonCorePublic

protocol DivImageContentMode {
  func resolveScale(_ expressionResolver: ExpressionResolver) -> DivImageScale
  func resolveContentAlignmentHorizontal(_ expressionResolver: ExpressionResolver)
    -> DivAlignmentHorizontal
  func resolveContentAlignmentVertical(_ expressionResolver: ExpressionResolver)
    -> DivAlignmentVertical
}

extension DivImageContentMode {
  func resolveContentMode(_ expressionResolver: ExpressionResolver) -> ImageContentMode {
    ImageContentMode(
      scale: resolveScale(expressionResolver).contentModeScale,
      verticalAlignment: resolveContentAlignmentVertical(expressionResolver).contentModeAlignment,
      horizontalAlignment: resolveContentAlignmentHorizontal(expressionResolver)
        .contentModeAlignment
    )
  }
}

extension DivImageScale {
  fileprivate var contentModeScale: ImageContentMode.Scale {
    switch self {
    case .fill:
      return .aspectFill
    case .fit:
      return .aspectFit
    case .noScale:
      return .noScale
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
    case .left:
      return .left
    case .center:
      return .center
    case .right:
      return .right
    }
  }
}
