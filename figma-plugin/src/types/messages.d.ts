import type {
    GeneratedNode,
    GeneratedNodeParameter,
    PluginUIScreen
} from './common';

export interface UpdateMainButtonStateMessage {
    type: 'updateMainButtonState',
    data: {
        isActive: boolean;
    }
}

export interface UpdateParametersButtonStateMessage {
    type: 'updateParametersButtonState',
    data: {
        isActive: boolean;
    }
}

export interface RenderGeneratedNodesMessage {
    type: 'renderGeneratedNodes';
    data: {
        generatedNodes: GeneratedNode[];
    }
}

export interface RenderGeneratedNodeMessage {
    type: 'renderGeneratedNode';
    data: {
        node: GeneratedNode;
        parameters: GeneratedNodeParameter[];
    }
}

export interface RenderParametersMessage {
    type: 'renderParameters';
    data: {
        parameters: GeneratedNodeParameter[]
    };
}

export interface GenerationErrorMessage {
    type: 'generationError';
}

export interface CheckPersonalTokenMessage {
    type: 'checkPersonalToken';
    data: {
        isTokenSaved: boolean;
    }
}

export interface UpdateStyleSettingsMessage {
    type: 'updateStyleSettings';
    data: {
        lightName: string;
        darkName: string;
    }
}

export interface PreviewError {
    message: string;
    level: 'error' | 'warn';
    nodeInfo: {
        id: string;
        name: string;
    }
}

export interface ShowPreviewErrorsMessage {
    type: 'showPreviewErrors';
    data: {
        errors: PreviewError[];
    }
}

export type FigmaCodeMessage =
    | UpdateMainButtonStateMessage
    | UpdateParametersButtonStateMessage
    | RenderGeneratedNodesMessage
    | RenderGeneratedNodeMessage
    | RenderParametersMessage
    | GenerationErrorMessage
    | CheckPersonalTokenMessage
    | UpdateStyleSettingsMessage
    | SetPluginUIScreenMessage
    | ShowPreviewErrorsMessage;

export interface RemoveGeneratedNodeMessage {
    type: 'removeGeneratedNode';
    data: {
        id: string;
    }
}

export interface CreateGeneratedNodeMessage {
    type: 'createGeneratedNode';
}

export interface SaveStylesMessage {
    type: 'saveStyles';
    data: {
        lightName: string;
        darkName: string;
    }
}

export interface ClearStylesMessage {
    type: 'clearStyles';
}

export interface OpenGeneratedNodeMessage {
    type: 'openGeneratedNode';
    data: {
        id: string;
    }
}

export interface CreateJsonMessage {
    type: 'createJson';
    data: {
        nodeId: string;
        needUpdateImages: boolean;
    }
}

export interface AddParameterMessage {
    type: 'addParameter';
    data: {
        id: string;
    }
}

export interface GetParametersMessage {
    type: 'getParameters';
    data: {
        id: string;
    }
}

export interface RemoveParameterMessage {
    type: 'removeParameter';
    data: {
        parentId: string;
        parameterId: string;
    }
}

export interface SaveTokenMessage {
    type: 'saveToken';
    data: {
        personalToken: string;
    }
}

export interface SetPluginUIScreenMessage {
    type: 'setPluginUIScreen';
    data: {
        screen: PluginUIScreen;
    }
}

export interface ShowFigmaNotificationMessage {
    type: 'showFigmaNotification';
    data: {
        message: string;
        error?: boolean;
    }
}

export interface ResizePluginMessage {
    type: 'resizePlugin';
    data: {
        width: number;
        height: number;
    }
}

export interface SetFigmaSelection {
    type: 'setFigmaSelection';
    data: {
        id: string;
    }
}

export type UIMessage =
    | CreateJsonMessage
    | CreateGeneratedNodeMessage
    | SaveStylesMessage
    | ClearStylesMessage
    | OpenGeneratedNodeMessage
    | RemoveGeneratedNodeMessage
    | AddParameterMessage
    | GetParametersMessage
    | RemoveParameterMessage
    | SaveTokenMessage
    | SetPluginUIScreenMessage
    | ShowFigmaNotificationMessage
    | ResizePluginMessage
    | SetFigmaSelection;
