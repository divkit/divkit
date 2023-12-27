import { DivFixedSize, DivMatchParentSize, DivSizeUnit, DivWrapContentSize } from './generated';
import { DivExpression } from './safe-expression';

export function fixed(value: number | DivExpression, unit: DivSizeUnit | undefined = undefined): DivFixedSize {
    if (unit === undefined) {
        return new DivFixedSize({ value: value });
    }
    return new DivFixedSize({ unit: unit, value: value });
}

export function matchParent(): DivMatchParentSize {
    return new DivMatchParentSize();
}

export function weighted(weight: number): DivMatchParentSize {
    return new DivMatchParentSize({ weight: weight });
}

export function wrapContent(): DivWrapContentSize {
    return new DivWrapContentSize();
}
