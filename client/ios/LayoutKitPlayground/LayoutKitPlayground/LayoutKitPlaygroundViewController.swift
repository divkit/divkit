import CoreGraphics
import LayoutKit
import UIKit
import VGSL

public final class LayoutKitPlaygroundViewController: UIViewController {
  private var blockView: BlockView?
  private var blockProvider: () throws -> Block
  private var actionHandler: (() -> Void)?
  private var buttonActionURL: URL?

  public init(
    blockProvider: @escaping () throws -> Block,
    actionHandler: (() -> Void)? = nil,
    buttonActionURL: URL? = nil
  ) {
    self.blockView = nil
    self.blockProvider = blockProvider
    self.actionHandler = actionHandler
    self.buttonActionURL = buttonActionURL
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func viewDidLoad() {
    super.viewDidLoad()

    do {
      let block = try blockProvider()
      updateBlock(block: block)
    } catch {
      let block = LayoutKitPlaygroundViewController.createDefaultBlock()
      updateBlock(block: block)
      print("An error occurred while creating the block: \(error). Using fallback block.")
    }
  }

  public override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()
    blockView?.frame = view.bounds
  }
}

extension LayoutKitPlaygroundViewController {
  fileprivate func updateBlock(block: Block) {
    blockView = block.reuse(
      blockView,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: self.view
    )
  }

  fileprivate static func createDefaultBlock() -> Block {
    TextBlock(
      widthTrait: .intrinsic,
      text: "Default Block".with(typo: Typo(size: 16.0, weight: .medium))
    ).addingEdgeGaps(16.0)
      .addingDecorations(
        boundary: BoundaryTrait.clipCorner(CornerRadii(8.0)),
        border: BlockBorder(color: .gray, width: 2.0),
        backgroundColor: .lightGray
      )
  }
}

extension LayoutKitPlaygroundViewController: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from _: AnyObject) {
    switch event.payload {
    case let .url(url) where url == buttonActionURL:
      actionHandler?()
      do {
        let updatedBlock = try blockProvider()
        updateBlock(block: updatedBlock)
      } catch {
        let block = LayoutKitPlaygroundViewController.createDefaultBlock()
        updateBlock(block: block)
        print("An error occurred while creating the block: \(error). Using fallback block.")
      }
    default:
      break
    }
  }
}
