import CommonCorePublic
import LayoutKitInterface

public class TabPageViewModel {
  public let block: Block
  public let path: UIElementPath

  fileprivate init(block: Block, path: UIElementPath) {
    self.block = block
    self.path = path
  }
}

extension Block {
  public func makeTabPage(with path: UIElementPath) -> TabPageViewModel {
    TabPageViewModel(block: self, path: path)
  }
}

extension TabPageViewModel: Equatable {
  public static func ==(lhs: TabPageViewModel, rhs: TabPageViewModel) -> Bool {
    if lhs === rhs {
      return true
    }

    return lhs.block == rhs.block &&
      lhs.path == rhs.path
  }
}

public func ===(lhs: [TabPageViewModel], rhs: [TabPageViewModel]) -> Bool {
  guard lhs.count == rhs.count else { return false }

  for pair in zip(lhs, rhs) {
    guard pair.0 === pair.1 else {
      return false
    }
  }

  return true
}

public func !==(lhs: [TabPageViewModel], rhs: [TabPageViewModel]) -> Bool {
  !(lhs === rhs)
}

extension TabPageViewModel: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    block.getImageHolders()
  }
}

extension TabPageViewModel: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    {
      Path: \(path)
    \(block.debugDescription.indented())
    }
    """
  }
}
