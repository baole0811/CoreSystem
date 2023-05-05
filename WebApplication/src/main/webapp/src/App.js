
import './App.css';
import "./assets/boxicons-2.0.7/css/boxicons.min.css";
import "./assets/css/grid.css";
import "./assets/css/index.css";
import 'react-toastify/dist/ReactToastify.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {AddBookExchange, Home} from "./pages";
import {ToastContainer} from "react-toastify";
function App() {
  return (
      <BrowserRouter>
                    <div className="layout">
                      <div className="layout__content">
                        <div className="layout__content-main">
                          <Routes>
                            <Route path="/" element={<Home/>}/>
                            <Route path="/post-book" element={<AddBookExchange/>}/>
                          </Routes>
                        </div>
                      </div>
                    </div>
          <ToastContainer/>
      </BrowserRouter>
  );
}

export default App;
