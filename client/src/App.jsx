import {
  BrowserRouter,
  Navigate,
  Route,
  Router,
  Routes,
} from "react-router-dom";
import { DataForm } from "./pages/DataForm";
import { useState } from "react";
import "./App.css";
import { GeneralTable } from "./pages/GeneralTable";

function App() {
  const [respuestas, setRespuestas] = useState();

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route
            path="/"
            element={<DataForm setRespuestas={setRespuestas} />}
          />
          <Route path="/table" element={<GeneralTable tabla={respuestas} />} />
          <Route path="/*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
