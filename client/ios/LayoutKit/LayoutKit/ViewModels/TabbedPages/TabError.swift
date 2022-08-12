// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

import CommonCore

public enum TabError: NonEmptyString, BlockError {
  case missingChildren
  case unsupportedFooter
  case conflictingPagesAndTabsCount

  public var errorMessage: NonEmptyString { rawValue }
}
