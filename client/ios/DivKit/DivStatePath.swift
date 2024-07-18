import LayoutKit
import VGSL

public enum DivDataStateIDTag {}
public typealias DivDataStateID = Tagged<DivDataStateIDTag, Int>
public enum DivStateIDTag {}
public typealias DivStateID = Tagged<DivStateIDTag, String>
public enum DivStatePathTag {}
public typealias DivStatePath = Tagged<DivStatePathTag, UIElementPath>
public enum DivBlockPathTag {}
public typealias DivBlockPath = Tagged<DivBlockPathTag, UIElementPath>

extension Tagged where Tag == DivStatePathTag, RawValue == UIElementPath {
  public static func makeDivStatePath(from string: String) -> Self? {
    DivStatePath(rawValue: UIElementPath.parse(string))
  }

  public var stateId: DivDataStateID? {
    Int(rawValue.root).map(DivDataStateID.init(rawValue:))
  }

  public var stateBlockPath: DivStatePath {
    if let parentElement = rawValue.parent {
      return DivStatePath(rawValue: parentElement)
    }
    return DivData.rootPath
  }

  public func split() -> (parentPath: Self, stateId: DivStateID)? {
    guard let parent = rawValue.parent else { return nil }
    return (
      DivStatePath(rawValue: parent),
      DivStateID(rawValue: String(rawValue.leaf))
    )
  }

  public static func +(parent: DivStatePath, child: DivStateID) -> DivStatePath {
    DivStatePath(rawValue: parent.rawValue + child.rawValue)
  }

  public static func +(parent: DivStatePath, blockId: String) -> DivBlockPath {
    DivBlockPath(rawValue: parent.rawValue + blockId)
  }
}

extension Tagged where Tag == DivDataStateIDTag, RawValue == Int {
  public func asPath() -> DivStatePath {
    DivStatePath(rawValue: UIElementPath("\(rawValue)"))
  }
}

extension DivStatePath? {
  public static func +(parent: Self, child: String) -> DivStatePath {
    if let parent {
      parent + DivStateID(rawValue: child)
    } else {
      DivStatePath(rawValue: UIElementPath(child))
    }
  }
}

extension Tagged where Tag == DivBlockPathTag, RawValue == UIElementPath {
  public var blockId: String {
    rawValue.leaf
  }

  public var statePath: DivStatePath? {
    guard let parent = rawValue.parent else {
      return nil
    }
    return DivStatePath(rawValue: parent)
  }
}
