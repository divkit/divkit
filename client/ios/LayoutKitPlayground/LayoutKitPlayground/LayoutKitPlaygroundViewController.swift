import UIKit
import VGSL
import CoreGraphics
import LayoutKit

public final class LayoutKitPlaygroundViewController: UIViewController {
  
  private var blockView: BlockView?
  private var blockProvider: (() throws -> Block)
  private var actionHandler: (() -> ())?
  private var buttonActionURL: URL?
  
  public init(
    blockProvider: @escaping () throws -> Block,
    actionHandler: (() -> ())? = nil,
    buttonActionURL: URL? = nil
  ) {
    self.blockView = nil
    self.blockProvider = blockProvider
    self.actionHandler = actionHandler
    self.buttonActionURL = buttonActionURL
    super.init(nibName: nil, bundle: nil)
  }
  
  public required init?(coder: NSCoder) {
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

private extension LayoutKitPlaygroundViewController {
  func updateBlock(block: Block) {
    blockView = block.reuse(
      blockView,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: self.view
    )
  }
  
  static func createDefaultBlock() -> Block {
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
  public func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    switch event.payload {
    case .url(let url) where url == buttonActionURL:
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
