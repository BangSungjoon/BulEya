const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database('./cctv_data.db');

// 테이블 생성 (최초 실행 시)
db.serialize(() => {
    db.run(`
      CREATE TABLE IF NOT EXISTS cctv_info (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        station_id INTEGER NOT NULL,
        beacon_code INTEGER NOT NULL,
        ws_url TEXT NOT NULL
      )
    `);
  });

const storeCctvData = (station_id, beacon_code, ws_url) => {
    return new Promise((resolve, reject) => {
      const stmt = db.prepare('INSERT INTO cctv_info (station_id, beacon_code, ws_url) VALUES (?, ?, ?)');
      stmt.run(station_id, beacon_code, JSON.stringify(ws_url), (err) => {
        if (err) reject(err);
        else resolve();
      });
      stmt.finalize();
    });
};

module.exports = { storeCctvData };