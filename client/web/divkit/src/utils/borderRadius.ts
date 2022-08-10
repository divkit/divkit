import type { CornersRadius } from '../types/border';
import { pxToEm } from './pxToEm';

export function borderRadius(cornersRadius: CornersRadius, defaultRadius = 0, fontSize = 10): string {
    return [
        cornersRadius['top-left'],
        cornersRadius['top-right'],
        cornersRadius['bottom-right'],
        cornersRadius['bottom-left']
    ]
        .map(it => pxToEm((it || defaultRadius) / fontSize * 10))
        .join(' ');
}
