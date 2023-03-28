import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivTabs: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let tabsPath = context.parentPath + (id ?? DivTabs.type)
    return try modifyError({
      DivBlockModelingError(
        "DivTabs error: \($0.errorMessage)",
        path: tabsPath
      )
    }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: context, tabsPath: tabsPath) },
        context: context,
        actions: nil,
        actionAnimation: nil,
        doubleTapActions: nil,
        longTapActions: nil
      )
    }
  }

  private func makeBaseBlock(
    context: DivBlockModelingContext,
    tabsPath: UIElementPath
  ) throws -> Block {
    let tabsContext = modified(context) {
      $0.parentPath = tabsPath
    }
    let tabs = items.iterativeFlatMap { item, index in
      try? item.makeTab(context: tabsContext, index: index)
    }

    let listModel = TabListViewModel(
      tabTitles: tabs.map { $0.title },
      titleStyle: tabTitleStyle.makeTitleStyle(
        fontSpecifiers: context.fontSpecifiers,
        expressionResolver: context.expressionResolver
      ),
      listPaddings: titlePaddings.makeEdgeInsets(with: context.expressionResolver)
    )

    let contentsModel = try TabContentsViewModel(
      pages: tabs.map { $0.page },
      pagesHeight: resolveDynamicHeight(context.expressionResolver)
        ? .bySelectedPage
        : .byHighestPage,
      path: tabsContext.parentPath,
      scrollingEnabled: resolveSwitchTabsByContentSwipeEnabled(context.expressionResolver)
    )

    return try TabsBlock(
      model: try TabViewModel(
        listModel: listModel,
        contentsModel: contentsModel,
        separatorStyle: makeSeparatorStyle(with: context.expressionResolver)
      ),
      state: makeState(context: tabsContext, tabs: tabs),
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context)
    )
  }

  private func makeSeparatorStyle(
    with expressionResolver: ExpressionResolver
  ) -> TabSeparatorStyle? {
    resolveHasSeparator(expressionResolver) ? TabSeparatorStyle(
      color: resolveSeparatorColor(expressionResolver),
      insets: separatorPaddings.makeEdgeInsets(with: expressionResolver)
    ) : nil
  }

  private func makeState(
    context: DivBlockModelingContext,
    tabs: [Tab]
  ) -> TabViewState {
    let stateStorage = context.blockStateStorage
    let path = context.parentPath
    let index: CGFloat
    if let state: TabViewState = stateStorage.getState(path) {
      index = state.selectedPageIndex
    } else {
      index = CGFloat(resolveSelectedTab(context.expressionResolver))
    }
    let newState = TabViewState(
      selectedPageIndex: min(index, CGFloat(tabs.count) - 1),
      countOfPages: tabs.count
    )
    stateStorage.setState(path: path, state: newState)
    return newState
  }
}

// IMPORTANT: Here we are changing `private` to `internal` due to bug in swift
// compiler. If this typealias is private when resuliting swiftmodule file is
// unstable.
// TODO(ilyakuteev): Fix this in swift toolchain.
typealias Tab = (title: UILink, page: TabPageViewModel)

extension DivTabs.TabTitleStyle {
  private func makeTypo(
    fontSpecifiers: FontSpecifiers,
    fontWeight: FontWeight,
    with expressionResolver: ExpressionResolver
  ) -> Typo {
    let font = fontSpecifiers.font(
      family: resolveFontFamily(expressionResolver).fontFamily,
      weight: fontWeight,
      size: resolveFontSizeUnit(expressionResolver)
        .makeScaledValue(resolveFontSize(expressionResolver))
    )
    return Typo(font: font)
      .with(height: resolveLineHeight(expressionResolver))
      .kerned(CGFloat(resolveLetterSpacing(expressionResolver)))
      .allowHeightOverrun
  }

  fileprivate func makeTitleStyle(
    fontSpecifiers: FontSpecifiers,
    expressionResolver: ExpressionResolver
  ) -> LayoutKit.TabTitleStyle {
    let defaultTypo = resolveFontWeight(expressionResolver).fontWeight
    return LayoutKit.TabTitleStyle(
      typo: makeTypo(
        fontSpecifiers: fontSpecifiers,
        fontWeight: resolveActiveFontWeight(expressionResolver)?.fontWeight ?? defaultTypo,
        with: expressionResolver
      ),
      inactiveTypo: makeTypo(
        fontSpecifiers: fontSpecifiers,
        fontWeight: resolveInactiveFontWeight(expressionResolver)?.fontWeight ?? defaultTypo,
        with: expressionResolver
      ),
      paddings: paddings.makeEdgeInsets(with: expressionResolver),
      cornerRadius: makeCornerRadii(with: expressionResolver),
      baseTextColor: resolveInactiveTextColor(expressionResolver),
      activeTextColor: resolveActiveTextColor(expressionResolver),
      activeBackgroundColor: resolveActiveBackgroundColor(expressionResolver),
      inactiveBackgroundColor: resolveInactiveBackgroundColor(expressionResolver) ?? .clear,
      itemSpacing: CGFloat(resolveItemSpacing(expressionResolver))
    )
  }

  private func makeCornerRadii(with expressionResolver: ExpressionResolver) -> CornerRadii? {
    let cornerRadius = resolveCornerRadius(expressionResolver)
    let topLeft = cornersRadius?.resolveTopLeft(expressionResolver)
      ?? cornerRadius
    let topRight = cornersRadius?.resolveTopRight(expressionResolver)
      ?? cornerRadius
    let bottomLeft = cornersRadius?.resolveBottomLeft(expressionResolver)
      ?? cornerRadius
    let bottomRight = cornersRadius?.resolveBottomRight(expressionResolver)
      ?? cornerRadius

    if topLeft == nil, topRight == nil, bottomLeft == nil, bottomRight == nil {
      return nil
    }

    return CornerRadii(
      topLeft: CGFloat(topLeft ?? 0),
      topRight: CGFloat(topRight ?? 0),
      bottomLeft: CGFloat(bottomLeft ?? 0),
      bottomRight: CGFloat(bottomRight ?? 0)
    )
  }
}

extension Typo {
  fileprivate func with(height: Int?) -> Typo {
    if let height = height {
      return with(height: CGFloat(height))
    } else {
      return self
    }
  }
}

extension DivTabs.Item {
  fileprivate func makeTab(context: DivBlockModelingContext, index: Int) throws -> Tab {
    let tabContext = modified(context) { $0.parentPath = $0.parentPath + index }
    let pageContext = modified(tabContext) { $0.parentPath += "page" }
    let title = makeTitle(context: tabContext)
    let page = try div.value
      .makeBlock(context: pageContext)
      .makeTabPage(with: pageContext.parentPath)
    return (title: title, page: page)
  }

  private func makeTitle(context: DivBlockModelingContext) -> UILink {
    let path = context.parentPath + "title"
    let action = makeAction(context: context.actionContext)
    return UILink(
      text: resolveTitle(context.expressionResolver) ?? "",
      url: action?.url,
      path: action?.path ?? path
    )
  }
}
