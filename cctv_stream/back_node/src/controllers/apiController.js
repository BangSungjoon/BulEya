const apiService = require('../services/apiService');

const getCctvData = async (req, res) => {
    try {
        const { station_id, beacon_code } = req.query;

        // 필수 파라미터 검사
        if (!station_id || !beacon_code) {
            return res.status(400).json({ error: 'station_id와 beacon_code는 필수입니다.' });
        }

        // 숫자 여부 검사
        const stationIdNum = parseInt(station_id, 10);
        const beaconCodeNum = parseInt(beacon_code, 10);
        if (isNaN(stationIdNum) || isNaN(beaconCodeNum)) {
            return res.status(400).json({ error: 'station_id와 beacon_code는 숫자여야 합니다.' });
        }

        const cctvData = await apiService.getCctvData(station_id, beacon_code);
        res.status(200).json({ 'socket_url': cctvData });
    } catch (err) {
        if (err.message.includes('CCTV 데이터가 없습니다.')) {
            res.status(404).json({ error: err.message });
        }
        else if (err.message.includes('CCTV 데이터 가져오기 오류')) {
            res.status(500).json({ error: "서버 오류" });
        }
    }
}

module.exports = { getCctvData };