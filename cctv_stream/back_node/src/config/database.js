const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database('./cctv_data.db');

// 테이블 생성 (최초 실행 시)
const initializeDb = () => {
  return new Promise((resolve, reject) => {
    db.serialize(() => {
      // 테이블 초기화 후 재생성
      db. run('DROP TABLE IF EXISTS cctv_info');
      db.run(`
        CREATE TABLE IF NOT EXISTS cctv_info (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          station_id INTEGER NOT NULL,
          beacon_code INTEGER NOT NULL,
          ws_url TEXT NOT NULL
        )`,
        (err) => {
          if (err) reject(err);
          else resolve();
        }
      );
    });
  });
}

const storeCctvData = (station_id, beacon_code, ws_url) => {
    return new Promise((resolve, reject) => {
      const stmt = db.prepare('INSERT INTO cctv_info (station_id, beacon_code, ws_url) VALUES (?, ?, ?)');
      stmt.run(station_id, beacon_code, ws_url, (err) => {
        if (err) reject(err);
        else resolve();
        stmt.finalize();
      });
    });
};

const getCctvData = (station_id, beacon_code) => {
    return new Promise((resolve, reject) => {
      db.get('SELECT ws_url FROM cctv_info WHERE station_id = ? AND beacon_code = ?', [station_id, beacon_code], (err, row) => {
        if (err) reject(err);
        else resolve(row ? row.ws_url : null);
      });
    });
}

module.exports = { initializeDb, storeCctvData, getCctvData };