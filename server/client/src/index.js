import React from "react";
//import ReactDOM from "react-dom";
import { ApplicationProvider } from "./components/ApplicationContext";
import App from "./App";
import { createRoot } from "react-dom/client";

import "bootstrap/dist/css/bootstrap.min.css";

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
  <ApplicationProvider>
    <title>Playwright Test Outcome</title>
    <App />
  </ApplicationProvider>
);
