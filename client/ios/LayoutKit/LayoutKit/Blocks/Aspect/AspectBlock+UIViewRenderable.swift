import CommonCorePublic

extension AspectBlock {
  public static func makeBlockView() -> BlockView {
    Content.makeBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    content.canConfigureBlockView(view)
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    content.configureBlockView(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}
