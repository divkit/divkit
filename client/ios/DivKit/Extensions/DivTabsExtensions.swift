import CoreGraphics
import Foundation

import LayoutKit
import VGSL

extension DivTabs: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let path = context.parentPath + (id ?? DivTabs.type)
    let tabsContext = context.modifying(parentPath: path)
    return try modifyError({ DivBlockModelingError($0.message, path: path) }) {
      try applyBaseProperties(
        to: { try makeBaseBlock(context: tabsContext) },
        context: tabsContext,
        actionsHolder: nil
      )
    }
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let itemContext = context.modifying(errorsStorage: DivErrorsStorage(errors: []))
    let tabs = items.iterativeFlatMap { item, index in
      do {
        return try item.makeTab(context: itemContext, index: index)
      } catch {
        itemContext.addError(error: error)
        return nil
      }
    }
    if tabs.isEmpty {
      throw DivBlockModelingError(
        "Tabs error: missing children",
        path: context.parentPath,
        causes: itemContext.errorsStorage.errors
      )
    } else {
      context.errorsStorage.add(contentsOf: itemContext.errorsStorage)
    }

    let titleStyle = tabTitleStyle ?? DivTabs.TabTitleStyle()
    let listModel = TabListViewModel(
      tabTitles: tabs.map(\.title),
      titleStyle: titleStyle.resolve(context),
      layoutDirection: context.layoutDirection,
      listPaddings: titlePaddings.resolve(context)
    )

    let expressionResolver = context.expressionResolver
    let contentsModel = try TabContentsViewModel(
      pages: tabs.map(\.page),
      pagesHeight: resolveDynamicHeight(expressionResolver)
        ? .bySelectedPage
        : .byHighestPage,
      path: context.parentPath,
      scrollingEnabled: resolveSwitchTabsByContentSwipeEnabled(expressionResolver),
      layoutDirection: context.layoutDirection
    )

    return try TabsBlock(
      model: TabViewModel(
        listModel: listModel,
        contentsModel: contentsModel,
        separatorStyle: resolveSeparatorStyle(context)
      ),
      state: makeState(context: context, tabs: tabs),
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context)
    )
  }

  private func resolveSeparatorStyle(
    _ context: DivBlockModelingContext
  ) -> TabSeparatorStyle? {
    let expressionResolver = context.expressionResolver
    return resolveHasSeparator(expressionResolver)
      ? TabSeparatorStyle(
        color: resolveSeparatorColor(expressionResolver),
        insets: separatorPaddings.resolve(context)
      ) : nil
  }

  private func makeState(
    context: DivBlockModelingContext,
    tabs: [Tab]
  ) -> TabViewState {
    let stateStorage = context.blockStateStorage
    let path = context.parentPath
    let index: CGFloat = if let state: TabViewState = stateStorage.getState(path) {
      state.selectedPageIndex
    } else {
      CGFloat(resolveSelectedTab(context.expressionResolver))
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
    fontProvider: DivFontProvider,
    fontWeight: DivFontWeight,
    expressionResolver: ExpressionResolver
  ) -> Typo {
    let font = fontProvider.font(
      family: resolveFontFamily(expressionResolver) ?? "",
      weight: fontWeight,
      size: resolveFontSizeUnit(expressionResolver)
        .makeScaledValue(resolveFontSize(expressionResolver))
    )
    return Typo(font: font)
      .with(height: resolveLineHeight(expressionResolver))
      .kerned(CGFloat(resolveLetterSpacing(expressionResolver)))
      .allowHeightOverrun
  }

  fileprivate func resolve(
    _ context: DivBlockModelingContext
  ) -> TabTitleStyle {
    let expressionResolver = context.expressionResolver
    let fontProvider = context.fontProvider
    let defaultFontWeight = resolveFontWeight(expressionResolver)
    return TabTitleStyle(
      typo: makeTypo(
        fontProvider: fontProvider,
        fontWeight: resolveActiveFontWeight(expressionResolver) ?? defaultFontWeight,
        expressionResolver: expressionResolver
      ),
      inactiveTypo: makeTypo(
        fontProvider: fontProvider,
        fontWeight: resolveInactiveFontWeight(expressionResolver) ?? defaultFontWeight,
        expressionResolver: expressionResolver
      ),
      paddings: paddings.resolve(context),
      cornerRadius: resolveCornerRadii(expressionResolver),
      baseTextColor: resolveInactiveTextColor(expressionResolver),
      activeTextColor: resolveActiveTextColor(expressionResolver),
      activeBackgroundColor: resolveActiveBackgroundColor(expressionResolver),
      inactiveBackgroundColor: resolveInactiveBackgroundColor(expressionResolver) ?? .clear,
      itemSpacing: CGFloat(resolveItemSpacing(expressionResolver))
    )
  }

  private func resolveCornerRadii(_ expressionResolver: ExpressionResolver) -> CornerRadii? {
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
    if let height {
      with(height: CGFloat(height))
    } else {
      self
    }
  }
}

extension DivTabs.Item {
  fileprivate func makeTab(context: DivBlockModelingContext, index: Int) throws -> Tab {
    let titleContext = context.modifying(parentPath: context.parentPath + "title\(index)")
    let title = makeTitle(context: titleContext)
    let pageContext = context.modifying(parentPath: context.parentPath + index)
    let page = try div.value
      .makeBlock(context: pageContext)
      .makeTabPage(with: pageContext.parentPath)
    return (title: title, page: page)
  }

  private func makeTitle(context: DivBlockModelingContext) -> UILink {
    let action = titleClickAction?.uiAction(context: context)
    return UILink(
      text: resolveTitle(context.expressionResolver) ?? "",
      url: action?.url,
      path: context.parentPath
    )
  }
}
