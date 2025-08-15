import Foundation

public protocol PaddingProvidingBlock: Block {
  var paddings: EdgeInsets { get }
  var child: Block { get }
}

extension PaddingProvidingBlock {
  public func margins(direction: ScrollDirection) -> CGFloat {
    guard child is PaddingProvidingBlock else { return 0 }

    switch direction {
    case .horizontal:
      return paddings.horizontal.sum
    case .vertical:
      return paddings.vertical.sum
    }
  }
}
