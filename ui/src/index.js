import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { TestChimpSDK } from "testchimp-js";

window.onload = function () {
  TestChimpSDK.startRecording({
    enableRecording:true,
    projectId: "3cc5e2fb-80d6-4c4b-9ab1-98cf6da202f1",
    sessionRecordingApiKey: "67292933-a8fe-411b-8e01-092c36591d83",
    endpoint: "https://ingress-staging.testchimp.io",
    samplingProbabilityOnError: 0.1,
    samplingProbability: 1.0,
    maxSessionDurationSecs: 500,
    eventWindowToSaveOnError: 200,
    tracedUriRegexListToTrack:".*://shoplify-staging.awarelabs\.io.*$",
    untracedUriRegexListToTrack:"/^$/",
    environment:"QA"
  });
};

const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);

root.render(
  <React.StrictMode>
      <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
