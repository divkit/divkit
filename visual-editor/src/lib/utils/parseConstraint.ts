export function parseConstraint(subtype: string | undefined, constraint: string | undefined): {
    min?: number;
    max?: number;
} {
    if (!constraint || !subtype) {
        return {};
    }

    const res: {
        min?: number;
        max?: number;
    } = {};

    const re = /number (>|>=|<|<=) (-?\d+)/g;

    if (!re.test(constraint)) {
        console.error('Unknown constraint', constraint);
        return {};
    }
    re.lastIndex = -1;

    let match;
    while ((match = re.exec(constraint))) {
        const num = Number(match[2]);
        const op = match[1];

        if (op === '>') {
            res.min = subtype === 'integer' ? num + 1 : num + .01;
        } else if (op === '>=') {
            res.min = num;
        } else if (op === '<') {
            res.max = subtype === 'integer' ? num - 1 : num - .01;
        } else if (op === '<=') {
            res.max = num;
        }
    }

    return res;
}
