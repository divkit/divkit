// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation

import CommonCore
import DivKit
import LayoutKit

public final class BlurExtensionHandler: DivExtensionHandler {
  public let id = extensionID

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    block.addingDecorations(blurEffect: div.blurStyle(with: context.expressionResolver))
  }
}

extension DivBase {
  func blurStyle(
    with expressionResolver: ExpressionResolver
  ) -> BlurEffect? {
    guard
      let blurThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let string = blurThemeExtension.params?[styleKey] as? String
    else {
      return nil
    }

    guard let expressionLink = ExpressionLink<InternalBlurEffect>(rawValue: string, validator: nil)
    else {
      return InternalBlurEffect(rawValue: string).flatMap(BlurEffect.init)
    }

    return expressionResolver.resolveStringBasedValue(
      expression: .link(expressionLink),
      initializer: InternalBlurEffect.init(rawValue:)
    ).flatMap(BlurEffect.init)
  }
}

private enum InternalBlurEffect: String {
  case light
  case dark
  case disabled
}

extension BlurEffect {
  fileprivate init?(internalBlurEffect: InternalBlurEffect) {
    switch internalBlurEffect {
    case .light:
      self = .light
    case .dark:
      self = .dark
    case .disabled:
      return nil
    }
  }
}

private let styleKey = "style"
private let extensionID = "blur"
