export type StoreTypes = 'string' | 'number' | 'boolean';
export type StoreAllTypes = 'string' | 'integer' | 'number' | 'boolean' | 'color' | 'url' | 'dict' | 'array';

export interface Store {
    /**
     * Fetches primitive from the store
     * @param name
     * @param type Expected value tpye
     * @deprecated Provide get() instead
     */
    getValue?(
        name: string,
        type: StoreTypes
    ): string | number | boolean | undefined;

    /**
     * Save primitive into the store
     * @param name
     * @param type Value type (for example, can be url)
     * @param value
     * @param lifetime Value lifetime in seconds
     * @deprecated Provide set() instead
     */
    setValue?(
        name: string,
        type: StoreTypes,
        value: string | number | boolean,
        lifetime: number
    ): void;

    /**
     * Fetches primitive from the store
     * @param name
     * @param type Expected value tpye
     */
    get?(
        name: string,
        type: StoreAllTypes
    ): object | string | bigint | number | boolean | undefined;

    /**
     * Save primitive into the store
     * @param name
     * @param type Value type (for example, can be url)
     * @param value
     * @param lifetime Value lifetime in seconds
     */
    set?(
        name: string,
        type: StoreAllTypes,
        value: object | string | bigint | number | boolean,
        lifetime: number
    ): void;
}
