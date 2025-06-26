import { useState } from 'react'
import snowPlow from './assets/snowplow_image.png'
import './Login.css'

type ButtonProperties = {
  message: string;
  func: () => void;
};

type InputProperties = {
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  type?: string; // "text", "password", etc. Optionnel
};

var doggyStyle = {
    width: "100%",
    height: "400px",
    backgroundImage: "url(" + { snowPlow } + ")"
};

function Login() {
    //LOGIN PART
  const [text_login, setTextLogin] = useState('');
  const [text_pwd, setTextPWD] = useState('');
  
  // REGISTER PART
  const [text_loginREgister, setTextLoginRegister] = useState('');
  const [text_pwdRegister, setTextPwdRegister] = useState('');
  const [text_confPWD, setTextConfPWD] = useState('');
  const [text_email, setTextEmail] = useState('');
  const [text_confEmail, setTextConfEmail] = useState('');

  const login = () => {alert(text_login + setTextLogin)};
  const register = () => {alert(text_loginREgister + text_pwdRegister + text_confPWD + text_email + text_confEmail)};
  return (
    <>
    <div className='card'>
      <h1 className="login-container">Login page</h1>
      <div className = "auth-wrapper">
        {/*
        <div className="card login-card">
        */}
            <CustomInput 
            placeholder="Enter Login"
            value={text_login}
            onChange={(e) => setTextLogin(e.target.value)}
            />

            <CustomInput
            placeholder="Enter Password"
            value={text_pwd}
            onChange={(e) => setTextPWD(e.target.value)}
            />

            {/*
            <div className="card card-horizontal">
            */}
                <CustomButton message="Login" func={login} />
            {/*
            </div>
            */}
        </div>

        <p />
    
        {/*
        <div className= "card register-card">
        */}
            <CustomInput
            placeholder="Enter Login"
            value={text_loginREgister}
            onChange={(e) => setTextLoginRegister(e.target.value)}
            />

            <CustomInput
            placeholder="Enter Password"
            value={text_pwdRegister}
            onChange={(e) => setTextPwdRegister(e.target.value)}
            />

            <CustomInput
            placeholder="Confirm Password"
            value={text_confPWD}
            onChange={(e) => setTextConfPWD(e.target.value)}
            />

            <CustomInput
            placeholder="Email"
            value={text_email}
            onChange={(e) => setTextEmail(e.target.value)}
            />

            <CustomInput
            placeholder="Confirm Email"
            value={text_confEmail}
            onChange={(e) => setTextConfEmail(e.target.value)}
            />

            {/*<div className="card card-horizontal">*/}
            <CustomButton message="Register" func={register} />
            {/*</div>*/}
        </div>
    </>
  );
}

const CustomButton = ({message, func}: ButtonProperties) => {
    return (
    <button className="custom-button" onClick={func}>
      {message}
    </button>
  );
};

const CustomInput = ({ placeholder, value, onChange, type = "text" }: InputProperties) => {
  return (
    <input
      className="custom-input"
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
    />
  );
};

export default Login
