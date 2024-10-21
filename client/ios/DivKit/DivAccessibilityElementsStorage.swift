import UIKit

final class DivAccessibilityElementsStorage {
  private var ids = [String]()
  private var idToForwardId = [String: String]()
  
  func put(id: String, nextId: String) {
    ids.append(id)
    idToForwardId[id] = nextId
  }
  
  func getOrderedIds() -> [String] {
    var orderedIds = [String]()
    var idsSet = Set<String>()
    
    for elementId in ids {
      if idsSet.contains(elementId) {
        continue
      }
      
      var nodeId = elementId
      while !idsSet.contains(nodeId) {
        orderedIds.append(nodeId)
        idsSet.insert(nodeId)
        
        if let nextNodeId = idToForwardId[nodeId] {
          nodeId = nextNodeId
        }
      }
    }
    
    return orderedIds
  }
  
  func getAccessibilityElements(from view: UIView) -> [Any]? {
    let orderedIds = getOrderedIds()
    
    guard !orderedIds.isEmpty else {
      return view.superview?.accessibilityElements
    }
    
    var systemElements = [Any]()
    if let parentElements = view.superview?.accessibilityElements {
      systemElements = parentElements
    }
    
    var elements = [Any]()
    for id in orderedIds {
      if let element = findElement(withID: id, in: view),
         let subview = element as? UIView,
         !subview.isHidden,
         subview.alpha > 0,
         subview.frame != .zero,
         subview.isDescendant(of: view)
      {
        elements.append(contentsOf: [element])
        systemElements.removeAll {
          ($0 as? UIView)?.accessibilityIdentifier == id
        }
      }
    }
    
    elements += systemElements
    return elements
  }
  
  private func findElement(withID id: String, in view: UIView) -> Any? {
    if view.accessibilityIdentifier == id {
      return view
    }
    for subview in view.subviews {
      if let found = findElement(withID: id, in: subview) {
        return found
      }
    }
    return nil
  }
}
