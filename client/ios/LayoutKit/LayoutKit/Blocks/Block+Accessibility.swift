import CoreGraphics
import Foundation

import BaseUIPublic

extension Block {
  public func addingAccessibilityID(_ id: @autoclosure () -> String?) -> Block {
    #if INTERNAL_BUILD
    guard let accessibilityID = id() else {
      return self
    }
    return AccessibilityBlock(child: self, accessibilityID: accessibilityID, traits: .none)
    #else
    return self
    #endif
  }

  public func addingAccessibilityID<T: RawRepresentable>(_ id: @autoclosure () -> T?) -> Block
    where T.RawValue == String {
    #if INTERNAL_BUILD
    guard let accessibilityID = id().map(\.rawValue) else {
      return self
    }
    return AccessibilityBlock(child: self, accessibilityID: accessibilityID, traits: .none)
    #else
    return self
    #endif
  }

  public func addingAccessibilityID(
    withTraits id: @autoclosure ()
      -> (id: String?, AccessibilityElement.Traits)?
  ) -> Block {
    #if INTERNAL_BUILD
    guard let (accessibilityID, traits) = id(), let id = accessibilityID else {
      return self
    }
    return AccessibilityBlock(child: self, accessibilityID: id, traits: traits)
    #else
    return self
    #endif
  }

  public func addingAccessibilityID<T: RawRepresentable>(
    withTraits id: @autoclosure ()
      -> (id: T?, AccessibilityElement.Traits)?
  ) -> Block where T.RawValue == String {
    #if INTERNAL_BUILD
    guard let (accessibilityID, traits) = id(), let id = accessibilityID.map(\.rawValue) else {
      return self
    }
    return AccessibilityBlock(child: self, accessibilityID: id, traits: traits)
    #else
    return self
    #endif
  }
}

#if INTERNAL_BUILD
final class AccessibilityBlock: WrapperBlock {
  let accessibilityID: String
  let traits: AccessibilityElement.Traits
  let child: Block

  fileprivate init(child: Block, accessibilityID: String, traits: AccessibilityElement.Traits) {
    self.child = child
    self.accessibilityID = accessibilityID
    self.traits = traits
  }

  func laidOut(for width: CGFloat) -> Block {
    updatingChild(child.laidOut(for: width))
  }

  func laidOut(for size: CGSize) -> Block {
    updatingChild(child.laidOut(for: size))
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? AccessibilityBlock else { return false }
    return child == other.child
      && accessibilityID == other.accessibilityID
      && traits == other.traits
  }

  func makeCopy(wrapping child: Block) -> AccessibilityBlock {
    modifying(child: child)
  }

  private func updatingChild(_ newChild: Block) -> AccessibilityBlock {
    guard newChild !== child else { return self }
    return modifying(child: newChild)
  }
}

extension AccessibilityBlock {
  func modifying(
    child: Block? = nil,
    accessibilityID: String? = nil,
    traits: AccessibilityElement.Traits? = nil
  ) -> AccessibilityBlock {
    AccessibilityBlock(
      child: child ?? self.child,
      accessibilityID: accessibilityID ?? self.accessibilityID,
      traits: traits ?? self.traits
    )
  }
}
#endif
