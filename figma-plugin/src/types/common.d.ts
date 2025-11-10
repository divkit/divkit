export type NodeWithChildren = FrameNode | InstanceNode | GroupNode | ComponentNode;

export type NodeWithLayoutParams = NodeWithChildren | TextNode | RectangleNode;

export interface GeneratedNode {
    id: string;
    name: string;
    width?: number;
    height?: number;
    jsonString?: string;
    parametersIds?: string[];
}

export interface GeneratedNodeParameter {
    id: string;
    divkitType: string;
    nodeName: string;
}

export type PluginUIScreen = 'main' | 'preview' | 'settings' | 'errors';
