import { expect } from 'vitest';

// The standard snapshot serializer is too accurate for us
// It stores all the "undefined" values, of which there are many in our data
expect.addSnapshotSerializer({
    test(_val) {
        return true;
    },
    print(val, _serialize) {
        return JSON.stringify(val, null, 2);
    },
});
