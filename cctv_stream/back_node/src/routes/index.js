const express = require('express');
const router = express.Router();
const apiController = require('../controllers/apiController');

router.get('/socket-info', apiController.getCctvData);

module.exports = router;