// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

import BaseUI
import CommonCore
import LayoutKit

public protocol DivExtensionHandler: AccessibilityContaining {
  var id: String { get }

  func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block

  func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block
}

extension DivExtensionHandler {
  public func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }
}
