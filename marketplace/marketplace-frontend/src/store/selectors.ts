import { RootState } from './store'

export const getPage = (state: RootState) => state.query.page
export const getFilters = (state: RootState) => state.query.filters
