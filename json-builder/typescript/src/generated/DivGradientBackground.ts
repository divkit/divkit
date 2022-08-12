// Generated code. Do not modify.

import { Exact, IntBoolean, NonEmptyArray } from '../helper';
import { TemplateBlock, Type } from '../template';
import { DivExpression } from '../expression';

/**
 * Градиентный фон.
 */
export class DivGradientBackground<T extends DivGradientBackgroundProps = DivGradientBackgroundProps> {
    readonly _props?: Exact<DivGradientBackgroundProps, T>;

    readonly type = 'gradient';
    /**
     * Угол направления градиента.
     */
    angle?: Type<number> | DivExpression;
    /**
     * Цвета. Точки градиента будут расположены на равном расстоянии друг от друга.
     */
    colors: Type<NonEmptyArray<string>> | DivExpression;

    constructor(props: Exact<DivGradientBackgroundProps, T>) {
        this.angle = props.angle;
        this.colors = props.colors;
    }
}

interface DivGradientBackgroundProps {
    /**
     * Угол направления градиента.
     */
    angle?: Type<number> | DivExpression;
    /**
     * Цвета. Точки градиента будут расположены на равном расстоянии друг от друга.
     */
    colors: Type<NonEmptyArray<string>> | DivExpression;
}
