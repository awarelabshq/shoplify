import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, IconButton, Menu, MenuItem } from '@mui/material';
import AccountCircle from '@mui/icons-material/AccountCircle';
import { Link, useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import './TitleBar.css';
import { TestChimpSDK } from "testchimp-js";

const TitleBar = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const currentUserEmail = Cookies.get('currentUserEmail');
  const navigate = useNavigate();

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleSignOut = () => {
    Cookies.remove('currentUserEmail');
    Cookies.remove('currentToken');
    Cookies.remove('currentUserId');
    handleClose();
    TestChimpSDK.endTrackedSession();
    navigate('/login');
  };

  return (
    <AppBar position="static" style={{ backgroundColor: '#010101' }}>
      <Toolbar>
        <Typography variant="h6" style={{ flexGrow: 1, color: '#FFFFFF' }}>
          Shoplify
        </Typography>
        {currentUserEmail && (
          <>
            <MenuItem component={Link} to="/shop" style={{ color: '#FFFFFF' }}>
              Browse
            </MenuItem>
            <MenuItem component={Link} to="/cart" style={{ color: '#FFFFFF' }}>
              Cart
            </MenuItem>
            <IconButton
              edge="end"
              color="inherit"
              aria-label="account of current user"
              aria-haspopup="true"
              onClick={handleMenu}
            >
              <AccountCircle />
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem onClick={handleSignOut}>Sign Out</MenuItem>
            </Menu>
          </>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default TitleBar;
