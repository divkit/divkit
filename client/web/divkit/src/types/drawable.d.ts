import type { Shape } from './shape';
import type { Stroke } from './border';

export interface Drawable {
    type: 'shape_drawable';
    shape: Shape;
    /** @deprecated */
    color?: string;
    /** @deprecated */
    stroke?: Stroke;
}
