import Foundation

import CommonCorePublic

public enum TabError: NonEmptyString, BlockError {
  case missingChildren
  case unsupportedFooter
  case conflictingPagesAndTabsCount

  public var errorMessage: NonEmptyString { rawValue }
}
