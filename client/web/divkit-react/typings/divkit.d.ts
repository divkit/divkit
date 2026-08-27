import type React from 'react';
import type { Patch } from '@divkitframework/divkit/typings/common.d.ts';
import { render as clientRender } from '@divkitframework/divkit/client-hydratable';
import { render as serverRender } from '@divkitframework/divkit/server';

export type DivKitProps = Omit<Parameters<typeof clientRender>[0] & Parameters<typeof serverRender>[0], 'hydrate' | 'target'>;

export interface DivKitHandle {
    applyPatch: (patch: Patch) => boolean;
}

export declare const DivKit: React.ForwardRefExoticComponent<
    DivKitProps & React.RefAttributes<DivKitHandle>
>;
