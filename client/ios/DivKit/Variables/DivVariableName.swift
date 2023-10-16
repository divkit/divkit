import Foundation

import BasePublic

public enum DivVariableNameTag {}
public typealias DivVariableName = Tagged<DivVariableNameTag, String>

public typealias DivVariables = [DivVariableName: DivVariableValue]
