export interface JsonVariable {
    name?: string;
    type?: string;
    value?: unknown;
}

export interface Variable {
    id: string;
    name: string;
    type: string;
    value: string;
}

export function parseVariableValue(variable: Variable): unknown {
    const { type, value } = variable;

    switch (type) {
        case 'number':
        case 'integer':
            const val = Number(value);

            if (Number.isNaN(val)) {
                throw new Error('Value is NaN');
            }

            if (type === 'integer' && val !== Math.floor(val)) {
                throw new Error('Integer cannot have fractional part');
            }

            return val;

        case 'dict':
        case 'array':
            const parsed = JSON.parse(value);

            if (type === 'array' && !Array.isArray(parsed)) {
                throw new Error('Not an array');
            }
            if (type === 'dict' && !(typeof parsed === 'object' && parsed && !Array.isArray(parsed))) {
                throw new Error('Not an object');
            }

            return parsed;
        case 'boolean':
            if (value === 'true') {
                return true;
            }
            if (value === 'false') {
                return false;
            }

            throw new Error('Not a boolean');
    }

    return value;
}
