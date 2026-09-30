@testable import DivKit
import DivKitTestsSupport
import Foundation
import Testing

@Suite
struct DivActionIntentTests {
  @Test
  func download() {
    #expect(
      intent("div-action://download?url=https://download.url") ==
        .download(patchUrl: url("https://download.url"))
    )
  }

  @Test
  func hideTooltip() {
    #expect(
      intent("div-action://hide_tooltip?id=123") ==
        .hideTooltip(id: "123")
    )
  }

  @Test
  func showTooltip() {
    #expect(
      intent("div-action://show_tooltip?id=123") ==
        .showTooltip(id: "123", multiple: false)
    )
  }

  @Test
  func showTooltip_InvalidParams() {
    #expect(intent("div-action://show_tooltip") == nil)
  }

  @Test
  func showTooltip_InvalidScheme() {
    #expect(intent("divaction://show_tooltip?id=123") == nil)
  }

  @Test
  func setState() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .short
        )
    )
  }

  @Test
  func setState_TemporaryIsTrue() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second&temporary=true") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .short
        )
    )
  }

  @Test
  func setState_TemporaryIsFalse() {
    #expect(
      intent("div-action://set_state?state_id=0/state/second&temporary=false") ==
        .setState(
          divStatePath: path("0/state/second"),
          lifetime: .long
        )
    )
  }

  @Test
  func setVariable() {
    #expect(
      intent("div-action://set_variable?name=var1&value=newvalue") ==
        .setVariable(name: "var1", value: "newvalue")
    )
  }

  @Test
  func setVariable_InvalidParams() {
    #expect(intent("div-action://set_variable?name=var1") == nil)
  }

  @Test
  func setCurrentItem() {
    #expect(
      intent("div-action://set_current_item?id=div_id&item=10") ==
        DivActionIntent.scrollAction(id: "div_id", .setCurrentItem(index: 10), animated: true)
    )
  }

  @Test
  func setCurrentItem_InvalidItem() {
    #expect(intent("set_current_item?id=0/div_id&item=abc") == nil)
  }

  @Test
  func setNextItem() {
    #expect(
      intent("div-action://set_next_item?id=div_id&step=3&overflow=ring") ==
        DivActionIntent.scrollAction(
          id: "div_id",
          .setNextItem(step: 3, overflow: .ring),
          animated: true
        )
    )
  }

  @Test
  func setPreviousItem() {
    #expect(
      intent("div-action://set_previous_item?id=div_id&step=3&overflow=clamp") ==
        DivActionIntent.scrollAction(
          id: "div_id",
          .setPreviousItem(step: 3, overflow: .clamp),
          animated: true
        )
    )
  }

  @Test
  func scrollAction_DefaultAnimated() {
    scrollActionTestCases(animated: nil, expectedAnimated: true)
  }

  @Test
  func scrollAction_AnimatedIsTrue() {
    scrollActionTestCases(animated: "true", expectedAnimated: true)
  }

  @Test
  func scrollAction_AnimatedIsFalse() {
    scrollActionTestCases(animated: "false", expectedAnimated: false)
  }

  @Test
  func scrollAction_InvalidAnimatedUsesDefault() {
    for animated in ["", "0", "1", "False", "invalid"] {
      #expect(
        intent("div-action://set_current_item?id=div_id&item=2&animated=\(animated)") ==
          .scrollAction(id: "div_id", .setCurrentItem(index: 2), animated: true)
      )
    }
  }

  @Test
  func setStoredValue_String() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&type=string&lifetime=100") ==
        .setStoredValue(
          DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 100),
          .global
        )
    )
  }

  @Test
  func setStoredValue_Boolean() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=true&type=boolean&lifetime=100") ==
        .setStoredValue(
          DivStoredValue(name: "var", value: "true", type: .boolean, lifetimeInSec: 100),
          .global
        )
    )
  }

  @Test
  func setStoredValue_Bool() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=true&type=bool&lifetime=100") ==
        .setStoredValue(
          DivStoredValue(name: "var", value: "true", type: .bool, lifetimeInSec: 100),
          .global
        )
    )
  }

  @Test
  func setStoredValue_WithoutValueReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&type=string&lifetime=100") == nil
    )
  }

  @Test
  func setStoredValue_WithoutTypeReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&lifetime=100") == nil
    )
  }

  @Test
  func setStoredValue_WithoutLifetimeReturnsNil() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=value&type=string") == nil
    )
  }

  @Test
  func setStoredValue_ScopeGlobal() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=v&type=string&lifetime=100&scope=global"
      ) ==
        .setStoredValue(
          DivStoredValue(name: "var", value: "v", type: .string, lifetimeInSec: 100),
          .global
        )
    )
  }

  @Test
  func setStoredValue_ScopeCard() {
    #expect(
      intent("div-action://set_stored_value?name=var&value=v&type=string&lifetime=100&scope=card"
      ) ==
        .setStoredValue(
          DivStoredValue(name: "var", value: "v", type: .string, lifetimeInSec: 100),
          .card
        )
    )
  }

  private func scrollActionTestCases(animated: String?, expectedAnimated: Bool) {
    let cases: [(String, DivActionIntent.Scroll)] = [
      ("div-action://set_current_item?id=div_id&item=2", .setCurrentItem(index: 2)),
      ("div-action://set_next_item?id=div_id&step=2", .setNextItem(step: 2, overflow: .clamp)),
      (
        "div-action://set_previous_item?id=div_id&step=2",
        .setPreviousItem(step: 2, overflow: .clamp)
      ),
      (
        "div-action://scroll_forward?id=div_id&step=10",
        .scroll(mode: .forward(10, overflow: .clamp))
      ),
      (
        "div-action://scroll_backward?id=div_id&step=10",
        .scroll(mode: .backward(10, overflow: .clamp))
      ),
      ("div-action://scroll_to_position?id=div_id&step=10", .scroll(mode: .position(10))),
      ("div-action://scroll_to_start?id=div_id", .scroll(mode: .start)),
      ("div-action://scroll_to_end?id=div_id", .scroll(mode: .end)),
    ]
    for (actionURL, expectedScroll) in cases {
      var url = actionURL
      if let animated {
        url += "&animated=\(animated)"
      }
      #expect(
        intent(url) == .scrollAction(id: "div_id", expectedScroll, animated: expectedAnimated)
      )
    }
  }
}

private func intent(_ url: String) -> DivActionIntent? {
  DivActionIntent(url: URL(string: url)!)
}

private func path(_ path: String) -> DivStatePath {
  DivStatePath.makeDivStatePath(from: path)
}
