import React from 'react';
import { render as serverRender } from '@divkitframework/divkit/server';
import type { DivKitProps } from '../typings/divkit';

export function DivKit(props: DivKitProps) {
    return <div
        dangerouslySetInnerHTML={{
            __html: serverRender(props)
        }}
    />;
}
