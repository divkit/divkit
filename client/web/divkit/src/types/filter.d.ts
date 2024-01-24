export interface Blur {
    type: 'blur';
    radius: number;
}

export interface RTLMirror {
    type: 'rtl_mirror';
}

export type Filter = Blur | RTLMirror;
