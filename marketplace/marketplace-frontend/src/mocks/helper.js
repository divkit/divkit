import { Provider } from 'react-redux'
import { MemoryRouter } from 'react-router'
import { App } from '../App'
import { store } from '../store/store'

export function renderApplication(initial = '/', Component = <App />) {
  return (
    <MemoryRouter initialEntries={[initial]}>
      <Provider store={store}>
        {Component}
      </Provider>
    </MemoryRouter>
  )
}
