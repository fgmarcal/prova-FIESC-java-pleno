import CadastroPessoaPage from "./components/pages/CadastroPessoasPage"
import { PessoaProvider } from "./context/PessoaContextProvider"

function App() {

  return (
    <PessoaProvider>
      <CadastroPessoaPage />
    </PessoaProvider>
  )
}

export default App
