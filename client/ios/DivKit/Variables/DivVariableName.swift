import Foundation
import VGSL

public enum DivVariableNameTag {}
public typealias DivVariableName = Tagged<DivVariableNameTag, String>

public typealias DivVariables = [DivVariableName: DivVariableValue]
