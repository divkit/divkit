export type CustomComponentTemplate = string | ((opts: {
    props?: object;
    variables: Map<string, string | number | boolean | unknown[] | object>;
}) => string);

export interface CustomComponentDescription {
    element: string;
    shadowRootMode?: 'open' | 'close';
    template?: CustomComponentTemplate;
}
