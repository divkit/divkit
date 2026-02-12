import LayoutKit

public protocol DivCustomActionHandling {
  func handle(
    payload: DivDictionary,
    context: DivActionHandlingContext,
    sender: AnyObject?
  )
}
